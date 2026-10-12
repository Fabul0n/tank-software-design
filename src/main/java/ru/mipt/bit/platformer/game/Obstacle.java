package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;

public interface Obstacle extends GameObject {
    boolean occupies(GridPoint2 coordinates);
}
