package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cactoss on 31.10.2016..
 */

public class NetworkPlayer implements Player {

    private int       id;
    private float     x;
    private float     y;
    private int       score;
    private Color     color;

    private int     state          = STATE_VISIBLE;


    private double direction=4; //invalid value


    public NetworkPlayer(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = COLORS[id];
        this.id = id;
    }

    public void resetTo(int x, int y) {
        this.x = x;
        this.y = y;
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

    public int getState() {
        return state;
    }

    public List<GridPoint2> getRenderList() { return new ArrayList<GridPoint2>(0); }//return renderList; }

    public List<GridPoint2> move() {
        throw new UnsupportedOperationException("There's no move() for network players");
    }

    @Override
    public List<GridPoint2> moveTo(float newX, float newY, float thickness) {
        List<GridPoint2> occupied = new ArrayList<GridPoint2>(16);
        if (state == STATE_VISIBLE && (x!=newX || y!=newY)) { //ne želimo okupirati polja ako je linija INVISIBLE
            int edgeToHead = Math.round((thickness - 1) / 2);
            for(int i=-edgeToHead; i<=edgeToHead; i++) {
                int ix0 = (int)Math.round(x + i*Math.cos(direction + Math.PI/2)),
                        ix1 = (int)Math.round(newX + i*Math.cos(direction + Math.PI/2)),
                        iy0 = (int)Math.round(y + i*Math.sin(direction + Math.PI/2)),
                        iy1 = (int)Math.round(newY + i*Math.sin(direction + Math.PI/2));
                List<GridPoint2> line = Utils.bresenham(ix0, iy0, ix1, iy1);
                line.remove(new GridPoint2(ix0, iy0));
                occupied.addAll(line);
            }
        }
        if(state != STATE_DEAD) {
            x = newX;
            y = newY;
        }
        return occupied;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
