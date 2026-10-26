package ru.mipt.bit.platformer.game.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.Movable;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import static org.junit.jupiter.api.Assertions.*;

class RandomControllerTest {
    @Test
    void splitsHalfOfProbabilityEquallyBetweenAvailableDirections() {
        List<Direction> directions = Arrays.asList(Direction.values());
        for (int count = 1; count <= 4; count++) {
            List<Direction> allowed = directions.subList(4 - count, 4);
            int standingOutcomes = 0;
            for (int outcome = 0; outcome < count * 2; outcome++) {
                RecordingMovable movable = new RecordingMovable();
                movable.available = EnumSet.copyOf(allowed);
                SequenceRandom random = new SequenceRandom(outcome);
                ControlStrategy controller = new RandomController(movable, random, 0.4f);

                controller.handle(0f);

                assertEquals(Arrays.asList(count * 2), random.bounds);
                if (outcome < count) {
                    assertEquals(Arrays.asList(allowed.get(outcome)), movable.moves);
                } else {
                    assertTrue(movable.moves.isEmpty());
                    standingOutcomes++;
                }
            }
            assertEquals(count, standingOutcomes);
        }
    }

    @Test
    void excludesTreesAndCellsReservedByAnotherTank() {
        for (int outcome = 0; outcome < 2; outcome++) {
            GameField field = new GameField(3, 3);
            Tank tank = new Tank(new GridPoint2(1, 1), field, 0.4f);
            Tank other = new Tank(new GridPoint2(0, 0), field, 0.4f);
            field.spawn(tank);
            field.spawn(other);
            field.spawn(new Tree(new GridPoint2(1, 2)));
            assertTrue(other.move(Direction.UP));
            SequenceRandom random = new SequenceRandom(outcome);
            RandomController controller = new RandomController(tank, random, 0.4f);

            controller.handle(0f);

            assertEquals(Arrays.asList(4), random.bounds);
            assertTrue(tank.isMoving());
            assertFalse(tank.occupies(new GridPoint2(0, 1)));
            assertFalse(tank.occupies(new GridPoint2(1, 2)));
            GridPoint2 target = outcome == 0 ? new GridPoint2(1, 0) : new GridPoint2(2, 1);
            assertTrue(tank.occupies(target));
        }
    }

    @Test
    void excludesDirectionsOutsideField() {
        for (int outcome = 0; outcome < 2; outcome++) {
            GameField field = new GameField(2, 2);
            Tank tank = new Tank(new GridPoint2(0, 0), field, 0.4f);
            field.spawn(tank);
            SequenceRandom random = new SequenceRandom(outcome);

            new RandomController(tank, random, 0.4f).handle(0f);

            assertEquals(Arrays.asList(4), random.bounds);
            assertTrue(tank.occupies(outcome == 0 ? new GridPoint2(0, 1) : new GridPoint2(1, 0)));
        }
    }

    @Test
    void boxedInTankStandsWithoutChoosingOrTurning() {
        Tank tank = new Tank(new GridPoint2(1, 1), tile -> true, 0.4f);
        SequenceRandom random = new SequenceRandom();
        RandomController controller = new RandomController(tank, random, 0.4f);

        controller.handle(0f);
        controller.handle(0.4f);

        assertEquals(0, random.calls);
        assertFalse(tank.isMoving());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertEquals(0f, tank.movementSnapshot().rotation());
    }

    @Test
    void refreshesAvailableDirectionsWhenNextDecisionIsDue() {
        RecordingMovable movable = new RecordingMovable();
        movable.available.clear();
        SequenceRandom random = new SequenceRandom(0, 0);
        RandomController controller = new RandomController(movable, random, 0.4f);
        controller.handle(0f);
        movable.available.add(Direction.LEFT);
        controller.handle(0.2f);
        assertTrue(movable.moves.isEmpty());
        controller.handle(0.2f);
        assertEquals(Arrays.asList(Direction.LEFT), movable.moves);

        movable.available = EnumSet.of(Direction.DOWN);
        controller.handle(0.4f);
        assertEquals(Arrays.asList(Direction.LEFT, Direction.DOWN), movable.moves);
        assertEquals(Arrays.asList(2, 2), random.bounds);
    }

    @Test
    void standingStillLastsForWholeDecisionInterval() {
        RecordingMovable movable = new RecordingMovable();
        SequenceRandom random = new SequenceRandom(7, 3);
        RandomController controller = new RandomController(movable, random, 0.4f);
        controller.handle(0f);
        for (int i = 0; i < 3; i++) {
            controller.handle(0.1f);
            assertTrue(movable.moves.isEmpty());
            assertEquals(1, random.calls);
        }
        controller.handle(0.1f);
        assertEquals(Arrays.asList(Direction.RIGHT), movable.moves);
        assertEquals(2, random.calls);
    }

