package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Zajedničke metode i polja klasama {@link LocalPlayer} i {@link NetworkPlayer}
 * Created by cactoss on 5.11.2016.
 */

public interface Player {
    //sva polja u interfejsima su po defaultu public static final, dok su metode public
    boolean DIRECTION_LEFT  = true; //counter-clockwise
    boolean DIRECTION_RIGHT = false;
    Color[] COLORS          = {Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN,
                               Color.LIGHT_GRAY, Color.CHARTREUSE, Color.BLUE, Color.MAGENTA};

    int STATE_VISIBLE = 0, STATE_INVISIBLE = 1, STATE_DEAD = 2;

    double TURNING_ANGLE = 0.05;

    float getX();

    float getY();

    Color getColor();

    double getDirection();

    int getState();

    List<Rectangle> getRenderList();

    void addRectangle(Rectangle rectangle);

    void move();

    void moveTo(float x, float y, int thickness);

    void setDirection(double direction);
}
