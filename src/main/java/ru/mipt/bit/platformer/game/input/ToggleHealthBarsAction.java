package ru.mipt.bit.platformer.game.input;

import java.util.Objects;
import ru.mipt.bit.platformer.game.graphics.HealthBarDisplay;

public final class ToggleHealthBarsAction implements Action {
    private final HealthBarDisplay display;

    public ToggleHealthBarsAction(HealthBarDisplay display) {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void execute() {
        display.toggle();
    }
}
