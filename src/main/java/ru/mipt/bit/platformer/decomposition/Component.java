package ru.mipt.bit.platformer.decomposition;

public interface Component {
    void update(float deltaTime);

    default void dispose() {
    }
}
