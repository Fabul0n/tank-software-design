package ru.mipt.bit.platformer.game.input;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

public final class CommandQueue implements Consumer<Action> {
    private final Queue<Action> commands = new ArrayDeque<>();

    @Override
    public void accept(Action command) {
        commands.add(Objects.requireNonNull(command));
    }

    public void execute() {
        while (!commands.isEmpty()) {
            commands.remove().execute();
        }
    }
}
