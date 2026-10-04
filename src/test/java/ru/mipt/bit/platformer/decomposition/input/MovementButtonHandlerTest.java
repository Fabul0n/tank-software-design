package ru.mipt.bit.platformer.decomposition.input;

import ru.mipt.bit.platformer.decomposition.model.Direction;
import ru.mipt.bit.platformer.decomposition.model.Movable;

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
    void blockedPriorityDirectionDoesNotFallThroughToAnotherBinding() {
        java.util.List<Direction> attempted = new java.util.ArrayList<>();
        MovementButtonHandler handler = new MovementButtonHandler(direction -> {
            attempted.add(direction);
            return false;
        }, Arrays.asList(new DirectionBinding(Direction.UP, W), new DirectionBinding(Direction.LEFT, A)));

        handler.handle(new FakeKeyState(W, A));

        assertEquals(Arrays.asList(Direction.UP), attempted);
    }

    @Test
    void supportsCustomKeysAndPriorityWithoutChangingHandler() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable, Arrays.asList(
                new DirectionBinding(Direction.RIGHT, SPACE),
                new DirectionBinding(Direction.UP, W)));

        handler.handle(new FakeKeyState(W, SPACE));

        assertEquals(Direction.RIGHT, movable.lastDirection);
        assertEquals(1, movable.moves);
    }

    @Test
    void copiesBindingsAndKeyCodes() {
        RecordingMovable movable = new RecordingMovable();
        int[] keys = {SPACE};
        java.util.List<DirectionBinding> bindings = new java.util.ArrayList<>();
        bindings.add(new DirectionBinding(Direction.LEFT, keys));
        MovementButtonHandler handler = new MovementButtonHandler(movable, bindings);
        keys[0] = W;
        bindings.clear();

        handler.handle(new FakeKeyState(SPACE));

        assertEquals(Direction.LEFT, movable.lastDirection);
    }

    @Test
    void rejectsNullMovable() {
        assertThrows(NullPointerException.class, () -> new MovementButtonHandler(null, java.util.Collections.emptyList()));
    }

    @Test
    void movesTankWhenMovementKeyIsPressed() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable, Arrays.asList(
                new DirectionBinding(Direction.UP, W),
                new DirectionBinding(Direction.DOWN, DOWN),
                new DirectionBinding(Direction.LEFT, A)));

        handler.handle(new FakeKeyState(W));

        assertEquals(Direction.UP, movable.lastDirection);
        assertEquals(1, movable.moves);
    }

    @Test
    void ignoresUnrelatedKeys() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable, Arrays.asList(
                new DirectionBinding(Direction.UP, W),
                new DirectionBinding(Direction.DOWN, DOWN),
                new DirectionBinding(Direction.LEFT, A)));

        handler.handle(new FakeKeyState(SPACE));

        assertFalse(movable.wasMoved());
    }

    @Test
    void usesConfiguredDirectionPriority() {
        RecordingMovable movable = new RecordingMovable();
        MovementButtonHandler handler = new MovementButtonHandler(movable, Arrays.asList(
                new DirectionBinding(Direction.UP, W),
                new DirectionBinding(Direction.DOWN, DOWN),
                new DirectionBinding(Direction.LEFT, A)));

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
