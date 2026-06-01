package com.cybrisoft.createmoderntech.block.gauge;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegionalStressGaugeBlock extends DirectionalKineticBlock implements IBE<RegionalStressGaugeBlockEntity>, IWrenchable {
    public static final BooleanProperty SUPPLIER = BooleanProperty.create("regional_stress_supplier");

    public RegionalStressGaugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(SUPPLIER, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Class<RegionalStressGaugeBlockEntity> getBlockEntityClass() {
        return RegionalStressGaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RegionalStressGaugeBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.REGIONAL_STRESS_GAUGE.get();
    }

    public InteractionResult toggleMode(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        return onBlockEntityUse(level, pos, be -> {
            level.setBlock(pos, state.cycle(SUPPLIER), Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (toggleMode(state, context.getLevel(), context.getClickedPos()) == InteractionResult.SUCCESS) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SUPPLIER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (super.getStateForPlacement(context) == null) {
            return null;
        }

//        if (context.getLevel().getBlockState(context.getClickedPos()).is(this)) {
//
//        }

        if (canSurvive(Objects.requireNonNull(super.getStateForPlacement(context)), context.getLevel(), context.getClickedPos())) {
            return super.getStateForPlacement(context);
        }

        List<Direction> beside = new ArrayList<>();

        switch (Objects.requireNonNull(super.getStateForPlacement(context)).getValue(FACING).getAxis()) {
            case Y -> {
                beside.add(Direction.NORTH);
                beside.add(Direction.SOUTH);
                beside.add(Direction.EAST);
                beside.add(Direction.WEST);
            }
            case X -> {
                beside.add(Direction.UP);
                beside.add(Direction.DOWN);
                beside.add(Direction.EAST);
                beside.add(Direction.WEST);
            }
            case Z -> {
                beside.add(Direction.NORTH);
                beside.add(Direction.SOUTH);
                beside.add(Direction.UP);
                beside.add(Direction.DOWN);
            }
        }

        for (Direction direction : beside) {
            if (canSurvive(Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, direction), context.getLevel(), context.getClickedPos())) {
                return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, direction);
            }
        }

        return canSurvive(Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, Objects.requireNonNull(super.getStateForPlacement(context)).getValue(FACING).getOpposite()), context.getLevel(), context.getClickedPos()) ? Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, Objects.requireNonNull(super.getStateForPlacement(context)).getValue(FACING).getOpposite()) : null;
    }
}