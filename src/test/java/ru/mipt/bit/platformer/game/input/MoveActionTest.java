package ru.mipt.bit.platformer.game.input;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Movable;
import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {
    @Test
    void movesAnIndependentMovable() {
        RecordingMovable movable = new RecordingMovable();
        Action action = new MoveAction(movable, Direction.LEFT);

        action.execute();

        assertEquals(Direction.LEFT, movable.direction);
        assertEquals(1, movable.calls);
    }

    @Test
    void rejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new MoveAction(null, Direction.UP));
        assertThrows(NullPointerException.class, () -> new MoveAction(new RecordingMovable(), null));
    }

    private static class RecordingMovable implements Movable {
        private Direction direction;
        private int calls;

        @Override
        public boolean move(Direction direction) {
            this.direction = direction;
            calls++;
            return true;
        }

        @Override
        public boolean canMove(Direction direction) {
            return true;
        }

        @Override
        public boolean isMoving() {
            return false;
        }

        @Override
        public void update(float deltaTime) {
        }

        @Override
        public boolean occupies(GridPoint2 tile) {
            return false;
        }
    }
}
