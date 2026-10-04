package ru.mipt.bit.platformer.decomposition.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerController {
    private final KeyState keyState;
    private final List<ButtonHandler> buttonHandlers = new ArrayList<>();

    public PlayerController(KeyState keyState) {
        this.keyState = Objects.requireNonNull(keyState);
    }

    public void addButtonHandler(ButtonHandler buttonHandler) {
        buttonHandlers.add(Objects.requireNonNull(buttonHandler));
    }

    public void update() {
        for (ButtonHandler buttonHandler : buttonHandlers) {
            buttonHandler.handle(keyState);
        }
    }
}