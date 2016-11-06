package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Created by cactoss on 5.11.2016..
 */

public interface Player {

    float getX();

    float getY();

    Color getColor();

    float getDirection();

    void turn(boolean direction);

    boolean isDead();

    List<Rectangle> getRenderList();

    void addRectangle(Rectangle rectangle);

    void setTurningRight(boolean x);

    void setTurningLeft(boolean x);

    void move();
}
