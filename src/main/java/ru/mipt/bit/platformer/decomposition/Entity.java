package ru.mipt.bit.platformer.decomposition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public abstract class Entity implements Disposable {
    private final TextureRegion textureRegion;
    private final GridPoint2 coordinates;
    private final Rectangle boundingRectangle;
    private float rotation;

    private final Array<Component> components = new Array<>();

    public Entity(TiledMapTileLayer layer, Texture texture, GridPoint2 coordinates) {
        this.textureRegion = new TextureRegion(texture);
        this.coordinates = new GridPoint2(coordinates);
        this.boundingRectangle = createBoundingRectangle(textureRegion);
        moveRectangleAtTileCenter(layer, boundingRectangle, this.coordinates);
        this.rotation = 0f;
    }

    protected <T extends Component> T addComponent(T component) {
        components.add(component);
        return component;
    }

    public void update(float deltaTime) {
        for (Component component : components) {
            component.update(deltaTime);
        }
    }

    public <T extends Component> T getComponent(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void dispose() {
        for (Component component : components) {
            component.dispose();
        }
        textureRegion.getTexture().dispose();
    }
}
