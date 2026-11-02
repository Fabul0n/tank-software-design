package ru.mipt.bit.platformer.game.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static ru.mipt.bit.platformer.game.TestWorld.detecting;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.model.Tank;
import com.badlogic.gdx.math.GridPoint2;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardHandlerTest {
    @Test
    void executesArbitraryActionForAnyBoundKey() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> key == 2);
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        handlerGroup.bind(() -> calls.add("action"), 1, 2);

        handler.handle(0.1f);

        assertEquals(Arrays.asList("action"), calls);
    }

    @Test
    void ignoresUnboundKeys() {
        KeyboardHandler handler = new KeyboardHandler(key -> key == 3);
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        handlerGroup.bind(() -> fail("Unpressed binding was executed"), 1, 2);
        handler.handle(0.1f);
    }

    @Test
    void handlesOnlyFirstPressedBindingInGroupOnce() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        handlerGroup.bind(() -> calls.add("first"), 1, 2);
        handlerGroup.bind(() -> calls.add("second"), 3);

        handler.handle(0.1f);

        assertEquals(Arrays.asList("first"), calls);
    }

    @Test
    void blockedMovementDoesNotFallThroughToAnotherAction() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(tile -> true), 0.4f);
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        handlerGroup.bind(new MoveAction(tank, Direction.UP), 1);
        handlerGroup.bind(() -> fail("A lower-priority action was executed"), 2);

        handler.handle(0.1f);

        assertFalse(tank.isMoving());
        assertEquals(90f, tank.movementSnapshot().rotation());
    }

    @Test
    void executesIndependentGroupsWithTheirOwnPriorities() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        KeyboardHandler.ActionGroup movement = handler.createGroup();
        KeyboardHandler.ActionGroup otherActions = handler.createGroup();
        movement.bind(() -> calls.add("up"), 1, 2);
        movement.bind(() -> calls.add("down"), 3);
        otherActions.bind(() -> calls.add("primary"), 4, 5);
        otherActions.bind(() -> calls.add("secondary"), 6);

        handler.handle(0.1f);

        assertEquals(Arrays.asList("up", "primary"), calls);
    }

    @Test
    void blockedMovementDoesNotPreventIndependentAction() {
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(tile -> true), 0.4f);
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        KeyboardHandler.ActionGroup movement = handler.createGroup();
        movement.bind(new MoveAction(tank, Direction.UP), 1);
        movement.bind(() -> fail("A lower-priority movement was executed"), 2);
        handler.createGroup().bind(() -> calls.add("independent"), 3);

        handler.handle(0.1f);

        assertFalse(tank.isMoving());
        assertEquals(Arrays.asList("independent"), calls);
    }

    @Test
    void emptyAndInactiveGroupsDoNotPreventOtherGroups() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> key == 2);
        handler.createGroup();
        handler.createGroup().bind(() -> fail("Unpressed action was executed"), 1);
        handler.createGroup().bind(() -> calls.add("active"), 2);

        handler.handle(0.1f);

        assertEquals(Arrays.asList("active"), calls);
    }

    @Test
    void copiesKeyBindings() {
        int[] keys = {1};
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> key == 1);
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        handlerGroup.bind(() -> calls.add("action"), keys);
        keys[0] = 2;

        handler.handle(0.1f);

        assertEquals(Arrays.asList("action"), calls);
    }

    @Test
    void followsLatestHeldKeyAndRestoresPreviousKeyOnRelease() {
        boolean[] pressed = new boolean[3];
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> pressed[key]);
        KeyboardHandler.ActionGroup movement = handler.createGroup(KeyboardHandler.Priority.LAST_PRESSED);
        movement.bind(() -> calls.add("up"), 1);
        movement.bind(() -> calls.add("right"), 2);

        pressed[1] = true;
        handler.handle(0.1f);
        pressed[2] = true;
        handler.handle(0.1f);
        handler.handle(0.1f);
        pressed[2] = false;
        handler.handle(0.1f);
        pressed[2] = true;
        handler.handle(0.1f);
        pressed[1] = false;
        pressed[2] = false;
        handler.handle(0.1f);

        assertEquals(Arrays.asList("up", "right", "right", "up", "right"), calls);
    }

    @Test
    void tracksAlternativeKeysIndividually() {
        boolean[] pressed = new boolean[4];
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> pressed[key]);
        KeyboardHandler.ActionGroup movement = handler.createGroup(KeyboardHandler.Priority.LAST_PRESSED);
        movement.bind(() -> calls.add("up"), 1, 2);
        movement.bind(() -> calls.add("right"), 3);

        pressed[1] = true;
        handler.handle(0.1f);
        pressed[3] = true;
        handler.handle(0.1f);
        pressed[2] = true;
        handler.handle(0.1f);
        pressed[2] = false;
        handler.handle(0.1f);

        assertEquals(Arrays.asList("up", "right", "up", "right"), calls);
    }

    @Test
    void simultaneousNewKeysUseBindingOrderAndLeaveOtherGroupsIndependent() {
        List<String> calls = new ArrayList<>();
        KeyboardHandler handler = new KeyboardHandler(key -> true);
        KeyboardHandler.ActionGroup movement = handler.createGroup(KeyboardHandler.Priority.LAST_PRESSED);
        movement.bind(() -> calls.add("up"), 1, 2);
        movement.bind(() -> calls.add("right"), 3);
        handler.createGroup().bind(() -> calls.add("other"), 4);

        handler.handle(0.1f);
        handler.handle(0.1f);

        assertEquals(Arrays.asList("up", "other", "up", "other"), calls);
    }

    @Test
    void remembersNewDirectionWhileTankCompletesCurrentMove() {
        boolean[] pressed = new boolean[3];
        Tank tank = new Tank(new GridPoint2(1, 1), detecting(tile -> false), 0.4f);
        KeyboardHandler handler = new KeyboardHandler(key -> pressed[key]);
        KeyboardHandler.ActionGroup movement = handler.createGroup(KeyboardHandler.Priority.LAST_PRESSED);
        movement.bind(new MoveAction(tank, Direction.UP), 1);
        movement.bind(new MoveAction(tank, Direction.RIGHT), 2);

        pressed[1] = true;
        handler.handle(0.1f);
        pressed[2] = true;
        handler.handle(0.1f);
        assertEquals(90f, tank.movementSnapshot().rotation());
        tank.update(0.4f);
        handler.handle(0.1f);

        assertTrue(tank.occupies(new GridPoint2(1, 2)));
        assertTrue(tank.occupies(new GridPoint2(2, 2)));
        assertEquals(0f, tank.movementSnapshot().rotation());
    }

    @Test
    void rejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new KeyboardHandler(null));
        KeyboardHandler handler = new KeyboardHandler(key -> false);
        assertThrows(NullPointerException.class, () -> handler.createGroup(null));
        KeyboardHandler.ActionGroup handlerGroup = handler.createGroup();
        assertThrows(NullPointerException.class, () -> handlerGroup.bind(null, 1));
        assertThrows(NullPointerException.class, () -> handlerGroup.bind(() -> {}, (int[]) null));
    }
}
