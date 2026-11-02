package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.control.ControlStrategy;
import ru.mipt.bit.platformer.game.control.RandomController;
import ru.mipt.bit.platformer.game.graphics.Drawable;
import ru.mipt.bit.platformer.game.input.CommandQueue;
import ru.mipt.bit.platformer.game.input.KeyboardHandler;
import ru.mipt.bit.platformer.game.input.MoveAction;
import ru.mipt.bit.platformer.game.input.ShootAction;
import ru.mipt.bit.platformer.game.model.Tank;
import static org.junit.jupiter.api.Assertions.*;

class CommandFlowTest {
    @Test
    void selectsAllCommandsBeforeExecutionThenUpdatesAndDraws() {
        List<String> calls = new ArrayList<>();
        CommandQueue commands = new CommandQueue();
        GameField field = new GameField(3, 3) {
            @Override
            public void update(float deltaTime) { calls.add("update"); }
        };
        List<ControlStrategy> controls = Arrays.asList(
                dt -> {
                    calls.add("select1");
                    commands.accept(() -> calls.add("execute1"));
                },
                dt -> {
                    calls.add("select2");
                    commands.accept(() -> calls.add("execute2"));
                });
        Drawable graphics = new Drawable() {
            public void draw(Batch batch) { calls.add("draw"); }
            public void dispose() {}
        };
        new MainCycle(controls, commands, field, graphics).render(0.1f, null);
        commands.execute();
        assertEquals(Arrays.asList("select1", "select2", "execute1", "execute2", "update", "draw"), calls);
    }

    @Test
    void movementAndFireAreQueuedIndependentlyInSameFrame() {
        CommandQueue commands = new CommandQueue();
        List<String> calls = new ArrayList<>();
        KeyboardHandler keyboard = new KeyboardHandler(key -> true, commands);
        keyboard.createGroup().bind(() -> calls.add("move"), 1);
        keyboard.createGroup().bind(new ShootAction(() -> calls.add("shoot")), 2);
        keyboard.handle(0.1f);
        assertTrue(calls.isEmpty());
        commands.execute();
        assertEquals(Arrays.asList("move", "shoot"), calls);
    }

    @Test
    void queuedMovesRecheckOccupancyWhenExecuted() {
        GameField field = new GameField(3, 1);
        Tank first = new Tank(new GridPoint2(0, 0), field, 0.4f);
        Tank second = new Tank(new GridPoint2(2, 0), field, 0.4f);
        field.spawn(first);
        field.spawn(second);
        assertTrue(first.canMove(Direction.RIGHT));
        assertTrue(second.canMove(Direction.LEFT));
        CommandQueue commands = new CommandQueue();
        commands.accept(new MoveAction(first, Direction.RIGHT));
        commands.accept(new MoveAction(second, Direction.LEFT));
        commands.execute();
        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    void randomControllerQueuesShotForStationaryOutcome() {
        GameField field = new GameField(3, 3);
        Tank tank = new Tank(new GridPoint2(1, 1), field, 0.4f);
        field.spawn(tank);
        List<String> calls = new ArrayList<>();
        CommandQueue commands = new CommandQueue();
        Random random = new Random() {
            @Override
            public int nextInt(int bound) { return bound - 1; }
        };
        RandomController controller = new RandomController(tank,
                new ShootAction(() -> calls.add("shoot")), commands, random, 0.4f);
        controller.handle(0f);
        assertTrue(calls.isEmpty());
        commands.execute();
        assertEquals(List.of("shoot"), calls);
        controller.handle(0.2f);
        commands.execute();
        assertEquals(1, calls.size());
        assertFalse(tank.isMoving());
    }

    @Test
    void surroundedTankCanStillShoot() {
        GameField field = new GameField(1, 1);
        Tank tank = new Tank(new GridPoint2(), field, 0.4f);
        field.spawn(tank);
        List<String> calls = new ArrayList<>();
        CommandQueue commands = new CommandQueue();
        new RandomController(tank, () -> calls.add("shoot"), commands, new Random(1), 0.4f).handle(0f);
        commands.execute();
        assertEquals(List.of("shoot"), calls);
    }
}
