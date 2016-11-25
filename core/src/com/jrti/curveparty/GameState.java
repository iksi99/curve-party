package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
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
    public static final int    TIMESTEP_DURATION        = 25;
    public static final int    SEC                      = 1000 / TIMESTEP_DURATION;
    public static final double TILT_THRESHOLD           = 0.8;
    public static final double INVISIBILITY_CHANCE      = 1.0/(4 * SEC);
    public static final int    MIN_INISIBILITY_DURATION = (int)(0.1 * SEC);
    public static final int    MAX_INVISIBILITY_DURATION = SEC;
    public static final int STARTING_MIN_DISTANCE = 60;

    public CurveParty game;

    private final int x;
    private final int y;
    private int numOfPlayers;


    //private Rectangle[][]   gameMatrix;
    private Set<GridPoint2> occupiedFields; //could (should?) use boolean matrix
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers, CurveParty game) {
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

        this.game = game;
    }

    public void startGame(final PixmapScreen screen) {
        Random rnd = new Random();
        GridPoint2[] points = Utils.randomPoints(rnd, numOfPlayers, x, y, 80, 50);
        final LocalPlayer localPlayer = addLocalPlayer(0, points[0].x, points[0].y, rnd.nextDouble() * 6.283185);
        localPlayer.setColor(Color.WHITE);
        for(int i=1; i<numOfPlayers; i++) {
            addAI(i, points[i].x, points[i].y, rnd.nextDouble()*6.283185);
        }
        if(game.useTouchCommands) setTouchControls(localPlayer);
        for(Player p : playerList) {
            List<GridPoint2> starting = new ArrayList<GridPoint2>(9);
            for(int i=-2;i<=2;i++) for(int j=-2; j<=2;j++) starting.add(new GridPoint2((int)p.getX()+i,(int)p.getY()+j));
            screen.drawPoints(starting, p.getColor());
        }
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            int score=0;
            @Override
            public void run() {
                if(localPlayer.getState() != Player.STATE_DEAD) score++;
                if (!game.useTouchCommands) {
                    double tilt = Gdx.input.getAccelerometerY();
                    if (tilt > TILT_THRESHOLD) localPlayer.turn(Player.DIRECTION_RIGHT);
                    else if (tilt < -TILT_THRESHOLD) localPlayer.turn(Player.DIRECTION_LEFT);
                }
                boolean alive = false;
                for(Player p : playerList) {
                    if(p.getState() != Player.STATE_DEAD) {
                        alive = true;
                        List<GridPoint2> moved = p.move();
                        if (p.getState() != Player.STATE_INVISIBLE)
                            screen.drawPoints(moved, p.getColor());
                    }
                }
                if(!alive) {
                    exec.shutdownNow();
                    //todo skor ?
                }
            }
        }, 2000, TIMESTEP_DURATION, TimeUnit.MILLISECONDS);
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
        AIPlayer p = AITypes.generateRandom(id, xPos, yPos, direction, this);
        playerList.add(p);
        return p;
    }
}
