package ru.mipt.bit.platformer.decomposition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

class EntityMovementTest {
    @Test
    void calculatesPositionIndependentlyOfSourceCoordinates() {
        GridPoint2 origin = new GridPoint2(1, 2);
        GridPoint2 destination = new GridPoint2(3, 4);
        EntityMovement movement = new EntityMovement(origin, destination, Direction.LEFT, 0.25f);
        origin.set(10, 20);
        destination.set(30, 40);

        assertEquals(new Vector2(1.5f, 2.5f), movement.position(Interpolation.linear));
        assertEquals(-180f, movement.rotation());
    }

    @Test
    void clampsPositionToMovementEndpoints() {
        EntityMovement before = new EntityMovement(new GridPoint2(1, 2), new GridPoint2(3, 4), Direction.UP, -1f);
        EntityMovement after = new EntityMovement(new GridPoint2(1, 2), new GridPoint2(3, 4), Direction.UP, 2f);

        assertEquals(new Vector2(1, 2), before.position(Interpolation.linear));
        assertEquals(new Vector2(3, 4), after.position(Interpolation.linear));
    }

    @Test
    void keepsIdlePosition() {
        EntityMovement movement = EntityMovement.idle(new GridPoint2(5, 6), Direction.DOWN);

        assertEquals(new Vector2(5, 6), movement.position(Interpolation.smooth));
        assertEquals(-90f, movement.rotation());
    }

    @Test
    void calculatedPositionCannotChangeMovement() {
        EntityMovement movement = new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 2), Direction.RIGHT, 1f);
        movement.position(Interpolation.linear).set(100, 100);

        assertEquals(new Vector2(2, 2), movement.position(Interpolation.linear));
    }

    @Test
    void appliesSelectedInterpolation() {
        EntityMovement movement = new EntityMovement(new GridPoint2(0, 0), new GridPoint2(8, 0), Direction.RIGHT, 0.25f);

        assertEquals(new Vector2(1.25f, 0), movement.position(Interpolation.smooth));
    }
}
