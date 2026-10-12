package ru.mipt.bit.platformer.game;

import java.util.Objects;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.game.graphics.Drawable;
import ru.mipt.bit.platformer.game.input.KeyboardHandler;

public final class MainCycle implements Disposable {
    private final KeyboardHandler keyboardHandler;
    private final GameField gameField;
    private final Drawable graphics;

    public MainCycle(KeyboardHandler keyboardHandler, GameField gameField, Drawable graphics) {
        this.keyboardHandler = Objects.requireNonNull(keyboardHandler);
        this.gameField = Objects.requireNonNull(gameField);
        this.graphics = Objects.requireNonNull(graphics);
    }

    public void render(float deltaTime, Batch batch) {
        keyboardHandler.handle();
        gameField.update(deltaTime);
        graphics.draw(batch);
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }
}
