package com.jrti.curveparty;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cactoss on 2.11.2016..
 */

public class GameState {

    private int x;
    private int y;
    private int numOfPlayers;


    private Rectangle[][]   gameMatrix;
    private Set<GridPoint2> occupiedFields;
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers) {
        this.x = x;
        this.y = y;
        this.numOfPlayers = numOfPlayers;
        gameMatrix = new Rectangle[x][y];
        occupiedFields = new HashSet<GridPoint2>();

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                gameMatrix[i][j] = new Rectangle(i, j, 0.5f, 0.5f);
            }
        }
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Rectangle[][] getGameMatrix() {
        return gameMatrix;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOccupied(GridPoint2 p) {
        return occupiedFields.contains(p);
    }

    public void setOccupied(GridPoint2 p) {
        occupiedFields.add(p);
    }

    public void setOccupied(Collection<GridPoint2> points) {
        occupiedFields.addAll(points);
    }

    public LocalPlayer addLocalPlayer(int id, int xPos, int yPos, float direction) {
        LocalPlayer p = new LocalPlayer(xPos, yPos, direction, id, this);
        playerList.add(p);
        return p;
    }
}
