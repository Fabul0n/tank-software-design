package ru.mipt.bit.platformer.game;


public interface Movable {
    boolean move(Direction direction);

    boolean canMove(Direction direction);

    boolean isMoving();
}
