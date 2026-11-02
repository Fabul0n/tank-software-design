package ru.mipt.bit.platformer.game.graphics;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.model.Tree;
import com.badlogic.gdx.math.GridPoint2;
import static org.junit.jupiter.api.Assertions.*;

class GameFieldGraphicsTest {
    @Test
    void drawsMapBeforeObjectsAndReleasesOwnedResources() {
        List<String> calls = new ArrayList<>();
        Batch batch = recordingBatch(calls);
        TiledMap map = new TiledMap() {
            @Override
            public void dispose() {
                calls.add("map.dispose");
                super.dispose();
            }
        };
        map.getLayers().add(new TiledMapTileLayer(2, 3, 128, 128));
        GameField field = new GameField(2, 3);
        field.spawn(new Tree(new GridPoint2(0, 0)));
        GameFieldGraphics graphics = new GameFieldGraphics(field, map, batch, object -> new Drawable() {
            @Override
            public void draw(Batch supplied) {
                assertSame(batch, supplied);
                calls.add("object.draw");
            }

            @Override
            public void dispose() {
                calls.add("object.dispose");
            }
        });

        graphics.draw(batch);
        graphics.dispose();

        assertEquals(Arrays.asList("begin", "end", "begin", "object.draw", "end",
                "object.dispose", "map.dispose"), calls);
    }

    @Test
    void rejectsMapWithDifferentDimensions() {
        TiledMap map = new TiledMap();
        map.getLayers().add(new TiledMapTileLayer(2, 3, 128, 128));
        assertThrows(IllegalArgumentException.class, () -> new GameFieldGraphics(
                new GameField(3, 3), map, recordingBatch(new ArrayList<>()), object -> null));
        map.dispose();
    }

    @Test
    void followsSpawnsAndRemovalsAndUnsubscribesOnDispose() {
        List<String> calls = new ArrayList<>();
        GameField field = new GameField(2, 3);
        TiledMap map = new TiledMap();
        map.getLayers().add(new TiledMapTileLayer(2, 3, 128, 128));
        Batch batch = recordingBatch(calls);
        GameFieldGraphics graphics = new GameFieldGraphics(field, map, batch, object -> {
            calls.add("created");
            return new Drawable() {
                public void draw(Batch supplied) { calls.add("drawn"); }
                public void dispose() { calls.add("disposed"); }
            };
        });
        Tree tree = new Tree(new GridPoint2(0, 0));
        field.spawn(tree);
        graphics.draw(batch);
        field.remove(tree);
        field.remove(tree);
        graphics.draw(batch);
        graphics.dispose();
        field.spawn(new Tree(new GridPoint2(1, 1)));

        assertEquals(1, calls.stream().filter("created"::equals).count());
        assertEquals(1, calls.stream().filter("drawn"::equals).count());
        assertEquals(1, calls.stream().filter("disposed"::equals).count());
    }

    private Batch recordingBatch(List<String> calls) {
        return (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[] {Batch.class},
                (proxy, method, arguments) -> {
                    if (Arrays.asList("begin", "end", "dispose").contains(method.getName())) {
                        calls.add(method.getName());
                    }
                    if (method.getName().equals("getColor")) {
                        return new com.badlogic.gdx.graphics.Color(1, 1, 1, 1);
                    }
                    if (method.getName().equals("getProjectionMatrix")) {
                        return new com.badlogic.gdx.math.Matrix4();
                    }
                    return null;
                });
    }
}
