package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public record EndWarpTransitionPacket(UUID sublevel) implements CustomPacketPayload {

    public static final Type<EndWarpTransitionPacket> TYPE =
            new Type<>(CreateModernTech.asResource("end_warp_transition"));

    public static final StreamCodec<FriendlyByteBuf, EndWarpTransitionPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, EndWarpTransitionPacket::sublevel,
                    EndWarpTransitionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
