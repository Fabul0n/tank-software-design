package ru.mipt.bit.platformer.game.control;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Movable;
import ru.mipt.bit.platformer.game.input.Action;
import ru.mipt.bit.platformer.game.input.MoveAction;

public final class RandomController implements ControlStrategy {
    private final Movable movable;
    private final Random random;
    private final Map<Direction, Action> movementCommands = new EnumMap<>(Direction.class);
    private final float decisionIntervalSeconds;
    private double elapsedSeconds;

    public RandomController(Movable movable, Random random, float decisionIntervalSeconds) {
        this.movable = Objects.requireNonNull(movable);
        this.random = Objects.requireNonNull(random);
        if (!Float.isFinite(decisionIntervalSeconds) || decisionIntervalSeconds <= 0f) {
            throw new IllegalArgumentException("Decision interval must be finite and positive");
        }
        this.decisionIntervalSeconds = decisionIntervalSeconds;
        elapsedSeconds = decisionIntervalSeconds;
        for (Direction direction : Direction.values()) {
            movementCommands.put(direction, new MoveAction(movable, direction));
        }
    }

    @Override
    public void handle(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and nonnegative");
        }
        elapsedSeconds = Math.min(decisionIntervalSeconds, elapsedSeconds + deltaTime);
        if (movable.isMoving() || elapsedSeconds < decisionIntervalSeconds) {
            return;
        }
        elapsedSeconds = 0;
        List<Action> availableCommands = new ArrayList<>();
        for (Map.Entry<Direction, Action> entry : movementCommands.entrySet()) {
            if (movable.canMove(entry.getKey())) {
                availableCommands.add(entry.getValue());
            }
        }
        if (availableCommands.isEmpty()) {
            return;
        }
        int outcome = random.nextInt(availableCommands.size() * 2);
        if (outcome < availableCommands.size()) {
            availableCommands.get(outcome).execute();
        }
    }
}
