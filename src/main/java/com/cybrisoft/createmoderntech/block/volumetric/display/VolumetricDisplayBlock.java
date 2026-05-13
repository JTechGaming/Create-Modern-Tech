package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VolumetricDisplayBlock extends DirectionalKineticBlock implements IBE<VolumetricDisplayBlockEntity> {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 1, 3),
            Block.box(0, 0, 13, 16, 1, 16),
            Block.box(13, 0, 3, 16, 1, 13),
            Block.box(0, 0, 3, 3, 1, 13),
            Block.box(0, 1, 0, 16, 2, 16),

            Block.box(2, 2, 2, 14, 10, 14),

            Block.box(0, 10, 0, 16, 14, 16),

            Block.box(10.5, 14, 5.5, 13, 16, 10.5),
            Block.box(3, 14, 5.5, 5.5, 16, 10.5),
            Block.box(3, 14, 10.5, 13, 16, 13),
            Block.box(3, 14, 3, 13, 16, 5.5)
    );

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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
