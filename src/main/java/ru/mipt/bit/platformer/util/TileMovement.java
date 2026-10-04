package ru.mipt.bit.platformer.util;

import java.util.Objects;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ru.mipt.bit.platformer.decomposition.model.EntityMovement;
import ru.mipt.bit.platformer.decomposition.rendering.MovementProjection;

public class TileMovement implements MovementProjection {
    private final TiledMapTileLayer tileLayer;
    private final Interpolation interpolation;

    public TileMovement(TiledMapTileLayer tileLayer, Interpolation interpolation) {
        this.tileLayer = Objects.requireNonNull(tileLayer);
        this.interpolation = Objects.requireNonNull(interpolation);
    }

    @Override
    public void project(Rectangle bounds, EntityMovement movement) {
        Vector2 position = movement.position(interpolation);
        bounds.setCenter(
                (position.x + 0.5f) * tileLayer.getTileWidth(),
                (position.y + 0.5f) * tileLayer.getTileHeight());
    }
}
