package ru.mipt.bit.platformer.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;

import com.badlogic.gdx.math.GridPoint2;

class GameFieldTest {
    @Test
    void tankOccupiesItsTileAndPreventsAnotherTankFromEntering() {
        GameField field = new GameField(3, 2);
        Tank first = new Tank(new GridPoint2(0, 0), field, 0.4f);
        Tank second = new Tank(new GridPoint2(1, 0), field, 0.4f);
        field.spawn(first);
        field.spawn(second);

        assertTrue(field.isTileOccupied(new GridPoint2(1, 0)));
        assertFalse(first.move(Direction.RIGHT));
        assertTrue(second.move(Direction.UP));
        field.update(0.4f);
        assertFalse(field.isTileOccupied(new GridPoint2(1, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(1, 1)));
        assertTrue(first.move(Direction.RIGHT));
    }

    @Test
    void rejectsInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> new GameField(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new GameField(5, 0));
    }

    @Test
    void rejectsNullEntity() {
        GameField world = new GameField(3, 3);

        assertThrows(NullPointerException.class, () -> world.spawn(null));
    }

    @Test
    void rejectsTilesOutsideWorld() {
        GameField world = new GameField(3, 2);

        assertTrue(world.isTileOccupied(new GridPoint2(-1, 0)));
        assertTrue(world.isTileOccupied(new GridPoint2(0, -1)));
        assertTrue(world.isTileOccupied(new GridPoint2(3, 0)));
        assertTrue(world.isTileOccupied(new GridPoint2(0, 2)));
    }

    @Test
    void allowsEmptyTilesInsideWorld() {
        GameField world = new GameField(3, 2);

        assertFalse(world.isTileOccupied(new GridPoint2(2, 1)));
    }

    @Test
    void blockedEntityOccupiesTile() {
        GameField world = new GameField(3, 3);
        world.spawn(new Tree(new GridPoint2(1, 1)));

        assertTrue(world.isTileOccupied(new GridPoint2(1, 1)));
        assertFalse(world.isTileOccupied(new GridPoint2(1, 2)));
    }

    @Test
    void updatesSpawnedEntities() {
        GameField world = new GameField(3, 3);
        Tank tank = new Tank(new GridPoint2(0, 0), world::isTileOccupied, 0.4f);
        world.spawn(tank);

        tank.move(Direction.RIGHT);
        world.update(0.4f);

        assertTrue(tank.occupies(new GridPoint2(1, 0)));
    }

    @Test
    void clearRemovesEntities() {
        GameField world = new GameField(3, 3);
        world.spawn(new Tree(new GridPoint2(1, 1)));

        world.clear();

        assertFalse(world.isTileOccupied(new GridPoint2(1, 1)));
    }

    @Test
    void delegatesBlockingDecisionToEntities() {
        GameField world = new GameField(3, 3);
        BlockingEntity entity = new BlockingEntity(new GridPoint2(0, 0), new GridPoint2(2, 2));
        world.spawn(entity);

        assertTrue(world.isTileOccupied(new GridPoint2(2, 2)));
        assertFalse(world.isTileOccupied(new GridPoint2(0, 0)));
    }

    private static class BlockingEntity implements Obstacle {
        private final GridPoint2 blockedTile;

        private BlockingEntity(GridPoint2 coordinates, GridPoint2 blockedTile) {
            this.blockedTile = blockedTile;
        }

        @Override
        public void update(float deltaTime) {
        }

        @Override
        public boolean occupies(GridPoint2 coordinates) {
            return blockedTile.equals(coordinates);
        }
    }
}
