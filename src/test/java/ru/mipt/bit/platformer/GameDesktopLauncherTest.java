package ru.mipt.bit.platformer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mipt.bit.platformer.level.LevelLayout;
import static org.junit.jupiter.api.Assertions.*;

class GameDesktopLauncherTest {
    @Test
    void configuresAiTankCountIndependentlyOfLevelSource() throws Exception {
        assertEquals(3, GameDesktopLauncher.selectAiTankCount(new String[0]));
        String[] args = {"--random", "5", "4", "2", "42", "--ai-tanks", "6"};
        assertEquals(6, GameDesktopLauncher.selectAiTankCount(args));
        LevelLayout level = GameDesktopLauncher.selectLevelSource(args).load();
        assertEquals(5, level.widthInTiles());
        assertEquals(4, level.heightInTiles());
        assertEquals(0, GameDesktopLauncher.selectAiTankCount(new String[] {"--ai-tanks", "0"}));
        assertThrows(IllegalArgumentException.class,
                () -> GameDesktopLauncher.selectAiTankCount(new String[] {"--ai-tanks", "-1"}));
        assertThrows(IllegalArgumentException.class,
                () -> GameDesktopLauncher.selectLevelSource(new String[] {"--ai-tanks"}));
    }

    @TempDir
    Path directory;

    @Test
    void selectsRandomSourceByDefaultAndAcceptsParameters() throws Exception {
        LevelLayout defaults = GameDesktopLauncher.selectLevelSource(new String[0]).load();
        assertEquals(10, defaults.widthInTiles());
        assertEquals(8, defaults.heightInTiles());
        LevelLayout custom = GameDesktopLauncher.selectLevelSource(new String[] {"--random", "1", "1", "0", "42"}).load();
        assertEquals(LevelLayout.Cell.PLAYER, custom.cellAt(0, 0));
        assertEquals(1, custom.widthInTiles());
    }

    @Test
    void selectsFileSource() throws Exception {
        Path file = directory.resolve("level with spaces.txt");
        Files.write(file, "TX_".getBytes(StandardCharsets.UTF_8));
        LevelLayout level = GameDesktopLauncher.selectLevelSource(new String[] {"--file", file.toString(), "--ai-tanks", "0"}).load();
        assertEquals(3, level.widthInTiles());
        assertEquals(LevelLayout.Cell.PLAYER, level.cellAt(1, 0));
    }

    @Test
    void rejectsInvalidArguments() {
        for (String[] args : new String[][] {{"--file"}, {"--unknown"}, {"--random", "5"}, {"--random", "x", "2", "1"}}) {
            assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.selectLevelSource(args));
        }
    }
}
