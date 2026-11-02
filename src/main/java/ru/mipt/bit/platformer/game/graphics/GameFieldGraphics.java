package ru.mipt.bit.platformer.game.graphics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import ru.mipt.bit.platformer.game.FieldListener;
import ru.mipt.bit.platformer.game.Obstacle;
import java.util.Objects;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ru.mipt.bit.platformer.game.GameField;

public class GameFieldGraphics implements Drawable, FieldListener {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Map<Obstacle, Drawable> drawables = new LinkedHashMap<>();
    private final GameField field;
    private final Function<Obstacle, Drawable> drawableFactory;

    public GameFieldGraphics(GameField gameField, TiledMap map, Batch batch,
            Function<Obstacle, Drawable> drawableFactory) {
        this.field = Objects.requireNonNull(gameField);
        this.drawableFactory = Objects.requireNonNull(drawableFactory);
        this.map = Objects.requireNonNull(map);
        TiledMapTileLayer tileLayer = GdxGameUtils.getSingleLayer(map);
        if (!gameField.matchesDimensions(tileLayer.getWidth(), tileLayer.getHeight())) {
            throw new IllegalArgumentException("Map and gameField dimensions must match");
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
        mapRenderer.getViewBounds().set(0, 0,
                tileLayer.getWidth() * tileLayer.getTileWidth(), tileLayer.getHeight() * tileLayer.getTileHeight());
        field.subscribe(this);
    }

    @Override
    public void added(Obstacle object) {
        if (!drawables.containsKey(object)) {
            drawables.put(object, Objects.requireNonNull(drawableFactory.apply(object)));
        }
    }

    @Override
    public void removed(Obstacle object) {
        Drawable drawable = drawables.remove(object);
        if (drawable != null) {
            drawable.dispose();
        }
    }

    @Override
    public void draw(Batch batch) {
        mapRenderer.setView(batch.getProjectionMatrix(), mapRenderer.getViewBounds().x,
                mapRenderer.getViewBounds().y, mapRenderer.getViewBounds().width, mapRenderer.getViewBounds().height);
        mapRenderer.render();
        batch.begin();
        try {
            for (Drawable drawable : drawables.values()) {
                drawable.draw(batch);
            }
        } finally {
            batch.end();
        }
    }

    @Override
    public void dispose() {
        field.unsubscribe(this);
        for (Drawable drawable : drawables.values()) {
            drawable.dispose();
        }
        drawables.clear();
        mapRenderer.dispose();
        map.dispose();
    }
}
