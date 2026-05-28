package com.cybrisoft.createmoderntech.block.aicore;

import com.cybrisoft.createmoderntech.util.ServerAINetworkManager;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class AICoreBlockEntity extends SmartBlockEntity {
    public UUID networkId = null;
    private final Set<BlockPos> connectedDevices = new HashSet<>();

    public AICoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (networkId == null) {
            networkId = UUID.randomUUID();
            setChanged();
        }

        if (getLevel().isClientSide()) return;

        ServerAINetworkManager.register(networkId, getBlockPos());
    }

    @Override
    public void destroy() {
        ServerAINetworkManager.remove(networkId);

        super.destroy();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (networkId != null) tag.putUUID("NetworkId", networkId);
        long[] longArray = connectedDevices.stream().mapToLong(BlockPos::asLong).toArray();
        tag.putLongArray("ConnectedDevices", longArray);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        networkId = tag.hasUUID("NetworkId") ? tag.getUUID("NetworkId") : null;

        connectedDevices.clear();
        if (tag.contains("ConnectedDevices")) {
            long[] longArray = tag.getLongArray("ConnectedDevices");
            for (long packedPos : longArray) {
                connectedDevices.add(BlockPos.of(packedPos));
            }
        }
    }

    public void registerDevicePosition(BlockPos pos) {
        connectedDevices.add(pos);
        setChanged();
    }

    public void unregisterDevicePosition(BlockPos pos) {
        if (connectedDevices.remove(pos)) {
            setChanged();
        }
    }

    public Set<BlockPos> getConnectedDevices() {
        return this.connectedDevices;
    }
}
