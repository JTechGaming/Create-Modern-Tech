package com.cybrisoft.createmoderntech.block.audiotrigger;

import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockEntity;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockEntity;
import com.cybrisoft.createmoderntech.network.PlaySpeakerPacket;
import com.cybrisoft.createmoderntech.util.ServerAINetworkManager;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AudioTriggerBlockEntity extends SmartBlockEntity {
    public UUID networkId = null;
    public String message = "";
    private boolean wasPowered = false;

    public AudioTriggerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (networkId == null) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null || getLevel() == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.registerDevicePosition(getBlockPos());
        }
    }

    @Override
    public void destroy() {
        if (networkId == null) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null || getLevel() == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.unregisterDevicePosition(getBlockPos());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    boolean registered = false;

    public void tick() {
        tryRegister();
        if (level == null || level.isClientSide()) return;

        boolean isPowered = level.hasNeighborSignal(worldPosition);
        if (isPowered && !wasPowered) {
            trigger();
        }
        wasPowered = isPowered;
    }

    private void tryRegister() {
        if (registered || networkId == null || getLevel() == null || getLevel().isClientSide()) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.registerDevicePosition(getBlockPos());
            registered = true;
        }
    }

    private void trigger() {
        if (networkId == null || message.isBlank()) return;

        List<BlockPos> speakerPositions = new ArrayList<>();
        ServerLevel serverLevel = (ServerLevel) level;

        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null) return;
        if (serverLevel == null) return;
        if (serverLevel.getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            for (BlockPos pos : be.getConnectedDevices()) {
                if (serverLevel.getBlockEntity(pos) instanceof SpeakerBlockEntity) {
                    speakerPositions.add(pos);
                }
            }
        }

        UUID netId = this.networkId;
        String msg = this.message;

        for (ServerPlayer player : serverLevel.players()) {
            boolean inRange = speakerPositions.stream().anyMatch(speakerPos -> {
                    SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, speakerPos);
                    Vec3 playerPos = player.position();
                    if (subLevel != null) {
                        Vector3dc shipPos = subLevel.logicalPose().position();
                        playerPos.add(shipPos.x(), shipPos.y(), shipPos.z());
                    }
                    return playerPos.distanceTo(Vec3.atCenterOf(speakerPos)) <=
                            ((SpeakerBlockEntity) serverLevel.getBlockEntity(speakerPos)).range;
            });

            if (inRange) {
                PacketDistributor.sendToPlayer(player,
                        new PlaySpeakerPacket(netId, msg, speakerPositions));
            }
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (networkId != null) tag.putUUID("NetworkId", networkId);
        tag.putString("Message", message);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        networkId = tag.hasUUID("NetworkId") ? tag.getUUID("NetworkId") : null;
        message = tag.getString("Message");
    }
}
