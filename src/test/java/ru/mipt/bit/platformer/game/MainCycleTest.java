package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.graphics.Drawable;
import ru.mipt.bit.platformer.game.control.ControlStrategy;
import ru.mipt.bit.platformer.game.control.RandomController;
import java.util.Random;
import ru.mipt.bit.platformer.game.input.KeyboardHandler;
import ru.mipt.bit.platformer.game.input.MoveAction;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import static org.junit.jupiter.api.Assertions.*;

class MainCycleTest {
    @Test
    void playerAndAiUseSeparateStrategiesWithoutCollisions() {
        GameField field = new GameField(6, 6);
        Tank player = new Tank(new GridPoint2(0, 0), field, 0.4f);
        Tank firstAi = new Tank(new GridPoint2(2, 2), field, 0.4f);
        Tank secondAi = new Tank(new GridPoint2(4, 4), field, 0.4f);
        List<Tank> tanks = Arrays.asList(player, firstAi, secondAi);
        for (Tank tank : tanks) {
            field.spawn(tank);
        }
        GridPoint2 treeTile = new GridPoint2(3, 3);
        field.spawn(new Tree(treeTile));
        boolean[] pressed = {true};
        KeyboardHandler keyboard = new KeyboardHandler(key -> pressed[0]);
        keyboard.bind(new MoveAction(player, Direction.UP), 1);
        List<ControlStrategy> controllers = new ArrayList<>();
        controllers.add(keyboard);
        controllers.add(new RandomController(firstAi, new Random(42), 0.4f));
        controllers.add(new RandomController(secondAi, new Random(24), 0.4f));
        Drawable graphics = new Drawable() {
            @Override
            public void draw(Batch batch) {
                for (int y = -1; y <= 6; y++) {
                    for (int x = -1; x <= 6; x++) {
                        GridPoint2 tile = new GridPoint2(x, y);
                        int occupants = 0;
                        for (Tank tank : tanks) {
                            if (tank.occupies(tile)) {
                                occupants++;
                                assertTrue(x >= 0 && y >= 0 && x < 6 && y < 6);
                                assertNotEquals(treeTile, tile);
                            }
                        }
                        assertTrue(occupants <= 1, "Overlapping tanks at " + tile);
                    }
                }
            }

            @Override
            public void dispose() {
            }
        };
        MainCycle cycle = new MainCycle(controllers, field, graphics);
        cycle.render(0.4f, null);
        assertTrue(player.occupies(new GridPoint2(0, 1)));
        pressed[0] = false;
        boolean aiLeftStart = false;
        for (int frame = 0; frame < 500; frame++) {
            cycle.render(0.1f, null);
            assertTrue(player.occupies(new GridPoint2(0, 1)));
            assertFalse(player.isMoving());
            aiLeftStart |= !firstAi.occupies(new GridPoint2(2, 2));
        }
        assertTrue(aiLeftStart);
    }

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
        List<ControlStrategy> controllers = new ArrayList<>();
        controllers.add(keyboardHandler);
        controllers.add(deltaTime -> calls.add("ai:" + deltaTime));
        MainCycle cycle = new MainCycle(controllers, field, graphics);
        controllers.clear();

        cycle.render(0.25f, null);
        cycle.dispose();

        assertEquals(Arrays.asList("input", "ai:0.25", "update:0.25", "draw", "dispose"), calls);
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
        MainCycle cycle = new MainCycle(Arrays.asList(handler), field, graphics);

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
        assertThrows(NullPointerException.class, () -> new MainCycle(Arrays.asList(handler, null), field, graphics));
        assertThrows(NullPointerException.class, () -> new MainCycle(Arrays.asList(handler), null, graphics));
        assertThrows(NullPointerException.class, () -> new MainCycle(Arrays.asList(handler), field, null));
    }
}
