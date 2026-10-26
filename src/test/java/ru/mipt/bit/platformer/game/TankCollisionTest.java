package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import static org.junit.jupiter.api.Assertions.*;

class TankCollisionTest {
    @Test
    void reservesBothCellsImmediatelyAndReleasesOriginOnlyOnArrival() {
        GameField field = new GameField(4, 2);
        Tank tank = spawn(field, 0, 0);
        assertTrue(tank.move(Direction.RIGHT));
        assertTrue(field.isTileOccupied(new GridPoint2(0, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(1, 0)));
        assertFalse(field.isTileOccupied(new GridPoint2(2, 0)));

        field.update(0.2f);
        assertTrue(field.isTileOccupied(new GridPoint2(0, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(1, 0)));

        field.update(0.2f);
        assertFalse(field.isTileOccupied(new GridPoint2(0, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(1, 0)));

        assertTrue(tank.move(Direction.RIGHT));
        assertFalse(field.isTileOccupied(new GridPoint2(0, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(1, 0)));
        assertTrue(field.isTileOccupied(new GridPoint2(2, 0)));
    }

    @Test
    void onlyFirstTankCanReserveSharedDestination() {
        GameField field = new GameField(3, 1);
        Tank first = spawn(field, 0, 0);
        Tank second = spawn(field, 2, 0);

        assertTrue(first.move(Direction.RIGHT));
        assertFalse(second.move(Direction.LEFT));
        assertFalse(second.isMoving());
        assertFalse(second.occupies(new GridPoint2(1, 0)));
    }

    @Test
    void otherTankCannotEnterOriginDuringTransition() {
        GameField field = new GameField(2, 2);
        Tank first = spawn(field, 0, 0);
        Tank following = spawn(field, 0, 1);

        assertTrue(first.move(Direction.RIGHT));
        assertFalse(following.move(Direction.DOWN));
        field.update(0.2f);
        assertFalse(following.move(Direction.DOWN));
        field.update(0.2f);
        assertTrue(following.move(Direction.DOWN));
    }

    @Test
    void tanksCannotSwapOccupiedCells() {
        GameField field = new GameField(2, 1);
        Tank first = spawn(field, 0, 0);
        Tank second = spawn(field, 1, 0);

        assertFalse(first.move(Direction.RIGHT));
        assertFalse(second.move(Direction.LEFT));
        assertFalse(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    void rejectedMovementDoesNotReserveAnotherCell() {
        GameField field = new GameField(2, 2);
        Tank tank = spawn(field, 0, 0);
        field.spawn(new Tree(new GridPoint2(1, 0)));

        assertFalse(tank.move(Direction.RIGHT));
        assertTrue(tank.occupies(new GridPoint2(0, 0)));
        assertFalse(tank.occupies(new GridPoint2(1, 0)));
        assertFalse(tank.occupies(new GridPoint2(0, 1)));
        assertTrue(tank.move(Direction.UP));
    }

    @Test
    void allFourBoundariesBlockTanksLikeObstacles() {
        Direction[] outward = {Direction.LEFT, Direction.DOWN, Direction.RIGHT, Direction.UP};
        int[][] positions = {{0, 1}, {1, 0}, {2, 1}, {1, 2}};
        for (int i = 0; i < outward.length; i++) {
            GameField field = new GameField(3, 3);
            Tank tank = spawn(field, positions[i][0], positions[i][1]);
            GridPoint2 start = new GridPoint2(positions[i][0], positions[i][1]);
            assertFalse(tank.move(outward[i]));
            field.update(1f);
            assertFalse(tank.isMoving());
            assertTrue(tank.occupies(start));
            assertFalse(tank.occupies(outward[i].nextTile(start)));
        }
    }

    private Tank spawn(GameField field, int x, int y) {
        Tank tank = new Tank(new GridPoint2(x, y), field, 0.4f);
        field.spawn(tank);
        return tank;
    }
}
