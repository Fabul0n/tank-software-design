package ru.mipt.bit.platformer.decomposition;

import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.Input.Keys.*;

public class PlayerController {
    private final MovementComponent movement;

    public PlayerController(MovementComponent movement) {
        this.movement = movement;
    }

    public void update() {
        if (Gdx.input.isKeyPressed(UP) || Gdx.input.isKeyPressed(W)) {
            movement.requestMove(0, 1, 90f);
        } else if (Gdx.input.isKeyPressed(DOWN) || Gdx.input.isKeyPressed(S)) {
            movement.requestMove(0, -1, -90f);
        } else if (Gdx.input.isKeyPressed(LEFT) || Gdx.input.isKeyPressed(A)) {
            movement.requestMove(-1, 0, -180f);
        } else if (Gdx.input.isKeyPressed(RIGHT) || Gdx.input.isKeyPressed(D)) {
            movement.requestMove(1, 0, 0f);
        }
    }
}
