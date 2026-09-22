package ru.mipt.bit.platformer.decomposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class TankTest {
    @Test
    void startsIdleAtCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> true);

        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertFalse(tank.isMoving());
        assertEquals(
                EntityMovement.idle(new GridPoint2(1, 1), Direction.RIGHT),
                tank.movementSnapshot());
    }

    @Test
    void movesToRequestedDirection() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> true);

        assertTrue(tank.move(Direction.UP));

        assertTrue(tank.isMoving());
        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(1, 2), Direction.UP, 0f),
                tank.movementSnapshot());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
    }

    @Test
    void completesMovementAfterMoveTime() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> true);
        tank.move(Direction.RIGHT);

        tank.update(0.2f);

        assertTrue(tank.isMoving());
        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 1), Direction.RIGHT, 0.5f),
                tank.movementSnapshot());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));

        tank.update(0.2f);

        assertFalse(tank.isMoving());
        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 1), Direction.RIGHT, 1f),
                tank.movementSnapshot());
        assertTrue(tank.occupies(new GridPoint2(2, 1)));
    }

    @Test
    void rejectsNewMoveWhileMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> true);
        tank.move(Direction.RIGHT);

        assertFalse(tank.move(Direction.UP));

        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 1), Direction.RIGHT, 0f),
                tank.movementSnapshot());
    }

    @Test
    void turnsButDoesNotMoveWhenTargetIsBlocked() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> false);

        assertFalse(tank.move(Direction.LEFT));

        assertFalse(tank.isMoving());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertEquals(
                EntityMovement.idle(new GridPoint2(1, 1), Direction.LEFT),
                tank.movementSnapshot());
    }

    @Test
    void exposesCurrentMovementState() {
        Tank tank = new Tank(new GridPoint2(1, 1), ignored -> true);

        tank.move(Direction.UP);
        tank.update(0.2f);

        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(1, 2), Direction.UP, 0.5f),
                tank.movementSnapshot());
    }
}
