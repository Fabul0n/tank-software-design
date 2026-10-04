package ru.mipt.bit.platformer.decomposition.input;

import com.badlogic.gdx.Gdx;

public class GdxKeyState implements KeyState {
    @Override
    public boolean isKeyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }
}