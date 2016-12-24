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
 * Keeps the state of a singleplayer game
 * Created by cactoss on 2.11.2016.
 */

public class GameState {
    /**
     * Delay between two frames in ms
     */
    public static final int    TIMESTEP_DURATION        = 25;
    /**
     * Number of frames per second
     */
    public static final int    SEC                      = 1000 / TIMESTEP_DURATION;
    /**
     * The treshold over which the Accelerometer input is registered
     */
    public static final double TILT_THRESHOLD           = 0.8;
    /**
     * The chance that a player becomes visible during a given frame
     */
    public static final double INVISIBILITY_CHANCE      = 1.0/(4 * SEC);
    /**
     * The minimum amount of frames a player can be invisible
     */
    public static final int    MIN_INISIBILITY_DURATION = (int)(0.1 * SEC);
    /**
     * The maximum amount of frames a player can be invisible
     */
    public static final int    MAX_INVISIBILITY_DURATION = SEC;
    /**
     * The minimum distance from the players' starting positions and the edge of the playing field
     */
    public static final int STARTING_MIN_DISTANCE = 60;

    public CurveParty game;

    /**
     * Size of the playing field in the X axis
     */
    private final int x;
    /**
     * Size of the playing field in the Y axis
     */
    private final int y;
    /**
     * Number of players in the game
     */
    private int numOfPlayers;

    /**
     * The list of fields that are occupied
     */
    private Set<GridPoint2> occupiedFields; //could (should?) use boolean matrix
    /**
     * The list of players in the game
     */
    private List<Player> playerList = new ArrayList<Player>();

    /**
     *
     * @param x Size of the playing field in the X axis
     * @param y Size of the playing field in the Y axis
     * @param numOfPlayers Number of players in the game
     * @param game The instance of the game class from which the constructor was called
     */
    public GameState(int x, int y, int numOfPlayers, CurveParty game) {
        this.x = x;
        this.y = y;
        this.numOfPlayers = numOfPlayers;
        occupiedFields = new HashSet<GridPoint2>();

        this.game = game;
    }


    /**
     * Initializes and runs the game
     * @param screen The screen from which the method was called
     */
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

    /**
     * Makes the player turn using touch input instead of Accelerometer
     * @param player The player for which the input is being set
     */
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

    /**
     *
     * @return Returns the list of all players in the game
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     *
     * @return Returns the size of the field in the X axis
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return Returns the size of the field in the Y axis
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if the point is occupied
     * @param p The point being checked
     * @return Returns true if occupied, otherwise returns false
     */
    public boolean isOccupied(GridPoint2 p) {
        return occupiedFields.contains(p);
    }

    /**
     * Checks if the point is available to be occupied
     * @param point The point being checked
     * @return Returns true if available, otherwise returns false
     */
    public boolean isAvailable(GridPoint2 point) {
        return point.x > 0 && point.y > 0 && point.x < x && point.y < y && !isOccupied(point);
    }

    /**
     * Sets the point as occupied
     * @param p The point being occupied
     */
    public void setOccupied(GridPoint2 p) {
        occupiedFields.add(p);
    }

    /**
     * Sets multiple points as occupied
     * @param points Points being occupied
     */
    public void setOccupied(Collection<GridPoint2> points) {
        occupiedFields.addAll(points);
    }

    /**
     * Adds a new human player to the list of players
     * @param id A number for identifying different players
     * @param xPos Position of the player on the X axis
     * @param yPos Position of the player on the Y axis
     * @param direction The direction the player is facing, measured in radians from the positive X
     *                  direction.
     * @return Returns the player that was added.
     */
    public LocalPlayer addLocalPlayer(int id, int xPos, int yPos, double direction) {
        LocalPlayer p = new LocalPlayer(xPos, yPos, direction, id, this);
        playerList.add(p);
        return p;
    }

    /**
     * Adds a new AI controlled player to the list of players
     * @param id A number for identifying different players
     * @param xPos Position of the player on the X axis
     * @param yPos Position of the player on the Y axis
     * @param direction The direction the player is facing, measured in radians from the positive X
     *                  direction.
     * @return Returns the player that was added.
     */
    public AIPlayer addAI(int id, int xPos, int yPos, double direction) {
        AIPlayer p = AITypes.generateRandom(id, xPos, yPos, direction, this);
        playerList.add(p);
        return p;
    }
}
