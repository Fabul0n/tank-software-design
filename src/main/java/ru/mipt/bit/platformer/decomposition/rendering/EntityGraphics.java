package ru.mipt.bit.platformer.decomposition.rendering;

import ru.mipt.bit.platformer.decomposition.model.EntityMovement;
import ru.mipt.bit.platformer.decomposition.model.MovementState;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

import java.util.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class EntityGraphics implements Graphics {
    private final MovementState entity;
    private final TextureRegion textureRegion;
    private final Rectangle bounds;
    private final MovementProjection movementProjection;
    private float rotation;

    public EntityGraphics(MovementState entity, Texture texture, MovementProjection movementProjection) {
        this.entity = Objects.requireNonNull(entity);
        this.textureRegion = new TextureRegion(Objects.requireNonNull(texture));
        this.bounds = createBoundingRectangle(textureRegion);
        this.movementProjection = Objects.requireNonNull(movementProjection);
        update();
    }

    @Override
    public void update() {
        EntityMovement movement = entity.movementSnapshot();
        movementProjection.project(bounds, movement);
        rotation = movement.rotation();
    }

    @Override
    public void render(Batch batch) {
        drawTextureRegionUnscaled(
                batch,
                textureRegion,
                bounds,
                rotation);
    }

    @Override
    public void dispose() {
        textureRegion.getTexture().dispose();
    }
}
