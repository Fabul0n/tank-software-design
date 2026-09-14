package ru.mipt.bit.platformer.decomposition;

import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class GameWorld {
    private final Array<Entity> entities = new Array<>();
    private final TiledMapTileLayer groundLayer;

    public GameWorld(TiledMapTileLayer groundLayer) {
        this.groundLayer = groundLayer;
    }

    public void spawn(Entity entity) {
        entities.add(entity);
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public void render(Batch batch) {
        for (Entity entity : entities) {
            drawTextureRegionUnscaled(
                    batch,
                    entity.getTextureRegion(),
                    entity.getBoundingRectangle(),
                    entity.getRotation());
        }
    }

    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
        entities.clear();
    }

    public boolean isTileFree(GridPoint2 coordinates) {
        if (coordinates.x < 0 || coordinates.y < 0 || coordinates.x >= groundLayer.getWidth()
                || coordinates.y >= groundLayer.getHeight()) {
            return false;
        }
        for (Entity entity : entities) {
            if (entity instanceof Tree && entity.getCoordinates().equals(coordinates)) {
                return false;
            }
        }
        return true;
    }
}
