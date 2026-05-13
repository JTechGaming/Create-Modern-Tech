package com.cybrisoft.createmoderntech.block.volumetric.controller;

import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VolumetricControllerBlock extends DirectionalKineticBlock {
    public VolumetricControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return getBlockDirection().getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == getBlockDirection() || face == getBlockDirection().getOpposite();
    }

    public abstract Direction getBlockDirection();

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean crouching = context.getPlayer() != null && context.getPlayer().isCrouching();

        Direction vertical = context.getPlayer() != null && context.getPlayer().getXRot() > 0
                ? getBlockDirection() : getBlockDirection().getOpposite();

        return defaultBlockState().setValue(FACING, crouching ? vertical : vertical.getOpposite());
    }
}