    @Test
    void pauseDurationDoesNotDependOnFrameRate() {
        for (int fps : new int[] {30, 60, 120}) {
            RecordingMovable movable = new RecordingMovable();
            SequenceRandom random = new SequenceRandom(7, 0);
            RandomController controller = new RandomController(movable, random, 0.4f);
            controller.handle(0f);
            int frames = fps * 2 / 5;
            for (int frame = 1; frame < frames; frame++) {
                controller.handle(1f / fps);
                assertEquals(1, random.calls);
            }
            controller.handle(1f / fps);
            assertEquals(2, random.calls);
            assertEquals(Arrays.asList(Direction.UP), movable.moves);
        }
    }

    @Test
    void consecutiveStandingDecisionsEachHaveTheirOwnPause() {
        RecordingMovable movable = new RecordingMovable();
        SequenceRandom random = new SequenceRandom(4, 7, 2);
        RandomController controller = new RandomController(movable, random, 0.4f);
        controller.handle(0f);
        controller.handle(0.4f);
        controller.handle(0.2f);
        assertTrue(movable.moves.isEmpty());
        assertEquals(2, random.calls);
        controller.handle(0.2f);
        assertEquals(Arrays.asList(Direction.LEFT), movable.moves);
    }

    @Test
    void usesConfiguredIntervalAndDoesNotAdvanceWithoutElapsedTime() {
        RecordingMovable movable = new RecordingMovable();
        SequenceRandom random = new SequenceRandom(7, 1);
        RandomController controller = new RandomController(movable, random, 2f);
        controller.handle(0f);
        for (int i = 0; i < 100; i++) {
            controller.handle(0f);
        }
        controller.handle(1f);
        assertEquals(1, random.calls);
        controller.handle(1f);
        assertEquals(Arrays.asList(Direction.DOWN), movable.moves);
    }

    @Test
    void doesNotChooseOrExecuteCommandsWhileMoving() {
        RecordingMovable movable = new RecordingMovable();
        movable.moving = true;
        SequenceRandom random = new SequenceRandom(0);
        RandomController controller = new RandomController(movable, random, 0.4f);
        controller.handle(0.4f);
        assertEquals(0, random.calls);
        assertTrue(movable.moves.isEmpty());
        movable.moving = false;
        controller.handle(0f);
        assertEquals(Arrays.asList(Direction.UP), movable.moves);
    }

    @Test
    void standingStillPreservesOrientationAndPosition() {
        Tank tank = new Tank(new GridPoint2(1, 1), tile -> false, 0.4f);
        new RandomController(tank, new SequenceRandom(7), 0.4f).handle(0f);
        assertFalse(tank.isMoving());
        assertTrue(tank.occupies(new GridPoint2(1, 1)));
        assertEquals(0f, tank.movementSnapshot().rotation());
    }

    @Test
    void producesRepeatableCommandsWithSameSeed() {
        RecordingMovable first = new RecordingMovable();
        RecordingMovable second = new RecordingMovable();
        RandomController firstController = new RandomController(first, new Random(42), 0.4f);
        RandomController secondController = new RandomController(second, new Random(42), 0.4f);
        for (int i = 0; i < 100; i++) {
            firstController.handle(0.4f);
            secondController.handle(0.4f);
        }
        assertFalse(first.moves.isEmpty());
        assertEquals(first.moves, second.moves);
    }

    @Test
    void rejectsInvalidIntervalsAndElapsedTime() {
        for (float interval : new float[] {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RandomController(new RecordingMovable(), new Random(), interval));
        }
        RandomController controller = new RandomController(new RecordingMovable(), new Random(), 0.4f);
        for (float delta : new float[] {-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> controller.handle(delta));
        }
    }

    @Test
    void rejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new RandomController(null, new Random(), 0.4f));
        assertThrows(NullPointerException.class, () -> new RandomController(new RecordingMovable(), null, 0.4f));
    }

    private static class SequenceRandom extends Random {
        private final int[] outcomes;
        private final List<Integer> bounds = new ArrayList<>();
        private int calls;

        private SequenceRandom(int... outcomes) {
            this.outcomes = outcomes;
        }

        @Override
        public int nextInt(int bound) {
            bounds.add(bound);
            int outcome = outcomes[calls++];
            assertTrue(outcome >= 0 && outcome < bound);
            return outcome;
        }
    }

    private static class RecordingMovable implements Movable {
        private final List<Direction> moves = new ArrayList<>();
        private Set<Direction> available = EnumSet.allOf(Direction.class);
        private boolean moving;

        @Override
        public boolean move(Direction direction) {
            assertTrue(canMove(direction));
            moves.add(direction);
            return true;
        }

        @Override
        public boolean canMove(Direction direction) {
            return !moving && available.contains(direction);
        }

        @Override
        public boolean isMoving() {
            return moving;
        }

        @Override
        public boolean occupies(GridPoint2 tile) {
            return false;
        }

        @Override
        public void update(float deltaTime) {
        }
    }
}
