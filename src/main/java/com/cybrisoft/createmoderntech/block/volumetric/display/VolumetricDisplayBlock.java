package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VolumetricDisplayBlock extends DirectionalKineticBlock implements IBE<VolumetricDisplayBlockEntity> {
    public VolumetricDisplayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean crouching = context.getPlayer() != null && context.getPlayer().isCrouching();

        Direction vertical = context.getPlayer() != null && context.getPlayer().getXRot() > 0
                ? Direction.UP : Direction.DOWN ;

        return defaultBlockState().setValue(FACING, crouching ? vertical : vertical.getOpposite());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public Class<VolumetricDisplayBlockEntity> getBlockEntityClass() {
        return VolumetricDisplayBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends VolumetricDisplayBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get();
    }
}
