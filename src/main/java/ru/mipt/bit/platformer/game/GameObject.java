package ru.mipt.bit.platformer.game;

@FunctionalInterface
public interface GameObject {
    void update(float deltaTime);
}
