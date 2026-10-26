package ru.mipt.bit.platformer.game.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.mipt.bit.platformer.game.input.keyboard.Keyboard;
import ru.mipt.bit.platformer.game.control.ControlStrategy;

public class KeyboardHandler implements ControlStrategy {
    private final Keyboard keyboard;
    private final List<Binding> bindings = new ArrayList<>();

    public KeyboardHandler(Keyboard keyboard) {
        this.keyboard = Objects.requireNonNull(keyboard);
    }

    public void bind(Action action, int... keyCodes) {
        bindings.add(new Binding(action, keyCodes));
    }

    @Override
    public void handle(float deltaTime) {
        for (Binding binding : bindings) {
            if (binding.handle(keyboard)) {
                return;
            }
        }
    }

    private static final class Binding {
        private final Action action;
        private final int[] keyCodes;

        private Binding(Action action, int[] keyCodes) {
            this.action = Objects.requireNonNull(action);
            this.keyCodes = Objects.requireNonNull(keyCodes).clone();
        }

        private boolean handle(Keyboard keyboard) {
            for (int keyCode : keyCodes) {
                if (keyboard.isKeyPressed(keyCode)) {
                    action.execute();
                    return true;
                }
            }
            return false;
        }
    }
}
