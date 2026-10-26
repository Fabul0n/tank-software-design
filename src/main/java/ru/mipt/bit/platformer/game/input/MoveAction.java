package ru.mipt.bit.platformer.game.input;

import java.util.Objects;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Movable;

public final class MoveAction implements Action {
    private final Movable movable;
    private final Direction direction;

    public MoveAction(Movable movable, Direction direction) {
        this.movable = Objects.requireNonNull(movable);
        this.direction = Objects.requireNonNull(direction);
    }

    @Override
    public void execute() {
        movable.move(direction);
    }
}
