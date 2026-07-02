package com.cybrisoft.createmoderntech.block.volumetric.controller.pan;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PanZControllerBlock extends VolumetricControllerBlock implements IBE<PanZControllerBlockEntity>, IWrenchable {
    public PanZControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.SOUTH;
    }

    @Override
    public Class<PanZControllerBlockEntity> getBlockEntityClass() {
        return PanZControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PanZControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.PAN_Z_CONTROLLER.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.FAIL;
    }
}
