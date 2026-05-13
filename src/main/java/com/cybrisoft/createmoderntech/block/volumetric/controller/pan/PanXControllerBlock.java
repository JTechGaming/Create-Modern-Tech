package com.cybrisoft.createmoderntech.block.volumetric.controller.pan;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PanXControllerBlock extends VolumetricControllerBlock implements IBE<PanXControllerBlockEntity> {
    public PanXControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.EAST;
    }

    @Override
    public Class<PanXControllerBlockEntity> getBlockEntityClass() {
        return PanXControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PanXControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.PAN_X_CONTROLLER.get();
    }
}
