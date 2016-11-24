package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.jrti.curveparty.CurveParty.PREFS_KEY_USE_TOUCH;
import static com.jrti.curveparty.CurveParty.PREFS_NAME;

/**
 * Created by cactoss on 7.11.2016..
 */

public class MainMenu implements Screen {
    //public static final boolean USING_PIXMAP = true;

    public static int PAD_LOGO = 0, PAD_BUTTONS;

    private final CurveParty game;

    private Texture logoTexture;

    private Image logoImage;

    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;

    int width, height;

    TextButton singleplayer;
    TextButton multiplayer;
    CheckBox useTouch;
    TextButton exitButton;

    public MainMenu(final CurveParty game) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        PAD_BUTTONS = height / 36;

        this.game = game;
        logoTexture = new Texture(Gdx.files.internal("logo.png"));

        logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.fit);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin();
        BitmapFont font = game.getFont(height/15);
        skin.add("default-font", font, BitmapFont.class);
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("uiskin.json"));

        Viewport v = new FitViewport(width, height, camera);
        stage = new Stage(v, game.spriteBatch);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(false);
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
        singleplayer    = new TextButton("Singleplayer", skin);
        multiplayer = new TextButton("Multiplayer", skin);
        useTouch = new CheckBox("Use touch instead of tilt", skin);
        exitButton    = new TextButton("Exit", skin);
        //singleplayer.getLabel().setFontScale(Math.max(height / 250, 1.7f));
        //multiplayer.getLabel().setFontScale(Math.max(height / 250, 1.7f));
        useTouch.getLabel().setFontScale(0.8f);
        useTouch.getCells().get(0).size(Math.max(height / 10, 30), Math.max(height / 10, 30));
        useTouch.setChecked(game.useTouchCommands);
        //exitButton.getLabel().setFontScale(Math.max(height / 250, 1.7f));
        useTouch.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.useTouchCommands = !game.useTouchCommands;
                Gdx.app.getPreferences(PREFS_NAME).putBoolean(PREFS_KEY_USE_TOUCH, game.useTouchCommands).flush();
            }
        });

        singleplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //if(USING_PIXMAP)
                    game.setScreen(new PixmapScreen(game, 1).startSingleplayer());
                //else
                //    game.setScreen(new GameScreen(game));
            }
        });
        multiplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MultiplayerMenu(game));
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
        mainTable.add(useTouch).padBottom(PAD_BUTTONS).row();
        mainTable.add(exitButton).padBottom(PAD_BUTTONS).row();

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void hide() {
        singleplayer    = null;
        multiplayer = null;
        exitButton    = null;
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
