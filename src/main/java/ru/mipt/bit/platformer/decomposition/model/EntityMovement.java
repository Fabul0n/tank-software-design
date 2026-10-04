package ru.mipt.bit.platformer.decomposition.model;

import java.util.Objects;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public final class EntityMovement {
    private final GridPoint2 origin;
    private final GridPoint2 destination;
    private final Direction direction;
    private final float progress;

    public EntityMovement(GridPoint2 origin, GridPoint2 destination, Direction direction, float progress) {
        this.origin = new GridPoint2(Objects.requireNonNull(origin));
        this.destination = new GridPoint2(Objects.requireNonNull(destination));
        this.direction = Objects.requireNonNull(direction);
        this.progress = MathUtils.clamp(progress, 0f, 1f);
    }

    public static EntityMovement idle(GridPoint2 coordinates, Direction direction) {
        return new EntityMovement(coordinates, coordinates, direction, 1f);
    }

    /** Calculates the visible position in tile coordinates without exposing movement internals. */
    public Vector2 position(Interpolation interpolation) {
        float amount = Objects.requireNonNull(interpolation).apply(progress);
        return new Vector2(
                origin.x + (destination.x - origin.x) * amount,
                origin.y + (destination.y - origin.y) * amount);
    }

    public float rotation() {
        return direction.rotation();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EntityMovement)) {
            return false;
        }
        EntityMovement movement = (EntityMovement) object;
        return Float.compare(movement.progress, progress) == 0
                && origin.equals(movement.origin)
                && destination.equals(movement.destination)
                && direction == movement.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination, direction, progress);
    }
}
