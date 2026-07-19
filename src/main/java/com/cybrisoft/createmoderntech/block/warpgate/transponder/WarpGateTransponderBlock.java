package com.cybrisoft.createmoderntech.block.warpgate.transponder;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpGateTransponderBlock extends KineticBlock implements IBE<WarpGateTransponderBlockEntity> {
    public WarpGateTransponderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<WarpGateTransponderBlockEntity> getBlockEntityClass() {
        return WarpGateTransponderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WarpGateTransponderBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.WARP_GATE_TRANSPONDER.get();
    }
}
