package net.minestom.server.entity.metadata;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Metadata;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InteractionMeta extends EntityMeta {

    public static final byte OFFSET = EntityMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 3;
    private @Nullable EventListener<PlayerEntityInteractEvent> interactHandler;

    public InteractionMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

    public float getWidth() {
        return super.metadata.getIndex(OFFSET, 0.0F);
    }

    public void setWidth(float value) {
        super.metadata.setIndex(OFFSET, Metadata.Float(value));
    }

    public float getHeight() {
        return super.metadata.getIndex(OFFSET + 1, 0.0F);
    }

    public void setHeight(float value) {
        super.metadata.setIndex(OFFSET + 1, Metadata.Float(value));
    }

    public void setSize(float width, float height) {
        this.setSize(width, height, true);
    }

    public void setSize(float width, float height, boolean responsive) {
        this.setWidth(width);
        this.setHeight(height);
        this.setResponsive(responsive);
    }

    public @Nullable EventListener<PlayerEntityInteractEvent> getInteractHandler() {
        return interactHandler;
    }

    public void setInteractHandler(@Nullable Consumer<PlayerEntityInteractEvent> consumer) {
        if (consumer == null && this.interactHandler != null) {
            MinecraftServer.getGlobalEventHandler().removeListener(this.interactHandler);
            return;
        }
        if (consumer == null) return;
        this.consumeEntity(entity -> {
            this.interactHandler = EventListener.builder(PlayerEntityInteractEvent.class)
                    .filter(event -> event.getEntity().getUuid().equals(entity.getUuid()))
                    .expireWhen(event -> entity.isRemoved())
                    .handler(consumer)
                    .build();
            MinecraftServer.getGlobalEventHandler().addListener(this.interactHandler);
            System.out.println("Added interact handler");
        });
    }

    public boolean isResponsive() {
        return super.metadata.getIndex(OFFSET + 2, false);
    }

    public void setResponsive(boolean value) {
        super.metadata.setIndex(OFFSET + 2, Metadata.Boolean(value));
    }
}