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
 * Created by cactoss on 25.11.2016..
 */

public class VictoryScreen implements Screen {
    //public static final boolean USING_PIXMAP = true;

    public static int PAD_LOGO = 0, PAD_BUTTONS;

    private final CurveParty game;

    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private BitmapFont font;
    private NetworkPlayer[] players;

    int width, height;

    TextButton exitButton;


    public VictoryScreen(final CurveParty game, NetworkPlayer[] players) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        PAD_BUTTONS = height / 36;

        this.game = game;
        this.players = players;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin();
        this.font = game.getFont(height/10);
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

        int maxScore = 0;
        NetworkPlayer winner = players[0];

        for (NetworkPlayer p : players) {
            if(p.getScore() > maxScore) winner = p;
        }
        game.spriteBatch.begin();
        font.draw(game.spriteBatch, "Pobednik je " + winner.getName(), width / 2 - 200, height / 2 + 100);
        game.spriteBatch.end();
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
        exitButton    = new TextButton("Izlaz", skin);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        mainTable.add(exitButton).padTop(2*height/3).row();

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void hide() {
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
