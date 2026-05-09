package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.util.ChunkCache;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VolumetricDisplayBlockEntity extends KineticBlockEntity {
    // --- Lerp state ---
    float startSpeed = 0.0f;
    float targetSpeed = 0.0f;
    float speed = 0.0f;
    float lerp = 0.0f;
    float lastPartialTicks = 0f;

    // --- Pan state ---
    float panX = 0f;
    float panZ = 0f;
    boolean chunkRequestDirty = true;

    // --- Cache state ---
    final ChunkCache chunkCache = new ChunkCache();
    public long lastCacheUpdate = 0;
    BlockPos lastCenterPos = null;
    public final Map<Long, short[]> heightmapCache = new HashMap<>();

    // --- VBO state ---
    VertexBuffer staticVBO = null;
    public boolean vboDirty = true;
    float lastBuiltGrowth = -1f;

    // --- Async rebuild state ---
    // Written by background thread, read/cleared by render thread
    volatile MeshData pendingMesh = null;
    volatile List<ChunkCache.VoxelData> pendingVoxels = null;
    CompletableFuture<Void> rebuildFuture = null;

    public VolumetricDisplayBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
    }
}