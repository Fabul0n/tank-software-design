package ru.mipt.bit.platformer.game.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.model.EntityMovement;

class GdxGameUtilsTest {
    @Test
    void centersBoundsUsingTileSizeAndInterpolatedPosition() {
        TiledMapTileLayer layer = new TiledMapTileLayer(10, 8, 128, 64);
        Rectangle bounds = new Rectangle(0, 0, 40, 20);
        EntityMovement movement = new EntityMovement(new GridPoint2(1, 2), new GridPoint2(2, 2), Direction.RIGHT, 0.25f);

        GdxGameUtils.positionAtMovement(bounds, movement, layer, Interpolation.smooth);

        assertEquals(192f, bounds.x);
        assertEquals(150f, bounds.y);
        assertEquals(40f, bounds.width);
        assertEquals(20f, bounds.height);
    }
}
