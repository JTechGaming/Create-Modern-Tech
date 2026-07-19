package com.cybrisoft.createmoderntech.block.warpgate.drive;

import com.cybrisoft.createmoderntech.block.warpgate.amplifier.WarpAmplifierBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpDriveBlockEntity extends KineticBlockEntity {
    public boolean isOn = false;

    public WarpDriveBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null) return;

        boolean isPowered = level.hasNeighborSignal(worldPosition);
        boolean isSpinning = getSpeed() != 0;

        isOn = isPowered && isSpinning;
    }
}
