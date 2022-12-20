package net.minestom.server.region;

import net.minestom.server.Tickable;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Instance region is a part of an instance that has two coordinates (x and z) and a size.
 *
 * @author Obyvante
 * @see Point
 * @see Instance
 */
public class InstanceRegion extends Region implements Tickable {
    protected final Instance instance;
    protected final String id;
    private final Set<Consumer<Player>> enterHandlers = new HashSet<>(0);
    private final Set<Consumer<Player>> exitHandlers = new HashSet<>(0);
    private final Set<Player> players = new HashSet<>(0);

    /**
     * Creates a new {@link InstanceRegion} with the two edges.
     *
     * @param id       {@link String} the id of the region.
     * @param instance {@link Instance} the instance of the region.
     * @param edge1    {@link Point} first edge of the region.
     * @param edge2    {@link Point} second edge of the region.
     * @throws NullPointerException if {@param id}, {@param instance}, {@param edge1} or {@param edge2} is {@code null}.
     * @see Point
     * @see Instance
     */
    public InstanceRegion(@NotNull final String id, @NotNull final Instance instance, @NotNull final Point edge1, @NotNull final Point edge2) {
        super(edge1, edge2);
        this.id = Objects.requireNonNull(id);
        this.instance = Objects.requireNonNull(instance);
    }

    /**
     * @return {@link String}
     */
    @NotNull
    public final String getId() {
        return this.id;
    }

    /**
     * @return {@link Instance}
     * @see Instance
     */
    @NotNull
    public final Instance getInstance() {
        return this.instance;
    }

    /**
     * Checks if the region contains the point.
     *
     * @param instance {@link Instance} the instance of the point.
     * @param point    {@link Point} to check.
     * @return {@code true} if the region contains the point, {@code false} otherwise.
     * @throws NullPointerException if {@param instance} or {@param point} is {@code null}.
     * @see Instance
     * @see Point
     */
    public final boolean contains(@NotNull final Instance instance, @NotNull final Point point) {
        return this.instance.equals(instance) && super.contains(point);
    }

    /**
     * @return {@link Set<Player>}
     * @see Player
     */
    @NotNull
    public Set<Player> getPlayers() {
        return this.players;
    }


    /*
    HANDLERS
     */

    /**
     * Adds a handler called when a player enters the region.
     *
     * @param handler {@link Consumer<Player>}
     * @return {@link InstanceRegion} for chaining.
     * @throws NullPointerException if {@param handler} is {@code null}.
     * @see Player
     */
    @NotNull
    public final InstanceRegion addEnterHandler(@NotNull final Consumer<Player> handler) {
        this.enterHandlers.add(Objects.requireNonNull(handler));
        return this;
    }

    /**
     * Removes a handler called when a player enters the region.
     *
     * @param handler {@link Consumer<Player>}
     * @return {@link InstanceRegion} for chaining.
     * @throws NullPointerException if {@param handler} is {@code null}.
     * @see Player
     */
    @NotNull
    public final InstanceRegion removeEnterHandler(@NotNull final Consumer<Player> handler) {
        this.enterHandlers.remove(Objects.requireNonNull(handler));
        return this;
    }

    /**
     * Adds a handler called when a player exits the region.
     *
     * @param handler {@link Consumer<Player>}
     * @return {@link InstanceRegion} for chaining.
     * @throws NullPointerException if {@param handler} is {@code null}.
     * @see Player
     */
    @NotNull
    public final InstanceRegion addExitHandler(@NotNull final Consumer<Player> handler) {
        this.exitHandlers.add(Objects.requireNonNull(handler));
        return this;
    }

    /**
     * Removes a handler called when a player exits the region.
     *
     * @param handler {@link Consumer<Player>}
     * @return {@link InstanceRegion} for chaining.
     * @throws NullPointerException if {@param handler} is {@code null}.
     * @see Player
     */
    @NotNull
    public final InstanceRegion removeExitHandler(@NotNull final Consumer<Player> handler) {
        this.exitHandlers.remove(Objects.requireNonNull(handler));
        return this;
    }


    /*
    OVERRIDES
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final long time) {
        // Handles players outside the region.
        for (final var player : this.instance.getPlayers()) {
            if (this.players.contains(player)) {
                if (player.getInstance() == null || !this.contains(player.getInstance(), player.getPosition())) {
                    this.players.remove(player);
                    this.exitHandlers.forEach(handler -> handler.accept(player));
                }
            } else {
                if (player.getInstance() == null || !this.contains(player.getInstance(), player.getPosition()))
                    continue;
                this.players.add(player);
                this.enterHandlers.forEach(handler -> handler.accept(player));
            }
        }
    }
}
