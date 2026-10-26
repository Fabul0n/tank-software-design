package ru.mipt.bit.platformer.game.control;

@FunctionalInterface
public interface ControlStrategy {
    void handle(float deltaTime);
}
