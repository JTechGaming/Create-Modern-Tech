package com.cybrisoft.createmoderntech.block.volumetric.controller.beacon;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlock;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.capabilities.Capabilities;
import com.simibubi.create.foundation.block.IBE;

public class BeaconControllerBlock extends VolumetricControllerBlock implements IBE<BeaconControllerBlockEntity> {
    // set/clear beacon
    public static final BooleanProperty POWERED_TOP = BooleanProperty.create("powered_top");
    // output compass
    public static final BooleanProperty POWERED_BOTTOM = BooleanProperty.create("powered_bottom");

    public BeaconControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED_TOP, false)
                .setValue(POWERED_BOTTOM, false));
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.NORTH;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED_TOP, POWERED_BOTTOM);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide()) return;

        boolean newTop    = level.getSignal(pos.above(), Direction.DOWN) > 0;
        boolean newBottom = level.getSignal(pos.below(), Direction.UP) > 0;

        boolean wasTop    = state.getValue(POWERED_TOP);
        boolean wasBottom = state.getValue(POWERED_BOTTOM);

        BlockState newState = state
                .setValue(POWERED_TOP, newTop)
                .setValue(POWERED_BOTTOM, newBottom);
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof BeaconControllerBlockEntity be) {
            // set or clear beacon at cursor
            if (newTop && !wasTop) be.onSetClearTriggered();
            // output compass for selected beacon
            if (newBottom && !wasBottom) be.onOutputCompassTriggered();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED_TOP, false)
                .setValue(POWERED_BOTTOM, false);
    }

    @Override
    public Class<BeaconControllerBlockEntity> getBlockEntityClass() {
        return BeaconControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeaconControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.BEACON_CONTROLLER.get();
    }
}