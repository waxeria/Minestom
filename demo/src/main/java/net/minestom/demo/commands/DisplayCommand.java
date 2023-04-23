package net.minestom.demo.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

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
        var meta = (BlockDisplayMeta) entity.getEntityMeta();
        meta.setBlockState(Block.STONE_STAIRS.stateId());
        meta.setScale(new Vec(1, 1, 1));
        entity.setInstance(player.getInstance(), player.getPosition());
        meta.setViewRange(999);
        meta.setInterpolationDuration(0);

        final var ride = new Entity(EntityType.ARMOR_STAND);
        final var ride_meta = (ArmorStandMeta) ride.getEntityMeta();
        ride.setInstance(player.getInstance(), player.getPosition());
        ride_meta.setInvisible(true);
        ride.setNoGravity(true);
        ride.addPassenger(entity);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, event -> {
            ride.teleport(event.getPlayer().getPosition().add(0, 1.5, 0));
        });
    }

    public void spawnText(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof Player player))
            return;

        var entity = new Entity(EntityType.TEXT_DISPLAY);
        var meta = (TextDisplayMeta) entity.getEntityMeta();
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setText(Component.text("Test 1.")
                .appendNewline()
                .append(Component.text("Test 2"))
                .appendNewline()
                .appendNewline()
                .append(Component.text("Test 3").color(NamedTextColor.DARK_GRAY))
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
        var meta = (InteractionMeta) entity.getEntityMeta();
        meta.setHeight(3F);
        meta.setWidth(1F);
        meta.setResponsive(true);
        meta.setInteractHandler(event -> player.sendMessage("Interacted with interaction entity"));
        entity.setInstance(player.getInstance(), player.getPosition());
    }
}