package net.minestom.server.coordinate;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a location containing coordinates, a view and an instance.
 *
 * @param instance {@link Instance} of the location.
 * @param pos      {@link Pos} of the location.
 * @see Pos
 * @see Instance
 */
public record Location(@NotNull Instance instance, @NotNull Pos pos) {
    @Override
    public String toString() {
        return this.instance.getUniqueId() + " " + this.pos;
    }
}
