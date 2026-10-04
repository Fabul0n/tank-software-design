package ru.mipt.bit.platformer.decomposition.model;

import com.badlogic.gdx.math.GridPoint2;

public class Tree extends Entity {
    public Tree(GridPoint2 coordinates) {
        super(coordinates);
    }

    @Override
    public boolean blocksMovementAt(GridPoint2 coordinates) {
        return occupies(coordinates);
    }
}
