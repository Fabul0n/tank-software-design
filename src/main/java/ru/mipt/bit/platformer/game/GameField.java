package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.badlogic.gdx.math.GridPoint2;

public class GameField implements ObstacleDetector {
    private final List<Obstacle> gameObjects = new ArrayList<>();
    private final int widthInTiles;
    private final int heightInTiles;

    public GameField(int widthInTiles, int heightInTiles) {
        if (widthInTiles <= 0 || heightInTiles <= 0) {
            throw new IllegalArgumentException("Field size must be positive");
        }
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
    }

    public void spawn(Obstacle gameObject) {
        gameObjects.add(Objects.requireNonNull(gameObject));
    }

    public void update(float deltaTime) {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(deltaTime);
        }
    }

    @Override
    public boolean isTileOccupied(GridPoint2 coordinates) {
        if (coordinates.x < 0 || coordinates.y < 0 || coordinates.x >= widthInTiles || coordinates.y >= heightInTiles) {
            return true;
        }
        for (Obstacle obstacle : gameObjects) {
            if (obstacle.occupies(coordinates)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesDimensions(int widthInTiles, int heightInTiles) {
        return this.widthInTiles == widthInTiles && this.heightInTiles == heightInTiles;
    }

    public void clear() {
        gameObjects.clear();
    }
}
