package com.cybrisoft.createmoderntech.block.volumetric.controller.rotation;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class YawControllerBlock extends VolumetricControllerBlock implements IBE<YawControllerBlockEntity>, IWrenchable {
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

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.FAIL;
    }
}
