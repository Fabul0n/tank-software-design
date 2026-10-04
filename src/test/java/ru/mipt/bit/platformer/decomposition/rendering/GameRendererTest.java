package ru.mipt.bit.platformer.decomposition.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.jupiter.api.Test;

class GameRendererTest {
    @Test
    void supportsIndependentGraphicsAndClearsThemAfterDisposal() {
        List<String> calls = new ArrayList<>();
        Graphics customGraphics = new Graphics() {
            @Override
            public void update() {
                calls.add("update");
            }

            @Override
            public void render(Batch batch) {
                calls.add("render");
            }

            @Override
            public void dispose() {
                calls.add("dispose");
            }
        };
        GameRenderer renderer = new GameRenderer();
        renderer.add(customGraphics);

        renderer.update();
        renderer.render(null);
        renderer.dispose();
        renderer.update();
        renderer.render(null);
        renderer.dispose();

        assertEquals(Arrays.asList("update", "render", "dispose"), calls);
    }

    @Test
    void rejectsNullGraphics() {
        assertThrows(NullPointerException.class, () -> new GameRenderer().add(null));
    }
}
