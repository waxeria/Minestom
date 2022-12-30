package net.minestom.server.hologram;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Location;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Hologram multi line is a {@link Hologram} which can be used to display multiple lines of text.
 *
 * @author Obyvante
 * @see Hologram
 * @since 0.1.0-SNAPSHOT
 */
@SuppressWarnings("unused")
public class HologramMultiLine {
    /**
     * STATIC
     */
    public static double DEFAULT_NAMEPLATE_Y_OFFSET_STEP = 0.3D;


    /*
    BASE
     */
    protected List<Component> content = new ArrayList<>(0);
    protected Hologram[] holograms = new Hologram[0];
    protected double[] indexOffsets = new double[0];
    private Pos lastPosition;
    private boolean autoViewable = false;

    /**
     * Creates a new {@link HologramMultiLine}.
     */
    public HologramMultiLine() {
    }

    /**
     * Creates a new {@link HologramMultiLine}.
     *
     * @param autoViewable If the {@link HologramMultiLine} should be automatically viewable.
     */
    public HologramMultiLine(final boolean autoViewable) {
        this.autoViewable = autoViewable;
    }

    /**
     * @return {@link Collection<Hologram>} of the holograms this hologram is rendered on.
     * @see Hologram
     */
    @NotNull
    public Hologram[] getHolograms() {
        return this.holograms;
    }

    /**
     * @return {@link List<Component>} of the content this hologram is rendered with.
     */
    @NotNull
    public List<Component> getContent() {
        return this.content;
    }

    /**
     * Gets {@link Hologram} content at the given index.
     *
     * @param index Index of the content.
     * @return {@link Component} of the content at the given index.
     * @throws IndexOutOfBoundsException if {@param index} is out of bounds.
     */
    @NotNull
    public Component getContentLine(final int index) {
        return this.content.get(index);
    }

    /**
     * @return {@link double[]} of the offsets of each line.
     */
    public double[] getIndexOffsets() {
        return this.indexOffsets;
    }

    /**
     * Gets the offset of the line at the given index.
     *
     * @param extraOffset Extra offset to add to the index offset.
     * @return {@link double[]} of the offsets of each line.
     */
    public double[] getIndexOffsets(final double extraOffset) {
        double[] offsets = new double[this.indexOffsets.length];
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = this.indexOffsets[i] + extraOffset;
        }
        return offsets;
    }

    /**
     * @return {@link Location} of the {@link HologramMultiLine}.
     */
    @NotNull
    public Pos getLastPosition() {
        return this.lastPosition;
    }


    /*
    CHECKS
     */

    /**
     * @return {@code true} if the content is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.content.isEmpty();
    }


    /*
    ACTIONS
     */

    /**
     * Adds viewers to the hologram.
     *
     * @param viewers {@link Collection<Player>} of viewers to add.
     * @return {@link HologramMultiLine} (this)
     */
    @NotNull
    public HologramMultiLine addViewers(@NotNull final Set<Player> viewers) {
        for (final var hologram : this.holograms) viewers.forEach(hologram::addViewer);
        return this;
    }

    /**
     * Removes viewers from the hologram.
     *
     * @param viewers {@link Collection<Player>} of viewers to remove.
     * @return {@link HologramMultiLine} (this)
     */
    @NotNull
    public HologramMultiLine removeViewers(@NotNull final Set<Player> viewers) {
        for (final var hologram : this.holograms) viewers.forEach(hologram::removeViewer);
        return this;
    }

    /**
     * Updates last position of the hologram.
     *
     * @param pos {@link Pos} of the hologram.
     * @return {@link HologramMultiLine} (this)
     * @throws NullPointerException if {@param pos} is {@code null}.
     */
    @NotNull
    public HologramMultiLine updateLastPosition(@NotNull final Pos pos) {
        this.lastPosition = Objects.requireNonNull(pos);
        return this;
    }

    /**
     * Removes line at the given index.
     *
     * @param index {@link int} index of {@link Component} hologram.
     * @return {@link HologramMultiLine} (this)
     */
    @NotNull
    public HologramMultiLine removeLineContent(final int index) {
        // Declares required variable(s).
        final var entity = this.holograms[0].getEntity();
        // Handles the case where the hologram is not yet created.
        this.content.remove(index);
        // Updates content.
        this.updateContent(Objects.requireNonNull(entity.getInstance()), entity.getPosition(), this.content);
        return this;
    }


    /**
     * Adds content to line.
     *
     * @param index       {@link int} index of {@link Component} hologram.
     * @param lineContent {@link Component} to add the hologram with.
     * @return {@link HologramMultiLine} (this)
     * @throws NullPointerException if {@param content} is {@code null}.
     */
    @NotNull
    public HologramMultiLine addLineContent(final int index, @NotNull final Component lineContent) {
        // Declares required variable(s).
        final var entity = this.holograms[0].getEntity();
        // Handles the case where the hologram is not yet created.
        this.content.add(index, Objects.requireNonNull(lineContent));
        // Updates content.
        this.updateContent(Objects.requireNonNull(entity.getInstance()), entity.getPosition(), this.content);
        return this;
    }

    /**
     * Updates content line.
     *
     * @param index       {@link int} index of {@link Component} hologram.
     * @param lineContent {@link Component} to update the hologram with.
     * @return {@link HologramMultiLine} (this)
     * @throws NullPointerException if {@param content} is {@code null}.
     */
    @NotNull
    public HologramMultiLine updateLineContent(final int index, @NotNull final Component lineContent) {
        // Handles the case where the index is in bounds.
        this.content.set(index, lineContent);
        this.holograms[index].setText(Objects.requireNonNull(lineContent));
        return this;
    }

    /**
     * Updates current content.
     *
     * @param instance {@link Instance} of the multi line hologram.
     * @param pos      {@link Pos} of the multi line hologram.
     * @param content  {@link List<Component>}
     * @throws NullPointerException if {@param content} is {@code null}.
     */
    public void updateContent(@NotNull final Instance instance, @NotNull final Pos pos, @NotNull final List<Component> content) {
        // Creates a new list with the new holograms.
        this.content = new ArrayList<>(Objects.requireNonNull(content));
        this.indexOffsets = new double[this.content.size()];

        // Clears the holograms cache.
        for (final var hologram : this.holograms) hologram.remove();
        this.holograms = new Hologram[this.content.size()];

        // Creates new holograms with the new holograms.
        for (int index = content.size() - 1; index >= 0; index--) {
            final var calculation = ((this.content.size() - 1) - index) * HologramMultiLine.DEFAULT_NAMEPLATE_Y_OFFSET_STEP;
            this.indexOffsets[index] = calculation;
            final var hologram = new Hologram(
                    instance,
                    pos.withY(y -> y + calculation),
                    this.content.get(index),
                    this.autoViewable,
                    true);
            // Adds new hologram to the cache.
            this.holograms[index] = hologram;
        }
    }

    /**
     * Removes holograms.
     */
    public void remove() {
        for (final var hologram : this.holograms) hologram.remove();
        this.holograms = new Hologram[0];
        this.content = new ArrayList<>(0);
    }
}
