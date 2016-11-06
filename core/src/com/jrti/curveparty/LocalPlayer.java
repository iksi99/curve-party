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

    private float     x;
    private float     y;
    private Color     color;
    private Rectangle head;

    private int     state          = STATE_VISIBLE;
    private boolean isTurningLeft  = false;
    private boolean isTurningRight = false;

    private GameState gameState;

    private float speed     = 1.3f;
    private double direction;

    private List<Rectangle> renderList = new ArrayList<Rectangle>();

    public LocalPlayer(float x, float y, float direction, int id, GameState gameState) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.color = COLORS[id];

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

    public List<Rectangle> getRenderList() { return renderList; }

    public void addRectangle(Rectangle rectangle) {
        renderList.add(rectangle);
    }

    public void move() {
        moveTo((int) (x + speed * Math.cos(direction)), (int) (y + speed * Math.sin(direction)), 1);
    }

    @Override
    public void moveTo(int newX, int newY, int thickness) {
        Rectangle newHead = head; // ako imas null pointer exception brisi ovo

        Vector2 currentPosition = new Vector2(x, y);
        Vector2 newPosition     = new Vector2(newX, newY);

        //if (newPosition.x < gameState.getX() && newPosition.y < gameState.getY()
        //&& newPosition.x > 0 && newPosition.y > 0) {
        try {
            if (state == STATE_VISIBLE) { //ne želimo okupirati polja ako je linija INVISIBLE
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
                                state = STATE_DEAD;
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
            }

            x = (int) newPosition.x;
            y = (int) newPosition.y;
        } catch (ArrayIndexOutOfBoundsException e) {
            state = STATE_DEAD;
        }
        //} else {
        //getState = true;
        //}


        head = newHead;
    }

    @Override
    public void setDirection(double direction) {
        this.direction = direction;
    }
}
