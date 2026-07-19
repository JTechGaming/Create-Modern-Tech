package com.cybrisoft.createmoderntech.block.warpgate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class WarpGateBlock extends Block {
    public static final EnumProperty<WarpGateSegment> SEGMENT =
            EnumProperty.create("segment", WarpGateSegment.class);

    public WarpGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SEGMENT, WarpGateSegment.VERTICAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEGMENT);
    }

    public static WarpGateSegment computeSegment(BlockPos ringPos, BlockPos centerPos, boolean isZ) {
        int dx = ringPos.getX() - centerPos.getX();
        int dy = ringPos.getY() - centerPos.getY();
        int dz = ringPos.getZ() - centerPos.getZ();

        // lateral offset along the ring plane axis
        int lateral = isZ ? dz : dx;

        double angle = Math.toDegrees(Math.atan2(dy, lateral));
        // angle: 90=top, -90=bottom, 0=right, 180/-180=left

        boolean goingUp = lateral > 0; // positive lateral = ascending on that side

        if (Math.abs(lateral) <= 1) return WarpGateSegment.VERTICAL; // sides
        if (Math.abs(dy) <= 1) return isZ ? WarpGateSegment.HORIZONTAL_Z : WarpGateSegment.HORIZONTAL_X;

        // diagonals
        if (isZ) return goingUp ? WarpGateSegment.DIAGONAL_Z_UP : WarpGateSegment.DIAGONAL_Z_DOWN;
        else return goingUp ? WarpGateSegment.DIAGONAL_X_UP : WarpGateSegment.DIAGONAL_X_DOWN;
    }
}
