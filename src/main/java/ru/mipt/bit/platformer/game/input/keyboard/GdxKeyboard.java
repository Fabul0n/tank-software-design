package ru.mipt.bit.platformer.game.input.keyboard;

import com.badlogic.gdx.Gdx;

public class GdxKeyboard implements Keyboard {
    @Override
    public boolean isKeyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }
}
