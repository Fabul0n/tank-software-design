package ru.mipt.bit.platformer.game.input;

import java.util.Objects;
import ru.mipt.bit.platformer.game.Shooter;

public final class ShootAction implements Action {
    private final Shooter shooter;

    public ShootAction(Shooter shooter) {
        this.shooter = Objects.requireNonNull(shooter);
    }

    @Override
    public void execute() {
        shooter.shoot();
    }
}
