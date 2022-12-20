package net.minestom.server.region;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Region is a part of the world that has two coordinates (x and z) and a size.
 *
 * @author Obyvante
 * @see Point
 */
public class Region {
    protected final Point edge1;
    protected final Point edge2;
    protected final double maxX;
    protected final double minX;
    protected final double maxY;
    protected final double minY;
    protected final double maxZ;
    protected final double minZ;

    /**
     * Creates a new {@link Region} with the two edges.
     *
     * @param edge1 {@link Point} first edge of the region.
     * @param edge2 {@link Point} second edge of the region.
     * @throws NullPointerException if {@param edge1} or {@param edge2} is {@code null}.
     * @see Point
     */
    public Region(@NotNull final Point edge1, @NotNull final Point edge2) {
        this.edge1 = Objects.requireNonNull(edge1);
        this.edge2 = Objects.requireNonNull(edge2);
        this.maxX = Math.max(edge1.x(), edge2.x());
        this.minX = Math.min(edge1.x(), edge2.x());
        this.maxY = Math.max(edge1.y(), edge2.y());
        this.minY = Math.min(edge1.y(), edge2.y());
        this.maxZ = Math.max(edge1.z(), edge2.z());
        this.minZ = Math.min(edge1.z(), edge2.z());
    }

    /**
     * @return {@link Point}
     * @see Point
     */
    @NotNull
    public final Point getEdge1() {
        return this.edge1;
    }

    /**
     * @return {@link Point}
     * @see Point
     */
    @NotNull
    public final Point getEdge2() {
        return this.edge2;
    }

    /**
     * Gets center of the region.
     *
     * @return {@link Vec}
     * @see Vec
     */
    public final Vec getCenter() {
        return this.getCenter(true);
    }

    /**
     * Gets center of the region.
     *
     * @param includeY {@code true} if the y coordinate should be included, {@code false} otherwise.
     * @return {@link Vec}
     * @see Vec
     */
    public final Vec getCenter(final boolean includeY) {
        return new Vec(
                (this.maxX + this.minX) / 2,
                includeY ? (this.maxY + this.minY) / 2 : 0,
                (this.maxZ + this.minZ) / 2
        );
    }

    /**
     * @return {@link double}
     */
    public final double getSize() {
        return (this.maxX - this.minX) * (this.maxZ - this.minZ);
    }

    /**
     * Checks if the region contains the point.
     *
     * @param point {@link Point} to check.
     * @return {@code true} if the region contains the point, {@code false} otherwise.
     * @throws NullPointerException if {@param point} is {@code null}.
     * @see Point
     */
    public boolean contains(@NotNull final Point point) {
        return point.x() >= this.minX && point.x() <= this.maxX &&
                point.y() >= this.minY && point.y() <= this.maxY &&
                point.z() >= this.minZ && point.z() <= this.maxZ;
    }

    /**
     * Checks if the region contains the region.
     *
     * @param region {@link Region} to check.
     * @return {@code true} if the region contains the region, {@code false} otherwise.
     * @throws NullPointerException if {@param region} is {@code null}.
     * @see Region
     */
    public boolean contains(@NotNull final Region region) {
        return this.contains(region.edge1) && this.contains(region.edge2);
    }
}
