package ru.mipt.bit.platformer.decomposition;

import java.util.function.Predicate;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.util.TileMovement;

public class Tank extends Entity {
    private final MovementComponent movement;

    public Tank(TiledMapTileLayer layer, Texture texture, GridPoint2 coordinates, TileMovement tileMovement,
            Predicate<GridPoint2> canMoveTo) {
        super(layer, texture, coordinates);

        this.movement = addComponent(
                new MovementComponent(this, tileMovement, canMoveTo));

    }

    public MovementComponent getMovement() {
        return movement;
    }
}
