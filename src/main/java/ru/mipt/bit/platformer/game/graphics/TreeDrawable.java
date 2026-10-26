package ru.mipt.bit.platformer.game.graphics;

import java.util.Objects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.game.model.EntityMovement;
import ru.mipt.bit.platformer.game.model.Tree;

public class TreeDrawable implements Drawable {
    private final Tree tree;
    private final TextureRegion textureRegion;
    private final Rectangle bounds;
    private final TiledMapTileLayer tileLayer;
    private final Interpolation interpolation;

    public TreeDrawable(Tree tree, Texture texture, TiledMapTileLayer tileLayer, Interpolation interpolation) {
        this.tree = Objects.requireNonNull(tree);
        this.textureRegion = new TextureRegion(Objects.requireNonNull(texture));
        this.bounds = GdxGameUtils.createBoundingRectangle(this.textureRegion);
        this.tileLayer = Objects.requireNonNull(tileLayer);
        this.interpolation = Objects.requireNonNull(interpolation);
    }

    @Override
    public void draw(Batch batch) {
        EntityMovement movement = tree.movementSnapshot();
        GdxGameUtils.positionAtMovement(bounds, movement, tileLayer, interpolation);
        GdxGameUtils.drawTextureRegionUnscaled(batch, textureRegion, bounds, movement.rotation());
    }

    @Override
    public void dispose() {
        textureRegion.getTexture().dispose();
    }
}
