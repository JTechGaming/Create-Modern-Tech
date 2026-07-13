package com.cybrisoft.createmoderntech.block.warpgate;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpGateTerminalBlock extends Block implements IBE<WarpGateTerminalBlockEntity>, IWrenchable {
    public WarpGateTerminalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<WarpGateTerminalBlockEntity> getBlockEntityClass() {
        return WarpGateTerminalBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WarpGateTerminalBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.WARP_GATE_TERMINAL.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity ble = level.getBlockEntity(pos);
        if (ble instanceof WarpGateTerminalBlockEntity be) {
            be.drawGuides = !be.drawGuides;
        }

        return InteractionResult.FAIL;
    }
}
