package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by cactoss on 7.11.2016..
 */

public class MainMenu implements Screen {
    public static final int PAD_LOGO = 0, PAD_BUTTONS = 15;

    private final CurveParty game;

    private Texture logoTexture;

    private Image logoImage;

    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;

    int width, height;

    public MainMenu(final CurveParty game) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        this.game = game;
        logoTexture = new Texture(Gdx.files.internal("logo.png"));

        logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.fit);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);

        Viewport v = new FitViewport(width, height, camera);
        stage = new Stage(v, game.spriteBatch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        //Create buttons
        TextButton singleplayer    = new TextButton("Singleplayer", skin);
        TextButton multiplayer = new TextButton("Multiplayer", skin);
        TextButton exitButton    = new TextButton("Exit", skin);
        singleplayer.getLabel().setFontScale(2.2f);
        multiplayer.getLabel().setFontScale(2.2f);
        exitButton.getLabel().setFontScale(2.2f);

        singleplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mainTable.add(logoImage).padBottom(PAD_LOGO).row();
        mainTable.add(singleplayer).padBottom(PAD_BUTTONS).row();
        mainTable.add(multiplayer).padBottom(PAD_BUTTONS).row();
        mainTable.add(exitButton).padBottom(PAD_BUTTONS).row();

        //Add table to stage
        stage.addActor(mainTable);
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
