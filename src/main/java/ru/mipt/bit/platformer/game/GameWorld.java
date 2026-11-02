package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;

public interface GameWorld extends ObstacleDetector {
    void spawn(Obstacle object);
    void remove(Obstacle object);
    boolean hitAt(GridPoint2 tile, Obstacle projectile, Obstacle owner, int damage);
}
