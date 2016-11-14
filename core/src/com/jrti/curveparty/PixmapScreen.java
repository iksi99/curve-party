package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

/**
 * Koristi Pixmap za iscrtavanje kriva na ekran, bez problema dostiže konstantnu brzinu od 60fps
 * Created by luka on 10.11.16..
 */

public class PixmapScreen implements Screen {

    public static final int            GRID_X   = 800;
    public static final int            GRID_Y   = 450;
    private static final Pixmap.Format FORMAT   = Pixmap.Format.RGB565; //whatever
    private static final Color         BG_COLOR = Color.DARK_GRAY;

    private final CurveParty game;
    private Pixmap map;

    private Texture texture;

    public PixmapScreen(final CurveParty game) {
        this.game = game;

        map = new Pixmap(GRID_X, GRID_Y, FORMAT);
        map.setColor(BG_COLOR);
        map.fill();
        texture = new Texture(map);
    }

    public PixmapScreen startSingleplayer() {
        GameState gameState = new GameState(GRID_X, GRID_Y, 1);
        gameState.startGame(this);
        return this;
    }

    public PixmapScreen startMultiplayer() {
        //todo
        //videti šta i gde s matchmakingom, šta prikazivati korisniku dok se traži igra (v. NetworkGame)
        return this;
    }

    /**
     * this will reset current Pixmap if passed dimensions are different than current
     * @param x x dimension of the grid (horizontal)
     * @param y y dimenzion of the grid (vertical)
     */
    public void setPixmapSize(int x, int y) {
        if(x!=GRID_X || y !=GRID_Y) {
            map = new Pixmap(x, y, FORMAT);
            map.setColor(BG_COLOR);
            map.fill();
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.spriteBatch.begin();
        texture.dispose();
        texture = new Texture(map);
        game.spriteBatch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.spriteBatch.end();
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
    }

    public void drawPoints(List<GridPoint2> points, Color color) {
        map.setColor(color);
        for(GridPoint2 gp : points) {
            map.drawPixel(gp.x, gp.y);
        }
    }
}
