package ru.mipt.bit.platformer.level;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import static ru.mipt.bit.platformer.level.LevelLayout.Cell.*;
import static org.junit.jupiter.api.Assertions.*;

class RandomTankSpawnerTest {
    @TempDir
    Path directory;

    @Test
    void spawnsRequestedCountWithoutOverlapsForBothLevelSources() throws Exception {
        Path file = directory.resolve("level.txt");
        Files.write(file, "X_T_\n_T__\n____".getBytes(StandardCharsets.UTF_8));
        LevelSource[] sources = {new FileLevelSource(file), new RandomLevelSource(4, 3, 2, new Random(5))};
        for (LevelSource source : sources) {
            LevelLayout level = source.load();
            GameField field = populatedField(level);
            List<Tank> tanks = new RandomTankSpawner(new Random(7), 0.4f).spawn(field, level, 5);
            Set<GridPoint2> positions = new HashSet<>();
            assertEquals(5, tanks.size());
            for (Tank tank : tanks) {
                GridPoint2 position = positionOf(tank, level);
                assertEquals(EMPTY, level.cellAt(position.x, position.y));
                assertTrue(positions.add(position));
                assertTrue(field.isTileOccupied(position));
                assertFalse(tank.isMoving());
            }
        }
    }

    @Test
    void fillsLastFreeCellAndAllowsZeroTanks() {
        LevelLayout level = new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, EMPTY}});
        GameField field = populatedField(level);
        RandomTankSpawner spawner = new RandomTankSpawner(new Random(1), 0.4f);
        assertTrue(spawner.spawn(field, level, 0).isEmpty());
        List<Tank> tanks = spawner.spawn(field, level, 1);
        assertEquals(1, tanks.size());
        assertTrue(tanks.get(0).occupies(new GridPoint2(1, 0)));
        assertTrue(spawner.spawn(field, level, 0).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(field, level, 1));
    }

    @Test
    void refusesInsufficientSpaceWithoutPartiallySpawningTanks() {
        LevelLayout level = new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, EMPTY, EMPTY}});
        GameField field = populatedField(level);
        RandomTankSpawner spawner = new RandomTankSpawner(new Random(1), 0.4f);
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(field, level, 3));
        assertFalse(field.isTileOccupied(new GridPoint2(1, 0)));
        assertFalse(field.isTileOccupied(new GridPoint2(2, 0)));
    }

    @Test
    void excludesAlreadySpawnedTanksAndTheirReservedDestination() {
        LevelLayout level = new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, EMPTY, EMPTY}});
        GameField field = new GameField(3, 1);
        Tank player = new Tank(new GridPoint2(0, 0), field, 0.4f);
        field.spawn(player);
        assertTrue(player.move(Direction.RIGHT));

        List<Tank> tanks = new RandomTankSpawner(new Random(1), 0.4f).spawn(field, level, 1);

        assertTrue(tanks.get(0).occupies(new GridPoint2(2, 0)));
    }

    @Test
    void usesConfiguredMoveDurationAndRegistersTanksForUpdates() {
        LevelLayout level = new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, EMPTY}, {EMPTY, EMPTY}});
        GameField field = populatedField(level);
        Tank tank = new RandomTankSpawner(new Random(1), 2f).spawn(field, level, 1).get(0);
        Direction freeDirection = null;
        GridPoint2 start = positionOf(tank, level);
        for (Direction direction : Direction.values()) {
            if (!field.isTileOccupied(direction.nextTile(start))) {
                freeDirection = direction;
                break;
            }
        }
        assertNotNull(freeDirection);
        assertTrue(tank.move(freeDirection));
        field.update(1f);
        assertTrue(tank.isMoving());
        field.update(1f);
        assertFalse(tank.isMoving());
        assertFalse(tank.occupies(start));
        assertTrue(tank.occupies(freeDirection.nextTile(start)));
    }

    @Test
    void sameSeedReproducesPositionsAndDifferentSeedsVaryThem() {
        LevelLayout level = new RandomLevelSource(8, 6, 5, new Random(1)).load();
        List<Tank> first = new RandomTankSpawner(new Random(42), 0.4f).spawn(populatedField(level), level, 4);
        List<Tank> second = new RandomTankSpawner(new Random(42), 0.4f).spawn(populatedField(level), level, 4);
        for (int i = 0; i < first.size(); i++) {
            assertEquals(positionOf(first.get(i), level), positionOf(second.get(i), level));
        }
        Set<GridPoint2> starts = new HashSet<>();
        for (int seed = 0; seed < 20; seed++) {
            List<Tank> tanks = new RandomTankSpawner(new Random(seed), 0.4f).spawn(populatedField(level), level, 1);
            starts.add(positionOf(tanks.get(0), level));
        }
        assertTrue(starts.size() > 1);
    }

    @Test
    void rejectsInvalidParameters() {
        LevelLayout level = new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, EMPTY}});
        GameField field = populatedField(level);
        RandomTankSpawner spawner = new RandomTankSpawner(new Random(1), 0.4f);
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(field, level, -1));
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(new GameField(3, 3), level, 1));
        assertThrows(NullPointerException.class, () -> spawner.spawn(null, level, 1));
        assertThrows(NullPointerException.class, () -> spawner.spawn(field, null, 1));
        assertThrows(NullPointerException.class, () -> new RandomTankSpawner(null, 0.4f));
        for (float duration : new float[] {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new RandomTankSpawner(new Random(), duration));
        }
    }

    private GameField populatedField(LevelLayout level) {
        GameField field = new GameField(level.widthInTiles(), level.heightInTiles());
        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                if (level.cellAt(x, y) == TREE) {
                    field.spawn(new Tree(new GridPoint2(x, y)));
                } else if (level.cellAt(x, y) == PLAYER) {
                    field.spawn(new Tank(new GridPoint2(x, y), field, 0.4f));
                }
            }
        }
        return field;
    }

    private GridPoint2 positionOf(Tank tank, LevelLayout level) {
        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (tank.occupies(tile)) {
                    return tile;
                }
            }
        }
        throw new AssertionError("Tank is outside the field");
    }
}
