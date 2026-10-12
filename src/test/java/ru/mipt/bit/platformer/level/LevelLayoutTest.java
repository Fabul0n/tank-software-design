package ru.mipt.bit.platformer.level;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static ru.mipt.bit.platformer.level.LevelLayout.Cell.*;

class LevelLayoutTest {
    @Test
    void doesNotExposeOrRetainMutableRows() {
        LevelLayout.Cell[][] cells = {{PLAYER, TREE}, {EMPTY, EMPTY}};
        LevelLayout level = new LevelLayout(cells);
        cells[0][0] = EMPTY;
        cells[1] = new LevelLayout.Cell[] {TREE};
        assertEquals(PLAYER, level.cellAt(0, 0));
        assertEquals(EMPTY, level.cellAt(0, 1));
        assertEquals(2, level.widthInTiles());
    }

    @Test
    void rejectsInvalidLayoutsRegardlessOfSource() {
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(new LevelLayout.Cell[0][]));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(new LevelLayout.Cell[][] {{PLAYER}, {EMPTY, TREE}}));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(new LevelLayout.Cell[][] {{EMPTY}}));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(new LevelLayout.Cell[][] {{PLAYER, PLAYER}}));
    }
}
