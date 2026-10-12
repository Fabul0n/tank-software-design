package ru.mipt.bit.platformer.game.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ru.mipt.bit.platformer.game.GameField;

public class GameFieldGraphics implements Drawable {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final List<Drawable> drawables = new ArrayList<>();

    public GameFieldGraphics(GameField gameField, TiledMap map, Batch batch) {
        this.map = Objects.requireNonNull(map);
        TiledMapTileLayer tileLayer = GdxGameUtils.getSingleLayer(map);
        if (!gameField.matchesDimensions(tileLayer.getWidth(), tileLayer.getHeight())) {
            throw new IllegalArgumentException("Map and gameField dimensions must match");
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
        mapRenderer.getViewBounds().set(0, 0,
                tileLayer.getWidth() * tileLayer.getTileWidth(), tileLayer.getHeight() * tileLayer.getTileHeight());
    }

    public void add(Drawable drawable) {
        drawables.add(Objects.requireNonNull(drawable));
    }

    @Override
    public void draw(Batch batch) {
        mapRenderer.render();
        batch.begin();
        try {
            for (Drawable drawable : drawables) {
                drawable.draw(batch);
            }
        } finally {
            batch.end();
        }
    }

    @Override
    public void dispose() {
        for (Drawable drawable : drawables) {
            drawable.dispose();
        }
        drawables.clear();
        mapRenderer.dispose();
        map.dispose();
    }
}
