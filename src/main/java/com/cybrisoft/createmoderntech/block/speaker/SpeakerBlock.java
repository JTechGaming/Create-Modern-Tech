package com.cybrisoft.createmoderntech.block.speaker;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.property.Properties;

import java.util.UUID;

public class SpeakerBlock extends Block implements IBE<SpeakerBlockEntity> {

    public SpeakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, be -> {
            ItemStack held = player.getMainHandItem();

            // right-clicking with a speaker item
            if (held.getItem() instanceof SpeakerBlockItem) {
                UUID heldId = held.get(ModDataComponents.SPEAKER_NETWORK_ID.get());

                if (heldId != null) {
                    // item already has a UUID so link this placed block to it
                    be.networkId = heldId;
                    be.setChanged();
                    player.displayClientMessage(
                            Component.literal("Speaker linked to network"), true);
                } else {
                    // item has no UUID so copy this block's UUID to the item
                    if (be.networkId == null) be.networkId = UUID.randomUUID();
                    held.set(ModDataComponents.SPEAKER_NETWORK_ID.get(), be.networkId);
                    be.setChanged();
                    player.displayClientMessage(
                            Component.literal("Network ID copied to item"), true);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }

    @Override
    public Class<SpeakerBlockEntity> getBlockEntityClass() {
        return SpeakerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpeakerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SPEAKER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpeakerBlockEntity(ModBlockEntityTypes.SPEAKER.get(), pos, state);
    }
}
