package ru.mipt.bit.platformer.decomposition.rendering;

import java.util.Objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameRenderer implements Disposable {
    private final Array<Graphics> graphics = new Array<>();

    public void add(Graphics entityGraphics) {
        graphics.add(Objects.requireNonNull(entityGraphics));
    }

    public void update() {
        for (Graphics entityGraphics : graphics) {
            entityGraphics.update();
        }
    }

    public void render(Batch batch) {
        for (Graphics entityGraphics : graphics) {
            entityGraphics.render(batch);
        }
    }

    @Override
    public void dispose() {
        for (Graphics entityGraphics : graphics) {
            entityGraphics.dispose();
        }
        graphics.clear();
    }
}
