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

public class PanXControllerBlock extends VolumetricControllerBlock implements IBE<PanXControllerBlockEntity>, IWrenchable {
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

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.FAIL;
    }
}
