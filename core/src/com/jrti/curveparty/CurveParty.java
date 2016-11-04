package com.jrti.curveparty;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Locale;

public class CurveParty extends ApplicationAdapter {
    public static final String TAG = "CurveParty";
    public static final int GRID_X = 960;
    public static final int GRID_Y = 540;
    private static final boolean DEBUG = true;

    private ShapeRenderer ren;
    private OrthographicCamera camera;


    private GameState gameState = new GameState(GRID_X, GRID_Y, 1);
	
    @Override
    public void create () {
        if(DEBUG) Gdx.app.setLogLevel(Application.LOG_DEBUG);
        ren = new ShapeRenderer();

        camera = new OrthographicCamera(GRID_X, GRID_Y);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        ren.setProjectionMatrix(camera.combined);

        for (Player p : gameState.getPlayerList())
        {
            if(!p.isDead()) {
                Gdx.app.debug(TAG,
                              String.format(Locale.ENGLISH, "t: %d:\tx: %d, y: %d", System.currentTimeMillis(),
                                            p.getX(), p.getY()));
                p.move();
                ren.begin(ShapeRenderer.ShapeType.Filled);

                ren.setColor(p.getColor());
                for (Rectangle r : p.getRenderList()) {
                    ren.rect(r.x, r.y, 1, 1);
                }

                ren.end();
                Gdx.app.debug(TAG,
                              String.format(Locale.ENGLISH, "t: %d:\tx: %d, y: %d", System.currentTimeMillis(),
                                            p.getX(), p.getY()));
            }
        }
    }
	
    @Override
    public void dispose () {
        ren.dispose();
    }
}
