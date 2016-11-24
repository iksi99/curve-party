package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by luka on 17.11.16..
 */

public class AIPlayer implements Player {

    private final int FAR_AWAY;
    private static final int RECENT = 2;

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
    private int turnsInvisible;

    private final GameState game;
    private List<List<GridPoint2>> recentlyOccupied = new LinkedList<List<GridPoint2>>();

    public AIPlayer(int id, int x, int y, double direction, GameState game) {
        this.id = id;
        this.color = COLORS[id];
        this.x = x;
        this.y = y;
        this.dir = direction;
        this.state = STATE_VISIBLE;
        this.game = game;
        this.thickness = DEFAULT_THICKNESS;
        this.lookaheadLimit = (int)(speed * STEPS_TO_90_TURN * 1.5);
        FAR_AWAY = lookaheadLimit*2;
        this.lookaheadAngle = Math.toRadians(20);
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
        if(turningLeft > 0) return moveLeft();
        if(turningRight > 0) return moveRight();

        int straight = lookStraight();
        int left = lookLeft();
        int right = lookRight();
        if(straight == FAR_AWAY && left == FAR_AWAY && right == FAR_AWAY) return moveStraight();
        if(straight == FAR_AWAY && left == FAR_AWAY && right != FAR_AWAY) return moveLeft();
        if(straight == FAR_AWAY && left != FAR_AWAY && right == FAR_AWAY) return moveRight();
        if (straight != FAR_AWAY) {
            if(left >= right && left >= straight) {
                Gdx.app.log("AIPlayer", "turning left");
                turningLeft = 2;
                return moveLeft();
            } else if(right > left && right > straight) {
                Gdx.app.log("AIPlayer", "turning right");
                turningRight = 2;
                return moveRight();
            }
        }
        return moveStraight();
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
        return lookAhead(dir-lookaheadAngle, lookaheadLimit);
    }
    private int lookStraight() {
        return lookAhead(dir, lookaheadLimit);
    }
    private int lookRight() {
        return lookAhead(dir+lookaheadAngle, lookaheadLimit);
    }

    private int lookAhead(double angle, int limit) {
        int              nx     = (int) Math.round(x+limit*Math.cos(angle));
        int              ny     = (int) Math.round(y+limit*Math.sin(angle));
        List<GridPoint2> points = Utils.bresenham(Math.round(x), Math.round(y), nx, ny);
        points.remove(0);
        for(int i=0; i<points.size(); i++)
            if(!game.isAvailable(points.get(i))) {
                Gdx.app.log("AIPlayer", "lookahead got to " + points.get(i) + " ("+i+")");
                return i;
            }
        return FAR_AWAY;
    }
}
