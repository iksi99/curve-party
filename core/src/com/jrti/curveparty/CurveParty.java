package com.jrti.curveparty;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CurveParty extends Game {
    public SpriteBatch spriteBatch;
    public boolean USE_TOUCH_COMMANDS = false;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        this.setScreen(new MainMenu(this));
    }

    @Override
    public void render() {
       super.render();
    }

    @Override
    public void dispose() {}
}
