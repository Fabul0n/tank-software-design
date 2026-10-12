package ru.mipt.bit.platformer.game;


public interface Movable extends Obstacle {
    boolean move(Direction direction);
}
