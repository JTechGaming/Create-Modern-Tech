package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.ui.AudioTriggerScreen;
import com.cybrisoft.createmoderntech.util.TriggerVariableEntry;
import com.mojang.datafixers.types.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record OpenAudioTriggerScreenPacket(BlockPos pos, String message, List<TriggerVariableEntry> vars)
        implements CustomPacketPayload {

    public static final Type<OpenAudioTriggerScreenPacket> TYPE =
            new Type<>(CreateModernTech.asResource("open_audio_trigger_screen"));

    public static final StreamCodec<FriendlyByteBuf, OpenAudioTriggerScreenPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, OpenAudioTriggerScreenPacket::pos,
                    ByteBufCodecs.STRING_UTF8, OpenAudioTriggerScreenPacket::message,
                    TriggerVariableEntry.LIST_CODEC, OpenAudioTriggerScreenPacket::vars,
                    OpenAudioTriggerScreenPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}