package ru.mipt.bit.platformer.decomposition.model;

import java.util.Objects;
import java.util.function.Predicate;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

public class Tank extends Entity implements Movable {
    private final float moveTimePerTile;

    private final Predicate<GridPoint2> canMoveTo;
    private final GridPoint2 movementOrigin;
    private final GridPoint2 movementDestination;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, Predicate<GridPoint2> canMoveTo, float moveTimePerTile) {
        super(coordinates);
        if (!Float.isFinite(moveTimePerTile) || moveTimePerTile <= 0f) {
            throw new IllegalArgumentException("Move time per tile must be finite and positive");
        }
        this.moveTimePerTile = moveTimePerTile;
        this.canMoveTo = Objects.requireNonNull(canMoveTo);
        this.movementOrigin = tileCopy();
        this.movementDestination = tileCopy();
    }

    @Override
    public boolean move(Direction direction) {
        Objects.requireNonNull(direction);

        if (isMoving()) {
            return false;
        }

        face(direction);

        GridPoint2 target = direction.nextTile(tileCopy());
        if (!canMoveTo.test(target)) {
            return false;
        }

        movementOrigin.set(tileCopy());
        movementDestination.set(target);
        movementProgress = 0f;
        return true;
    }

    @Override
    public void update(float deltaTime) {
        if (!isMoving()) {
            return;
        }

        movementProgress = MathUtils.clamp(movementProgress + deltaTime / moveTimePerTile, 0f, 1f);
        if (!isMoving()) {
            placeAt(movementDestination);
        }
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    @Override
    public EntityMovement movementSnapshot() {
        return new EntityMovement(movementOrigin, movementDestination, direction(), movementProgress);
    }
}
