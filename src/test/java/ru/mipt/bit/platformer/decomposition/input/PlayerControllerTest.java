package ru.mipt.bit.platformer.decomposition.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlayerControllerTest {
    @Test
    void rejectsNullKeyState() {
        assertThrows(NullPointerException.class, () -> new PlayerController(null));
    }

    @Test
    void rejectsNullHandler() {
        PlayerController controller = new PlayerController(keyCode -> false);

        assertThrows(NullPointerException.class, () -> controller.addButtonHandler(null));
    }

    @Test
    void delegatesInputToEveryRegisteredHandler() {
        PlayerController controller = new PlayerController(keyCode -> false);
        CountingButtonHandler movementHandler = new CountingButtonHandler();
        CountingButtonHandler shootingHandler = new CountingButtonHandler();

        controller.addButtonHandler(movementHandler);
        controller.addButtonHandler(shootingHandler);
        controller.update();

        assertEquals(1, movementHandler.calls);
        assertEquals(1, shootingHandler.calls);
    }

    @Test
    void passesConfiguredKeyStateToHandlers() {
        KeyState keyState = keyCode -> keyCode == 1;
        PlayerController controller = new PlayerController(keyState);
        CapturingButtonHandler handler = new CapturingButtonHandler();

        controller.addButtonHandler(handler);
        controller.update();

        assertSame(keyState, handler.keyState);
    }

    private static class CountingButtonHandler implements ButtonHandler {
        private int calls;

        @Override
        public void handle(KeyState keyState) {
            calls++;
        }
    }

    private static class CapturingButtonHandler implements ButtonHandler {
        private KeyState keyState;

        @Override
        public void handle(KeyState keyState) {
            this.keyState = keyState;
        }
    }
}
