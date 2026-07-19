package com.cybrisoft.createmoderntech.network;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public record StartWarpTransitionPacket(UUID sublevel, Vec3 velocity) implements CustomPacketPayload {

    public static final Type<StartWarpTransitionPacket> TYPE =
            new Type<>(CreateModernTech.asResource("start_warp_transition"));

    public static final StreamCodec<FriendlyByteBuf, StartWarpTransitionPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, StartWarpTransitionPacket::sublevel,
                    CustomCodecs.VEC3_CODEC, StartWarpTransitionPacket::velocity,
                    StartWarpTransitionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
