package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

/**
 * Zajedničke metode i polja klasama {@link LocalPlayer} i {@link NetworkPlayer}
 * Created by cactoss on 5.11.2016.
 */

public interface Player {
    boolean DIRECTION_LEFT  = false; //counter-clockwise
    boolean DIRECTION_RIGHT = true;
    Color[] COLORS          = {Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN,
                               Color.LIGHT_GRAY, Color.CHARTREUSE, Color.BLUE, Color.MAGENTA};

    int STATE_VISIBLE = 0, STATE_INVISIBLE = 1, STATE_DEAD = 2;

    double TURNING_ANGLE     = 0.052;
    int STEPS_TO_90_TURN  = (int) ((Math.PI / 2) / TURNING_ANGLE);
    int    DEFAULT_THICKNESS = 5;
    int    DEFAULT_SPEED     = 2;

    float getX();

    float getY();

    Color getColor();

    double getDirection();

    int getState();

    List<GridPoint2> move();

    List<GridPoint2> moveTo(float x, float y, float thickness);

    int getId();

    void setColor(Color color);
}
