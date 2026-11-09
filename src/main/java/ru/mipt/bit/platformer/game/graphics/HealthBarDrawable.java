package ru.mipt.bit.platformer.game.graphics;

import java.util.Objects;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import ru.mipt.bit.platformer.game.model.Tank;

public final class HealthBarDrawable implements Drawable {
    private final Drawable wrapped;
    private final Tank tank;
    private final HealthBarDisplay display;
    private final TextureRegion fill;
    private final int tileSize;

    public HealthBarDrawable(Drawable wrapped, Tank tank, HealthBarDisplay display,
            TextureRegion fill, int tileSize) {
        this.wrapped = Objects.requireNonNull(wrapped);
        this.tank = Objects.requireNonNull(tank);
        this.display = Objects.requireNonNull(display);
        this.fill = Objects.requireNonNull(fill);
        this.tileSize = tileSize;
    }

    @Override
    public void draw(Batch batch) {
        wrapped.draw(batch);
        if (!display.isVisible()) {
            return;
        }
        Vector2 position = tank.movementSnapshot().interpolatePosition(Interpolation.smooth);
        float width = tileSize * 0.8f;
        float height = tileSize * 0.06f;
        float x = (position.x + 0.1f) * tileSize;
        float y = (position.y + 0.9f) * tileSize;
        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(0.2f, 0.2f, 0.2f, 1f);
            batch.draw(fill, x, y, width, height);
            batch.setColor(0.9f, 0.15f, 0.15f, 1f);
            batch.draw(fill, x, y, width * tank.healthFraction(), height);
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    @Override
    public void dispose() {
        wrapped.dispose();
    }
}
