package ru.mipt.bit.platformer.game;

@FunctionalInterface
public interface Damageable {
    void hit(int damage);
}
