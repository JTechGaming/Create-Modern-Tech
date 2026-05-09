package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.registry.ModPackets;
import com.cybrisoft.createmoderntech.util.ChunkCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VolumetricDisplayRenderer extends SmartBlockEntityRenderer<VolumetricDisplayBlockEntity> {
    private static final int MAX_CHUNK_RADIUS = 7;

    private static final float DISPLAY_HEIGHT = 1.5f;
    private static final float VOXEL_SIZE = 0.01f;
    private static final float VOXEL_SHAPE_SIZE = 0.005f;
    private static final float VOXEL_ALPHA = 0.4f;
    private static final float SCAN_BAND_WIDTH = 0.02f;

    private static final int LERP_SPEED = 10;
    private static final long CACHE_UPDATE_INTERVAL = 40;

    public VolumetricDisplayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private float easeOutQuart(float in) {
        return (float) (1 - Math.pow(1 - in, 4));
    }

    @Override
    protected void renderSafe(VolumetricDisplayBlockEntity blockEntity, float partialTicks,
                              PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, bufferSource, light, overlay);

        float deltaTicks = partialTicks - blockEntity.lastPartialTicks;
        if (deltaTicks < 0) deltaTicks += 1f;
        blockEntity.lastPartialTicks = partialTicks;

        Level level = blockEntity.getLevel();
        if (level == null) return;

        long currentTime = level.getGameTime();
        BlockPos centerPos = blockEntity.getBlockPos();
        BlockPos sampleCenter = centerPos.offset((int) blockEntity.panX, 0, (int) blockEntity.panZ);

        if (blockEntity.chunkRequestDirty) {
            ModPackets.requestMissingChunks(blockEntity, sampleCenter, MAX_CHUNK_RADIUS);
            blockEntity.chunkRequestDirty = false;
        }

        if (currentTime - blockEntity.lastCacheUpdate >= CACHE_UPDATE_INTERVAL ||
                blockEntity.lastCenterPos == null ||
                blockEntity.lastCenterPos.distSqr(sampleCenter) > 16) {
            blockEntity.chunkCache.update(level, sampleCenter, MAX_CHUNK_RADIUS, blockEntity.heightmapCache);
            blockEntity.lastCacheUpdate = currentTime;
            blockEntity.lastCenterPos = sampleCenter;
            blockEntity.vboDirty = true;
        }

        Matrix4f cameraView = new Matrix4f(RenderSystem.getModelViewMatrix());
        ms.pushPose();
        ms.translate(0.5, DISPLAY_HEIGHT, 0.5);
        renderVolumetricDisplay(ms, bufferSource, blockEntity, cameraView, deltaTicks, sampleCenter);
        ms.popPose();
    }

    private void renderVolumetricDisplay(PoseStack ms, MultiBufferSource bufferSource,
                                         VolumetricDisplayBlockEntity blockEntity,
                                         Matrix4f cameraView, float deltaTicks, BlockPos sampleCenter) {
        // --- Speed lerp ---
        float rawSpeed = Math.abs(blockEntity.getSpeed());
        float currentTarget = (rawSpeed < 0.01f) ? 0.0f : rawSpeed;

        if (blockEntity.targetSpeed != currentTarget) {
            blockEntity.targetSpeed = currentTarget;
            blockEntity.startSpeed = blockEntity.speed;
            blockEntity.lerp = 0f;
        }

        if (blockEntity.lerp < 1f) {
            blockEntity.lerp += deltaTicks / LERP_SPEED;
            if (blockEntity.lerp > 1f) blockEntity.lerp = 1f;
            blockEntity.speed = blockEntity.startSpeed + (blockEntity.targetSpeed - blockEntity.startSpeed) * easeOutQuart(blockEntity.lerp);
        }

        if (blockEntity.speed < 0.001f && blockEntity.targetSpeed == 0.0f) return;

        float growth = Math.min(blockEntity.speed / 256f, 1.0f);
        float time = blockEntity.getLevel().getGameTime() + AnimationTickHolder.getPartialTicks();
        float pulse = 1.0f + ((float) Math.sin(time * 0.2f) * 0.02f);

        float scanSpeed = (blockEntity.speed / 256f) * 0.1f;
        float scanPos = (time * scanSpeed) % 10.0f - 5.0f;

        // Upload finished async build if ready
        if (blockEntity.rebuildFuture != null && blockEntity.rebuildFuture.isDone()) {
            if (blockEntity.pendingMesh != null) {
                if (blockEntity.staticVBO == null)
                    blockEntity.staticVBO = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);

                blockEntity.staticVBO.bind();
                blockEntity.staticVBO.upload(blockEntity.pendingMesh);
                VertexBuffer.unbind();
                blockEntity.pendingMesh = null;
            }

            if (blockEntity.pendingVoxels != null) {
                blockEntity.chunkCache.swapVoxels(blockEntity.pendingVoxels);
                blockEntity.pendingVoxels = null;
            }

            blockEntity.rebuildFuture = null;
        }

        // Kick off new async build if needed and none is running
        if ((blockEntity.vboDirty || Math.abs(growth - blockEntity.lastBuiltGrowth) > 0.01f)
                && blockEntity.rebuildFuture == null) {
            blockEntity.lastBuiltGrowth = growth;
            blockEntity.vboDirty = false;

            // Snapshot inputs, background thread must not touch live state
            List<ChunkCache.VoxelData> snapshot = blockEntity.chunkCache.snapshotVoxels();
            BlockPos capCenter = sampleCenter;
            float capGrowth = growth;

            blockEntity.rebuildFuture = CompletableFuture.runAsync(() -> {
                ByteBufferBuilder byteBuffer = new ByteBufferBuilder(snapshot.size() * 5 * 8 * 28);
                BufferBuilder builder = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                for (ChunkCache.VoxelData voxel : snapshot) {
                    float relX = (voxel.x - capCenter.getX());
                    float relY = (voxel.y - capCenter.getY());
                    float relZ = (voxel.z - capCenter.getZ());

                    float posX = relX * VOXEL_SIZE * capGrowth;
                    float posY = relY * VOXEL_SIZE * capGrowth;
                    float posZ = relZ * VOXEL_SIZE * capGrowth;

                    float heightFactor = (voxel.y - capCenter.getY() + 32) / 64.0f;
                    float r = 0.2f + (heightFactor * 0.4f);
                    float g = 0.5f + (heightFactor * 0.5f);
                    float b = 1.0f;
                    float a = VOXEL_ALPHA;

                    float voxelH = voxel.height * capGrowth;
                    drawVoxelToBuilder(builder, posX, posY, posZ, voxelH, voxel, r, g, b, a);
                }

                blockEntity.pendingMesh = builder.buildOrThrow();
                blockEntity.pendingVoxels = snapshot;
            });
        }

        // Draw static VBO (base colors, no scan)
        if (blockEntity.staticVBO != null) {
            RenderType.lightning().setupRenderState();

            blockEntity.staticVBO.bind();
            Matrix4f modelView = new Matrix4f(cameraView).mul(ms.last().pose());
            blockEntity.staticVBO.drawWithShader(modelView, RenderSystem.getProjectionMatrix(),
                    GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();

            RenderType.lightning().clearRenderState();
        }

        // Inline scan pass — only voxels inside the scan band, submitted each frame
        VertexConsumer scanBuffer = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f matrix = ms.last().pose();
        float scaledScan = scanPos * VOXEL_SIZE * 50f;

        for (ChunkCache.VoxelData voxel : blockEntity.chunkCache.getVoxels()) {
            float relX = (voxel.x - sampleCenter.getX());
            float relY = (voxel.y - sampleCenter.getY());
            float relZ = (voxel.z - sampleCenter.getZ());

            float posZ = relZ * VOXEL_SIZE * growth * pulse;
            float distToScan = Math.abs(posZ - scaledScan);
            if (distToScan > SCAN_BAND_WIDTH) continue;

            float posX = relX * VOXEL_SIZE * growth * pulse;
            float posY = relY * VOXEL_SIZE * growth * pulse;

            float scanHighlight = Math.max(0, 1.0f - (distToScan / SCAN_BAND_WIDTH));
            float heightFactor = (voxel.y - sampleCenter.getY() + 32) / 64.0f;

            float r = 0.2f + (heightFactor * 0.4f) + (scanHighlight * 0.6f);
            float g = 0.5f + (heightFactor * 0.5f);
            float b = 1.0f;
            float a = VOXEL_ALPHA + (scanHighlight * 0.5f);

            float voxelH = voxel.height * growth;
            drawVoxelToConsumer(matrix, scanBuffer, posX, posY, posZ, voxelH, voxel, r, g, b, a);
        }
    }

    // -------------------------------------------------------------------------
    // Geometry helpers
    // -------------------------------------------------------------------------

    private void drawVoxelToConsumer(Matrix4f matrix, VertexConsumer buf,
                                     float x, float y, float z, float h,
                                     ChunkCache.VoxelData voxel,
                                     float r, float g, float b, float a) {
        float s = VOXEL_SHAPE_SIZE;
        float d = h * VOXEL_SIZE;
        quadC(matrix, buf, x-s,y,z-s, x-s,y,z+s, x+s,y,z+s, x+s,y,z-s, r,g,b,a);
        if (h <= 1) return;
        if (voxel.exposedWest)  quadC(matrix,buf, x-s,y,z+s, x-s,y-d,z+s, x-s,y-d,z-s, x-s,y,z-s, r,g,b,a);
        if (voxel.exposedEast)  quadC(matrix,buf, x+s,y,z-s, x+s,y-d,z-s, x+s,y-d,z+s, x+s,y,z+s, r,g,b,a);
        if (voxel.exposedNorth) quadC(matrix,buf, x-s,y,z-s, x-s,y-d,z-s, x+s,y-d,z-s, x+s,y,z-s, r,g,b,a);
        if (voxel.exposedSouth) quadC(matrix,buf, x+s,y,z+s, x+s,y-d,z+s, x-s,y-d,z+s, x-s,y,z+s, r,g,b,a);
    }

    private void drawVoxelToBuilder(BufferBuilder buf,
                                    float x, float y, float z, float h,
                                    ChunkCache.VoxelData voxel,
                                    float r, float g, float b, float a) {
        float s = VOXEL_SHAPE_SIZE;
        float d = h * VOXEL_SIZE;
        quadB(buf, x-s,y,z-s, x-s,y,z+s, x+s,y,z+s, x+s,y,z-s, r,g,b,a);
        if (h <= 1) return;
        if (voxel.exposedWest)  quadB(buf, x-s,y,z+s, x-s,y-d,z+s, x-s,y-d,z-s, x-s,y,z-s, r,g,b,a);
        if (voxel.exposedEast)  quadB(buf, x+s,y,z-s, x+s,y-d,z-s, x+s,y-d,z+s, x+s,y,z+s, r,g,b,a);
        if (voxel.exposedNorth) quadB(buf, x-s,y,z-s, x-s,y-d,z-s, x+s,y-d,z-s, x+s,y,z-s, r,g,b,a);
        if (voxel.exposedSouth) quadB(buf, x+s,y,z+s, x+s,y-d,z+s, x-s,y-d,z+s, x-s,y,z+s, r,g,b,a);
    }

    private void quadC(Matrix4f m, VertexConsumer buf,
                       float x1,float y1,float z1, float x2,float y2,float z2,
                       float x3,float y3,float z3, float x4,float y4,float z4,
                       float r,float g,float b,float a) {
        buf.addVertex(m,x1,y1,z1).setColor(r,g,b,a);
        buf.addVertex(m,x2,y2,z2).setColor(r,g,b,a);
        buf.addVertex(m,x3,y3,z3).setColor(r,g,b,a);
        buf.addVertex(m,x4,y4,z4).setColor(r,g,b,a);
        buf.addVertex(m,x4,y4,z4).setColor(r,g,b,a);
        buf.addVertex(m,x3,y3,z3).setColor(r,g,b,a);
        buf.addVertex(m,x2,y2,z2).setColor(r,g,b,a);
        buf.addVertex(m,x1,y1,z1).setColor(r,g,b,a);
    }

    private void quadB(BufferBuilder buf,
                       float x1,float y1,float z1, float x2,float y2,float z2,
                       float x3,float y3,float z3, float x4,float y4,float z4,
                       float r,float g,float b,float a) {
        buf.addVertex(x1,y1,z1).setColor(r,g,b,a);
        buf.addVertex(x2,y2,z2).setColor(r,g,b,a);
        buf.addVertex(x3,y3,z3).setColor(r,g,b,a);
        buf.addVertex(x4,y4,z4).setColor(r,g,b,a);
        buf.addVertex(x4,y4,z4).setColor(r,g,b,a);
        buf.addVertex(x3,y3,z3).setColor(r,g,b,a);
        buf.addVertex(x2,y2,z2).setColor(r,g,b,a);
        buf.addVertex(x1,y1,z1).setColor(r,g,b,a);
    }
}