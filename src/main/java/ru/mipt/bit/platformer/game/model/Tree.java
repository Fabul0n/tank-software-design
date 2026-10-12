package ru.mipt.bit.platformer.game.model;

import java.util.Objects;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.Obstacle;

public class Tree implements Obstacle {
    private final GridPoint2 coordinates;

    public Tree(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(Objects.requireNonNull(coordinates));
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile);
    }

    public EntityMovement movementSnapshot() {
        return EntityMovement.idle(coordinates, Direction.RIGHT);
    }
}
