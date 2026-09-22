package ru.mipt.bit.platformer;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;

import ru.mipt.bit.platformer.decomposition.EntityGraphics;
import ru.mipt.bit.platformer.decomposition.GameRenderer;
import ru.mipt.bit.platformer.decomposition.GameWorld;
import ru.mipt.bit.platformer.decomposition.GdxKeyState;
import ru.mipt.bit.platformer.decomposition.MovementButtonHandler;
import ru.mipt.bit.platformer.decomposition.PlayerController;
import ru.mipt.bit.platformer.decomposition.Tank;
import ru.mipt.bit.platformer.decomposition.Tree;
import ru.mipt.bit.platformer.util.TileMovement;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private GameWorld gameWorld;
    private GameRenderer gameRenderer;
    private PlayerController playerController;

    @Override
    public void create() {
        batch = new SpriteBatch();

        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);

        gameWorld = new GameWorld(groundLayer.getWidth(), groundLayer.getHeight());
        gameRenderer = new GameRenderer();

        Tank tank = new Tank(new GridPoint2(1, 1), gameWorld::isTileFree);
        gameWorld.spawn(tank);
        gameRenderer.add(new EntityGraphics(
                tank,
                new Texture("images/tank_blue.png"),
                tileMovement));

        Tree tree = new Tree(new GridPoint2(1, 3));
        gameWorld.spawn(tree);
        gameRenderer.add(new EntityGraphics(
                tree,
                new Texture("images/greenTree.png"),
                tileMovement));

        playerController = new PlayerController(new GdxKeyState());
        playerController.addButtonHandler(new MovementButtonHandler(tank));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        playerController.update();
        gameWorld.update(deltaTime);
        gameRenderer.update();

        levelRenderer.render();

        batch.begin();
        gameRenderer.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameWorld.clear();
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}