package ru.mipt.bit.platformer.decomposition.input;

import ru.mipt.bit.platformer.decomposition.model.Movable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovementButtonHandler implements ButtonHandler {
    private final Movable movable;
    private final List<DirectionBinding> bindings;

    /** The first pressed binding wins, so list order defines direction priority. */
    public MovementButtonHandler(Movable movable, List<DirectionBinding> bindings) {
        this.movable = Objects.requireNonNull(movable);
        this.bindings = new ArrayList<>(Objects.requireNonNull(bindings));
        this.bindings.forEach(Objects::requireNonNull);
    }

    @Override
    public void handle(KeyState keyState) {
        for (DirectionBinding binding : bindings) {
            if (binding.handle(keyState, movable)) {
                return;
            }
        }
    }
}
