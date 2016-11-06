package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cactoss on 31.10.2016..
 */

public class LocalPlayer implements Player {
    public static final  boolean DIRECTION_LEFT  = true; //counter-clockwise
    public static final  boolean DIRECTION_RIGHT = false;
    private static final double  TURNING_ANGLE   = 0.05f;

    private float     x;
    private float     y;
    private Color     color;
    private Rectangle head;

    private boolean isDead         = false;
    private boolean isTurningLeft  = false;
    private boolean isTurningRight = false;

    private GameState gameState;

    private float speed     = 1.3f;
    private float direction = 0.5f;

    private List<Rectangle> renderList = new ArrayList<Rectangle>();

    public LocalPlayer(float x, float y, Color color, GameState gameState) {
        this.x = x;
        this.y = y;
        this.color = color;

        this.gameState = gameState;

        this.head = gameState.getGameMatrix()[(int) x][(int) y];
        renderList.add(head);
        gameState.setOccupied((int) x, (int) y);
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

    public float getDirection() {
        return direction;
    }

    public void turn(boolean direction) {
        if (direction == DIRECTION_LEFT) {
            this.direction += TURNING_ANGLE;
        } else {
            this.direction -= TURNING_ANGLE;
        }
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void setTurningLeft(boolean turningLeft) {
        isTurningLeft = turningLeft;
    }

    @Override
    public void setTurningRight(boolean turningRight) {
        isTurningRight = turningRight;
    }

    public List<Rectangle> getRenderList() { return renderList; }

    public void addRectangle(Rectangle rectangle) {
        renderList.add(rectangle);
    }

    public void move() {
        Rectangle newHead = head;

        Vector2 currentPosition = new Vector2(x, y);
        Vector2 newPosition     = new Vector2((float) (x + speed * Math.cos(direction)),
                                              (float) (y + speed * Math.sin(direction)));

        try {
            for (int i = (int) Math.min(currentPosition.x, newPosition.x);
                 i <= Math.max(currentPosition.x, newPosition.x);
                 i++) {
                for (int j = (int) Math.min(currentPosition.y, newPosition.y);
                     j <= Math.max(currentPosition.y, newPosition.y);
                     j++) {
                    Rectangle r = gameState.getGameMatrix()[i][j];

                    float[] vert = {
                            r.x, r.y,
                            r.x, r.y + 1,
                            r.x + 1, r.y + 1,
                            r.x + 1, r.y
                    };

                    Array<Vector2> vert2 = new Array<Vector2>();
                    vert2.add(new Vector2(r.x, r.y));
                    vert2.add(new Vector2(r.x, r.y + 1));
                    vert2.add(new Vector2(r.x + 1, r.y + 1));
                    vert2.add(new Vector2(r.x + 1, r.y));

                    Polygon pr = new Polygon(vert);

                    if (Intersector.intersectLinePolygon(currentPosition, newPosition, pr)) {
                        if (gameState.isOccupied(i, j) && !head.equals(r)) {
                            isDead = true;
                        } else {
                            gameState.setOccupied(i, j);
                            addRectangle(r);
                            if (Intersector.isPointInPolygon(vert2, newPosition)) {
                                newHead = r;
                            }
                        }
                    }
                }
            }

            x = newPosition.x;
            y = newPosition.y;
        } catch (ArrayIndexOutOfBoundsException e) {
            isDead = true;
        }

        if (isTurningLeft) {
            turn(DIRECTION_LEFT);
        } else if (isTurningRight) {
            turn(DIRECTION_RIGHT);
        }

        head = newHead;
    }
}
