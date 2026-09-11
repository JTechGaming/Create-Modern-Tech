package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockEntity;
import com.cybrisoft.createmoderntech.client.ClientPacketHandlers;
import com.cybrisoft.createmoderntech.network.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.HashMap;
import java.util.Map;

public class ModClientPackets {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModClientPackets::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Server -> Client
        registrar.playToClient(
                OpenAudioTriggerScreenPacket.TYPE,
                OpenAudioTriggerScreenPacket.CODEC,
                ClientPacketHandlers::handleOpenAudioTriggerScreen
        );
        registrar.playToClient(
                PlaySpeakerPacket.TYPE,
                PlaySpeakerPacket.CODEC,
                ClientPacketHandlers::handlePlaySpeaker
        );
        registrar.playToClient(
                HeightmapDataPacket.TYPE,
                HeightmapDataPacket.CODEC,
                ClientPacketHandlers::handleHeightmapData
        );
        registrar.playToClient(
                StartWarpTransitionPacket.TYPE,
                StartWarpTransitionPacket.CODEC,
                ClientPacketHandlers::handleStart
        );
        registrar.playToClient(
                EndWarpTransitionPacket.TYPE,
                EndWarpTransitionPacket.CODEC,
                ClientPacketHandlers::handleEnd
        );
    }
}