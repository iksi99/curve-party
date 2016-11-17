package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

/**
 * Created by cactoss on 7.11.2016..
 */

public class GameScreen implements Screen {
    public static final boolean PROFILE_RENDER = true;

    public static final int GRID_X = 800;
    public static final int GRID_Y = 450;

    private final CurveParty game;
    private ShapeRenderer ren;
    private OrthographicCamera camera;

    private LocalPlayer localPlayer;
    //u redu je da znamo koji je "naš" player i deklarišemo kao Local kad ga već izdvajamo

    private GameState gameState = new GameState(GRID_X, GRID_Y, 1);

    public GameScreen(final CurveParty game) {
        this.game = game;

        final int width = Gdx.graphics.getWidth();
        ren = new ShapeRenderer();

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

        camera = new OrthographicCamera(GRID_X, GRID_Y);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    int frames = 0;
    long total, prerender, render, move, execute;
    @Override
    public void render(float delta) {
        long begin = System.nanoTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        ren.setProjectionMatrix(camera.combined);

        long startRender = System.nanoTime(), addRect=startRender;
        ren.begin(ShapeRenderer.ShapeType.Filled);
        for (Player p : gameState.getPlayerList()) {
            ren.setColor(p.getColor());
            //for (GridPoint2 gp : p.getRenderList()) {
            //    ren.rect(gp.x, gp.y, 1, 1);
            //}
            addRect = System.nanoTime();

            if (p.getState() != Player.STATE_DEAD) {
                p.move();
            }
        }
        long moved = System.nanoTime();
        ren.end();
        long end = System.nanoTime();
        total += (end-begin);
        prerender += (startRender-begin);
        render += (addRect-begin);
        move += (moved-addRect);
        execute += (end-moved);
        frames++;
        if(PROFILE_RENDER && frames % 100 == 0) { //prosek za svaki frejm, u prethodnih 100 frejmova, u mikrosekundama
            Gdx.app.log("GameScreen", String.format("d: %d, Total: %d\n"
                                                    + "prerender: %d, render: %d, move: %d, execute: %d",
                                                    (int)(delta*1000000), total/100000, prerender/100000,
                                                    render/100000, move/100000, execute/100000));
            total=prerender=render=move=execute=0;
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }


    @Override
    public void dispose() {
        ren.dispose();
    }
}
