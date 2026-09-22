package ru.mipt.bit.platformer.decomposition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameRenderer implements Disposable {
    private final Array<EntityGraphics> graphics = new Array<>();

    public void add(EntityGraphics entityGraphics) {
        graphics.add(entityGraphics);
    }

    public void update() {
        for (EntityGraphics entityGraphics : graphics) {
            entityGraphics.update();
        }
    }

    public void render(Batch batch) {
        for (EntityGraphics entityGraphics : graphics) {
            entityGraphics.render(batch);
        }
    }

    @Override
    public void dispose() {
        for (EntityGraphics entityGraphics : graphics) {
            entityGraphics.dispose();
        }
        graphics.clear();
    }
}
