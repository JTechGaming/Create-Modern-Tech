package com.cybrisoft.createmoderntech.block.lens;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class VerticalLensBlock extends DirectionalBlock implements IWrenchable {
    // UP (270X)
    private static final VoxelShape UP_SHAPE = Shapes.or(
            Block.box(1, 14, 13, 15, 16, 15), Block.box(1, 14, 1, 15, 16, 3),
            Block.box(13, 14, 3, 15, 16, 13), Block.box(1, 14, 3, 3, 16, 13),
            Block.box(1, 10, 1, 15, 14, 15), Block.box(2, 4, 2, 14, 10, 14),
            Block.box(3, 2, 3, 13, 4, 13), Block.box(11, 0, 3, 13, 2, 13),
            Block.box(3, 0, 3, 5, 2, 13), Block.box(5, 0, 3, 11, 2, 5),
            Block.box(5, 0, 11, 11, 2, 13)
    );

    // DOWN (90X)
    private static final VoxelShape DOWN_SHAPE = Shapes.or(
            Block.box(1, 0, 1, 15, 2, 3), Block.box(1, 0, 13, 15, 2, 15),
            Block.box(13, 0, 3, 15, 2, 13), Block.box(1, 0, 3, 3, 2, 13),
            Block.box(1, 2, 1, 15, 6, 15), Block.box(2, 6, 2, 14, 12, 14),
            Block.box(3, 12, 3, 13, 14, 13), Block.box(11, 14, 3, 13, 16, 13),
            Block.box(3, 14, 3, 5, 16, 13), Block.box(5, 14, 11, 11, 16, 13),
            Block.box(5, 14, 3, 11, 16, 5)
    );

    public VerticalLensBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
            default -> UP_SHAPE;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isVertical()) {
            return this.defaultBlockState().setValue(FACING, clickedFace.getOpposite());
        }
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection());
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockState newState = state.setValue(FACING, state.getValue(FACING) == Direction.UP ? Direction.DOWN : Direction.UP);
        context.getLevel().setBlock(context.getClickedPos(), newState, 3);
        return InteractionResult.SUCCESS;
    }
}