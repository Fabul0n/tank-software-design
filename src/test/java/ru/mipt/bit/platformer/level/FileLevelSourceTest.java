package ru.mipt.bit.platformer.level;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static ru.mipt.bit.platformer.level.LevelLayout.Cell.*;

class FileLevelSourceTest {
    @TempDir
    Path directory;

    private LevelLayout load(String text) throws IOException {
        Path file = directory.resolve("level.txt");
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return new FileLevelSource(file).load();
    }

    @Test
    void readsWholeLevelWithFirstRowAtTheTop() throws IOException {
        LevelLayout level = load("T__T\n_X__\n__T_\n");
        assertEquals(4, level.widthInTiles());
        assertEquals(3, level.heightInTiles());
        assertEquals(TREE, level.cellAt(0, 2));
        assertEquals(TREE, level.cellAt(3, 2));
        assertEquals(PLAYER, level.cellAt(1, 1));
        assertEquals(TREE, level.cellAt(2, 0));
        assertEquals(EMPTY, level.cellAt(0, 0));
    }

    @Test
    void acceptsCrLfBomAndMissingFinalNewline() throws IOException {
        LevelLayout level = load("\uFEFF_T\r\nX_");
        assertEquals(PLAYER, level.cellAt(0, 0));
        assertEquals(TREE, level.cellAt(1, 1));
    }

    @Test
    void acceptsSingleCellLevel() throws IOException {
        LevelLayout level = load("X");
        assertEquals(1, level.widthInTiles());
        assertEquals(1, level.heightInTiles());
        assertEquals(PLAYER, level.cellAt(0, 0));
    }

    @Test
    void rejectsEmptyOrNonrectangularLevels() {
        for (String invalid : new String[] {"", "\n", "X_\n_", "X_\n\n", "X_\n___"}) {
            assertThrows(IllegalArgumentException.class, () -> load(invalid));
        }
    }

    @Test
    void rejectsMissingOrRepeatedPlayer() {
        assertThrows(IllegalArgumentException.class, () -> load("__\nTT"));
        assertThrows(IllegalArgumentException.class, () -> load("X_\n_X"));
    }

    @Test
    void reportsLocationOfUnknownSymbol() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> load("X_\n_?"));
        assertTrue(error.getMessage().contains("line 2, column 2"));
        assertThrows(IllegalArgumentException.class, () -> load("X "));
    }

    @Test
    void reportsMissingFile() {
        assertThrows(IOException.class, () -> new FileLevelSource(directory.resolve("missing.txt")).load());
    }
}
