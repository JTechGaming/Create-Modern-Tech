package com.cybrisoft.createmoderntech.block.warpgate.amplifier;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpAmplifierBlock extends KineticBlock implements IBE<WarpAmplifierBlockEntity> {
    public WarpAmplifierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    public Class<WarpAmplifierBlockEntity> getBlockEntityClass() {
        return WarpAmplifierBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WarpAmplifierBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.WARP_AMPLIFIER.get();
    }
}
