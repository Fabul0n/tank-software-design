package ru.mipt.bit.platformer.decomposition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class DirectionTest {
    @Test
    void storesVectorAndRotation() {
        assertEquals(new GridPoint2(0, 1), Direction.UP.vector());
        assertEquals(new GridPoint2(0, -1), Direction.DOWN.vector());
        assertEquals(new GridPoint2(-1, 0), Direction.LEFT.vector());
        assertEquals(new GridPoint2(1, 0), Direction.RIGHT.vector());

        assertEquals(90f, Direction.UP.rotation());
        assertEquals(-90f, Direction.DOWN.rotation());
        assertEquals(-180f, Direction.LEFT.rotation());
        assertEquals(0f, Direction.RIGHT.rotation());
    }

    @Test
    void calculatesNextCoordinatesWithoutMutatingSourcePoint() {
        GridPoint2 source = new GridPoint2(2, 3);

        assertEquals(new GridPoint2(2, 4), Direction.UP.nextTile(source));
        assertEquals(new GridPoint2(2, 3), source);
    }

    @Test
    void returnsVectorCopy() {
        GridPoint2 vector = Direction.RIGHT.vector();
        vector.set(100, 100);

        assertEquals(new GridPoint2(1, 0), Direction.RIGHT.vector());
    }
}
