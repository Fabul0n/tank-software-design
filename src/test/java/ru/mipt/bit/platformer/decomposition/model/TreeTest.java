package ru.mipt.bit.platformer.decomposition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class TreeTest {
    @Test
    void blocksMovement() {
        Tree tree = new Tree(new GridPoint2(3, 4));

        assertTrue(tree.blocksMovementAt(new GridPoint2(3, 4)));
    }

    @Test
    void storesOwnCoordinatesCopy() {
        GridPoint2 source = new GridPoint2(3, 4);
        Tree tree = new Tree(source);

        source.set(0, 0);

        assertTrue(tree.occupies(new GridPoint2(3, 4)));
    }

    @Test
    void exposesIdleMovementState() {
        Tree tree = new Tree(new GridPoint2(3, 4));

        assertEquals(
                EntityMovement.idle(new GridPoint2(3, 4), Direction.RIGHT),
                tree.movementSnapshot());
    }
}
