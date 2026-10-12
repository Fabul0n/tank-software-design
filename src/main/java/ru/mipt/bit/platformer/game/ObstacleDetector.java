package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;

@FunctionalInterface
public interface ObstacleDetector {
    boolean isTileOccupied(GridPoint2 coordinates);
}
