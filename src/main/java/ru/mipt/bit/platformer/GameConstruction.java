package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
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

public final class GameConstruction {
    private GameConstruction() {
    }

    public static MainCycle create(Batch batch, LevelLayout level, int tileSize) {
        Texture firstGrass = new Texture("images/tileGrass1.png");
        Texture secondGrass = new Texture("images/tileGrass2.png");
        TiledMap map = GdxGameUtils.createGroundMap(level.widthInTiles(), level.heightInTiles(), tileSize,
                new TextureRegion(firstGrass), new TextureRegion(secondGrass));
        Array<Disposable> mapResources = new Array<>();
        mapResources.add(firstGrass);
        mapResources.add(secondGrass);
        map.setOwnedResources(mapResources);
        TiledMapTileLayer layer = GdxGameUtils.getSingleLayer(map);
        GameField field = new GameField(level.widthInTiles(), level.heightInTiles());
        KeyboardHandler keyboardHandler = new KeyboardHandler(new GdxKeyboard());
        GameFieldGraphics graphics = new GameFieldGraphics(field, map, batch);

        for (int y = 0; y < level.heightInTiles(); y++) {
            for (int x = 0; x < level.widthInTiles(); x++) {
                GridPoint2 position = new GridPoint2(x, y);
                switch (level.cellAt(x, y)) {
                    case PLAYER:
                        Tank tank = new Tank(position, field, 0.4f);
                        field.spawn(tank);
                        keyboardHandler.bind(new MoveAction(tank, Direction.UP), Keys.UP, Keys.W);
                        keyboardHandler.bind(new MoveAction(tank, Direction.DOWN), Keys.DOWN, Keys.S);
                        keyboardHandler.bind(new MoveAction(tank, Direction.LEFT), Keys.LEFT, Keys.A);
                        keyboardHandler.bind(new MoveAction(tank, Direction.RIGHT), Keys.RIGHT, Keys.D);
                        graphics.add(new TankDrawable(tank, new Texture("images/tank_blue.png"), layer, Interpolation.smooth));
                        break;
                    case TREE:
                        Tree tree = new Tree(position);
                        field.spawn(tree);
                        graphics.add(new TreeDrawable(tree, new Texture("images/greenTree.png"), layer, Interpolation.smooth));
                        break;
                    case EMPTY:
                        break;
                }
            }
        }
        return new MainCycle(keyboardHandler, field, graphics);
    }
}
