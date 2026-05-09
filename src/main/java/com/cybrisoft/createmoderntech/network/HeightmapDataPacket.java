package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

public record HeightmapDataPacket(BlockPos blockEntityPos, Map<Long, short[]> heightmaps)
        implements CustomPacketPayload {

    public static final Type<HeightmapDataPacket> TYPE =
            new Type<>(CreateModernTech.asResource("heightmap_data"));

    // StreamCodec for short[], write length then each value
    private static final StreamCodec<ByteBuf, short[]> SHORT_ARRAY_CODEC = StreamCodec.of(
            (buf, arr) -> {
                buf.writeShort(arr.length);
                for (short s : arr) buf.writeShort(s);
            },
            buf -> {
                short[] arr = new short[buf.readShort()];
                for (int i = 0; i < arr.length; i++) arr[i] = buf.readShort();
                return arr;
            }
    );

    public static final StreamCodec<ByteBuf, HeightmapDataPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, HeightmapDataPacket::blockEntityPos,
                    ByteBufCodecs.map(java.util.HashMap::new, ByteBufCodecs.VAR_LONG, SHORT_ARRAY_CODEC),
                    HeightmapDataPacket::heightmaps,
                    HeightmapDataPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}