package ru.mipt.bit.platformer.game;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.game.model.Bullet;
import ru.mipt.bit.platformer.game.model.Tank;
import ru.mipt.bit.platformer.game.model.Tree;
import ru.mipt.bit.platformer.game.input.ShootAction;
import static org.junit.jupiter.api.Assertions.*;

class CombatTest {
    @Test
    void adjacentTargetTakesConfiguredDamageAndDiesAtZero() {
        GameField field = new GameField(8, 3);
        Tank shooter = tank(field, 1, 1, 100, 20);
        Tank target = tank(field, 2, 1, 40, 10);
        new ShootAction(shooter).execute();
        assertTrue(field.isTileOccupied(new GridPoint2(2, 1)));
        shooter.update(0.4f);
        shooter.shoot();
        assertFalse(field.isTileOccupied(new GridPoint2(2, 1)));
        assertFalse(target.move(Direction.UP));
        target.shoot();
        assertFalse(field.isTileOccupied(new GridPoint2(3, 1)));
    }

    @Test
    void treeStopsBulletAndRemainsOnField() {
        GameField field = new GameField(8, 3);
        Tank shooter = tank(field, 1, 1, 100, 50);
        Tree tree = new Tree(new GridPoint2(3, 1));
        field.spawn(tree);
        List<Obstacle> active = observe(field);
        shooter.shoot();
        field.update(1f);
        assertTrue(active.contains(tree));
        assertEquals(2, active.size());
    }

    @Test
    void bulletsDestroyEachOtherAndRemovedBulletDoesNotUpdateAgain() {
        GameField field = new GameField(8, 3);
        Tank owner = tank(field, 0, 0, 100, 50);
        Bullet first = new Bullet(new GridPoint2(2, 1), Direction.RIGHT, field, owner, 50, 0.1f);
        Bullet second = new Bullet(new GridPoint2(3, 1), Direction.LEFT, field, owner, 50, 0.1f);
        field.spawn(first);
        field.spawn(second);
        List<Obstacle> active = observe(field);
        field.update(0.1f);
        assertEquals(List.of(owner), active);
        assertFalse(first.occupies(new GridPoint2(3, 1)));
        assertFalse(second.occupies(new GridPoint2(2, 1)));
    }

    @Test
    void largeTickDoesNotSkipTarget() {
        GameField field = new GameField(20, 3);
        Tank shooter = tank(field, 1, 1, 100, 50);
        tank(field, 8, 1, 1, 10);
        shooter.shoot();
        field.update(2f);
        assertEquals(List.of(shooter), observe(field));
    }

    @Test
    void shotOutsideFieldDisappearsImmediately() {
        GameField field = new GameField(2, 2);
        Tank shooter = tank(field, 1, 0, 100, 50);
        shooter.shoot();
        assertEquals(List.of(shooter), observe(field));
    }

    @Test
    void reloadLimitsHeldFireAndMovementDoesNotHitShooter() {
        GameField field = new GameField(20, 3);
        Tank shooter = tank(field, 1, 1, 1, 50);
        List<Obstacle> active = observe(field);
        shooter.move(Direction.RIGHT);
        shooter.shoot();
        shooter.shoot();
        assertEquals(2, active.size());
        field.update(0.4f);
        assertTrue(active.contains(shooter));
        shooter.shoot();
        assertEquals(3, active.size());
    }

    @Test
    void movingTargetCanBeHitInEitherReservedCell() {
        for (int x : new int[] {2, 3}) {
            GameField field = new GameField(8, 8);
            Tank target = tank(field, 2, 2, 1, 50);
            target.move(Direction.RIGHT);
            Tank owner = tank(field, 0, 0, 100, 50);
            Bullet bullet = new Bullet(new GridPoint2(x, 2), Direction.UP, field, owner, 1, 0.1f);
            field.spawn(bullet);
            bullet.update(0f);
            assertFalse(target.isMoving());
            assertFalse(field.isTileOccupied(new GridPoint2(2, 2)));
            assertFalse(field.isTileOccupied(new GridPoint2(3, 2)));
        }
    }

    @Test
    void observerReceivesExistingObjectsAndExactlyOneRemoval() {
        GameField field = new GameField(3, 3);
        Tree tree = new Tree(new GridPoint2(1, 1));
        field.spawn(tree);
        List<Obstacle> active = observe(field);
        assertEquals(List.of(tree), active);
        field.spawn(tree);
        assertEquals(1, active.size());
        field.clear();
        assertTrue(active.isEmpty());
        field.remove(tree);
        assertTrue(active.isEmpty());
    }

    @Test
    void fieldAllowsSpawningAndRemovingDuringUpdate() {
        GameField field = new GameField(3, 3);
        Tree created = new Tree(new GridPoint2(1, 1));
        class Creator implements Obstacle, GameObject {
            public boolean occupies(GridPoint2 tile) { return false; }
            public void update(float deltaTime) {
                field.spawn(created);
                field.remove(this);
            }
        }
        Obstacle creator = new Creator();
        field.spawn(creator);
        field.update(0.1f);
        assertEquals(List.of(created), observe(field));
    }

    @Test
    void defaultBulletTakesTwoTenthsOfSecondPerTile() {
        GameField field = new GameField(20, 3);
        Tank shooter = new Tank(new GridPoint2(1, 1), field, 0.4f);
        field.spawn(shooter);
        List<Obstacle> active = observe(field);
        shooter.shoot();
        Obstacle bullet = active.stream().filter(object -> object instanceof Bullet).findFirst().orElseThrow();

        field.update(0.1f);
        assertTrue(bullet.occupies(new GridPoint2(2, 1)));
        field.update(0.1f);
        assertTrue(bullet.occupies(new GridPoint2(3, 1)));
    }

    @Test
    void defaultReloadAllowsNextShotAfterOneSecond() {
        GameField field = new GameField(20, 3);
        Tank shooter = new Tank(new GridPoint2(1, 1), field, 0.4f);
        field.spawn(shooter);
        List<Obstacle> active = observe(field);
        shooter.shoot();
        field.update(0.75f);
        shooter.shoot();
        assertEquals(2, active.size());
        field.update(0.25f);
        shooter.shoot();
        assertEquals(3, active.size());
    }

    @Test
    void damageBelowZeroRemovesTankOnlyOnce() {
        GameField field = new GameField(3, 3);
        Tank target = tank(field, 1, 1, 10, 50);
        List<Obstacle> active = observe(field);
        target.hit(20);
        target.hit(20);
        assertTrue(active.isEmpty());
        assertFalse(target.canMove(Direction.UP));
        target.shoot();
        assertTrue(active.isEmpty());
    }

    @Test
    void staticObstacleNeedsOnlyOccupancyAndSurvivesFieldUpdate() {
        GameField field = new GameField(3, 3);
        Obstacle wall = tile -> tile.equals(new GridPoint2(1, 1));
        field.spawn(wall);
        field.update(1f);
        assertTrue(field.isTileOccupied(new GridPoint2(1, 1)));
    }

    private Tank tank(GameField field, int x, int y, int health, int damage) {
        Tank tank = new Tank(new GridPoint2(x, y), field, 0.4f, health, damage, 0.1f, 0.4f);
        field.spawn(tank);
        return tank;
    }

    private List<Obstacle> observe(GameField field) {
        List<Obstacle> active = new ArrayList<>();
        field.subscribe(new FieldListener() {
            public void added(Obstacle object) { active.add(object); }
            public void removed(Obstacle object) { assertTrue(active.remove(object)); }
        });
        return active;
    }
}
