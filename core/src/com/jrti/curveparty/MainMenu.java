package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/**
 * Created by cactoss on 7.11.2016..
 */

public class MainMenu implements Screen {

    final CurveParty game;

    Texture logoTexture;

    Rectangle logo;

    OrthographicCamera camera;

    int width, height;

    public MainMenu(final CurveParty game) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        this.game = game;
        logoTexture = new Texture(Gdx.files.internal("logo.png"));

        logo = new Rectangle(width/2 - logoTexture.getWidth()/4, height/2 - logoTexture.getHeight()/4,
                logoTexture.getWidth()/2 , logoTexture.getHeight()/2);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (screenX >= logo.x && screenX <= logo.x + logo.width &&
                        screenY > logo.y && screenY < logo.y + logo.height) {
                    game.setScreen(new GameScreen(game));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        game.spriteBatch.draw(logoTexture, logo.x, logo.y, logo.width, logo.height);
        game.spriteBatch.end();
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
    }
}
