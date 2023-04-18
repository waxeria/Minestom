package net.minestom.demo.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.InteractionMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;

public class DisplayCommand extends Command {

    public DisplayCommand() {
        super("display");

        addSyntax(this::spawnItem, ArgumentType.Literal("item"));
        addSyntax(this::spawnBlock, ArgumentType.Literal("block"));
        addSyntax(this::spawnText, ArgumentType.Literal("text"));
        addSyntax(this::spawnInteraction, ArgumentType.Literal("interaction"));
    }

    public void spawnItem(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player))
            return;

        var entity = new Entity(EntityType.ITEM_DISPLAY);
        entity.setNoGravity(true);
        var meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setItemStack(ItemStack.of(Material.REDSTONE));
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.GROUND);
        entity.setInstance(player.getInstance(), player.getPosition());
        meta.setScale(new Vec(10, 10, 10));
    }

    public void spawnBlock(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player))
            return;

        var entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.setNoGravity(true);
        var meta = (BlockDisplayMeta) entity.getEntityMeta();
        meta.setBlockState(Block.STONE_STAIRS.stateId());
        meta.setScale(new Vec(1, 1, 1));
        entity.setInstance(player.getInstance(), player.getPosition());
        meta.setViewRange(999);
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            meta.setInterpolationDuration(100);
            meta.setScale(new Vec(100, 100, 100));
        }).delay(Duration.ofSeconds(1)).schedule();
    }

    public void spawnText(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player))
            return;

        var entity = new Entity(EntityType.TEXT_DISPLAY);
        entity.setNoGravity(true);
        var meta = (TextDisplayMeta) entity.getEntityMeta();
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setText(Component.text("Deniz pasiftir.")
                .appendNewline()
                .append(Component.text("Deniz pasiftir.2"))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Deniz pasiftir.2").color(NamedTextColor.DARK_GRAY))
        );
        meta.setBackgroundColor(NamedTextColor.GREEN, 0.1F);
        meta.setSeeThrough(true);
        meta.setScale(new Vec(1, 1, 1));
        entity.setInstance(player.getInstance(), player.getPosition());
    }

    public void spawnInteraction(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player))
            return;

        var entity = new Entity(EntityType.INTERACTION);
        entity.setNoGravity(true);
        var meta = (InteractionMeta) entity.getEntityMeta();
        meta.setHeight(3F);
        meta.setWidth(1F);
        meta.setResponsive(true);
        meta.setInteractHandler(event -> player.sendMessage("Interacted with interaction entity"));
        entity.setInstance(player.getInstance(), player.getPosition());
    }
}