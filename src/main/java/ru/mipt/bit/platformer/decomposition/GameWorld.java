package ru.mipt.bit.platformer.decomposition;

import java.util.Objects;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class GameWorld {
    private final Array<Entity> entities = new Array<>();
    private final int width;
    private final int height;

    public GameWorld(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World size must be positive");
        }
        this.width = width;
        this.height = height;
    }

    public void spawn(Entity entity) {
        entities.add(Objects.requireNonNull(entity));
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public boolean isTileFree(GridPoint2 coordinates) {
        if (!contains(coordinates)) {
            return false;
        }

        for (Entity entity : entities) {
            if (entity.blocksMovementAt(coordinates)) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        entities.clear();
    }

    private boolean contains(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.y >= 0 && coordinates.x < width && coordinates.y < height;
    }
}
