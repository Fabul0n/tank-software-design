package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;

@FunctionalInterface
public interface Obstacle {
    boolean occupies(GridPoint2 coordinates);
}
