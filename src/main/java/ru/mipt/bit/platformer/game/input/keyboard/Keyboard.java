package ru.mipt.bit.platformer.game.input.keyboard;


@FunctionalInterface
public interface Keyboard {
    boolean isKeyPressed(int keyCode);
}
