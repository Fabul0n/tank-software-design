package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.badlogic.gdx.math.GridPoint2;

public class GameField implements GameWorld {
    private final List<Obstacle> gameObjects = new ArrayList<>();
    private final List<FieldListener> listeners = new ArrayList<>();
    private final int widthInTiles;
    private final int heightInTiles;

    public GameField(int widthInTiles, int heightInTiles) {
        if (widthInTiles <= 0 || heightInTiles <= 0) {
            throw new IllegalArgumentException("Field size must be positive");
        }
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
    }

    @Override
    public void spawn(Obstacle gameObject) {
        Objects.requireNonNull(gameObject);
        if (gameObjects.contains(gameObject)) {
            return;
        }
        gameObjects.add(gameObject);
        for (FieldListener listener : new ArrayList<>(listeners)) {
            listener.added(gameObject);
        }
    }

    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and nonnegative");
        }
        for (Obstacle gameObject : new ArrayList<>(gameObjects)) {
            if (gameObjects.contains(gameObject) && gameObject instanceof GameObject) {
                ((GameObject) gameObject).update(deltaTime);
            }
        }
    }

    @Override
    public boolean isTileOccupied(GridPoint2 coordinates) {
        if (outside(coordinates)) {
            return true;
        }
        for (Obstacle obstacle : gameObjects) {
            if (obstacle.occupies(coordinates)) {
                return true;
            }
        }
        return false;
    }

    private boolean outside(GridPoint2 tile) {
        return tile.x < 0 || tile.y < 0 || tile.x >= widthInTiles || tile.y >= heightInTiles;
    }

    @Override
    public boolean hitAt(GridPoint2 tile, Obstacle projectile, Obstacle owner, int damage) {
        if (outside(tile)) {
            return true;
        }
        boolean collision = false;
        for (Obstacle object : new ArrayList<>(gameObjects)) {
            if (object != projectile && object != owner && object.occupies(tile)) {
                collision = true;
                if (object instanceof Damageable) {
                    ((Damageable) object).hit(damage);
                }
            }
        }
        return collision;
    }

    @Override
    public void remove(Obstacle object) {
        if (gameObjects.remove(object)) {
            for (FieldListener listener : new ArrayList<>(listeners)) {
                listener.removed(object);
            }
        }
    }

    public void subscribe(FieldListener listener) {
        Objects.requireNonNull(listener);
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
        for (Obstacle object : new ArrayList<>(gameObjects)) {
            listener.added(object);
        }
    }

    public void unsubscribe(FieldListener listener) {
        listeners.remove(listener);
    }

    public boolean matchesDimensions(int widthInTiles, int heightInTiles) {
        return this.widthInTiles == widthInTiles && this.heightInTiles == heightInTiles;
    }

    public void clear() {
        for (Obstacle object : new ArrayList<>(gameObjects)) {
            remove(object);
        }
    }
}
