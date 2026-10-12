package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.graphics.Drawable;
import ru.mipt.bit.platformer.game.input.KeyboardHandler;
import ru.mipt.bit.platformer.game.input.MoveAction;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import static org.junit.jupiter.api.Assertions.*;

class MainCycleTest {
    @Test
    void handlesInputBeforeUpdatingFieldAndDrawing() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler keyboardHandler = new KeyboardHandler(key -> true);
        keyboardHandler.bind(() -> calls.add("input"), 1);
        GameField field = new GameField(3, 3) {
            @Override
            public void update(float deltaTime) {
                calls.add("update:" + deltaTime);
                super.update(deltaTime);
            }
        };
        Drawable graphics = new Drawable() {
            @Override
            public void draw(Batch batch) {
                calls.add("draw");
            }

            @Override
            public void dispose() {
                calls.add("dispose");
            }
        };
        MainCycle cycle = new MainCycle(keyboardHandler, field, graphics);

        cycle.render(0.25f, null);
        cycle.dispose();

        assertEquals(Arrays.asList("input", "update:0.25", "draw", "dispose"), calls);
    }

    @Test
    void inputFieldAndRenderingWorkTogether() {
        GameField field = new GameField(3, 3);
        Tank tank = new Tank(new GridPoint2(0, 0), field, 0.4f);
        field.spawn(tank);
        field.spawn(new Tree(new GridPoint2(2, 0)));
        KeyboardHandler handler = new KeyboardHandler(key -> key == 1);
        handler.bind(new MoveAction(tank, Direction.RIGHT), 1);
        Drawable graphics = new Drawable() {
            @Override
            public void draw(Batch batch) {
                assertTrue(tank.occupies(new GridPoint2(1, 0)));
            }

            @Override
            public void dispose() {
            }
        };
        MainCycle cycle = new MainCycle(handler, field, graphics);

        cycle.render(0.4f, null);
        cycle.render(0.4f, null);

        assertFalse(tank.isMoving());
        assertFalse(tank.move(Direction.RIGHT));
    }

    @Test
    void rejectsNullDependencies() {
        KeyboardHandler handler = new KeyboardHandler(key -> false);
        GameField field = new GameField(3, 3);
        Drawable graphics = new Drawable() {
            @Override
            public void draw(Batch batch) {
            }

            @Override
            public void dispose() {
            }
        };
        assertThrows(NullPointerException.class, () -> new MainCycle(null, field, graphics));
        assertThrows(NullPointerException.class, () -> new MainCycle(handler, null, graphics));
        assertThrows(NullPointerException.class, () -> new MainCycle(handler, field, null));
    }
}
