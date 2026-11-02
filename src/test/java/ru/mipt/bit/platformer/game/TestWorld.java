package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;

public final class TestWorld {
    private TestWorld() {
    }

    public static GameWorld detecting(ObstacleDetector detector) {
        return new GameField(100, 100) {
            @Override
            public boolean isTileOccupied(GridPoint2 tile) {
                return detector.isTileOccupied(tile);
            }
        };
    }
}
