package ru.mipt.bit.platformer.decomposition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

public class Tree extends Entity {
    public Tree(TiledMapTileLayer layer, Texture texture, GridPoint2 coordinates) {
        super(layer, texture, coordinates);
    }
}
