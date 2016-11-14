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
    private Color     color;

    private int     state          = STATE_VISIBLE;

    private NetworkGame gameState;

    private double direction=4; //invalid value


    public NetworkPlayer(int id, float x, float y, NetworkGame gameState) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = COLORS[id];
        this.id = id;

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

    public int getState() {
        return state;
    }

    public List<GridPoint2> getRenderList() { return new ArrayList<GridPoint2>(0); }//return renderList; }

    public List<GridPoint2> move() {
        throw new UnsupportedOperationException("There's no move() for network players");
    }

    @Override
    public List<GridPoint2> moveTo(float newX, float newY, float thickness) {
        //todo implement direction-less moving (use Utils#bresenham, see LocalPlayer#moveTo)
        return null;
    }

    @Override
    public int getId() {
        return id;
    }
}
