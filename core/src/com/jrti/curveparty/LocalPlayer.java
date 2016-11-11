package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private List<GridPoint2> renderList = new ArrayList<GridPoint2>(1024);

    public LocalPlayer(int x, int y, float direction, int id, GameState gameState) {
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

    public List<GridPoint2> getRenderList() { return renderList; }

    public void addRectangle(GridPoint2 point) {
        if(!MainMenu.USING_PIXMAP)
            renderList.add(point);
    }

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
    public List<GridPoint2> moveTo(float newX, float newY, int thickness) {
        List<GridPoint2> occupied = new ArrayList<GridPoint2>(16);
        if (state == STATE_VISIBLE && (x!=newX || y!=newY)) { //ne želimo okupirati polja ako je linija INVISIBLE
            int edgeToHead = (thickness - 1) / 2;
            for(int i=-edgeToHead; i<=edgeToHead; i++) {
                int ix0 = (int)Math.round(x + i*Math.cos(direction + Math.PI/2)),
                    ix1 = (int)Math.round(newX + i*Math.cos(direction + Math.PI/2)),
                    iy0 = (int)Math.round(y + i*Math.sin(direction + Math.PI/2)),
                    iy1 = (int)Math.round(newY + i*Math.sin(direction + Math.PI/2));
                Set<GridPoint2> line = bresenham(ix0, iy0, ix1, iy1);
                line.remove(new GridPoint2(ix0, iy0));
                occupied.addAll(line);
                for (GridPoint2 p : line) {
                    if(p.x < 0 || p.y < 0 || p.x >= gameState.getX() || p.y >= gameState.getY()) {
                        state = STATE_DEAD;
                    } else if (!gameState.isOccupied(p) || i!=0) {
                        addRectangle(p);
                        if(i == 0) gameState.setOccupied(p);
                    } else {
                        state = STATE_DEAD;
                    }
                }
            }
        }
        if(state != STATE_DEAD) {
            x = newX;
            y = newY;
        }
        return occupied;
    }

    private Set<GridPoint2> bresenham(int x0, int y0, int x1, int y1) {
        Set<GridPoint2> result = new HashSet<GridPoint2>((int) speed * 2);
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = (dx > dy ? dx : -dy) / 2, e2;

        for (; ; ) {
            result.add(new GridPoint2(x0, y0));
            if (x0 == x1 && y0 == y1) break;
            e2 = err;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dy) {
                err += dx;
                y0 += sy;
            }
        }
        return result;
    }

    @Override
    public void setDirection(double direction) {
        this.direction = direction;
    }

    @Override
    public int getId() {
        return id;
    }
}
