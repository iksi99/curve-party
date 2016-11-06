package com.jrti.curveparty;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class CurveParty extends ApplicationAdapter {
    public static final int GRID_X = 960;
    public static final int GRID_Y = 540;

    private ShapeRenderer      ren;
    private OrthographicCamera camera;

    private Player localPlayer;

    private GameState gameState = new GameState(GRID_X, GRID_Y, 1);

    @Override
    public void create() {
        final int width = Gdx.graphics.getWidth();
        ren = new ShapeRenderer();

        Random rnd = new Random();
        localPlayer = gameState.addLocalPlayer(0, rnd.nextInt(GRID_X - 100) + 50, rnd.nextInt(GRID_Y - 70) + 35);

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

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        ren.setProjectionMatrix(camera.combined);

        for (Player p : gameState.getPlayerList()) {
            ren.begin(ShapeRenderer.ShapeType.Filled);

            ren.setColor(p.getColor());
            for (Rectangle r : p.getRenderList()) {
                ren.rect(r.x, r.y, 1, 1);
            }

            ren.end();
            if (!p.isDead()) {
                p.move();

            }
        }
    }

    @Override
    public void dispose() {
        ren.dispose();
    }
}
