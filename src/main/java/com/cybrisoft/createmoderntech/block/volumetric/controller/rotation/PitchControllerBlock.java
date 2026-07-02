package com.cybrisoft.createmoderntech.block.volumetric.controller.rotation;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PitchControllerBlock extends VolumetricControllerBlock implements IBE<PitchControllerBlockEntity>, IWrenchable {
    public PitchControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.EAST;
    }

    @Override
    public Class<PitchControllerBlockEntity> getBlockEntityClass() {
        return PitchControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PitchControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.PITCH_CONTROLLER.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.FAIL;
    }
}
