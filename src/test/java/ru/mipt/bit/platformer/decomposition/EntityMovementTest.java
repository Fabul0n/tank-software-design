package ru.mipt.bit.platformer.decomposition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class EntityMovementTest {
    @Test
    void storesMovementStateCopies() {
        GridPoint2 origin = new GridPoint2(1, 2);
        GridPoint2 destination = new GridPoint2(3, 4);
        EntityMovement movement = new EntityMovement(origin, destination, Direction.LEFT, 0.5f);

        origin.set(10, 20);
        destination.set(30, 40);

        assertEquals(new GridPoint2(1, 2), movement.origin());
        assertEquals(new GridPoint2(3, 4), movement.destination());
        assertEquals(Direction.LEFT, movement.direction());
        assertEquals(0.5f, movement.progress());
    }

    @Test
    void clampsProgress() {
        assertEquals(0f, new EntityMovement(new GridPoint2(), new GridPoint2(), Direction.UP, -1f).progress());
        assertEquals(1f, new EntityMovement(new GridPoint2(), new GridPoint2(), Direction.UP, 2f).progress());
    }

    @Test
    void createsIdleMovementAtSingleTile() {
        EntityMovement movement = EntityMovement.idle(new GridPoint2(5, 6), Direction.DOWN);

        assertEquals(new GridPoint2(5, 6), movement.origin());
        assertEquals(new GridPoint2(5, 6), movement.destination());
        assertEquals(Direction.DOWN, movement.direction());
        assertEquals(1f, movement.progress());
    }

    @Test
    void returnsCoordinateCopies() {
        EntityMovement movement = new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 2), Direction.RIGHT, 1f);

        movement.origin().set(100, 100);
        movement.destination().set(200, 200);

        assertEquals(new GridPoint2(1, 1), movement.origin());
        assertEquals(new GridPoint2(2, 2), movement.destination());
    }
}
