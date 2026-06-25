package com.cybrisoft.createmoderntech.util;

import com.cybrisoft.createmoderntech.client.ClientPacketHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChunkCache {
    private static final int LOD_STEP = 1;

    private List<VoxelData> voxels = new ArrayList<>();

    public void update(Level level, BlockPos centerPos, int chunkRadius, Map<Long, short[]> fallback) {
        List<VoxelData> newVoxels = new ArrayList<>();

        int centerChunkX = centerPos.getX() >> 4;
        int centerChunkZ = centerPos.getZ() >> 4;
        int diameter = chunkRadius * 2 + 1;

        ChunkAccess[][] chunks = new ChunkAccess[diameter][diameter];
        for (int cx = 0; cx < diameter; cx++)
            for (int cz = 0; cz < diameter; cz++)
                chunks[cx][cz] = level.getChunk(centerChunkX - chunkRadius + cx,
                        centerChunkZ - chunkRadius + cz);

        for (int ci = 0; ci < diameter; ci++) {
            for (int ck = 0; ck < diameter; ck++) {
                ChunkAccess chunk = chunks[ci][ck];
                if (chunk == null) continue;

                int worldChunkX = centerChunkX - chunkRadius + ci;
                int worldChunkZ = centerChunkZ - chunkRadius + ck;

                for (int lx = 0; lx < 16; lx += LOD_STEP) {
                    for (int lz = 0; lz < 16; lz += LOD_STEP) {
                        int worldX = (worldChunkX << 4) + lx;
                        int worldZ = (worldChunkZ << 4) + lz;

                        long key = ClientPacketHandlers.packChunkPos(worldChunkX, worldChunkZ);
                        short[] heights = fallback.get(key);

                        int worldY;
                        if (heights != null) {
                            worldY = heights[lx * 16 + lz];
                        } else if (level.hasChunk(worldChunkX, worldChunkZ)) {
                            worldY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, lx, lz) - 1;
                        } else {
                            continue;
                        }

                        if (worldY < -64 || worldY > 320) continue;

                        int yWest  = sampleHeight(chunks, ci, ck, lx - LOD_STEP, lz,            diameter);
                        int yEast  = sampleHeight(chunks, ci, ck, lx + LOD_STEP, lz,            diameter);
                        int yNorth = sampleHeight(chunks, ci, ck, lx,            lz - LOD_STEP, diameter);
                        int ySouth = sampleHeight(chunks, ci, ck, lx,            lz + LOD_STEP, diameter);

                        boolean exposedWest  = worldY > yWest;
                        boolean exposedEast  = worldY > yEast;
                        boolean exposedNorth = worldY > yNorth;
                        boolean exposedSouth = worldY > ySouth;

                        int lo = Math.min(Math.min(yWest, yEast), Math.min(yNorth, ySouth));
                        if (lo == Integer.MAX_VALUE) lo = worldY;
                        int heightDiff = Math.max(1, worldY - lo);

                        newVoxels.add(new VoxelData(worldX, worldY, worldZ, heightDiff,
                                exposedWest, exposedEast, exposedNorth, exposedSouth, false));
                    }
                }

                boolean hasData = level.hasChunk(worldChunkX, worldChunkZ) ||
                        fallback.containsKey(ClientPacketHandlers.packChunkPos(worldChunkX, worldChunkZ));
                if (!hasData) {
                    newVoxels.add(new VoxelData(
                            (worldChunkX << 4) + 8, 63, (worldChunkZ << 4) + 8, 0,
                            false, false, false, false, true
                    ));
                }
            }
        }

        // Replace atomically so the render thread never sees a half-built list
        voxels = newVoxels;
    }

    private static int sampleHeight(ChunkAccess[][] chunks, int ci, int ck,
                                    int lx, int lz, int diameter) {
        int nci = ci, nck = ck, nlx = lx, nlz = lz;

        while (nlx < 0)   { nlx += 16; nci--; }
        while (nlx >= 16) { nlx -= 16; nci++; }
        while (nlz < 0)   { nlz += 16; nck--; }
        while (nlz >= 16) { nlz -= 16; nck++; }

        if (nci < 0 || nci >= diameter || nck < 0 || nck >= diameter) return Integer.MAX_VALUE;
        ChunkAccess neighbor = chunks[nci][nck];
        if (neighbor == null) return Integer.MAX_VALUE;
        return neighbor.getHeight(Heightmap.Types.WORLD_SURFACE, nlx, nlz) - 1;
    }

    /** Returns the live voxel list, is render thread only. */
    public List<VoxelData> getVoxels() { return voxels; }

    public List<VoxelData> snapshotVoxels() { return voxels; }

    /** Swaps in a newly built voxel list from the async build, is render thread only. */
    public void swapVoxels(List<VoxelData> newVoxels) { voxels = newVoxels; }

    public static class VoxelData {
        public final int x, y, z, height;
        public final boolean exposedWest, exposedEast, exposedNorth, exposedSouth, isPlaceholder;

        VoxelData(int x, int y, int z, int height,
                  boolean exposedWest, boolean exposedEast,
                  boolean exposedNorth, boolean exposedSouth, boolean isPlaceholder) {
            this.x = x; this.y = y; this.z = z; this.height = height;
            this.exposedWest = exposedWest; this.exposedEast = exposedEast;
            this.exposedNorth = exposedNorth; this.exposedSouth = exposedSouth;
            this.isPlaceholder = isPlaceholder;
        }
    }
}