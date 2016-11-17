package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by cactoss on 31.10.2016..
 */

public class LocalPlayer implements Player {

    private float     x;
    private float     y;
    private Color     color;

    private int     state          = STATE_VISIBLE;
    private boolean isTurningLeft  = false;
    private boolean isTurningRight = false;

    private GameState gameState;

    private float speed     = 1;
    private double direction;
    private int thickness;
    private int id;

    private List<List<GridPoint2>> recentlyOccupied = new LinkedList<List<GridPoint2>>();
    private static final int RECENT = 2;
    //private List<GridPoint2> renderList = new ArrayList<GridPoint2>(1024);

    public LocalPlayer(int x, int y, double direction, int id, GameState gameState) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.color = COLORS[id];
        this.id = id;
        thickness = DEFAULT_THICKNESS;

        this.gameState = gameState;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public double getDirection() {
        return direction;
    }

    public void turn(boolean direction) {
        if (direction == DIRECTION_LEFT) {
            this.direction += TURNING_ANGLE;
        } else {
            this.direction -= TURNING_ANGLE;
        }
    }

    public int getState() {
        return state;
    }

    public void setTurningLeft(boolean turningLeft) {
        isTurningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        isTurningRight = turningRight;
    }

    @Override
    public List<GridPoint2> move() {
        float x1 = (float) (x + speed * Math.cos(direction));
        float y1 = (float) (y + speed * Math.sin(direction));
        List<GridPoint2> ret = moveTo(x1, y1, thickness);
        if (isTurningLeft) {
            turn(DIRECTION_LEFT);
        } else if (isTurningRight) {
            turn(DIRECTION_RIGHT);
        }
        return ret;
    }

    @Override
    public List<GridPoint2> moveTo(float newX, float newY, float thickness) {
        List<GridPoint2> occupied = new ArrayList<GridPoint2>(16);
        if (state == STATE_VISIBLE && (x!=newX || y!=newY)) { //ne želimo okupirati polja ako je linija INVISIBLE
            int edgeToHead = Math.round((thickness - 1) / 2);
            boolean collision = false;
            for(int i=-edgeToHead; i<=edgeToHead; i++) {
                int ix0 = (int)Math.round(x + i*Math.cos(direction + Math.PI/2)),
                        ix1 = (int)Math.round(newX + i*Math.cos(direction + Math.PI/2)),
                        iy0 = (int)Math.round(y + i*Math.sin(direction + Math.PI/2)),
                        iy1 = (int)Math.round(newY + i*Math.sin(direction + Math.PI/2));
                List<GridPoint2> line = Utils.bresenham(ix0, iy0, ix1, iy1);
                line.remove(new GridPoint2(ix0, iy0));
                occupied.addAll(line);
                for (GridPoint2 p : line) {
                    if(p.x < 0 || p.y < 0 || p.x >= gameState.getX() || p.y >= gameState.getY()
                       || (!isRecentlyOccupied(p) && gameState.isOccupied(p))) {
                        state = STATE_DEAD;
                        collision = true;
                    } else {
                        //addRectangle(p);
                    }
                }
            }
            if(!collision) {
                gameState.setOccupied(occupied);
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

    private boolean isRecentlyOccupied(GridPoint2 p) {
        for(List<GridPoint2> l : recentlyOccupied) {
            for(GridPoint2 gp : l)
                if(gp.equals(p)) return true;
        }
        return false;
    }

    private Set<GridPoint2> drawOffsetLine(int i, float newX, float newY) {
        int ix0 = (int)Math.round(x + i*Math.cos(direction + Math.PI/2)),
                ix1 = (int)Math.round(newX + i*Math.cos(direction + Math.PI/2)),
                iy0 = (int)Math.round(y + i*Math.sin(direction + Math.PI/2)),
                iy1 = (int)Math.round(newY + i*Math.sin(direction + Math.PI/2));
        if(i>0) Gdx.app.log("LocalPlayer", String.format(Locale.ENGLISH, "+{%d,%d}->{%d,%d}", ix0, iy0, ix1, iy1));
        else    Gdx.app.log("LocalPlayer", String.format(Locale.ENGLISH, "-{%d,%d}->{%d,%d}", ix0, iy0, ix1, iy1));
        List<GridPoint2> line = Utils.bresenham(ix0, iy0, ix1, iy1);
        line.remove(new GridPoint2(ix0, iy0));
        Gdx.app.log("LocalPlayer", "occupied " + line);
        for (GridPoint2 p : line) {
            if(p.x < 0 || p.y < 0 || p.x >= gameState.getX() || p.y >= gameState.getY()
               || (!recentlyOccupied.contains(p) && gameState.isOccupied(p))) {
                state = STATE_DEAD;
                Gdx.app.log("LocalPlayer", "died at (" + p.x + "," + p.y + ")");
                line = null; break;
            } else {
                //addRectangle(p);
            }
        }
        return new HashSet<GridPoint2>(line==null?new ArrayList<GridPoint2>() : line);
    }

    @Override
    public int getId() {
        return id;
    }
}
