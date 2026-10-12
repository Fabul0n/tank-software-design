package ru.mipt.bit.platformer.game.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

public interface Drawable extends Disposable {
    void draw(Batch batch);
}
