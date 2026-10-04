package ru.mipt.bit.platformer.decomposition.input;

import ru.mipt.bit.platformer.decomposition.model.Direction;
import ru.mipt.bit.platformer.decomposition.model.Movable;

import java.util.Objects;

public final class DirectionBinding {
    private final Direction direction;
    private final int[] keyCodes;

    public DirectionBinding(Direction direction, int... keyCodes) {
        this.direction = Objects.requireNonNull(direction);
        this.keyCodes = Objects.requireNonNull(keyCodes).clone();
    }

    /** Returns whether a key was handled, even if movement itself was blocked. */
    boolean handle(KeyState keyState, Movable movable) {
        for (int keyCode : keyCodes) {
            if (keyState.isKeyPressed(keyCode)) {
                movable.move(direction);
                return true;
            }
        }
        return false;
    }
}
