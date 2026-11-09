package ru.mipt.bit.platformer.game.model;

import java.util.Objects;
import ru.mipt.bit.platformer.game.GameObject;
import ru.mipt.bit.platformer.game.Obstacle;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Movable;
import ru.mipt.bit.platformer.game.GameWorld;
import ru.mipt.bit.platformer.game.Shooter;
import ru.mipt.bit.platformer.game.Damageable;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

public class Tank implements GameObject, Obstacle, Movable, Shooter, Damageable {
    private final GridPoint2 coordinates;
    private Direction direction = Direction.RIGHT;
    private final float secondsPerTile;

    private final GameWorld world;
    private int health;
    private final int maxHealth;
    private final int bulletDamage;
    private final float bulletSecondsPerTile;
    private final float reloadSeconds;
    private float reloadRemaining;
    private final GridPoint2 movementOrigin;
    private final GridPoint2 movementDestination;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, GameWorld world, float secondsPerTile) {
        this(coordinates, world, secondsPerTile, 100, 50, 0.2f, 1f);
    }

    public Tank(GridPoint2 coordinates, GameWorld world, float secondsPerTile,
            int health, int bulletDamage, float bulletSecondsPerTile, float reloadSeconds) {
        if (health <= 0 || bulletDamage <= 0 || !Float.isFinite(bulletSecondsPerTile)
                || bulletSecondsPerTile <= 0 || !Float.isFinite(reloadSeconds) || reloadSeconds <= 0) {
            throw new IllegalArgumentException("Invalid tank or weapon settings");
        }
        this.health = health;
        this.maxHealth = health;
        this.bulletDamage = bulletDamage;
        this.bulletSecondsPerTile = bulletSecondsPerTile;
        this.reloadSeconds = reloadSeconds;
        this.coordinates = new GridPoint2(Objects.requireNonNull(coordinates));
        if (!Float.isFinite(secondsPerTile) || secondsPerTile <= 0f) {
            throw new IllegalArgumentException("Move time per tile must be finite and positive");
        }
        this.secondsPerTile = secondsPerTile;
        this.world = Objects.requireNonNull(world);
        this.movementOrigin = new GridPoint2(coordinates);
        this.movementDestination = new GridPoint2(coordinates);
    }

    @Override
    public boolean move(Direction direction) {
        Objects.requireNonNull(direction);

        if (health <= 0 || isMoving()) {
            return false;
        }

        this.direction = direction;

        if (!canMove(direction)) {
            return false;
        }

        movementOrigin.set(coordinates);
        movementDestination.set(direction.nextTile(coordinates));
        movementProgress = 0f;
        return true;
    }

    @Override
    public boolean canMove(Direction direction) {
        Objects.requireNonNull(direction);
        return health > 0 && !isMoving() && !world.isTileOccupied(direction.nextTile(coordinates));
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and nonnegative");
        }
        if (health <= 0) {
            return;
        }
        reloadRemaining = Math.max(0f, reloadRemaining - deltaTime);
        if (!isMoving()) {
            return;
        }

        movementProgress = MathUtils.clamp(movementProgress + deltaTime / secondsPerTile, 0f, 1f);
        if (!isMoving()) {
            coordinates.set(movementDestination);
        }
    }

    @Override
    public boolean isMoving() {
        return health > 0 && movementProgress < 1f;
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return health > 0 && (coordinates.equals(tile) || (isMoving() && movementDestination.equals(tile)));
    }

    @Override
    public void shoot() {
        if (health <= 0 || reloadRemaining > 0f) {
            return;
        }
        reloadRemaining = reloadSeconds;
        Bullet bullet = new Bullet(direction.nextTile(coordinates), direction, world, this,
                bulletDamage, bulletSecondsPerTile);
        world.spawn(bullet);
        bullet.update(0f);
    }

    @Override
    public void hit(int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be positive");
        }
        if (health <= 0) {
            return;
        }
        health -= damage;
        if (health <= 0) {
            world.remove(this);
        }
    }

    public float healthFraction() {
        return Math.max(0, health) / (float) maxHealth;
    }

    public EntityMovement movementSnapshot() {
        return new EntityMovement(movementOrigin, movementDestination, direction, movementProgress);
    }
}
