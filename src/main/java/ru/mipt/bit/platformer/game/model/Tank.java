package ru.mipt.bit.platformer.game.model;

import java.util.Objects;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Movable;
import ru.mipt.bit.platformer.game.ObstacleDetector;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

public class Tank implements Movable {
    private final GridPoint2 coordinates;
    private Direction direction = Direction.RIGHT;
    private final float secondsPerTile;

    private final ObstacleDetector obstacleDetector;
    private final GridPoint2 movementOrigin;
    private final GridPoint2 movementDestination;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, ObstacleDetector obstacleDetector, float secondsPerTile) {
        this.coordinates = new GridPoint2(Objects.requireNonNull(coordinates));
        if (!Float.isFinite(secondsPerTile) || secondsPerTile <= 0f) {
            throw new IllegalArgumentException("Move time per tile must be finite and positive");
        }
        this.secondsPerTile = secondsPerTile;
        this.obstacleDetector = Objects.requireNonNull(obstacleDetector);
        this.movementOrigin = new GridPoint2(coordinates);
        this.movementDestination = new GridPoint2(coordinates);
    }

    @Override
    public boolean move(Direction direction) {
        Objects.requireNonNull(direction);

        if (isMoving()) {
            return false;
        }

        this.direction = direction;

        GridPoint2 targetTile = direction.nextTile(coordinates);
        if (obstacleDetector.isTileOccupied(targetTile)) {
            return false;
        }

        movementOrigin.set(coordinates);
        movementDestination.set(targetTile);
        movementProgress = 0f;
        return true;
    }

    @Override
    public void update(float deltaTime) {
        if (!isMoving()) {
            return;
        }

        movementProgress = MathUtils.clamp(movementProgress + deltaTime / secondsPerTile, 0f, 1f);
        if (!isMoving()) {
            coordinates.set(movementDestination);
        }
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile);
    }

    public EntityMovement movementSnapshot() {
        return new EntityMovement(movementOrigin, movementDestination, direction, movementProgress);
    }
}
