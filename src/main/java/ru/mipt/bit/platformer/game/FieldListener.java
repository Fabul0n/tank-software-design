package ru.mipt.bit.platformer.game;

public interface FieldListener {
    void added(Obstacle object);
    void removed(Obstacle object);
}
