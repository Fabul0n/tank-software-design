package ru.mipt.bit.platformer.decomposition;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

import java.util.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.util.TileMovement;

public class EntityGraphics implements Disposable {
    private final Entity entity;
    private final TextureRegion textureRegion;
    private final Rectangle bounds;
    private final TileMovement tileMovement;

    public EntityGraphics(Entity entity, Texture texture, TileMovement tileMovement) {
        this.entity = Objects.requireNonNull(entity);
        this.textureRegion = new TextureRegion(Objects.requireNonNull(texture));
        this.bounds = createBoundingRectangle(textureRegion);
        this.tileMovement = Objects.requireNonNull(tileMovement);
        update();
    }

    public void update() {
        EntityMovement movement = entity.movementSnapshot();
        tileMovement.moveRectangleBetweenTileCenters(
                bounds,
                movement.origin(),
                movement.destination(),
                movement.progress());
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(
                batch,
                textureRegion,
                bounds,
                entity.movementSnapshot().direction().rotation());
    }

    @Override
    public void dispose() {
        textureRegion.getTexture().dispose();
    }
}
