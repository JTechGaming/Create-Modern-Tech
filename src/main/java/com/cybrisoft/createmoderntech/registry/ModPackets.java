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

public class ModPackets {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModPackets::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Client -> Server
        registrar.playToServer(
                RequestHeightmapPacket.TYPE,
                RequestHeightmapPacket.CODEC,
                ModPackets::handleRequestHeightmap
        );
        registrar.playToServer(
                UpdateAudioTriggerPacket.TYPE,
                UpdateAudioTriggerPacket.CODEC,
                ModPackets::handleUpdateAudioTrigger
        );

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

    private static void handleUpdateAudioTrigger(UpdateAudioTriggerPacket packet,
                                                 IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.getBlockEntity(packet.pos()) instanceof AudioTriggerBlockEntity be) {
                be.message = packet.message();
                be.setChanged();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Server-side handler: receive chunk requests, sample heightmaps, send back
    // -------------------------------------------------------------------------

    private static void handleRequestHeightmap(RequestHeightmapPacket packet,
                                               net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = player.serverLevel();

            Map<Long, short[]> result = new HashMap<>();

            for (long packedChunk : packet.chunkCoords()) {
                int cx = (int) (packedChunk & 0xFFFFFFFFL);
                int cz = (int) (packedChunk >> 32);

                // Only fulfill if the server has the chunk loaded
                if (!level.hasChunk(cx, cz)) continue;

                ChunkAccess chunk = level.getChunk(cx, cz);
                short[] heights = new short[16 * 16];

                for (int lx = 0; lx < 16; lx++) {
                    for (int lz = 0; lz < 16; lz++) {
                        heights[lx * 16 + lz] = (short) (chunk.getHeight(
                                Heightmap.Types.WORLD_SURFACE, lx, lz) - 1);
                    }
                }

                result.put(packedChunk, heights);
            }

            if (!result.isEmpty()) {
                PacketDistributor.sendToPlayer(player,
                        new HeightmapDataPacket(packet.blockEntityPos(), result));
            }
        });
    }
}