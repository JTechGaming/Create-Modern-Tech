package com.cybrisoft.createmoderntech.block.volumetric.controller;

import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VolumetricControllerBlockEntity extends KineticBlockEntity {
    protected float accumulatedValue = 0f;
    protected BlockPos shaftPos = null;
    protected float sensitivity = 0.01f;// How many units per RPM per tick

    public VolumetricControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide()) return;
        if (shaftPos == null) return;

        float speed = getSpeed();
        if (Math.abs(speed) < 0.01f) return;

        accumulatedValue += speed * sensitivity;
        accumulatedValue = clampValue(accumulatedValue);

        if (level.getBlockEntity(shaftPos) instanceof VolumetricShaftBlockEntity shaft) {
            shaft.setControllerValue(getControllerType(), accumulatedValue);
        }
    }

    /**
     * Subclasses return their specific controller type.
     */
    public abstract VolumetricShaftBlockEntity.ControllerType getControllerType();

    /**
     * Subclasses can override to clamp the accumulated value (e.g. pitch to -90..90).
     * Default is unclamped.
     */
    protected float clampValue(float value) {
        return value;
    }

    public void setShaftPos(BlockPos pos) {
        this.shaftPos = pos;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        accumulatedValue = tag.getFloat("AccumulatedValue");
        if (tag.contains("ShaftX")) {
            shaftPos = new BlockPos(tag.getInt("ShaftX"), tag.getInt("ShaftY"), tag.getInt("ShaftZ"));
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("AccumulatedValue", accumulatedValue);
        if (shaftPos != null) {
            tag.putInt("ShaftX", shaftPos.getX());
            tag.putInt("ShaftY", shaftPos.getY());
            tag.putInt("ShaftZ", shaftPos.getZ());
        }
    }
}