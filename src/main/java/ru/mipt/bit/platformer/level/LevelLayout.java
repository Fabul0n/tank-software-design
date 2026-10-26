package ru.mipt.bit.platformer.level;

import java.util.Objects;

public final class LevelLayout {
    public enum Cell {
        EMPTY, TREE, PLAYER
    }

    private final Cell[][] cells;

    LevelLayout(Cell[][] cells) {
        Objects.requireNonNull(cells);
        if (cells.length == 0 || cells[0] == null || cells[0].length == 0) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = cells[0].length;
        this.cells = new Cell[cells.length][width];
        int players = 0;
        for (int y = 0; y < cells.length; y++) {
            if (cells[y] == null || cells[y].length != width) {
                throw new IllegalArgumentException("Level must be rectangular");
            }
            for (int x = 0; x < width; x++) {
                Cell cell = Objects.requireNonNull(cells[y][x]);
                this.cells[y][x] = cell;
                if (cell == Cell.PLAYER) {
                    players++;
                }
            }
        }
        if (players != 1) {
            throw new IllegalArgumentException("Level must contain exactly one player, found: " + players);
        }
    }

    public int widthInTiles() {
        return cells[0].length;
    }

    public int heightInTiles() {
        return cells.length;
    }

    public Cell cellAt(int x, int y) {
        return cells[y][x];
    }
}
