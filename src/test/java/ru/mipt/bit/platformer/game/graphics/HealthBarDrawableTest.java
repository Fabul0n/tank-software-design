package ru.mipt.bit.platformer.game.graphics;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.model.Tank;
import static org.junit.jupiter.api.Assertions.*;

class HealthBarDrawableTest {
    @Test
    void delegatesDrawingWhileHiddenAndDisposesWrappedDrawable() {
        RecordingBatch recording = new RecordingBatch();
        RecordingDrawable wrapped = new RecordingDrawable();
        HealthBarDisplay display = new HealthBarDisplay();
        Drawable decorated = decorate(wrapped, tank(), display);
        decorated.draw(recording.batch);
        assertEquals(1, wrapped.draws);
        assertTrue(recording.rectangles.isEmpty());
        decorated.dispose();
        assertEquals(1, wrapped.disposals);
    }

    @Test
    void followsMovementAndDamageAndRestoresBatchColor() {
        RecordingBatch recording = new RecordingBatch();
        RecordingDrawable wrapped = new RecordingDrawable();
        HealthBarDisplay display = new HealthBarDisplay();
        display.toggle();
        Tank tank = tank();
        Drawable decorated = decorate(wrapped, tank, display);
        tank.move(Direction.RIGHT);
        tank.update(0.2f);
        tank.hit(25);
        decorated.draw(recording.batch);

        assertEquals(1, wrapped.draws);
        assertEquals(2, recording.rectangles.size());
        assertArrayEquals(new float[] {160f, 190f, 80f, 6f}, recording.rectangles.get(0), 0.001f);
        assertArrayEquals(new float[] {160f, 190f, 60f, 6f}, recording.rectangles.get(1), 0.001f);
        assertEquals(Color.BLUE.toFloatBits(), recording.color);
    }

    @Test
    void oneToggleControlsExistingAndNewDecorators() {
        HealthBarDisplay display = new HealthBarDisplay();
        Drawable first = decorate(new RecordingDrawable(), tank(), display);
        display.toggle();
        Drawable second = decorate(new RecordingDrawable(), tank(), display);
        RecordingBatch recording = new RecordingBatch();
        first.draw(recording.batch);
        second.draw(recording.batch);
        assertEquals(4, recording.rectangles.size());
        display.toggle();
        first.draw(recording.batch);
        second.draw(recording.batch);
        assertEquals(4, recording.rectangles.size());
    }

    private Tank tank() {
        return new Tank(new GridPoint2(1, 1), new GameField(10, 10), 0.4f);
    }

    private Drawable decorate(Drawable wrapped, Tank tank, HealthBarDisplay display) {
        return new HealthBarDrawable(wrapped, tank, display, new TextureRegion(), 100);
    }

    private static final class RecordingDrawable implements Drawable {
        private int draws;
        private int disposals;

        @Override
        public void draw(Batch batch) { draws++; }

        @Override
        public void dispose() { disposals++; }
    }

    private static final class RecordingBatch {
        private final List<float[]> rectangles = new ArrayList<>();
        private float color = Color.BLUE.toFloatBits();
        private final Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[] {Batch.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getPackedColor":
                            return color;
                        case "setPackedColor":
                            color = (float) args[0];
                            break;
                        case "setColor":
                            color = new Color((float) args[0], (float) args[1],
                                    (float) args[2], (float) args[3]).toFloatBits();
                            break;
                        case "draw":
                            rectangles.add(new float[] {(float) args[1], (float) args[2],
                                    (float) args[3], (float) args[4]});
                            break;
                    }
                    return null;
                });
    }
}
