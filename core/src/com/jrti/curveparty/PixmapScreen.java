package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;

import java.util.List;
import java.util.Random;

/**
 * Koristi Pixmap za iscrtavanje kriva na ekran, u nadi da će biti brže nego kreiranje stotina kvadrata koristeći
 * ShapeRenderer. Trenutno ne radi (ništa se ne iscrtava). Potrebno je ručno skalirati piksele.
 * Created by luka on 10.11.16..
 */

public class PixmapScreen implements Screen {
    public static final int GRID_X = 800;
    public static final int GRID_Y = 450;

    private final CurveParty         game;
    private final Pixmap map = new Pixmap(GRID_X, GRID_Y, Pixmap.Format.RGB565);

    private LocalPlayer localPlayer;
    //u redu je da znamo koji je "naš" player i deklarišemo kao Local kad ga već izdvajamo

    private GameState gameState = new GameState(GRID_X, GRID_Y, 1);
    private Texture texture = new Texture(map);

    public PixmapScreen(final CurveParty game) {
        this.game = game;

        final int width = Gdx.graphics.getWidth();

        Random rnd = new Random();
        localPlayer = gameState.addLocalPlayer(0, rnd.nextInt(GRID_X - 100) + 50, rnd.nextInt(GRID_Y - 70) + 35,
                                               rnd.nextFloat() * 6.283185f);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (x <= width / 2) {
                    localPlayer.setTurningLeft(true);
                } else {
                    localPlayer.setTurningRight(true);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (screenX <= width / 2) {
                    localPlayer.setTurningLeft(false);
                } else {
                    localPlayer.setTurningRight(false);
                }
                return true;
            }
        });
    }

    @Override
    public void show() {
        texture.bind();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (Player p : gameState.getPlayerList()) {
            if (p.getState() != Player.STATE_DEAD) {
                List<GridPoint2> occupied = p.move();
                for(GridPoint2 gp : occupied) {
                    map.drawPixel(gp.x, gp.y, 0xF800);
                }
            }
        }
        texture.draw(map, 0, 0);//todo rucno skalirati
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
}
