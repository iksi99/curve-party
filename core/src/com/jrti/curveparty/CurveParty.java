package com.jrti.curveparty;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.ArrayList;
import java.util.Random;

public class CurveParty extends ApplicationAdapter {
	private ShapeRenderer ren;
	private OrthographicCamera camera;

	private java.util.List<Rectangle> renderObjectList = new ArrayList<Rectangle>();

	
	@Override
	public void create () {
		ren = new ShapeRenderer();

		for (int i = 0; i < 960; i++)
		{
			for (int j = 0; j < 540; j++)
			{
				Rectangle r = new Rectangle(i, j, 1, 1);
				renderObjectList.add(r);
			}
		}

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
		ren.begin(ShapeRenderer.ShapeType.Filled);

		for (Rectangle r : renderObjectList) {
				ren.setColor(Color.RED);
				ren.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
		}
		ren.end();
	}
	
	@Override
	public void dispose () {
		ren.dispose();
	}
}
