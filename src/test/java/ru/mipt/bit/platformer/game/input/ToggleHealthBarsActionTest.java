package ru.mipt.bit.platformer.game.input;

import com.badlogic.gdx.Input.Keys;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.graphics.HealthBarDisplay;
import static org.junit.jupiter.api.Assertions.*;

class ToggleHealthBarsActionTest {
    @Test
    void togglesOnlyOnNewPressThroughCommandQueue() {
        HealthBarDisplay display = new HealthBarDisplay();
        CommandQueue commands = new CommandQueue();
        boolean[] pressed = {false};
        KeyboardHandler keyboard = new KeyboardHandler(key -> pressed[0], commands);
        keyboard.bindOnPress(new ToggleHealthBarsAction(display), Keys.L);
        keyboard.handle(0.1f);
        commands.execute();
        assertFalse(display.isVisible());

        pressed[0] = true;
        keyboard.handle(0.1f);
        assertFalse(display.isVisible());
        commands.execute();
        assertTrue(display.isVisible());
        for (int i = 0; i < 10; i++) {
            keyboard.handle(0.1f);
            commands.execute();
            assertTrue(display.isVisible());
        }
        pressed[0] = false;
        keyboard.handle(0.1f);
        commands.execute();
        assertTrue(display.isVisible());
        pressed[0] = true;
        keyboard.handle(0.1f);
        commands.execute();
        assertFalse(display.isVisible());
    }

    @Test
    void toggleDoesNotBlockHeldActions() {
        HealthBarDisplay display = new HealthBarDisplay();
        int[] moves = {0};
        int[] shots = {0};
        KeyboardHandler keyboard = new KeyboardHandler(key -> true);
        keyboard.bindOnPress(new ToggleHealthBarsAction(display), Keys.L);
        keyboard.createGroup().bind(() -> moves[0]++, Keys.W);
        keyboard.createGroup().bind(() -> shots[0]++, Keys.SPACE);
        keyboard.handle(0.1f);
        keyboard.handle(0.1f);
        assertTrue(display.isVisible());
        assertEquals(2, moves[0]);
        assertEquals(2, shots[0]);
    }
}
