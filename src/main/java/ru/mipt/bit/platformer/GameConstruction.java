package ru.mipt.bit.platformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Input.Keys;
import ru.mipt.bit.platformer.game.control.ControlStrategy;
import ru.mipt.bit.platformer.game.control.RandomController;
import ru.mipt.bit.platformer.level.RandomTankSpawner;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import ru.mipt.bit.platformer.game.graphics.HealthBarDisplay;
import ru.mipt.bit.platformer.game.graphics.HealthBarDrawable;
import ru.mipt.bit.platformer.game.graphics.Drawable;
import ru.mipt.bit.platformer.game.input.ToggleHealthBarsAction;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.level.LevelLayout;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.game.Direction;
import ru.mipt.bit.platformer.game.GameField;
import ru.mipt.bit.platformer.game.MainCycle;
import ru.mipt.bit.platformer.game.graphics.GameFieldGraphics;
import ru.mipt.bit.platformer.game.graphics.GdxGameUtils;
import ru.mipt.bit.platformer.game.graphics.TankDrawable;
import ru.mipt.bit.platformer.game.graphics.TreeDrawable;
import ru.mipt.bit.platformer.game.input.KeyboardHandler;
import ru.mipt.bit.platformer.game.input.MoveAction;
import ru.mipt.bit.platformer.game.input.keyboard.GdxKeyboard;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import ru.mipt.bit.platformer.game.model.Bullet;
import ru.mipt.bit.platformer.game.graphics.BulletDrawable;
import ru.mipt.bit.platformer.game.input.ShootAction;
import ru.mipt.bit.platformer.game.input.CommandQueue;

public final class GameConstruction {
    private GameConstruction() {
    }

    public static MainCycle create(Batch batch, LevelLayout level, int tileSize, int aiTankCount, Random random) {
        GameField field = new GameField(level.widthInTiles(), level.heightInTiles());
        CommandQueue commands = new CommandQueue();
        KeyboardHandler keyboardHandler = new KeyboardHandler(new GdxKeyboard(), commands);
        HealthBarDisplay healthBars = new HealthBarDisplay();
        keyboardHandler.bindOnPress(new ToggleHealthBarsAction(healthBars), Keys.L);
        KeyboardHandler.ActionGroup movement = keyboardHandler.createGroup(KeyboardHandler.Priority.LAST_PRESSED);
        List<ControlStrategy> controllers = new ArrayList<>();
        controllers.add(keyboardHandler);
        float secondsPerTile = 0.4f;

        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                GridPoint2 position = new GridPoint2(x, y);
                switch (level.cellAt(x, y)) {
                    case PLAYER:
                        Tank tank = new Tank(position, field, secondsPerTile);
                        field.spawn(tank);
                        keyboardHandler.createGroup().bind(new ShootAction(tank), Keys.SPACE);
                        movement.bind(new MoveAction(tank, Direction.UP), Keys.UP, Keys.W);
                        movement.bind(new MoveAction(tank, Direction.DOWN), Keys.DOWN, Keys.S);
                        movement.bind(new MoveAction(tank, Direction.LEFT), Keys.LEFT, Keys.A);
                        movement.bind(new MoveAction(tank, Direction.RIGHT), Keys.RIGHT, Keys.D);
                        break;
                    case TREE:
                        Tree tree = new Tree(position);
                        field.spawn(tree);
                        break;
                    case EMPTY:
                        break;
                }
            }
        }
        List<Tank> aiTanks = new RandomTankSpawner(random, secondsPerTile).spawn(field, level, aiTankCount);
        for (Tank tank : aiTanks) {
            controllers.add(new RandomController(tank, new ShootAction(tank), commands, random, secondsPerTile));
        }

        Texture firstGrass = new Texture("images/tileGrass1.png");
        Texture secondGrass = new Texture("images/tileGrass2.png");
        TiledMap map = GdxGameUtils.createGroundMap(level.widthInTiles(), level.heightInTiles(), tileSize,
                new TextureRegion(firstGrass), new TextureRegion(secondGrass));
        Array<Disposable> mapResources = new Array<>();
        mapResources.add(firstGrass);
        mapResources.add(secondGrass);
        Texture healthBarTexture = createHealthBarTexture();
        mapResources.add(healthBarTexture);
        TextureRegion healthBarFill = new TextureRegion(healthBarTexture);
        map.setOwnedResources(mapResources);
        TiledMapTileLayer layer = GdxGameUtils.getSingleLayer(map);
        GameFieldGraphics graphics = new GameFieldGraphics(field, map, batch, object -> {
            if (object instanceof Tank) {
                Tank tank = (Tank) object;
                Drawable tankDrawable = new TankDrawable(tank, new Texture("images/tank_blue.png"), layer, Interpolation.smooth);
                return new HealthBarDrawable(tankDrawable, tank, healthBars, healthBarFill, tileSize);
            }
            if (object instanceof Tree) {
                return new TreeDrawable((Tree) object, new Texture("images/greenTree.png"), layer, Interpolation.smooth);
            }
            if (object instanceof Bullet) {
                return new BulletDrawable((Bullet) object, new Texture("images/bullet.png"), tileSize);
            }
            throw new IllegalArgumentException("Unsupported drawable: " + object.getClass());
        });

        return new MainCycle(controllers, commands, field, graphics);
    }
    private static Texture createHealthBarTexture() {
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixel.setColor(1f, 1f, 1f, 1f);
            pixel.fill();
            return new Texture(pixel);
        } finally {
            pixel.dispose();
        }
    }

}
