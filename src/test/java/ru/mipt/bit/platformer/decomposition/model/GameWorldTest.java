package ru.mipt.bit.platformer.decomposition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

class GameWorldTest {
    @Test
    void rejectsInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> new GameWorld(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new GameWorld(5, 0));
    }

    @Test
    void rejectsNullEntity() {
        GameWorld world = new GameWorld(3, 3);

        assertThrows(NullPointerException.class, () -> world.spawn(null));
    }

    @Test
    void rejectsTilesOutsideWorld() {
        GameWorld world = new GameWorld(3, 2);

        assertFalse(world.isTileFree(new GridPoint2(-1, 0)));
        assertFalse(world.isTileFree(new GridPoint2(0, -1)));
        assertFalse(world.isTileFree(new GridPoint2(3, 0)));
        assertFalse(world.isTileFree(new GridPoint2(0, 2)));
    }

    @Test
    void allowsEmptyTilesInsideWorld() {
        GameWorld world = new GameWorld(3, 2);

        assertTrue(world.isTileFree(new GridPoint2(2, 1)));
    }

    @Test
    void blockedEntityOccupiesTile() {
        GameWorld world = new GameWorld(3, 3);
        world.spawn(new Tree(new GridPoint2(1, 1)));

        assertFalse(world.isTileFree(new GridPoint2(1, 1)));
        assertTrue(world.isTileFree(new GridPoint2(1, 2)));
    }

    @Test
    void updatesSpawnedEntities() {
        GameWorld world = new GameWorld(3, 3);
        Tank tank = new Tank(new GridPoint2(0, 0), world::isTileFree, 0.4f);
        world.spawn(tank);

        tank.move(Direction.RIGHT);
        world.update(0.4f);

        assertTrue(tank.occupies(new GridPoint2(1, 0)));
    }

    @Test
    void clearRemovesEntities() {
        GameWorld world = new GameWorld(3, 3);
        world.spawn(new Tree(new GridPoint2(1, 1)));

        world.clear();

        assertTrue(world.isTileFree(new GridPoint2(1, 1)));
    }

    @Test
    void delegatesBlockingDecisionToEntities() {
        GameWorld world = new GameWorld(3, 3);
        BlockingEntity entity = new BlockingEntity(new GridPoint2(0, 0), new GridPoint2(2, 2));
        world.spawn(entity);

        assertFalse(world.isTileFree(new GridPoint2(2, 2)));
        assertTrue(world.isTileFree(new GridPoint2(0, 0)));
    }

    private static class BlockingEntity extends Entity {
        private final GridPoint2 blockedTile;

        private BlockingEntity(GridPoint2 coordinates, GridPoint2 blockedTile) {
            super(coordinates);
            this.blockedTile = blockedTile;
        }

        @Override
        public boolean blocksMovementAt(GridPoint2 coordinates) {
            return blockedTile.equals(coordinates);
        }
    }
}
