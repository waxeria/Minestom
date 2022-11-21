package net.minestom.server.event.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class EntityMoveEvent implements EntityInstanceEvent, CancellableEvent {

    private final Entity entity;
    private Pos newPosition;

    private boolean cancelled;

    public EntityMoveEvent(@NotNull Entity entity, @NotNull Pos newPosition) {
        this.entity = entity;
        this.newPosition = newPosition;
    }

    @Override
    public @NotNull Entity getEntity() {
        return entity;
    }

    public @NotNull Pos getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(@NotNull Pos position) {
        this.newPosition = position;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
