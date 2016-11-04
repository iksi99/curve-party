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

public class Player {
    public static final String TAG = "CurveParty";

    private Color color;
    private Rectangle head;

    private boolean isDead = false;

    private GameState gameState;

    private double speed = 3f;
    private double direction = 1f;

    private List<Rectangle> renderList = new ArrayList<Rectangle>();

    public Player(int x, int y, Color color, GameState gameState) {
        this.color = color;

        this.gameState = gameState;

        this.head = gameState.getGameMatrix()[x][y];
        renderList.add(head);
        gameState.setOccupied(x, y);
    }

    public int getX() {
        return (int) head.getX();
    }

    public int getY() {
        return (int) head.getY();
    }

    public Color getColor() {
        return color;
    }

    public boolean isDead() {
        return isDead;
    }

    public List<Rectangle> getRenderList() { return renderList; }

    public void addRectangle(Rectangle rectangle)
    {
        renderList.add(rectangle);
    }

    public void move() {
        int     x               = (int) head.getX(), y = (int) head.getY();
        Vector2 currentPosition = new Vector2(x, y);
        Vector2 newPosition     = new Vector2((float)(x + speed * Math.cos(direction)),
                                              (float)(y + speed * Math.sin(direction)));

        //if (newPosition.x < gameState.getX() && newPosition.y < gameState.getY()
                //&& newPosition.x > 0 && newPosition.y > 0) {
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
                                head.setPosition(newPosition);
                            }
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            isDead = true;
        }
        //} else {
            //isDead = true;
        //}
    }
}
