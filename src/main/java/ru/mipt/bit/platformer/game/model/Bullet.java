package ru.mipt.bit.platformer.game.model;

import java.util.Objects;
import ru.mipt.bit.platformer.game.GameObject;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.GameWorld;
import ru.mipt.bit.platformer.game.Obstacle;
import ru.mipt.bit.platformer.game.Damageable;

public final class Bullet implements GameObject, Obstacle, Damageable {
    private final GridPoint2 coordinates;
    private final Direction direction;
    private final GameWorld world;
    private final Obstacle owner;
    private final int damage;
    private final float secondsPerTile;
    private double elapsed;
    private boolean active = true;

    public Bullet(GridPoint2 coordinates, Direction direction, GameWorld world,
            Obstacle owner, int damage, float secondsPerTile) {
        this.coordinates = new GridPoint2(Objects.requireNonNull(coordinates));
        this.direction = Objects.requireNonNull(direction);
        this.world = Objects.requireNonNull(world);
        this.owner = Objects.requireNonNull(owner);
        if (damage <= 0 || !Float.isFinite(secondsPerTile) || secondsPerTile <= 0f) {
            throw new IllegalArgumentException("Invalid bullet settings");
        }
        this.damage = damage;
        this.secondsPerTile = secondsPerTile;
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and nonnegative");
        }
        if (!active || collide()) {
            return;
        }
        elapsed += deltaTime;
        while (active && elapsed >= secondsPerTile) {
            elapsed -= secondsPerTile;
            coordinates.set(direction.nextTile(coordinates));
            if (collide()) {
                return;
            }
        }
    }

    private boolean collide() {
        if (world.hitAt(coordinates, this, owner, damage)) {
            destroy();
            return true;
        }
        return false;
    }

    private void destroy() {
        active = false;
        world.remove(this);
    }

    @Override
    public void hit(int damage) {
        destroy();
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return active && coordinates.equals(tile);
    }

    public EntityMovement movementSnapshot() {
        return new EntityMovement(coordinates, direction.nextTile(coordinates), direction,
                (float) (elapsed / secondsPerTile));
    }
}
