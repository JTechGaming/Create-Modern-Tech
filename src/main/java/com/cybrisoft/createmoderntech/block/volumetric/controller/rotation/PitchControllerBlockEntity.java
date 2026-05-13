package com.cybrisoft.createmoderntech.block.volumetric.controller.rotation;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PitchControllerBlockEntity extends VolumetricControllerBlockEntity {
    public PitchControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        sensitivity = 0.04f;
    }

    @Override
    public VolumetricShaftBlockEntity.ControllerType getControllerType() {
        return VolumetricShaftBlockEntity.ControllerType.PITCH;
    }

    // Pitch clamps to -90..90
    @Override
    protected float clampValue(float value) {
        return Math.max(-90f, Math.min(90f, value));
    }
}
