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

public class NetworkPlayer implements Player {

    private int       id;
    private float     x;
    private float     y;
    private Color     color;
    private Rectangle head;

    private int     state          = STATE_VISIBLE;
    private boolean isTurningLeft  = false;
    private boolean isTurningRight = false;

    private GameState gameState;

    private float speed     = 2f;
    private double direction;
    private float turnAngle = 0.05f;

    private List<Rectangle> renderList = new ArrayList<Rectangle>();

    public NetworkPlayer(float x, float y, int id, double direction, GameState gameState) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = COLORS[id];
        this.direction = direction;

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

    public int getState() {
        return state;
    }

    public List<Rectangle> getRenderList() { return renderList; }

    public void addRectangle(Rectangle rectangle) {
        renderList.add(rectangle);
    }

    //todo videti šta s ovim - NetworkPlayer se generalno neće kretati pravo, tj. ova metoda je višak u interfejsu
    //todo Player, ali je neophodna zbog načina na koji se igra renderuje. Trebalo bi preferirati moveTo
    public void move() {
        moveTo((float) (x + speed * Math.cos(direction)), (float) (y + speed * Math.sin(direction)), 1);
    }

    @Override
    public void moveTo(float newX, float newY, int thickness) {
        Rectangle newHead = head;

        Vector2 currentPosition = new Vector2(x, y);
        Vector2 newPosition     = new Vector2(newX, newY);

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


        head = newHead;
    }

    @Override
    public void setDirection(double direction) {
        this.direction = direction;
    }
}
