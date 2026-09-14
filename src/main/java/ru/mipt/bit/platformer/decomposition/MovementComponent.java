package ru.mipt.bit.platformer.decomposition;

import java.util.function.Predicate;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.util.TileMovement;

public class MovementComponent implements Component {
    private static final float MOVE_TIME_PER_TITLE = 0.4f;

    private final Entity entity;
    private final TileMovement tileMovement;
    private final Predicate<GridPoint2> canMoveTo;

    private final GridPoint2 destination;
    private float progress = 1f;

    public MovementComponent(Entity entity, TileMovement tileMovement, Predicate<GridPoint2> canMoveTo) {
        this.entity = entity;
        this.tileMovement = tileMovement;
        this.canMoveTo = canMoveTo;
        this.destination = new GridPoint2(entity.getCoordinates());
    }

    public boolean requestMove(int dx, int dy, float rotation) {
        if (progress < 1f)
            return false;

        entity.setRotation(rotation);

        GridPoint2 target = new GridPoint2(entity.getCoordinates()).add(dx, dy);
        if (!canMoveTo.test(target)) {
            entity.setRotation(rotation);
            return false;
        }

        destination.set(target);
        progress = 0f;
        return true;
    }

    @Override
    public void update(float deltaTime) {
        tileMovement.moveRectangleBetweenTileCenters(
                entity.getBoundingRectangle(),
                entity.getCoordinates(),
                destination,
                progress);

        progress = Math.min(1f, progress + deltaTime / MOVE_TIME_PER_TITLE);

        if (progress >= 1) {
            entity.getCoordinates().set(destination);
        }
    }

    public boolean isMoving() {
        return progress < 1f;
    }

}
