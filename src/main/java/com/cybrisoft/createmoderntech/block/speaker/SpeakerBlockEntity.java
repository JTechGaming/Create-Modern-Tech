package com.cybrisoft.createmoderntech.block.speaker;

import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockEntity;
import com.cybrisoft.createmoderntech.util.ServerAINetworkManager;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class SpeakerBlockEntity extends SmartBlockEntity {
    public UUID networkId = null;
    public float range = 16.0f;

    public SpeakerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

    boolean registered = false;

    @Override
    public void tick() {
        super.tick();

        if (registered || networkId == null || getLevel() == null || getLevel().isClientSide()) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.registerDevicePosition(getBlockPos());
            registered = true;
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (networkId != null) tag.putUUID("NetworkId", networkId);
        tag.putFloat("Range", range);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        networkId = tag.hasUUID("NetworkId") ? tag.getUUID("NetworkId") : null;
        range = tag.getFloat("Range");
        if (range < 1.0f) range = 16.0f;
    }
}
