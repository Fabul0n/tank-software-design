package ru.mipt.bit.platformer.level;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class FileLevelSource implements LevelSource {
    private final Path path;

    public FileLevelSource(Path path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public LevelLayout load() throws IOException {
        List<String> rows = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (!rows.isEmpty() && rows.get(0).startsWith("\uFEFF")) {
            rows.set(0, rows.get(0).substring(1));
        }
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty: " + path);
        }
        int width = rows.get(0).length();
        int height = rows.size();
        LevelLayout.Cell[][] cells = new LevelLayout.Cell[height][width];
        for (int row = 0; row < height; row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Different row width at line " + (row + 1));
            }
            for (int x = 0; x < width; x++) {
                LevelLayout.Cell cell;
                switch (line.charAt(x)) {
                    case '_': cell = LevelLayout.Cell.EMPTY; break;
                    case 'T': cell = LevelLayout.Cell.TREE; break;
                    case 'X': cell = LevelLayout.Cell.PLAYER; break;
                    default:
                        throw new IllegalArgumentException("Unknown symbol at line " + (row + 1)
                                + ", column " + (x + 1) + ": " + line.charAt(x));
                }
                cells[height - row - 1][x] = cell;
            }
        }
        return new LevelLayout(cells);
    }
}
