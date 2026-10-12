package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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

    public static MainCycle create(Batch batch, TiledMap map, GridPoint2 playerStart,
            Iterable<GridPoint2> treePositions) {
        TiledMapTileLayer layer = GdxGameUtils.getSingleLayer(map);
        GameField field = new GameField(layer.getWidth(), layer.getHeight());
        Tank tank = new Tank(playerStart, field, 0.4f);
        field.spawn(tank);

        KeyboardHandler keyboardHandler = new KeyboardHandler(new GdxKeyboard());
        keyboardHandler.bind(new MoveAction(tank, Direction.UP), Keys.UP, Keys.W);
        keyboardHandler.bind(new MoveAction(tank, Direction.DOWN), Keys.DOWN, Keys.S);
        keyboardHandler.bind(new MoveAction(tank, Direction.LEFT), Keys.LEFT, Keys.A);
        keyboardHandler.bind(new MoveAction(tank, Direction.RIGHT), Keys.RIGHT, Keys.D);

        GameFieldGraphics graphics = new GameFieldGraphics(field, map, batch);
        graphics.add(new TankDrawable(tank, new Texture("images/tank_blue.png"), layer, Interpolation.smooth));
        for (GridPoint2 treePosition : treePositions) {
            Tree tree = new Tree(treePosition);
            field.spawn(tree);
            graphics.add(new TreeDrawable(tree, new Texture("images/greenTree.png"), layer, Interpolation.smooth));
        }

        return new MainCycle(keyboardHandler, field, graphics);
    }
}
