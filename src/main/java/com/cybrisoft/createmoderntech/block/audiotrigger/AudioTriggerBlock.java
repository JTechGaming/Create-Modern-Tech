package com.cybrisoft.createmoderntech.block.audiotrigger;

import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockItem;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockItem;
import com.cybrisoft.createmoderntech.network.OpenAudioTriggerScreenPacket;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.TriggerVariableEntry;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AudioTriggerBlock extends Block implements IBE<AudioTriggerBlockEntity> {

    public AudioTriggerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        return onBlockEntityUse(level, pos, be -> {
            ItemStack held = player.getMainHandItem();

            if (held.getItem() instanceof AICoreBlockItem) {
                if (held.has(ModDataComponents.AI_NETWORK_ID.get())) {
                    be.networkId = held.get(ModDataComponents.AI_NETWORK_ID.get());
                } else {
                    be.networkId = UUID.randomUUID();
                    held.set(ModDataComponents.AI_NETWORK_ID.get(), be.networkId);
                }
                be.setChanged();
                player.displayClientMessage(Component.literal("Network ID copied to item"), true);
                return InteractionResult.SUCCESS;
            }

            PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenAudioTriggerScreenPacket(pos, be.message, be.getVars()));
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AudioTriggerBlockEntity be) {
            // tick handles the actual triggering, this just wakes it up
            be.setChanged();
        }
    }

    @Override
    public Class<AudioTriggerBlockEntity> getBlockEntityClass() {
        return AudioTriggerBlockEntity.class;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, blockState, be) -> {
            if (be instanceof AudioTriggerBlockEntity trigger) trigger.tick();
        };
    }

    @Override
    public BlockEntityType<? extends AudioTriggerBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AUDIO_TRIGGER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AudioTriggerBlockEntity(ModBlockEntityTypes.AUDIO_TRIGGER.get(), pos, state);
    }
}
