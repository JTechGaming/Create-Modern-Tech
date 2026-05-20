package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;

public record PlaySpeakerPacket(UUID networkId, String message, List<BlockPos> speakerPositions)
        implements CustomPacketPayload {

    public static final Type<PlaySpeakerPacket> TYPE =
            new Type<>(CreateModernTech.asResource("play_speaker"));

    public static final StreamCodec<FriendlyByteBuf, PlaySpeakerPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(UUIDUtil.CODEC), PlaySpeakerPacket::networkId,
                    ByteBufCodecs.STRING_UTF8, PlaySpeakerPacket::message,
                    BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), PlaySpeakerPacket::speakerPositions,
                    PlaySpeakerPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}