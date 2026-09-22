package ru.mipt.bit.platformer.decomposition;

import java.util.Objects;
import java.util.function.Predicate;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

public class Tank extends Entity implements Movable {
    private static final float MOVE_TIME_PER_TILE = 0.4f;

    private final Predicate<GridPoint2> canMoveTo;
    private final GridPoint2 movementOrigin;
    private final GridPoint2 movementDestination;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, Predicate<GridPoint2> canMoveTo) {
        super(coordinates);
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

        GridPoint2 target = direction.nextTile(tile());
        if (!canMoveTo.test(target)) {
            return false;
        }

        movementOrigin.set(tile());
        movementDestination.set(target);
        movementProgress = 0f;
        return true;
    }

    @Override
    public void update(float deltaTime) {
        if (!isMoving()) {
            return;
        }

        movementProgress = MathUtils.clamp(movementProgress + deltaTime / MOVE_TIME_PER_TILE, 0f, 1f);
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
