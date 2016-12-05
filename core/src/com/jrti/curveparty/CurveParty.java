package com.jrti.curveparty;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class CurveParty extends Game {
    public SpriteBatch spriteBatch;
    public boolean useTouchCommands = false;
    private FreeTypeFontGenerator FONT_GENERATOR;

    public static final String PREFS_NAME = "prefs";
    public static final String PREFS_KEY_USE_TOUCH = "useTouch";

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        useTouchCommands = Gdx.app.getPreferences(PREFS_NAME).getBoolean(PREFS_KEY_USE_TOUCH, false);
        FONT_GENERATOR = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        this.setScreen(new MainMenu(this));
    }

    public BitmapFont getFont(int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.shadowOffsetX=2;
        parameter.shadowOffsetY=2;
        parameter.characters = "abcčćdđefghijklmnopqrsštuvwxyzžABCČĆDĐEFGHIJKLMNOPQRSTUVWXYZŽ1234567890";
        return FONT_GENERATOR.generateFont(parameter);
    }

    @Override
    public void render() {
       super.render();
    }

    @Override
    public void dispose() {}
}
