package ru.mipt.bit.platformer.decomposition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class EntityTest {
    @Test
    void rejectsNullCoordinates() {
        assertThrows(NullPointerException.class, () -> new TestEntity(null));
    }

    @Test
    void storesOwnCoordinatesCopy() {
        GridPoint2 source = new GridPoint2(1, 2);
        TestEntity entity = new TestEntity(source);

        source.set(10, 20);

        assertTrue(entity.occupies(new GridPoint2(1, 2)));
        assertFalse(entity.occupies(new GridPoint2(10, 20)));
    }

    @Test
    void doesNotBlockMovementByDefault() {
        TestEntity entity = new TestEntity(new GridPoint2(1, 2));

        assertFalse(entity.blocksMovementAt(new GridPoint2(1, 2)));
    }

    @Test
    void exposesIdleMovementSnapshotByDefault() {
        TestEntity entity = new TestEntity(new GridPoint2(1, 2));

        assertEquals(
                EntityMovement.idle(new GridPoint2(1, 2), Direction.RIGHT),
                entity.movementSnapshot());
    }

    @Test
    void protectedOperationsChangeStateThroughEntityInterface() {
        TestEntity entity = new TestEntity(new GridPoint2(1, 2));

        entity.faceTo(Direction.UP);
        entity.place(new GridPoint2(3, 4));

        assertTrue(entity.occupies(new GridPoint2(3, 4)));
        assertEquals(
                EntityMovement.idle(new GridPoint2(3, 4), Direction.UP),
                entity.movementSnapshot());
    }

    private static class TestEntity extends Entity {
        private TestEntity(GridPoint2 coordinates) {
            super(coordinates);
        }

        private void faceTo(Direction direction) {
            face(direction);
        }

        private void place(GridPoint2 coordinates) {
            placeAt(coordinates);
        }
    }
}
