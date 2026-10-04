package ru.mipt.bit.platformer.decomposition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class DirectionTest {
    @Test
    void calculatesAdjacentTilesAndRotation() {
        assertEquals(new GridPoint2(0, 1), Direction.UP.nextTile(new GridPoint2()));
        assertEquals(new GridPoint2(0, -1), Direction.DOWN.nextTile(new GridPoint2()));
        assertEquals(new GridPoint2(-1, 0), Direction.LEFT.nextTile(new GridPoint2()));
        assertEquals(new GridPoint2(1, 0), Direction.RIGHT.nextTile(new GridPoint2()));

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
    void doesNotShareCalculatedCoordinates() {
        GridPoint2 vector = Direction.RIGHT.nextTile(new GridPoint2());
        vector.set(100, 100);

        assertEquals(new GridPoint2(1, 0), Direction.RIGHT.nextTile(new GridPoint2()));
    }
}
