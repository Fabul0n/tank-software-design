package ru.mipt.bit.platformer.game.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import static ru.mipt.bit.platformer.game.TestWorld.detecting;
import ru.mipt.bit.platformer.game.Direction;

import com.badlogic.gdx.math.GridPoint2;

class TankTest {
    @Test
    void reportsHealthFractionRelativeToInitialHealth() {
        Tank tank = new Tank(new GridPoint2(), detecting(tile -> false), 0.4f,
                80, 50, 0.2f, 1f);
        assertEquals(1f, tank.healthFraction());
        tank.hit(20);
        assertEquals(0.75f, tank.healthFraction());
        tank.hit(100);
        assertEquals(0f, tank.healthFraction());
    }

    @Test
    void defaultTankStartsWithOneHundredHealth() {
        Tank tank = new Tank(new GridPoint2(), detecting(tile -> false), 0.4f);
        tank.hit(25);
        assertEquals(0.75f, tank.healthFraction());
    }

    @Test
    void checksMovementWithoutTurningMovingOrReservingCells() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(tile -> tile.equals(new GridPoint2(2, 1))), 0.4f);
        assertTrue(tank.canMove(Direction.UP));
        assertFalse(tank.canMove(Direction.RIGHT));
        assertFalse(tank.isMoving());
        assertEquals(0f, tank.movementSnapshot().rotation());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertFalse(tank.occupies(new GridPoint2(1, 2)));
        assertThrows(NullPointerException.class, () -> tank.canMove(null));
    }

    @Test
    void cannotStartAnotherMoveUntilCurrentMoveCompletes() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(tile -> false), 0.4f);
        assertTrue(tank.move(Direction.UP));
        for (Direction direction : Direction.values()) {
            assertFalse(tank.canMove(direction));
        }
        tank.update(0.4f);
        assertTrue(tank.canMove(Direction.DOWN));
    }

    @Test
    void ownsItsCoordinatesAndRejectsNullDependencies() {
        GridPoint2 coordinates = new GridPoint2(1, 2);
        Tank tank = new Tank(coordinates, detecting(tile -> false), 0.4f);
        coordinates.set(10, 20);
        assertTrue(tank.occupies(new GridPoint2(1, 2)));
        assertThrows(NullPointerException.class, () -> new Tank(null, detecting(tile -> false), 0.4f));
        assertThrows(NullPointerException.class, () -> new Tank(new GridPoint2(), null, 0.4f));
    }

    @Test
    void usesConfiguredMoveTime() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 2f);
        tank.move(Direction.UP);

        tank.update(1f);
        assertEquals(new com.badlogic.gdx.math.Vector2(1f, 1.5f),
                tank.movementSnapshot().interpolatePosition(com.badlogic.gdx.math.Interpolation.linear));
        assertTrue(tank.occupies(new GridPoint2(1, 1)));

        tank.update(1f);
        assertFalse(tank.isMoving());
        assertTrue(tank.occupies(new GridPoint2(1, 2)));
    }

    @Test
    void rejectsInvalidMoveTime() {
        for (float duration : new float[] {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Tank(new GridPoint2(), detecting(ignored -> false), duration));
        }
    }

    @Test
    void startsIdleAtCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 0.4f);

        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertFalse(tank.isMoving());
        assertEquals(
                EntityMovement.idle(new GridPoint2(1, 1), Direction.RIGHT),
                tank.movementSnapshot());
    }

    @Test
    void movesToRequestedDirection() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 0.4f);

        assertTrue(tank.move(Direction.UP));

        assertTrue(tank.isMoving());
        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(1, 2), Direction.UP, 0f),
                tank.movementSnapshot());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
    }

    @Test
    void completesMovementAfterMoveTime() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 0.4f);
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
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 0.4f);
        tank.move(Direction.RIGHT);

        assertFalse(tank.move(Direction.UP));

        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(2, 1), Direction.RIGHT, 0f),
                tank.movementSnapshot());
    }

    @Test
    void turnsButDoesNotMoveWhenTargetIsBlocked() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> true), 0.4f);

        assertFalse(tank.move(Direction.LEFT));

        assertFalse(tank.isMoving());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertEquals(
                EntityMovement.idle(new GridPoint2(1, 1), Direction.LEFT),
                tank.movementSnapshot());
    }

    @Test
    void exposesCurrentMovementState() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(ignored -> false), 0.4f);

        tank.move(Direction.UP);
        tank.update(0.2f);

        assertEquals(
                new EntityMovement(new GridPoint2(1, 1), new GridPoint2(1, 2), Direction.UP, 0.5f),
                tank.movementSnapshot());
    }
}
