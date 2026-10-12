package ru.mipt.bit.platformer.game.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.model.Tank;
import com.badlogic.gdx.math.GridPoint2;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardHandlerTest {
    @Test
    void executesArbitraryActionForAnyBoundKey() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> key == 2);
        handler.bind(() -> calls.add("action"), 1, 2);

        handler.handle();

        assertEquals(Arrays.asList("action"), calls);
    }

    @Test
    void ignoresUnboundKeys() {
        KeyboardHandler handler = new KeyboardHandler(key -> key == 3);
        handler.bind(() -> fail("Unpressed binding was executed"), 1, 2);
        handler.handle();
    }

    @Test
    void handlesOnlyFirstPressedBindingOnce() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        handler.bind(() -> calls.add("first"), 1, 2);
        handler.bind(() -> calls.add("second"), 3);

        handler.handle();

        assertEquals(Arrays.asList("first"), calls);
    }

    @Test
    void blockedMovementDoesNotFallThroughToAnotherAction() {
        Tank tank = new Tank(new GridPoint2(1, 1), tile -> true, 0.4f);
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        handler.bind(new MoveAction(tank, Direction.UP), 1);
        handler.bind(() -> fail("A lower-priority action was executed"), 2);

        handler.handle();

        assertFalse(tank.isMoving());
        assertEquals(90f, tank.movementSnapshot().rotation());
    }

    @Test
    void copiesKeyBindings() {
        int[] keys = {1};
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> key == 1);
        handler.bind(() -> calls.add("action"), keys);
        keys[0] = 2;

        handler.handle();

        assertEquals(Arrays.asList("action"), calls);
    }

    @Test
    void rejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new KeyboardHandler(null));
        KeyboardHandler handler = new KeyboardHandler(key -> false);
        assertThrows(NullPointerException.class, () -> handler.bind(null, 1));
        assertThrows(NullPointerException.class, () -> handler.bind(() -> {}, (int[]) null));
    }
}
