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
        GameFieldGraphics graphics = new GameFieldGraphics(new GameField(2, 3), map, batch);
        graphics.add(new Drawable() {
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
                new GameField(3, 3), map, recordingBatch(new ArrayList<>())));
        map.dispose();
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
                    return null;
                });
    }
}
