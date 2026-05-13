package com.cybrisoft.createmoderntech.block.volumetric.controller.rotation;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class YawControllerBlock extends VolumetricControllerBlock implements IBE<YawControllerBlockEntity> {
    public YawControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.SOUTH;
    }

    @Override
    public Class<YawControllerBlockEntity> getBlockEntityClass() {
        return YawControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends YawControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.YAW_CONTROLLER.get();
    }
}
