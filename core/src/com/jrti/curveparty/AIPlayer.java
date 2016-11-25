package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.jrti.curveparty.GameState.SEC;

/**
 * Created by luka on 17.11.16..
 */

public class AIPlayer implements Player {

    private final int FAR_AWAY;
    private static final int RECENT       = 2;

    private final int lookaheadLimit;
    private final double lookaheadAngle;

    private final int id;
    private Color color;
    private Random rnd = new Random();

    private float x, y;
    private float speed=DEFAULT_SPEED;
    private double dir;
    private int state;
    private int thickness;
    private int turningLeft=0;
    private int turningRight=0;
    private int goStraight=0;

    private int     turnsInvisible;
    private boolean aggressiveRandomize;
    private int     almostEqualThreshold;
    private int     decisiveMove;
    private int     moveBoldness;
    private int     safetyTurnThreshold;

    private final GameState game;
    private List<List<GridPoint2>> recentlyOccupied = new LinkedList<List<GridPoint2>>();

    public AIPlayer(int id, int x, int y, double direction, GameState game) {
        this(id, x, y, direction, game, STEPS_TO_90_TURN * 3, Math.toRadians(30), false,
             2, SEC/2, 2, (int) (STEPS_TO_90_TURN*1.5));
    }

    public AIPlayer(int id, int x, int y, double direction, GameState game, int lookaheadLimit,
                    double lookaheadAngle, boolean aggressiveRandomize, int moveBoldness, int decisiveMove,
                    int almostEqual, int safeTurn) {
        this.id = id;
        this.color = COLORS[id];
        this.x = x;
        this.y = y;
        this.dir = direction;
        this.state = STATE_VISIBLE;
        this.game = game;
        this.thickness = DEFAULT_THICKNESS;
        this.lookaheadLimit = lookaheadLimit;
        this.lookaheadAngle = lookaheadAngle;
        this.aggressiveRandomize = aggressiveRandomize;
        this.decisiveMove = decisiveMove;
        this.almostEqualThreshold = almostEqual;
        this.moveBoldness = moveBoldness;
        FAR_AWAY = lookaheadLimit*2;
        this.safetyTurnThreshold = safeTurn;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public double getDirection() {
        return dir;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public List<GridPoint2> move() {
        setVisibility();
        if(turningLeft>0) turningLeft--;
        if(turningRight>0) turningRight--;
        if(goStraight>0) goStraight--;
        if(turningLeft > 0) return moveLeft();
        if(turningRight > 0) return moveRight();
        if(goStraight>0) return moveStraight();

        int straight = lookStraight();
        int left = lookLeft();
        int right = lookRight();
        if(straight < safetyTurnThreshold) {
            if(aggressiveRandomize && Utils.almostEqual(left, right, almostEqualThreshold)) return randomTurn();
            if(left > right || Utils.almostEqual(left, right, almostEqualThreshold)) return moveLeft();
            return moveRight();
        }
        if(Utils.almostEqual(straight, left, right, almostEqualThreshold)) return randomMove();
        if(aggressiveRandomize) {
            if(straight>right && Utils.almostEqual(straight, left, almostEqualThreshold)) return maybeLeft();
            if(straight>left && Utils.almostEqual(straight, right, almostEqualThreshold)) return maybeRight();
            if(left>straight && Utils.almostEqual(left, right, almostEqualThreshold)) return randomTurn();
        }
        if (straight != FAR_AWAY) {
            if(left >= right && left >= straight) {
                turningLeft = moveBoldness;
                return moveLeft();
            } else if(right > left && right > straight) {
                turningRight = moveBoldness;
                return moveRight();
            }
        }
        return moveStraight();
    }

    private List<GridPoint2> randomMove() {
        int roll = rnd.nextInt(3);
        switch (roll) {
            case 0: turningLeft= decisiveMove; return moveLeft();
            case 1: goStraight= decisiveMove; return moveStraight();
            case 2: turningRight= decisiveMove; return moveRight();
        }
        return null;
    }
    private List<GridPoint2> maybeLeft() {
        int roll = rnd.nextInt(2);
        if(roll==0) {turningLeft= decisiveMove; return moveLeft();}
        else        {goStraight= decisiveMove; return moveStraight();}
    }
    private List<GridPoint2> maybeRight() {
        int roll = rnd.nextInt(2);
        if(roll==0) {turningRight= decisiveMove; return moveRight();}
        else        {goStraight= decisiveMove; return moveStraight();}
    }
    private List<GridPoint2> randomTurn() {
        int roll = rnd.nextInt(2);
        if(roll==0) {turningLeft= decisiveMove; return moveLeft();}
        else        {turningRight= decisiveMove; return moveRight();}
    }


    private List<GridPoint2> moveLeft() {
        dir-=TURNING_ANGLE;
        return moveStraight();
    }

    private List<GridPoint2> moveRight() {
        dir+=TURNING_ANGLE;
        return moveStraight();
    }

    private List<GridPoint2> moveStraight() {
        return moveTo((float)(x + speed*Math.cos(dir)), (float)(y + speed*Math.sin(dir)), thickness);
    }

    @Override
    public List<GridPoint2> moveTo(float newX, float newY, float thickness) {
        List<GridPoint2> occupied = new ArrayList<GridPoint2>(16);
        if (state == STATE_VISIBLE && (x!=newX || y!=newY)) { //ne želimo okupirati polja ako je linija INVISIBLE
            int edgeToHead = Math.round((thickness - 1) / 2);
            boolean collision = false;
            for(int i=-edgeToHead; i<=edgeToHead; i++) {
                int ix0 = (int)Math.round(x + i*Math.cos(dir + Math.PI/2)),
                        ix1 = (int)Math.round(newX + i*Math.cos(dir + Math.PI/2)),
                        iy0 = (int)Math.round(y + i*Math.sin(dir + Math.PI/2)),
                        iy1 = (int)Math.round(newY + i*Math.sin(dir + Math.PI/2));
                List<GridPoint2> line = Utils.bresenham(ix0, iy0, ix1, iy1);
                line.remove(new GridPoint2(ix0, iy0));
                occupied.addAll(line);
                for (GridPoint2 p : line) {
                    if(p.x < 0 || p.y < 0 || p.x >= game.getX() || p.y >= game.getY()
                       || (!isRecentlyOccupied(p) && game.isOccupied(p))) {
                        state = STATE_DEAD;
                        collision = true;
                    } else {
                        //addRectangle(p);
                    }
                }
            }
            if(!collision) {
                game.setOccupied(occupied);
                recentlyOccupied.add(occupied);
                if(recentlyOccupied.size() > RECENT) {
                    recentlyOccupied.remove(0);
                }
            }
        }
        if(state != STATE_DEAD) {
            x = newX;
            y = newY;
        }
        return occupied;
    }

    void setVisibility() {
        switch (state) {
            case STATE_VISIBLE:
                int invisible = Utils.rollInvisible(rnd);
                if (invisible != 0) {
                    state = STATE_INVISIBLE;
                    turnsInvisible = invisible;
                }
                break;
            case STATE_INVISIBLE:
                turnsInvisible--;
                if (turnsInvisible == 0)
                    state = STATE_VISIBLE;
                break;
            case STATE_DEAD:
                ;
                break;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    private boolean isRecentlyOccupied(GridPoint2 p) {
        for(List<GridPoint2> l : recentlyOccupied) {
            for(GridPoint2 gp : l)
                if(gp.equals(p)) return true;
        }
        return false;
    }

    private int lookLeft() {
        return lookAhead(dir-lookaheadAngle, lookaheadLimit, x, y);
    }
    private int lookStraight() {
        int edgeToHead = (thickness-1)/2;
        float lx = (float) (x + edgeToHead * Math.cos(dir - Math.PI / 2));
        float ly = (float) (y + edgeToHead * Math.sin(dir - Math.PI / 2));
        float rx = (float) (x + edgeToHead * Math.cos(dir + Math.PI / 2));
        float ry = (float) (y + edgeToHead * Math.sin(dir + Math.PI / 2));
        return Math.min(lookAhead(dir, lookaheadLimit, lx, ly), lookAhead(dir, lookaheadLimit, rx, ry));
    }
    private int lookRight() {
        return lookAhead(dir+lookaheadAngle, lookaheadLimit, x, y);
    }

    private int lookAhead(double angle, int limit, float x, float y) {
        int              nx     = (int) Math.round(x+limit*Math.cos(angle));
        int              ny     = (int) Math.round(y+limit*Math.sin(angle));
        List<GridPoint2> points = Utils.bresenham(Math.round(x), Math.round(y), nx, ny);
        points.remove(0);
        for(int i=0; i<points.size(); i++)
            if(!game.isAvailable(points.get(i))) {
                return i;
            }
        return FAR_AWAY;
    }
}
