package ru.mipt.bit.platformer.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomLevelSource implements LevelSource {
    private final int widthInTiles;
    private final int heightInTiles;
    private final int treeCount;
    private final Random random;

    public RandomLevelSource(int widthInTiles, int heightInTiles, int treeCount, Random random) {
        if (widthInTiles <= 0 || heightInTiles <= 0 || (long) widthInTiles * heightInTiles > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level dimensions must be positive and fit an integer cell count");
        }
        if (treeCount < 0 || treeCount >= widthInTiles * heightInTiles) {
            throw new IllegalArgumentException("Tree count must leave at least one cell for the player");
        }
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.treeCount = treeCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public LevelLayout load() {
        LevelLayout.Cell[][] cells = new LevelLayout.Cell[heightInTiles][widthInTiles];
        List<Integer> availableCells = new ArrayList<>();
        for (int y = 0; y < heightInTiles; y++) {
            Arrays.fill(cells[y], LevelLayout.Cell.EMPTY);
            for (int x = 0; x < widthInTiles; x++) {
                availableCells.add(y * widthInTiles + x);
            }
        }
        Collections.shuffle(availableCells, random);
        int playerCell = availableCells.get(0);
        cells[playerCell / widthInTiles][playerCell % widthInTiles] = LevelLayout.Cell.PLAYER;
        for (int i = 1; i <= treeCount; i++) {
            int treeCell = availableCells.get(i);
            cells[treeCell / widthInTiles][treeCell % widthInTiles] = LevelLayout.Cell.TREE;
        }
        return new LevelLayout(cells);
    }
}
