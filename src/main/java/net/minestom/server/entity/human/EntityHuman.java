package net.minestom.server.entity.human;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Entity human is a player-like entity.
 *
 * @author Obyvante
 */
public class EntityHuman extends LivingEntity {
    private final String nameId = StringUtils.generateRandomString(16);
    private PlayerSkin skin;

    // Team
    private Team team;

    // Packets
    private PlayerInfoUpdatePacket ADD_INFO_PACKET;
    private PlayerInfoRemovePacket REMOVE_INFO_PACKET;
    private TeamsPacket CREATE_TEAM_PACKET;
    private TeamsPacket ADD_TEAM_PACKET;
    private TeamsPacket REMOVE_TEAM_PACKET;

    public EntityHuman(@NotNull final UUID uuid) {
        super(EntityType.PLAYER, uuid);
        // Updates player meta.
        this.updatePlayerMeta();
        // Initializes packets.
        this.createPackets();
    }

    public EntityHuman() {
        super(EntityType.PLAYER);
        // Updates player meta.
        this.updatePlayerMeta();
        // Initializes packets.
        this.createPackets();
    }

    private void updatePlayerMeta() {
        // Handle player meta.
        PlayerMeta meta = (PlayerMeta) this.entityMeta;
        meta.setCapeEnabled(false);
        meta.setHatEnabled(true);
        meta.setJacketEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setRightSleeveEnabled(true);
    }

    private void createPackets() {
        // Creates team.
        this.team = new TeamManager().createTeam("entity:human:" + this.uuid.toString());
        this.team.setNameTagVisibility(TeamsPacket.NameTagVisibility.NEVER);

        // Initializes packets.
        this.updateAddPacket();
        this.REMOVE_INFO_PACKET = new PlayerInfoRemovePacket(Collections.singletonList(this.uuid));
        this.CREATE_TEAM_PACKET = this.team.createTeamsCreationPacket();
        this.REMOVE_TEAM_PACKET = this.team.createTeamDestructionPacket();
        this.ADD_TEAM_PACKET = new TeamsPacket(this.team.getTeamName(), new TeamsPacket.AddEntitiesToTeamAction(Collections.singletonList(this.nameId)));
    }

    /**
     * @return {@link PlayerSkin} of the NPC.
     */
    @NotNull
    public PlayerSkin getSkin() {
        return this.skin;
    }

    /**
     * Sets the {@link PlayerSkin} of the NPC.
     *
     * @param skin {@link PlayerSkin} to be set.
     * @see PlayerSkin
     */
    public void setSkin(@Nullable final PlayerSkin skin) {
        this.skin = skin;
        // Updates cached add packet.
        this.updateAddPacket();
        // Handles current viewers.
        this.viewers.forEach(this::updateNewViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomName(@Nullable final Component customName) {
        super.setCustomName(customName);
        // Updates cached add packet.
        this.updateAddPacket();
        // Handles current viewers.
        this.viewers.forEach(this::updateNewViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomNameVisible(final boolean customNameVisible) {
        super.setCustomNameVisible(customNameVisible);
        // Updates team name tag visibility.
        this.team.setNameTagVisibility(customNameVisible ? TeamsPacket.NameTagVisibility.ALWAYS : TeamsPacket.NameTagVisibility.NEVER);
        // Updates cached team packet.
        this.updateTeamPacket();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Team getTeam() {
        return this.team;
    }

    /**
     * Updates add packet.
     */
    private void updateAddPacket() {
        List<PlayerInfoUpdatePacket.Property> prop = this.skin != null ?
                List.of(new PlayerInfoUpdatePacket.Property("textures", this.skin.textures(), this.skin.signature())) :
                List.of();
        this.ADD_INFO_PACKET = new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED),
                List.of(new PlayerInfoUpdatePacket.Entry(this.uuid, this.nameId, prop,
                        true, 0, GameMode.CREATIVE, Component.empty(), null)));
    }

    /**
     * Updates add packet.
     */
    public void updateTeamPacket() {
        this.CREATE_TEAM_PACKET = this.team.createTeamsCreationPacket();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(this.ADD_INFO_PACKET);
        super.updateNewViewer(player);
        player.sendPacket(this.CREATE_TEAM_PACKET);
        player.sendPacket(this.ADD_TEAM_PACKET);
        this.scheduler().buildTask(() -> {
            if (!this.isActive() || this.isDead || !this.viewers.contains(player)) return;
            player.sendPacket(this.REMOVE_INFO_PACKET);
        }).delay(Duration.ofMillis(player.getLatency() * 2L)).schedule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateOldViewer(@NotNull Player player) {
        player.sendPacket(this.REMOVE_INFO_PACKET);
        player.sendPacket(this.REMOVE_TEAM_PACKET);
        super.updateOldViewer(player);
    }
}
