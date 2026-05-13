package com.cybrisoft.createmoderntech.block.volumetric.controller.pan;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PanZControllerBlockEntity extends VolumetricControllerBlockEntity {
    public PanZControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        sensitivity = 0.01f;
    }

    @Override
    public VolumetricShaftBlockEntity.ControllerType getControllerType() {
        return VolumetricShaftBlockEntity.ControllerType.PAN_Z;
    }
}
