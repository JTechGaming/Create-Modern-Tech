package com.cybrisoft.createmoderntech.block.volumetric.controller.rotation;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class YawControllerBlockEntity extends VolumetricControllerBlockEntity {
    public YawControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        sensitivity = 0.04f; // degrees per RPM per tick
    }

    @Override
    public VolumetricShaftBlockEntity.ControllerType getControllerType() {
        return VolumetricShaftBlockEntity.ControllerType.YAW;
    }

    // Yaw wraps 0-360
    @Override
    protected float clampValue(float value) {
        return value % 360f;
    }
}
