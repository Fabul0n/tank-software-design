package ru.mipt.bit.platformer.decomposition.rendering;

import ru.mipt.bit.platformer.decomposition.model.EntityMovement;

import com.badlogic.gdx.math.Rectangle;

/** Positions graphics using a snapshot of model coordinates. */
@FunctionalInterface
public interface MovementProjection {
    void project(Rectangle bounds, EntityMovement movement);
}
