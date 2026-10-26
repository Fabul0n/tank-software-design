package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.game.control.ControlStrategy;
import ru.mipt.bit.platformer.game.graphics.Drawable;

public final class MainCycle implements Disposable {
    private final List<ControlStrategy> controllers;
    private final GameField gameField;
    private final Drawable graphics;

    public MainCycle(List<? extends ControlStrategy> controllers, GameField gameField, Drawable graphics) {
        this.controllers = new ArrayList<>(Objects.requireNonNull(controllers));
        this.controllers.forEach(Objects::requireNonNull);
        this.gameField = Objects.requireNonNull(gameField);
        this.graphics = Objects.requireNonNull(graphics);
    }

    public void render(float deltaTime, Batch batch) {
        for (ControlStrategy controller : controllers) {
            controller.handle(deltaTime);
        }
        gameField.update(deltaTime);
        graphics.draw(batch);
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }
}
