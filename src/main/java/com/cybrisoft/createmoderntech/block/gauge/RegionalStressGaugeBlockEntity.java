package com.cybrisoft.createmoderntech.block.gauge;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RegionalStressGaugeBlockEntity extends SmartBlockEntity {
    public float frontCapacity, frontDemand;
    public float backCapacity, backDemand;

    public RegionalStressGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    private int tickCounter = 0;

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;
        if (++tickCounter < 10) return;
        tickCounter = 0;

        Direction facing = getBlockState().getValue(RegionalStressGaugeBlock.FACING);
        readNetwork(facing, true);
        readNetwork(facing.getOpposite(), false);
        setChanged();
        //System.out.println("front demand: " + frontDemand + " , capacity: " + frontCapacity);
        //System.out.println("back demand: " + backDemand + " , capacity: " + backCapacity);
        // sync to client
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void readNetwork(Direction dir, boolean isFront) {
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
        float capacity = 0, demand = 0;

        if (neighbor instanceof KineticBlockEntity kinetic) {
            KineticNetwork network = kinetic.getOrCreateNetwork();
            if (network != null) {
                capacity = network.calculateCapacity();
                demand = network.calculateStress();
            }
        }

        if (isFront) {
            frontCapacity = capacity;
            frontDemand = demand;
        } else {
            backCapacity = capacity;
            backDemand = demand;
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("FrontCapacity", frontCapacity);
        tag.putFloat("FrontDemand", frontDemand);
        tag.putFloat("BackCapacity", backCapacity);
        tag.putFloat("BackDemand", backDemand);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        frontCapacity = tag.getFloat("FrontCapacity");
        frontDemand = tag.getFloat("FrontDemand");
        backCapacity = tag.getFloat("BackCapacity");
        backDemand = tag.getFloat("BackDemand");
    }
}