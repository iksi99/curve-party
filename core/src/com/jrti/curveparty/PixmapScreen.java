package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.github.czyzby.websocket.WebSocket;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.graphics.Color.rgba8888;

/**
 * Koristi Pixmap za iscrtavanje kriva na ekran, bez problema dostiže konstantnu brzinu od 60fps
 * Created by luka on 10.11.16..
 */

public class PixmapScreen implements Screen {


    private static final int FILL_IN_THRESHOLD = 5;

    public static class ColouredPoint {
        public final GridPoint2 point;
        public final Color colour;

        public ColouredPoint(GridPoint2 point, Color colour) {
            this.point = point;
            this.colour = colour;
        }
    }


    public int numberOfPlayers;

    public static final int            GRID_X   = 800;
    public static final int            GRID_Y   = 450;
    private static final Pixmap.Format FORMAT   = Pixmap.Format.RGB565; //whatever
    private static final Color         BG_COLOR = Color.DARK_GRAY;

    private final BitmapFont    font;

    private final CurveParty game;
    private Pixmap map;
    private WebSocket networkSocket;
    private boolean isSearchingForGame = false;
    private final BitmapFont searchingFont;

    static class Score {
        Color color; int score;
        Score(Color color, int score) {
            this.color = color;
            this.score = score;
        }
    }
    private Score[] scores;

    private Texture texture;
    private List<Texture> gameSprites = new ArrayList<Texture>();
    private List<Integer> spriteX = new ArrayList<Integer>();
    private List<Integer> spriteY = new ArrayList<Integer>();

    public PixmapScreen(final CurveParty game, int numberOfPlayers) {
        this.game = game;

        map = new Pixmap(GRID_X, GRID_Y, FORMAT);
        //map.setColor(BG_COLOR);
        //map.fill();
        texture = new Texture(map);
        font = game.getFont(14);
        searchingFont = game.getFont(40);

        this.numberOfPlayers = numberOfPlayers;
        Gdx.input.setCatchBackKey(true);
    }

    public PixmapScreen startSingleplayer() {
        map.setColor(BG_COLOR);
        map.fill();
        GameState gameState = new GameState(GRID_X, GRID_Y, 4, game);
        gameState.startGame(this);
        return this;
    }

    public PixmapScreen startMultiplayer() {
        isSearchingForGame = true;
        networkSocket = Network.findGame("iksi99", numberOfPlayers, new Network.MatchmakingCallbacks() {
            @Override
            public void onGameFound(String nickname, String id, String gameId)
            {
                NetworkGame networkGame = new NetworkGame();
                networkSocket = networkGame.startGame(URLEncoder.encode(id), URLEncoder.encode(gameId),
                                                      PixmapScreen.this, game);
                isSearchingForGame = false;
            }

            @Override
            public void onError(Throwable error) {
            }

        });

        return this;
    }

    /**
     * this will reset current Pixmap if passed dimensions are different than current
     * @param x x dimension of the grid (horizontal)
     * @param y y dimension of the grid (vertical)
     */
    public void setPixmapSize(int x, int y) {
        if(x!=GRID_X || y !=GRID_Y) {
            map = new Pixmap(x, y, FORMAT);
            //map.setColor(BG_COLOR);
            //map.fill();
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            game.setScreen(new MainMenu(game));
        }

        game.spriteBatch.begin();
        int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        if(isSearchingForGame) {
            searchingFont.draw(game.spriteBatch, "Traženje u toku...", w/2-80, h/2+10);
        } else {
            texture.dispose();
            texture = new Texture(map);
            game.spriteBatch.draw(texture, 0, 0, w, h);
            for (int i = 0; i < gameSprites.size(); i++) {
                game.spriteBatch.draw(gameSprites.get(i), spriteX.get(i), spriteY.get(i));
            }
            if (scores != null) {
                for (int i = 0; i < scores.length; i++) {
                    font.setColor(scores[i].color);
                    font.draw(game.spriteBatch,
                              String.valueOf(scores[i].score),
                              0.95f * w - i * 15,
                              h - 15); //todo proper
                }
            }
        }
        game.spriteBatch.end();
        gameSprites = new ArrayList<Texture>();
        spriteX = new ArrayList<Integer>();
        spriteY = new ArrayList<Integer>();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        if(networkSocket != null && networkSocket.isOpen()) {
            networkSocket.close();
        }
    }

    public void drawPoints(List<GridPoint2> points, Color color) {
        map.setColor(color);
        //int x0=GRID_X, y0=GRID_Y, x1=0, y1=0;
        for(GridPoint2 gp : points) {
            map.drawPixel(gp.x, gp.y);
            /*if(gp.x < x0) x0=gp.x;
            if(gp.x > x1) x1=gp.x;
            if(gp.y < y0) y0=gp.y;
            if(gp.y > y1) y1=gp.y;*/
        }
        //fillInBlanks(x0, y0, x1, y1);
    }

    public void drawPoints(List<ColouredPoint> pixels) {
        for(ColouredPoint p : pixels) {
            map.drawPixel(p.point.x, p.point.y, rgba8888(p.colour));
        }
    }

    private void fillInBlanks(int x0, int y0, int x1, int y1) {
        int bg = rgba8888(BG_COLOR);
        for(int x=x0; x<=x1; x++) {
            for(int y=y0; y<=y1; y++) {
                if(map.getPixel(x, y) == bg) {
                    int err=0;
                    int c=0;
                    testSurroundings:
                    for(int i=-1; i<=1; i++) {
                        for(int j=-1; j<=1; j++) {
                            if(j!=0 || i!=0) {
                                int pix = map.getPixel(x+i, y+j);
                                if(c==0 && pix==bg) err++;
                                else if(c==0) c=pix;
                                else if(c!=pix) err++;
                                if(err >= FILL_IN_THRESHOLD) break testSurroundings;
                            }
                        }
                    }
                    if(err < FILL_IN_THRESHOLD) map.drawPixel(x, y, c);
                }
            }
        }
    }

    public void drawSprite(Texture texture, int x, int y) {
        gameSprites.add(texture);
        spriteX.add(x);
        spriteY.add(y);
    }

    public void clearScreen() {
        map.setColor(BG_COLOR);
        map.fill();
    }

    public void clearPoints(Set<GridPoint2> points) {
        map.setColor(BG_COLOR);
        for(GridPoint2 gp : points)
            map.drawPixel(gp.x, gp.y);
    }

    public void drawScores(Score[] scores) {
        this.scores = scores;
    }

    /*public void drawHeads(Player p) {
        Rectangle head = new Rectangle();
        head.x = p.getX();
        head.y = p.getY();
        head.width = 10;
        head.height = 10;
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(p.getColor());
        renderer.rect(head.x, head.y, head.width, head.height);
        renderer.end();
    } previse sporo*/
}
