package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by cactoss on 2.11.2016..
 */

public class GameState {
    public static final boolean USE_TOUCH_COMMANDS = true;
    public static final int TIMESTEP_DURATION = 25;
    public static final int STEPS_IN_SEC = 1000/TIMESTEP_DURATION;
    public static final double TILT_THRESHOLD = 0.5;

    private final int x;
    private final int y;
    private int numOfPlayers;


    //private Rectangle[][]   gameMatrix;
    private Set<GridPoint2> occupiedFields; //could (should?) use boolean matrix
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers) {
        this.x = x;
        this.y = y;
        this.numOfPlayers = numOfPlayers;
        //gameMatrix = new Rectangle[x][y];
        occupiedFields = new HashSet<GridPoint2>();

        /*for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                gameMatrix[i][j] = new Rectangle(i, j, 0.5f, 0.5f);
            }
        }*/
    }

    public void startGame(final PixmapScreen screen) {
        Random rnd = new Random();
        final LocalPlayer localPlayer = addLocalPlayer(0, rnd.nextInt(x - 100) + 50, rnd.nextInt(y - 70) + 35,
                                               rnd.nextDouble() * 6.283185);
        for(int i=1; i<numOfPlayers; i++) {
            addAI(i, rnd.nextInt(x-100)+50, rnd.nextInt(y-70)+35, rnd.nextDouble()*6.283185);
        }
        if(USE_TOUCH_COMMANDS) setTouchControls(localPlayer);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(localPlayer.getState() != Player.STATE_DEAD) { //todo should check for player state or game end?
                    if (!USE_TOUCH_COMMANDS) {
                        double tilt = Gdx.input.getAccelerometerY();
                        if (tilt > TILT_THRESHOLD) localPlayer.turn(Player.DIRECTION_RIGHT);
                        else if (tilt < -TILT_THRESHOLD) localPlayer.turn(Player.DIRECTION_LEFT);
                    }
                    for(Player p : playerList) {
                        if(p.getState() != Player.STATE_DEAD) {
                            List<GridPoint2> moved = p.move();
                            if (p.getState() != Player.STATE_INVISIBLE)
                                screen.drawPoints(moved, p.getColor());
                        }
                    }
                }
            }
        }, 5, TIMESTEP_DURATION, TimeUnit.MILLISECONDS);
    }

    private void setTouchControls(final LocalPlayer player) {
        Gdx.input.setInputProcessor(new InputAdapter() {
            final int width = Gdx.graphics.getWidth();
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (x <= width / 2) {
                    player.setTurningLeft(true);
                } else {
                    player.setTurningRight(true);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (screenX <= width / 2) {
                    player.setTurningLeft(false);
                } else {
                    player.setTurningRight(false);
                }
                return true;
            }
        });
    }

    public List<Player> getPlayerList() {
        return playerList;
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

    public boolean isAvailable(GridPoint2 point) {
        return point.x > 0 && point.y > 0 && point.x < x && point.y < y && !isOccupied(point);
    }

    public void setOccupied(GridPoint2 p) {
        occupiedFields.add(p);
    }

    public void setOccupied(Collection<GridPoint2> points) {
        occupiedFields.addAll(points);
    }

    public LocalPlayer addLocalPlayer(int id, int xPos, int yPos, double direction) {
        LocalPlayer p = new LocalPlayer(xPos, yPos, direction, id, this);
        playerList.add(p);
        return p;
    }

    public AIPlayer addAI(int id, int xPos, int yPos, double direction) {
        AIPlayer p = new AIPlayer(id, xPos, yPos, direction, this);
        playerList.add(p);
        return p;
    }
}
