package com.cybrisoft.createmoderntech.block.speaker;

import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class SpeakerBlockItem extends BlockItem {
    public SpeakerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            UUID id = context.getItemInHand().get(ModDataComponents.SPEAKER_NETWORK_ID.get());
            if (id != null && context.getLevel().getBlockEntity(pos) instanceof SpeakerBlockEntity be) {
                be.networkId = id;
                be.setChanged();
            }
        }
        return result;
    }
}
