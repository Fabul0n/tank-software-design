package ru.mipt.bit.platformer.decomposition;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MovementButtonHandler implements ButtonHandler {
    private static final List<DirectionBinding> DIRECTION_BINDINGS = Arrays.asList(
            new DirectionBinding(Direction.UP, UP, W),
            new DirectionBinding(Direction.DOWN, DOWN, S),
            new DirectionBinding(Direction.LEFT, LEFT, A),
            new DirectionBinding(Direction.RIGHT, RIGHT, D));

    private final Movable movable;

    public MovementButtonHandler(Movable movable) {
        this.movable = Objects.requireNonNull(movable);
    }

    @Override
    public void handle(KeyState keyState) {
        for (DirectionBinding binding : DIRECTION_BINDINGS) {
            if (binding.isPressed(keyState)) {
                movable.move(binding.direction);
                return;
            }
        }
    }

    private static class DirectionBinding {
        private final Direction direction;
        private final int[] keyCodes;

        private DirectionBinding(Direction direction, int... keyCodes) {
            this.direction = direction;
            this.keyCodes = keyCodes;
        }

        private boolean isPressed(KeyState keyState) {
            for (int keyCode : keyCodes) {
                if (keyState.isKeyPressed(keyCode)) {
                    return true;
                }
            }
            return false;
        }
    }
}
