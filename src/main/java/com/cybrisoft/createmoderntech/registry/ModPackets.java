package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.network.HeightmapDataPacket;
import com.cybrisoft.createmoderntech.network.RequestHeightmapPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // Server -> Client
        registrar.playToClient(
                HeightmapDataPacket.TYPE,
                HeightmapDataPacket.CODEC,
                ModPackets::handleHeightmapData
        );
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

    // -------------------------------------------------------------------------
    // Client-side handler: receive heightmap data, store on block entity
    // -------------------------------------------------------------------------

    private static void handleHeightmapData(HeightmapDataPacket packet,
                                            net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level == null) return;

            if (mc.level.getBlockEntity(packet.blockEntityPos()) instanceof VolumetricDisplayBlockEntity be) {
                be.heightmapCache.putAll(packet.heightmaps());
                be.vboDirty = true;
                be.lastCacheUpdate = 0;
            }
        });
    }

    // -------------------------------------------------------------------------
    // Helper: pack chunk coords the same way on both sides
    // -------------------------------------------------------------------------

    public static long packChunkPos(int cx, int cz) {
        return ((long) cz << 32) | (cx & 0xFFFFFFFFL);
    }

    // -------------------------------------------------------------------------
    // Client-side: request chunks not already cached
    // -------------------------------------------------------------------------

    public static void requestMissingChunks(VolumetricDisplayBlockEntity be,
                                            BlockPos sampleCenter, int chunkRadius) {
        int centerCX = sampleCenter.getX() >> 4;
        int centerCZ = sampleCenter.getZ() >> 4;

        List<Long> toRequest = new ArrayList<>();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int cx = centerCX + dx;
                int cz = centerCZ + dz;
                long key = packChunkPos(cx, cz);

                if (!be.heightmapCache.containsKey(key)) {
                    toRequest.add(key);
                }
            }
        }

        if (!toRequest.isEmpty()) {
            PacketDistributor.sendToServer(new RequestHeightmapPacket(be.getBlockPos(), toRequest));
        }
    }
}