package ru.mipt.bit.platformer.level;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static ru.mipt.bit.platformer.level.LevelLayout.Cell.*;

class RandomLevelSourceTest {
    @Test
    void placesExactNumberOfTreesAndOnePlayerWithoutOverlaps() {
        for (int seed = 0; seed < 100; seed++) {
            LevelLayout level = new RandomLevelSource(7, 3, 12, new Random(seed)).load();
            assertEquals(7, level.widthInTiles());
            assertEquals(3, level.heightInTiles());
            assertEquals(12, count(level, TREE));
            assertEquals(1, count(level, PLAYER));
            assertEquals(8, count(level, EMPTY));
        }
    }

    @Test
    void handlesEmptyAndNearlyFullFields() {
        LevelLayout empty = new RandomLevelSource(3, 2, 0, new Random(1)).load();
        LevelLayout full = new RandomLevelSource(3, 2, 5, new Random(1)).load();
        LevelLayout single = new RandomLevelSource(1, 1, 0, new Random(1)).load();
        assertEquals(5, count(empty, EMPTY));
        assertEquals(5, count(full, TREE));
        assertEquals(1, count(full, PLAYER));
        assertEquals(PLAYER, single.cellAt(0, 0));
    }

    @Test
    void reproducesLevelWithSameSeed() {
        LevelLayout first = new RandomLevelSource(10, 8, 15, new Random(42)).load();
        LevelLayout second = new RandomLevelSource(10, 8, 15, new Random(42)).load();
        for (int y = 0; y < first.heightInTiles(); y++) {
            for (int x = 0; x < first.widthInTiles(); x++) {
                assertEquals(first.cellAt(x, y), second.cellAt(x, y));
            }
        }
    }

    @Test
    void variesPlayerAndTreeLocations() {
        Set<String> playerLocations = new HashSet<>();
        Set<String> treeLayouts = new HashSet<>();
        for (int seed = 0; seed < 30; seed++) {
            LevelLayout level = new RandomLevelSource(5, 4, 5, new Random(seed)).load();
            StringBuilder trees = new StringBuilder();
            for (int y = 0; y < level.heightInTiles(); y++) {
                for (int x = 0; x < level.widthInTiles(); x++) {
                    if (level.cellAt(x, y) == PLAYER) {
                        playerLocations.add(x + "," + y);
                    }
                    if (level.cellAt(x, y) == TREE) {
                        trees.append(x).append(',').append(y).append(';');
                    }
                }
            }
            treeLayouts.add(trees.toString());
        }
        assertTrue(playerLocations.size() > 1);
        assertTrue(treeLayouts.size() > 1);
    }

    @Test
    void rejectsInvalidDimensionsAndTreeCounts() {
        for (int[] params : new int[][] {{0, 2, 0}, {2, -1, 0}, {2, 2, -1}, {2, 2, 4}, {2, 2, 5}, {Integer.MAX_VALUE, 2, 0}}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RandomLevelSource(params[0], params[1], params[2], new Random()));
        }
        assertThrows(NullPointerException.class, () -> new RandomLevelSource(2, 2, 0, null));
    }

    private int count(LevelLayout level, LevelLayout.Cell expected) {
        int count = 0;
        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                if (level.cellAt(x, y) == expected) {
                    count++;
                }
            }
        }
        return count;
    }
}
