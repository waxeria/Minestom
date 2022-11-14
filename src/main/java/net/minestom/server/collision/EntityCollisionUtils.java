package net.minestom.server.collision;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.EntityTracker;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Entity collision utils is a class that contains methods to check if an entity can move to a specific position and handles motion of entities.
 *
 * @author <a href="https://github.com/iam4722202468">iam4722202468</a>, Obyvante
 * <a href="https://github.com/Minestom/Minestom/pull/763">from</a>
 */
// TODO: performance improvements
public final class EntityCollisionUtils {
    public static final Vec POWER = new Vec(0.07, 0.07, 0.07);
    public static final Set<EntityType> NO_COLLISION = Set.of(
            EntityType.ITEM,
            EntityType.ITEM_FRAME,
            EntityType.LLAMA_SPIT,
            EntityType.EXPERIENCE_ORB,
            EntityType.PAINTING,
            EntityType.ARMOR_STAND,
            EntityType.END_CRYSTAL,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.LIGHTNING_BOLT,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.SHULKER_BULLET,
            EntityType.SNOWBALL,
            EntityType.FIREBALL,
            EntityType.DRAGON_FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.EGG,
            EntityType.TNT,
            EntityType.ENDER_PEARL,
            EntityType.EYE_OF_ENDER,
            EntityType.FALLING_BLOCK,
            EntityType.FISHING_BOBBER);

    /**
     * Calculates entity collisions.
     *
     * @param entity                    {@link Entity} to calculate collisions for.
     * @param ignoreNoCollisionEntities Whether to ignore entities with no collision.
     * @return {@link Vec} representing the entity's motion.
     * @throws IllegalArgumentException If {@param entity} is {@code null}.
     */
    @NotNull
    public static Vec calculateEntityCollisions(@NotNull final Entity entity, final boolean ignoreNoCollisionEntities) {
        // If entity has no instance, no need to check for collisions.
        if (entity.getInstance() == null) return Vec.ZERO;

        // These entities don't have collisions
        Predicate<Entity> collisionPredicate = e -> !ignoreNoCollisionEntities || !EntityCollisionUtils.NO_COLLISION.contains(e.getEntityType());
        if (!collisionPredicate.test(entity)) return Vec.ZERO;

        // Declares required variables.
        final var entity_bounding_box = entity.getBoundingBox();
        final double entity_furthest_corner = Math.sqrt(entity_bounding_box.depth() + entity_bounding_box.height() + entity_bounding_box.width());
        var vector_acc = Vec.ZERO;

        // Gets nearby entities.
        final var nearby_entities = new HashSet<Entity>(0);
        entity.getInstance().getEntityTracker().nearbyEntities(entity.getPosition(), entity_furthest_corner, EntityTracker.Target.ENTITIES, target -> {
            if (!collisionPredicate.test(target) || target == entity) return;
            nearby_entities.add(target);
        });

        for (final var nearby_entity : nearby_entities) {
            BoundingBox collisionCheckBB = nearby_entity.getBoundingBox();
            if (collisionCheckBB.intersectBox(nearby_entity.getPosition().sub(entity.getPosition()), entity_bounding_box)) {
                // Find the shortest resolution to collision by calculating the two faces with the shortest distance
                // Only solve collision for X and Z. Y doesn't matter because gravity
                double currentDistanceX, currentDistanceZ;

                // X
                // Nearby left of entity
                currentDistanceX = entity.getPosition().x() - nearby_entity.getPosition().x();

                // If y is implemented, min distance calculation isn't h1 / 2 + h2 / 2, because entity position is from bottom of bounding box, not centre

                // Z
                // Nearby left of entity
                currentDistanceZ = entity.getPosition().z() - nearby_entity.getPosition().z();

                if (Math.abs(currentDistanceX) > Math.abs(currentDistanceZ)) {
                    // X-axis shorter
                    vector_acc = vector_acc.add(new Vec(currentDistanceX > 0 ? 1 : -1, 0, 0));
                } else {
                    // Z-axis shorter
                    vector_acc = vector_acc.add(new Vec(0, 0, currentDistanceZ > 0 ? 1 : -1));
                }
            }
        }
        return vector_acc.isZero() ? Vec.ZERO : vector_acc.normalize().mul(EntityCollisionUtils.POWER);
    }

    /**
     * Calculates entity collisions.
     *
     * @param entity                    {@link Entity} to calculate collisions for.
     * @param ignoreNoCollisionEntities Whether to ignore entities with no collision.
     * @return {@link Vec} representing the entity's motion.
     * @throws IllegalArgumentException If {@param entity} is {@code null}.
     */
    public static boolean hasEntityCollision(@NotNull final Entity entity, @NotNull final Pos position, final boolean ignoreNoCollisionEntities) {
        // If entity has no instance, no need to check for collisions.
        if (entity.getInstance() == null) return false;

        Predicate<Entity> collisionPredicate = e -> !ignoreNoCollisionEntities || !EntityCollisionUtils.NO_COLLISION.contains(e.getEntityType());

        // These entities don't have collisions
        if (!collisionPredicate.test(entity)) return false;

        // Declares required variables.
        final var entity_bounding_box = entity.getBoundingBox();
        final double entity_furthest_corner = Math.sqrt(entity_bounding_box.depth() + entity_bounding_box.height() + entity_bounding_box.width());

        // Gets nearby entities.
        final var nearby_entities = new HashSet<Entity>(0);
        entity.getInstance().getEntityTracker().nearbyEntities(position, entity_furthest_corner, EntityTracker.Target.ENTITIES, target -> {
            if (!collisionPredicate.test(target) || target == entity) return;
            nearby_entities.add(target);
        });
        return !nearby_entities.isEmpty();
    }
}
