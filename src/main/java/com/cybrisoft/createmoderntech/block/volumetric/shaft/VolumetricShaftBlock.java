package com.cybrisoft.createmoderntech.block.volumetric.shaft;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VolumetricShaftBlock extends KineticBlock implements IBE<VolumetricShaftBlockEntity> {
    public VolumetricShaftBlock(Properties properties) {
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
    public Class<VolumetricShaftBlockEntity> getBlockEntityClass() {
        return VolumetricShaftBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends VolumetricShaftBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.VOLUMETRIC_SHAFT.get();
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (level.getBlockEntity(pos) instanceof VolumetricShaftBlockEntity shaft) {
            shaft.markLayoutDirty();
        }
    }
}