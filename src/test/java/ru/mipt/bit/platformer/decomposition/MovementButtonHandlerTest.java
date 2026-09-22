package ru.mipt.bit.platformer.decomposition;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.Input.Keys.W;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class MovementButtonHandlerTest {
    @Test
    void rejectsNullMovable() {
        assertThrows(NullPointerException.class, () -> new MovementButtonHandler(null));
    }

    @Test
    void movesTankWhenMovementKeyIsPressed() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable);

        handler.handle(new FakeKeyState(W));

        assertEquals(Direction.UP, movable.lastDirection);
        assertEquals(1, movable.moves);
    }

    @Test
    void ignoresUnrelatedKeys() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable);

        handler.handle(new FakeKeyState(SPACE));

        assertFalse(movable.wasMoved());
    }

    @Test
    void usesConfiguredDirectionPriority() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable);

        handler.handle(new FakeKeyState(DOWN, A));

        assertEquals(Direction.DOWN, movable.lastDirection);
        assertEquals(1, movable.moves);
    }

    private static class FakeKeyState implements KeyState {
        private final Set<Integer> pressedKeys;

        private FakeKeyState(Integer... pressedKeys) {
            this.pressedKeys = new HashSet<>(Arrays.asList(pressedKeys));
        }

        @Override
        public boolean isKeyPressed(int keyCode) {
            return pressedKeys.contains(keyCode);
        }
    }

    private static class RecordingMovable implements Movable {
        private Direction lastDirection;
        private int moves;

        @Override
        public boolean move(Direction direction) {
            lastDirection = direction;
            moves++;
            return true;
        }

        private boolean wasMoved() {
            return moves > 0;
        }
    }
}
