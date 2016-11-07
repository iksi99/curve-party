package com.jrti.curveparty;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cactoss on 2.11.2016..
 */

public class GameState {

    private int x;
    private int y;
    private int numOfPlayers;


    private Rectangle[][] gameMatrix;
    private boolean[][]   occupiedFields;
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers) {
        this.x = x;
        this.y = y;
        this.numOfPlayers = numOfPlayers;
        gameMatrix = new Rectangle[x][y];
        occupiedFields = new boolean[x][y];

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

    public boolean isOccupied(int x, int y) {
        return occupiedFields[x][y];
    }

    public void setOccupied(int x, int y) {
        occupiedFields[x][y] = true;
    }

    public LocalPlayer addLocalPlayer(int id, int xPos, int yPos, float direction) {
        LocalPlayer p = new LocalPlayer(xPos, yPos, direction, id, this);
        playerList.add(p);
        return p;
    }
}
