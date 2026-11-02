package ru.mipt.bit.platformer.game.graphics;

import com.badlogic.gdx.graphics.Texture;
import java.util.Objects;
import ru.mipt.bit.platformer.game.model.EntityMovement;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import ru.mipt.bit.platformer.game.model.Bullet;

public final class BulletDrawable implements Drawable {
    private final Bullet bullet;
    private final Texture texture;
    private final int tileSize;

    public BulletDrawable(Bullet bullet, Texture texture, int tileSize) {
        this.bullet = Objects.requireNonNull(bullet);
        this.texture = Objects.requireNonNull(texture);
        this.tileSize = tileSize;
    }

    @Override
    public void draw(Batch batch) {
        EntityMovement movement = bullet.movementSnapshot();
        Vector2 position = movement.interpolatePosition(Interpolation.linear);
        float width = tileSize * 0.7f;
        float height = width * texture.getHeight() / texture.getWidth();
        float previousColor = batch.getPackedColor();
        batch.setColor(1f, 1f, 1f, 1f);
        try {
            batch.draw(texture, (position.x + 0.5f) * tileSize - width / 2f,
                    (position.y + 0.5f) * tileSize - height / 2f,
                    width / 2f, height / 2f, width, height, 1f, 1f, movement.rotation(),
                    0, 0, texture.getWidth(), texture.getHeight(), false, false);
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
