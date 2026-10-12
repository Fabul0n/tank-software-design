package ru.mipt.bit.platformer.game.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.NoSuchElementException;

import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.game.model.EntityMovement;

public final class GdxGameUtils {

    private GdxGameUtils() {
    }

    public static TiledMap createGroundMap(int widthInTiles, int heightInTiles, int tileSize,
            TextureRegion firstGrass, TextureRegion secondGrass) {
        if (widthInTiles <= 0 || heightInTiles <= 0 || tileSize <= 0) {
            throw new IllegalArgumentException("Map dimensions and tile size must be positive");
        }
        TiledMap map = new TiledMap();
        TiledMapTileLayer layer = new TiledMapTileLayer(widthInTiles, heightInTiles, tileSize, tileSize);
        StaticTiledMapTile firstTile = new StaticTiledMapTile(firstGrass);
        StaticTiledMapTile secondTile = new StaticTiledMapTile(secondGrass);
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                layer.setCell(x, y, new TiledMapTileLayer.Cell().setTile(
                        (x + y) % 2 == 0 ? firstTile : secondTile));
            }
        }
        map.getLayers().add(layer);
        return map;
    }

    public static <L extends MapLayer> L getSingleLayer(Map map) {
        MapLayers layers = map.getLayers();
        switch (layers.size()) {
            case 0:
                throw new NoSuchElementException("Map has no layers");
            case 1:
                @SuppressWarnings("unchecked")
                L layer = (L) layers.iterator().next();
                return layer;
            default:
                throw new IllegalArgumentException("Map has more than one layer");
        }
    }

    public static void drawTextureRegionUnscaled(Batch batch, TextureRegion region, Rectangle rectangle, float rotation) {
        int regionWidth = region.getRegionWidth();
        int regionHeight = region.getRegionHeight();
        float regionOriginX = regionWidth / 2f;
        float regionOriginY = regionHeight / 2f;
        batch.draw(region, rectangle.x, rectangle.y, regionOriginX, regionOriginY, regionWidth, regionHeight, 1f, 1f, rotation);
    }

    public static Rectangle createBoundingRectangle(TextureRegion region) {
        return new Rectangle()
                .setWidth(region.getRegionWidth())
                .setHeight(region.getRegionHeight());
    }

    public static void positionAtMovement(Rectangle bounds, EntityMovement movement,
            TiledMapTileLayer layer, Interpolation interpolation) {
        Vector2 position = movement.interpolatePosition(interpolation);
        bounds.setCenter((position.x + 0.5f) * layer.getTileWidth(),
                (position.y + 0.5f) * layer.getTileHeight());
    }

}
