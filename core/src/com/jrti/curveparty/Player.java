package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Created by cactoss on 5.11.2016..
 */

public interface Player {
    public float getX();

    public void setX(float x);

    public float getY();

    public void setY(float y);

    public Color getColor();

    public void setColor(Color color);

    public float getDirection();

    public void setDirection(float direction);

    public void turn(boolean direction);

    public boolean isDead();

    public List<Rectangle> getRenderList();

    public void addRectangle(Rectangle rectangle);

    public void setTurningRight(boolean x);

    public void setTurningLeft(boolean x);

    public void move();
}
