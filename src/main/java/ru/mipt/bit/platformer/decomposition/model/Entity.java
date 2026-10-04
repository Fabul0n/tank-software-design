package ru.mipt.bit.platformer.decomposition.model;

import java.util.Objects;

import com.badlogic.gdx.math.GridPoint2;

public abstract class Entity implements MovementState {
    private final GridPoint2 coordinates;
    private Direction direction = Direction.RIGHT;

    protected Entity(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(Objects.requireNonNull(coordinates));
    }

    public void update(float deltaTime) {
    }

    public boolean occupies(GridPoint2 coordinates) {
        return this.coordinates.equals(coordinates);
    }

    public boolean blocksMovementAt(GridPoint2 coordinates) {
        return false;
    }

    @Override
    public EntityMovement movementSnapshot() {
        return EntityMovement.idle(coordinates, direction);
    }

    protected GridPoint2 tileCopy() {
        return new GridPoint2(coordinates);
    }

    protected Direction direction() {
        return direction;
    }

    protected void face(Direction direction) {
        this.direction = Objects.requireNonNull(direction);
    }

    protected void placeAt(GridPoint2 coordinates) {
        this.coordinates.set(Objects.requireNonNull(coordinates));
    }
}
