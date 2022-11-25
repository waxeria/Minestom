package net.minestom.server.collision;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.EntityTracker;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Entity collision utils is a class that contains methods to check if an entity can move to a specific position and handles motion of entities.
 *
 * @author <a href="https://github.com/iam4722202468">iam4722202468</a>, Obyvante
 * <a href="https://github.com/Minestom/Minestom/pull/763">from</a>
 */
public final class EntityCollisionUtils {
    public static final Vec POWER = new Vec(0.07, 0.07, 0.07);
    public static final boolean[] COLLIDABLE = new boolean[EntityType.values().size()];

    static {
        Arrays.fill(COLLIDABLE, true);
        COLLIDABLE[EntityType.ITEM.id()] = false;
        COLLIDABLE[EntityType.ITEM_FRAME.id()] = false;
        COLLIDABLE[EntityType.PAINTING.id()] = false;
        COLLIDABLE[EntityType.GLOW_ITEM_FRAME.id()] = false;
        COLLIDABLE[EntityType.VILLAGER.id()] = false;
        COLLIDABLE[EntityType.LLAMA_SPIT.id()] = false;
        COLLIDABLE[EntityType.EXPERIENCE_ORB.id()] = false;
        COLLIDABLE[EntityType.PAINTING.id()] = false;
        COLLIDABLE[EntityType.ARMOR_STAND.id()] = false;
        COLLIDABLE[EntityType.END_CRYSTAL.id()] = false;
        COLLIDABLE[EntityType.AREA_EFFECT_CLOUD.id()] = false;
        COLLIDABLE[EntityType.LIGHTNING_BOLT.id()] = false;
        COLLIDABLE[EntityType.ARROW.id()] = false;
        COLLIDABLE[EntityType.SPECTRAL_ARROW.id()] = false;
        COLLIDABLE[EntityType.SHULKER_BULLET.id()] = false;
        COLLIDABLE[EntityType.SNOWBALL.id()] = false;
        COLLIDABLE[EntityType.FIREBALL.id()] = false;
        COLLIDABLE[EntityType.DRAGON_FIREBALL.id()] = false;
        COLLIDABLE[EntityType.SMALL_FIREBALL.id()] = false;
        COLLIDABLE[EntityType.EGG.id()] = false;
        COLLIDABLE[EntityType.TNT.id()] = false;
        COLLIDABLE[EntityType.ENDER_PEARL.id()] = false;
        COLLIDABLE[EntityType.EYE_OF_ENDER.id()] = false;
        COLLIDABLE[EntityType.FALLING_BLOCK.id()] = false;
        COLLIDABLE[EntityType.FISHING_BOBBER.id()] = false;
    }

    /**
     * Calculates entity collisions.
     *
     * @param entity {@link Entity} to calculate collisions for.
     * @return {@link Vec} representing the entity's motion.
     * @throws IllegalArgumentException If {@param entity} is {@code null}.
     */
    @NotNull
    public static Vec calculateEntityCollisions(@NotNull final Entity entity) {
        // If entity has no instance, no need to check for collisions.
        if (entity.getInstance() == null) return Vec.ZERO;

        // These entities don't have collisions
        if (!entity.getHasCollisions() || !EntityCollisionUtils.COLLIDABLE[entity.getEntityType().id()])
            return Vec.ZERO;

        // Gets nearby entities.
        final var nearby_entities = new HashSet<Entity>(0);
        entity.getInstance().getEntityTracker().nearbyEntities(entity.getPosition(), entity.getBoundingBox().width(), EntityTracker.Target.ENTITIES, target -> {
            if (target.getHasCollisions() && target != entity && EntityCollisionUtils.COLLIDABLE[target.getEntityType().id()])
                nearby_entities.add(target);
        });
        if (nearby_entities.isEmpty()) return Vec.ZERO;

        // Process nearby entities.
        final var vector = new int[]{0, 0, 0};
        for (final var nearby_entity : nearby_entities) {
            // Not interacting with the entity.
            if (!nearby_entity.getBoundingBox().intersectBox(nearby_entity.getPosition().sub(entity.getPosition()), entity.getBoundingBox()))
                continue;

            double currentDistanceX = entity.getPosition().x() - nearby_entity.getPosition().x();
            double currentDistanceZ = entity.getPosition().z() - nearby_entity.getPosition().z();

            // Calculation.
            if (Math.abs(currentDistanceX) > Math.abs(currentDistanceZ)) vector[0] += (currentDistanceX > 0 ? 1 : -1);
            else vector[2] += (currentDistanceZ > 0 ? 1 : -1);
        }
        return vector[0] == 0 && vector[2] == 0 ? Vec.ZERO : new Vec(vector[0], vector[1], vector[2]).normalize().mul(EntityCollisionUtils.POWER);
    }
}
