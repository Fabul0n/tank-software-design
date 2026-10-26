package ru.mipt.bit.platformer;

import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.mipt.bit.platformer.game.MainCycle;
import ru.mipt.bit.platformer.level.FileLevelSource;
import ru.mipt.bit.platformer.level.LevelLayout;
import ru.mipt.bit.platformer.level.LevelSource;
import ru.mipt.bit.platformer.level.RandomLevelSource;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher extends ApplicationAdapter {
    private static final int TILE_SIZE = 128;
    private final LevelLayout level;
    private final int aiTankCount;
    private final Random random;
    private Batch batch;
    private MainCycle cycle;
    private FitViewport viewport;

    public GameDesktopLauncher(LevelLayout level, int aiTankCount, Random random) {
        this.level = Objects.requireNonNull(level);
        if (aiTankCount < 0) {
            throw new IllegalArgumentException("AI tank count must not be negative");
        }
        this.aiTankCount = aiTankCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport((float) level.widthInTiles() * TILE_SIZE,
                (float) level.heightInTiles() * TILE_SIZE);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        cycle = GameConstruction.create(batch, level, TILE_SIZE, aiTankCount, random);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        cycle.render(Gdx.graphics.getDeltaTime(), batch);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        cycle.dispose();
        batch.dispose();
    }

    static int selectAiTankCount(String[] args) {
        int count = 3;
        if (args.length >= 2 && args[args.length - 2].equals("--ai-tanks")) {
            count = Integer.parseInt(args[args.length - 1]);
        }
        if (count < 0) {
            throw new IllegalArgumentException("AI tank count must not be negative");
        }
        return count;
    }

    private static String[] levelArguments(String[] args) {
        selectAiTankCount(args);
        if (args.length >= 2 && args[args.length - 2].equals("--ai-tanks")) {
            return Arrays.copyOf(args, args.length - 2);
        }
        return args;
    }

    static LevelSource selectLevelSource(String[] args) {
        args = levelArguments(args);
        if (args.length == 0 || (args.length == 1 && args[0].equals("--random"))) {
            return new RandomLevelSource(10, 8, 15, new Random());
        }
        if (args.length == 2 && args[0].equals("--file")) {
            return new FileLevelSource(Paths.get(args[1]));
        }
        if ((args.length == 4 || args.length == 5) && args[0].equals("--random")) {
            int width = Integer.parseInt(args[1]);
            int height = Integer.parseInt(args[2]);
            int trees = Integer.parseInt(args[3]);
            Random random = args.length == 5 ? new Random(Long.parseLong(args[4])) : new Random();
            return new RandomLevelSource(width, height, trees, random);
        }
        throw new IllegalArgumentException("Usage: (--random [width height trees [seed]] | --file path) [--ai-tanks count]");
    }

    public static void main(String[] args) throws IOException {
        LevelLayout level = selectLevelSource(args).load();
        int aiTankCount = selectAiTankCount(args);
        String[] levelArgs = levelArguments(args);
        Random random = levelArgs.length == 5 && levelArgs[0].equals("--random")
                ? new Random(Long.parseLong(levelArgs[4])) : new Random();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(level, aiTankCount, random), config);
    }
}
