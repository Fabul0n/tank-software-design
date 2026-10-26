package ru.mipt.bit.platformer.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.model.Tank;

public final class RandomTankSpawner {
    private final Random random;
    private final float secondsPerTile;

    public RandomTankSpawner(Random random, float secondsPerTile) {
        this.random = Objects.requireNonNull(random);
        if (!Float.isFinite(secondsPerTile) || secondsPerTile <= 0f) {
            throw new IllegalArgumentException("Move time per tile must be finite and positive");
        }
        this.secondsPerTile = secondsPerTile;
    }

    public List<Tank> spawn(GameField gameField, LevelLayout level, int tankCount) {
        Objects.requireNonNull(gameField);
        Objects.requireNonNull(level);
        if (tankCount < 0) {
            throw new IllegalArgumentException("Tank count must not be negative");
        }
        if (!gameField.matchesDimensions(level.widthInTiles(), level.heightInTiles())) {
            throw new IllegalArgumentException("Field and level dimensions must match");
        }
        List<GridPoint2> freeTiles = new ArrayList<>();
        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (level.cellAt(x, y) == LevelLayout.Cell.EMPTY && !gameField.isTileOccupied(tile)) {
                    freeTiles.add(tile);
                }
            }
        }
        if (tankCount > freeTiles.size()) {
            throw new IllegalArgumentException("Not enough free cells for " + tankCount + " AI tanks: " + freeTiles.size());
        }
        List<Tank> tanks = new ArrayList<>();
        if (tankCount == 0) {
            return tanks;
        }
        Collections.shuffle(freeTiles, random);
        for (int i = 0; i < tankCount; i++) {
            Tank tank = new Tank(freeTiles.get(i), gameField, secondsPerTile);
            gameField.spawn(tank);
            tanks.add(tank);
        }
        return tanks;
    }
}
