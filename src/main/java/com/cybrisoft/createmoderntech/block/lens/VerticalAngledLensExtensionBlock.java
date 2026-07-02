package com.cybrisoft.createmoderntech.block.lens;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class VerticalAngledLensExtensionBlock extends Block implements IWrenchable {
    private static final VoxelShape SHAPE_NORTH = Block.box(5, 5, 0, 11, 16, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(5, 5, 5, 16, 16, 11);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5, 5, 5, 11, 16, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 5, 5, 11, 16, 11);

    private static final VoxelShape SHAPE_NORTH_DOWN = Block.box(5, 0, 0, 11, 11, 11);
    private static final VoxelShape SHAPE_EAST_DOWN = Block.box(5, 0, 5, 16, 11, 11);
    private static final VoxelShape SHAPE_SOUTH_DOWN = Block.box(5, 0, 5, 11, 11, 16);
    private static final VoxelShape SHAPE_WEST_DOWN = Block.box(0, 0, 5, 11, 11, 11);

    public VerticalAngledLensExtensionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.ORIENTATION, FrontAndTop.NORTH_UP));
    }

    @Override
    public MapCodec<? extends DirectionalBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ORIENTATION);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.ORIENTATION)) {
            case UP_NORTH, NORTH_UP -> SHAPE_NORTH;
            case UP_EAST, EAST_UP -> SHAPE_EAST;
            case UP_SOUTH, SOUTH_UP -> SHAPE_SOUTH;
            case UP_WEST, WEST_UP -> SHAPE_WEST;
            case DOWN_NORTH -> SHAPE_NORTH_DOWN;
            case DOWN_EAST -> SHAPE_EAST_DOWN;
            case DOWN_SOUTH -> SHAPE_SOUTH_DOWN;
            case DOWN_WEST -> SHAPE_WEST_DOWN;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction verticalDir = context.getNearestLookingVerticalDirection();
        boolean isUp = verticalDir == Direction.UP;
        Direction horizontalDir = context.getHorizontalDirection().getOpposite();
        FrontAndTop result = null;
        switch (horizontalDir) {
            case UP, DOWN, NORTH -> result = isUp ? FrontAndTop.UP_NORTH : FrontAndTop.DOWN_NORTH;
            case SOUTH -> result = isUp ? FrontAndTop.UP_SOUTH : FrontAndTop.DOWN_SOUTH;
            case WEST -> result = isUp ? FrontAndTop.UP_WEST : FrontAndTop.DOWN_WEST;
            case EAST -> result = isUp ? FrontAndTop.UP_EAST : FrontAndTop.DOWN_EAST;
        }

        return this.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, result);
    }

    private static FrontAndTop getNext(BlockState state) {
        FrontAndTop current = state.getValue(BlockStateProperties.ORIENTATION);
        return switch (current) {
            case DOWN_NORTH -> FrontAndTop.DOWN_EAST;
            case DOWN_EAST -> FrontAndTop.DOWN_SOUTH;
            case DOWN_SOUTH -> FrontAndTop.DOWN_WEST;
            case DOWN_WEST -> FrontAndTop.UP_NORTH;
            case UP_NORTH -> FrontAndTop.UP_EAST;
            case UP_EAST -> FrontAndTop.UP_SOUTH;
            case UP_SOUTH -> FrontAndTop.UP_WEST;
            case UP_WEST -> FrontAndTop.DOWN_NORTH;
            case WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP -> FrontAndTop.DOWN_NORTH;
        };
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockState newState = state.setValue(BlockStateProperties.ORIENTATION, getNext(state));
        context.getLevel().setBlock(context.getClickedPos(), newState, 3);
        return InteractionResult.SUCCESS;
    }
}