package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record RequestHeightmapPacket(BlockPos blockEntityPos, List<Long> chunkCoords)
        implements CustomPacketPayload {

    public static final Type<RequestHeightmapPacket> TYPE =
            new Type<>(CreateModernTech.asResource("request_heightmap"));

    public static final StreamCodec<ByteBuf, RequestHeightmapPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RequestHeightmapPacket::blockEntityPos,
                    ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()), RequestHeightmapPacket::chunkCoords,
                    RequestHeightmapPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}