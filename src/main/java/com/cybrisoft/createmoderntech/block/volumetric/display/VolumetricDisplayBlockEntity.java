package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.cybrisoft.createmoderntech.util.ChunkCache;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VolumetricDisplayBlockEntity extends KineticBlockEntity {
    // Radius lerp state
    float startRadius = 0f;
    float targetRadius = 0f;
    float currentRadius = 0f;
    float radiusLerp = 0f;
    int lastIntRadius = 0;

    float lastPartialTicks = 0f;

    // --- Controls state ---
    public float panX = 0f;
    public float panZ = 0f;
    public float smoothPanX = 0f;
    public float smoothPanZ = 0f;
    public float yaw = 0f;
    public float pitch = 0f;
    public boolean chunkRequestDirty = true;

    public final List<BeaconData> beacons = new ArrayList<>();

    // Lens config cache
    float cachedMagnification = -1f;
    Vector3f cachedOffset = new Vector3f(0, -1, 0);
    float[] cachedColor = null;
    public LensConfig lensConfig = null;

    // --- Chunk cache state ---
    Object chunkCache = null;

    public long lastCacheUpdate = 0;
    BlockPos lastCenterPos = null;
    public BlockPos pendingIntCenter;
    public BlockPos bakingIntCenter;
    public final Map<Long, short[]> heightmapCache = new HashMap<>();
    ByteBufferBuilder sharedByteBuffer = null;

    // --- VBO state ---
    VertexBuffer staticVBO = null;
    public boolean vboDirty = true;

    // --- Async rebuild state ---
    volatile MeshData pendingMesh = null;

    //volatile List<ChunkCache.VoxelData> pendingVoxels = null;
    public volatile Object pendingVoxels = null;

    CompletableFuture<Void> rebuildFuture = null;

    public VolumetricDisplayBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("PanX", panX);
        tag.putFloat("PanZ", panZ);
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);

        ListTag beaconList = new ListTag();
        for (BeaconData beacon : beacons) {
            CompoundTag b = new CompoundTag();
            b.putFloat("X", beacon.x);
            b.putFloat("Z", beacon.z);
            b.putInt("Color", beacon.color);
            beaconList.add(b);
        }
        tag.put("Beacons", beaconList);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        panX = tag.getFloat("PanX");
        panZ = tag.getFloat("PanZ");
        yaw = tag.getFloat("Yaw");
        pitch = tag.getFloat("Pitch");

        beacons.clear();
        ListTag beaconList = tag.getList("Beacons", Tag.TAG_COMPOUND);
        for (int i = 0; i < beaconList.size(); i++) {
            CompoundTag b = beaconList.getCompound(i);
            beacons.add(new BeaconData(b.getFloat("X"), b.getFloat("Z"), b.getInt("Color")));
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (level != null && level.isClientSide) {
            if (staticVBO != null) {
                staticVBO.close();
                staticVBO = null;
            }
            if (pendingMesh != null) {
                pendingMesh.close();
                pendingMesh = null;
            }
        }
        if (sharedByteBuffer != null) {
            sharedByteBuffer.close();
            sharedByteBuffer = null;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class BeaconData {
        public final float x, z;
        public final int color; // packed 0xRRGGBB

        public BeaconData(float x, float z, int color) {
            this.x = x;
            this.z = z;
            this.color = color;
        }

        public float r() { return ((color >> 16) & 0xFF) / 255f; }
        public float g() { return ((color >> 8)  & 0xFF) / 255f; }
        public float b() { return  (color        & 0xFF) / 255f; }
    }

    public static class LensConfig {
        public float magnification = 1.0f;
        public Vector3f offset = new Vector3f();
        public float[] color = {0.5f, 0.7f, 0.8f, 1.0f};
        public Map<BlockPos, BlockState> lensCache = new HashMap<>();
        public BlockPos nextPos = null;
        public BlockPos startPos, endPos;
        public BlockState startState, endState;
    }
}