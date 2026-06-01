package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateAudioTriggerPacket(BlockPos pos, String message) implements CustomPacketPayload {

    public static final Type<UpdateAudioTriggerPacket> TYPE =
            new Type<>(CreateModernTech.asResource("update_audio_trigger"));

    public static final StreamCodec<FriendlyByteBuf, UpdateAudioTriggerPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, UpdateAudioTriggerPacket::pos,
                    ByteBufCodecs.STRING_UTF8, UpdateAudioTriggerPacket::message,
                    UpdateAudioTriggerPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
