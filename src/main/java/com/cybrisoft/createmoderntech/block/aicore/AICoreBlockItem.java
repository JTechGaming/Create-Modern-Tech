package com.cybrisoft.createmoderntech.block.aicore;

import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

import java.util.UUID;

public class AICoreBlockItem extends BlockItem {
    public AICoreBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            UUID id = context.getItemInHand().get(ModDataComponents.AI_NETWORK_ID.get());
            if (id != null && context.getLevel().getBlockEntity(pos) instanceof AICoreBlockEntity be) {
                be.networkId = id;
                be.setChanged();
            }
        }
        return result;
    }
}
