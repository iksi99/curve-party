package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cactoss on 2.11.2016..
 */

public class GameState {

    private int x;
    private int y;
    private int numOfPlayers;



    private Rectangle[][] gameMatrix;
    private int[][] occupiedFields;
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers)
    {
        gameMatrix = new Rectangle[x][y];
        occupiedFields = new int[x][y];

        for (int i = 0; i < x; i++)
        {
            for (int j = 0; j < y; j++)
            {
                //System.out.println("0");
                gameMatrix[i][j] = new Rectangle(i, j, 0.5f, 0.5f);
                occupiedFields[i][j] = 0;
            }
        }

        makePlayers();
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Rectangle[][] getGameMatrix() {
        return gameMatrix;
    }

    public int[][] getOccupiedFields() {
        return occupiedFields;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOccupied(int x, int y)
    {
        if (occupiedFields[x][y] != 0) return true;
        else return false;
    }

    public void setOccupied(int x, int y)
    {
        occupiedFields[x][y] = 1;
    }

    public void makePlayers() {
        playerList.add(new LocalPlayer(100, 100, Color.CYAN, this));
    }
}
