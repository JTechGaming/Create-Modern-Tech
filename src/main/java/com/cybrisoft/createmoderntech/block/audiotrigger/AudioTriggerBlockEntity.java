package com.cybrisoft.createmoderntech.block.audiotrigger;

import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockEntity;
import com.cybrisoft.createmoderntech.network.PlaySpeakerPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AudioTriggerBlockEntity extends SmartBlockEntity {
    public UUID networkId = null;
    public String message = "";
    private int cooldownTicks = 0;
    private boolean wasPowered = false;

    public AudioTriggerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (cooldownTicks > 0) { cooldownTicks--; return; }

        boolean isPowered = level.hasNeighborSignal(worldPosition);
        if (isPowered && !wasPowered) {
            trigger();
        }
        wasPowered = isPowered;
    }

    private void trigger() {
        if (networkId == null || message.isBlank()) return;
        cooldownTicks = message.length() * 4;

        List<BlockPos> speakerPositions = new ArrayList<>();
        ServerLevel serverLevel = (ServerLevel) level;

        int searchRadius = 4; // chunks
        int centerCX = worldPosition.getX() >> 4;
        int centerCZ = worldPosition.getZ() >> 4;

        for (int cx = centerCX - searchRadius; cx <= centerCX + searchRadius; cx++) {
            for (int cz = centerCZ - searchRadius; cz <= centerCZ + searchRadius; cz++) {
                if (!serverLevel.isLoaded(new BlockPos(cx << 4, 0, cz << 4))) continue;
                LevelChunk chunk = serverLevel.getChunk(cx, cz);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    if (entry.getValue() instanceof SpeakerBlockEntity speaker
                            && networkId.equals(speaker.networkId)) {
                        speakerPositions.add(entry.getKey());
                    }
                }
            }
        }

        UUID netId = this.networkId;
        String msg = this.message;

        for (ServerPlayer player : serverLevel.players()) {
            PacketDistributor.sendToPlayer(player,
                    new PlaySpeakerPacket(netId, msg, speakerPositions));
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (networkId != null) tag.putUUID("NetworkId", networkId);
        tag.putString("Message", message);
        tag.putInt("Cooldown", cooldownTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        networkId = tag.hasUUID("NetworkId") ? tag.getUUID("NetworkId") : null;
        message = tag.getString("Message");
        cooldownTicks = tag.getInt("Cooldown");
    }
}
