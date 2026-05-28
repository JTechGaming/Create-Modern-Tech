package com.cybrisoft.createmoderntech.block.aicore;

import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockItem;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockItem;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class AICoreBlock  extends Block implements IBE<AICoreBlockEntity> {
    public AICoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, be -> {
            ItemStack held = player.getMainHandItem();

            if (held.getItem() instanceof SpeakerBlockItem || held.getItem() instanceof AudioTriggerBlockItem) {
                if (be.networkId == null) throw new IllegalStateException("AICore networkID is null");
                held.set(ModDataComponents.AI_NETWORK_ID.get(), be.networkId);
                be.setChanged();
                player.displayClientMessage(Component.literal("Network ID copied to item"), true);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }

    @Override
    public Class<AICoreBlockEntity> getBlockEntityClass() {
        return AICoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AICoreBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AI_CORE_BLOCK.get();
    }
}
