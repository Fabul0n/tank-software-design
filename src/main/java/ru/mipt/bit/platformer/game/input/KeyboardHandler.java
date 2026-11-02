package ru.mipt.bit.platformer.game.input;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import ru.mipt.bit.platformer.game.input.keyboard.Keyboard;
import ru.mipt.bit.platformer.game.control.ControlStrategy;

public class KeyboardHandler implements ControlStrategy {
    private final Keyboard keyboard;
    private final Consumer<Action> commands;
    private final List<ActionGroup> groups = new ArrayList<>();

    public KeyboardHandler(Keyboard keyboard) {
        this(keyboard, Action::execute);
    }

    public KeyboardHandler(Keyboard keyboard, Consumer<Action> commands) {
        this.keyboard = Objects.requireNonNull(keyboard);
        this.commands = Objects.requireNonNull(commands);
    }

    public enum Priority {
        BINDING_ORDER, LAST_PRESSED
    }

    public ActionGroup createGroup() {
        return createGroup(Priority.BINDING_ORDER);
    }

    public ActionGroup createGroup(Priority priority) {
        ActionGroup group = new ActionGroup(Objects.requireNonNull(priority));
        groups.add(group);
        return group;
    }

    @Override
    public void handle(float deltaTime) {
        for (ActionGroup group : groups) {
            group.handle(keyboard, commands);
        }
    }

    public static final class ActionGroup {
        private final List<Binding> bindings = new ArrayList<>();

        private final Priority priority;
        private final Map<Integer, Long> pressedAt = new HashMap<>();
        private long poll;

        private ActionGroup(Priority priority) {
            this.priority = priority;
        }

        public void bind(Action action, int... keyCodes) {
            bindings.add(new Binding(action, keyCodes));
        }

        private void handle(Keyboard keyboard, Consumer<Action> commands) {
            if (priority == Priority.LAST_PRESSED) {
                handleLastPressed(keyboard, commands);
                return;
            }
            for (Binding binding : bindings) {
                if (binding.handle(keyboard, commands)) {
                    return;
                }
            }
        }
        private void handleLastPressed(Keyboard keyboard, Consumer<Action> commands) {
            poll++;
            Binding selected = null;
            long latestPress = Long.MIN_VALUE;
            for (Binding binding : bindings) {
                for (int keyCode : binding.keyCodes) {
                    if (!keyboard.isKeyPressed(keyCode)) {
                        pressedAt.remove(keyCode);
                        continue;
                    }
                    long press = pressedAt.computeIfAbsent(keyCode, key -> poll);
                    if (press > latestPress) {
                        latestPress = press;
                        selected = binding;
                    }
                }
            }
            if (selected != null) {
                commands.accept(selected.action);
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

        private boolean handle(Keyboard keyboard, Consumer<Action> commands) {
            for (int keyCode : keyCodes) {
                if (keyboard.isKeyPressed(keyCode)) {
                    commands.accept(action);
                    return true;
                }
            }
            return false;
        }
    }
}
