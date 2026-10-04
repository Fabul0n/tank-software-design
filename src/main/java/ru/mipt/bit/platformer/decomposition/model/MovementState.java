package ru.mipt.bit.platformer.decomposition.model;

/** Read-only movement state used by graphics independently of the entity implementation. */
@FunctionalInterface
public interface MovementState {
    EntityMovement movementSnapshot();
}
