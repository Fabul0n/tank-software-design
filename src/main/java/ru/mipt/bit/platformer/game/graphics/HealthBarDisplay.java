package ru.mipt.bit.platformer.game.graphics;

public final class HealthBarDisplay {
    private boolean visible;

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
