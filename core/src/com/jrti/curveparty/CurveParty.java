package com.jrti.curveparty;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.ArrayList;
import java.util.Random;

public class CurveParty extends ApplicationAdapter {
	private ShapeRenderer ren;
	private OrthographicCamera camera;

	private Player localPlayer;

	private GameState gameState = new GameState(1920, 1080, 1);
	
	@Override
	public void create () {
		ren = new ShapeRenderer();

		localPlayer = gameState.getPlayerList().get(0);

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown (int x, int y, int pointer, int button) {
				if(x <= 480) {
					localPlayer.setTurningLeft(true);
				} else {
					localPlayer.setTurningRight(true);
				}
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if(screenX <= 480) {
					localPlayer.setTurningLeft(false);
				} else {
					localPlayer.setTurningRight(false);
				}
				return true;
			}
		});

		camera = new OrthographicCamera(960, 540);
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

			ren.begin(ShapeRenderer.ShapeType.Filled);

			ren.setColor(p.getColor());
			for (Rectangle r : p.getRenderList()) {
				ren.rect(r.x, r.y, 1, 1);
			}

			ren.end();
			System.out.println(p.getX() + " " + p.getY());
			if(!p.isDead()) {
				p.move();

			}
		}

	}
	
	@Override
	public void dispose () {
		ren.dispose();
	}
}
