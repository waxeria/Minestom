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
@SuppressWarnings("unused")
public final class EntityCollisionUtils {
    public static final Vec POWER = new Vec(0.1, 0.1, 0.1);
    public static final boolean[] COLLIDABLE = new boolean[EntityType.values().size()];

    static {
        Arrays.fill(EntityCollisionUtils.COLLIDABLE, true);
        EntityCollisionUtils.COLLIDABLE[EntityType.ITEM.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.ITEM_FRAME.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.PAINTING.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.GLOW_ITEM_FRAME.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.VILLAGER.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.LLAMA_SPIT.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.EXPERIENCE_ORB.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.PAINTING.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.ARMOR_STAND.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.END_CRYSTAL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.AREA_EFFECT_CLOUD.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.LIGHTNING_BOLT.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.ARROW.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.SPECTRAL_ARROW.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.SHULKER_BULLET.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.SNOWBALL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.FIREBALL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.DRAGON_FIREBALL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.SMALL_FIREBALL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.EGG.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.TNT.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.ENDER_PEARL.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.EYE_OF_ENDER.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.FALLING_BLOCK.id()] = false;
        EntityCollisionUtils.COLLIDABLE[EntityType.FISHING_BOBBER.id()] = false;
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
