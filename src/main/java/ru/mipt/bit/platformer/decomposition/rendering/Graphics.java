package ru.mipt.bit.platformer.decomposition.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

public interface Graphics extends Disposable {
    void update();

    void render(Batch batch);
}
