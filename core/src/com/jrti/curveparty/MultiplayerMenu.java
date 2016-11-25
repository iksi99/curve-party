package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by cactoss on 21.11.2016..
 */

public class MultiplayerMenu implements Screen {
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

    TextField nick;
    TextButton twoplayer;
    TextButton threelayer;
    TextButton fourplayer;
    TextButton backButton;

    public MultiplayerMenu(final CurveParty game) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        PAD_BUTTONS = height / 72;

        this.game = game;
        logoTexture = new Texture(Gdx.files.internal("logo.png"));

        logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.fill);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin();
        BitmapFont font = game.getFont(height/10);
        skin.add("default-font", font, BitmapFont.class);
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("uiskin.json"));

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
        nick = new TextField("", skin);
        twoplayer    = new TextButton("2 igrača", skin);
        threelayer = new TextButton("3 igrača", skin);
        fourplayer    = new TextButton("4 igrača", skin);
        backButton = new TextButton("Nazad", skin);
        nick.setBounds(nick.getX(), nick.getY(), 500, nick.getHeight());
        twoplayer.getLabel().setFontScale(height / 500);
        threelayer.getLabel().setFontScale(height / 500);
        fourplayer.getLabel().setFontScale(height / 500);
        backButton.getLabel().setFontScale(height / 500);

        twoplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //if(USING_PIXMAP)
                game.setScreen(new PixmapScreen(game, 2).startMultiplayer(nick.getText()));
                //else
                //    game.setScreen(new GameScreen(game));
            }
        });
        threelayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PixmapScreen(game, 3).startMultiplayer(nick.getText()));
            }
        });
        fourplayer.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PixmapScreen(game, 4).startMultiplayer(nick.getText()));
            }
        });
        backButton.addListener(new ClickListener() {
           @Override
            public void clicked(InputEvent event, float x, float y) {
               game.setScreen(new MainMenu(game));
           }
        });

        mainTable.add(logoImage).padBottom(PAD_LOGO).row();
        mainTable.add(nick).padBottom(PAD_BUTTONS).row();
        mainTable.add(twoplayer).padBottom(PAD_BUTTONS).row();
        mainTable.add(threelayer).padBottom(PAD_BUTTONS).row();
        mainTable.add(fourplayer).padBottom(PAD_BUTTONS).row();
        mainTable.add(backButton).padBottom(PAD_BUTTONS).row();
        mainTable.getCell(nick).setActorWidth(400);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void hide() {
        twoplayer = null;
        threelayer = null;
        fourplayer = null;
        backButton = null;
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
