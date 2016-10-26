package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by cactoss on 26.10.2016..
 */

public class ColoredRectangle extends Rectangle {
    private float x;
    private float y;
    private float width;
    private float height;
    private Color c;


    // konstruktori
    public ColoredRectangle () {

    }

    public ColoredRectangle (float x, float y, float width, float height, Color c) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.c = new Color(c);
    }

    public ColoredRectangle (ColoredRectangle rect) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
        c = new Color(rect.c);
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }
}
