package ru.mipt.bit.platformer.game.graphics;

import java.util.Objects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.game.model.EntityMovement;
import ru.mipt.bit.platformer.game.model.Tank;

public class TankDrawable implements Drawable {
    private final Tank tank;
    private final TextureRegion textureRegion;
    private final Rectangle bounds;
    private final TiledMapTileLayer tileLayer;
    private final Interpolation interpolation;

    public TankDrawable(Tank tank, Texture texture, TiledMapTileLayer tileLayer, Interpolation interpolation) {
        this.tank = Objects.requireNonNull(tank);
        this.textureRegion = new TextureRegion(Objects.requireNonNull(texture));
        this.bounds = GdxGameUtils.createBoundingRectangle(this.textureRegion);
        this.tileLayer = Objects.requireNonNull(tileLayer);
        this.interpolation = Objects.requireNonNull(interpolation);
    }

    @Override
    public void draw(Batch batch) {
        EntityMovement movement = tank.movementSnapshot();
        GdxGameUtils.positionAtMovement(bounds, movement, tileLayer, interpolation);
        GdxGameUtils.drawTextureRegionUnscaled(batch, textureRegion, bounds, movement.rotation());
    }

    @Override
    public void dispose() {
        textureRegion.getTexture().dispose();
    }
}
