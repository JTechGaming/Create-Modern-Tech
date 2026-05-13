package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModPackets;
import com.cybrisoft.createmoderntech.util.ChunkCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VolumetricDisplayRenderer extends SmartBlockEntityRenderer<VolumetricDisplayBlockEntity> {
    private static final int MAX_CHUNK_RADIUS = 7;

    private static final float DISPLAY_HEIGHT = 2.0f;
    private static final float VOXEL_SIZE = 0.01f;
    private static final float VOXEL_SHAPE_SIZE = 0.005f;
    private static final float VOXEL_ALPHA = 0.5f;
    private static final float SCAN_BAND_WIDTH = 0.02f;

    private static final int LERP_SPEED = 10;

    private static final RenderType HOLOGRAM_RENDER_TYPE = RenderType.create(
            "hologram",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
    );

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
        BlockPos rawPos = blockEntity.getBlockPos();
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, rawPos);
        Vec3 projectedPos;
        if (subLevel != null) {
            projectedPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(rawPos));
        } else {
            projectedPos = Vec3.atCenterOf(rawPos);
        }
        BlockPos centerPos = BlockPos.containing(projectedPos);
        BlockPos sampleCenter = centerPos.offset((int) blockEntity.panX, 0, (int) blockEntity.panZ);

        Level worldLevel = Minecraft.getInstance().level;
        if (worldLevel == null) return;

        if (blockEntity.chunkRequestDirty) {
            ModPackets.requestMissingChunks(blockEntity, sampleCenter, MAX_CHUNK_RADIUS);
            blockEntity.chunkRequestDirty = false;
        }

        if (blockEntity.lastCenterPos == null || blockEntity.lastCenterPos.distSqr(sampleCenter) > 64) {
            blockEntity.chunkCache.update(worldLevel, sampleCenter, MAX_CHUNK_RADIUS, blockEntity.heightmapCache);
            blockEntity.lastCacheUpdate = currentTime;
            blockEntity.lastCenterPos = sampleCenter;
            blockEntity.vboDirty = true;
        }

        float magnification = 1.0f;
        float offset = 0;
        BlockState aboveBlockState = level.getBlockState(rawPos.above());
        int bottomMagLevel = 0;
        if (isFacingDown(aboveBlockState)) {
            if (aboveBlockState.is(ModBlocks.LENS_1X.get()))       bottomMagLevel = 1;
            else if (aboveBlockState.is(ModBlocks.LENS_2X.get()))  bottomMagLevel = 2;
            else if (aboveBlockState.is(ModBlocks.LENS_4X.get()))  bottomMagLevel = 4;
            else if (aboveBlockState.is(ModBlocks.LENS_10X.get())) bottomMagLevel = 10;
            else if (aboveBlockState.is(ModBlocks.LENS_16X.get())) bottomMagLevel = 16;
            offset++;
        }
        BlockPos lensPos = rawPos.above().above();
        float[] color = {0.5f, 0.7f, 0.8f, 1.0f};
        if (bottomMagLevel != 0) {
            aboveBlockState = level.getBlockState(lensPos);
            while (isLensExtension(aboveBlockState)) {
                color = blendColor(aboveBlockState, color);
                lensPos = lensPos.above();
                aboveBlockState = level.getBlockState(lensPos);
                if (aboveBlockState.is(ModBlocks.TELEPHOTO_EXTENSION)) {
                    offset += 0.5f;
                }
                if (aboveBlockState.is(ModBlocks.LIGHT_BOOST_FILTER)) {
                    color[3] *= 1.2f;
                }
                offset++;
            }
            aboveBlockState = level.getBlockState(lensPos);
            if (isFacingUp(aboveBlockState)) {
                int topMagLevel = 0;
                if (aboveBlockState.is(ModBlocks.LENS_1X.get()))       topMagLevel = 1;
                else if (aboveBlockState.is(ModBlocks.LENS_2X.get()))  topMagLevel = 2;
                else if (aboveBlockState.is(ModBlocks.LENS_4X.get()))  topMagLevel = 4;
                else if (aboveBlockState.is(ModBlocks.LENS_10X.get())) topMagLevel = 10;
                else if (aboveBlockState.is(ModBlocks.LENS_16X.get())) topMagLevel = 16;
                magnification = (float) topMagLevel / bottomMagLevel;
                offset++;
            }
        }

        if (blockEntity.cachedMagnification != magnification ||
                blockEntity.cachedOffset != offset ||
                !Arrays.equals(blockEntity.cachedColor, color)) {
            blockEntity.cachedMagnification = magnification;
            blockEntity.cachedOffset = offset;
            blockEntity.cachedColor = color;
            blockEntity.vboDirty = true;
        }

        Matrix4f cameraView = new Matrix4f(RenderSystem.getModelViewMatrix());
        ms.pushPose();
        ms.translate(0.5, DISPLAY_HEIGHT + offset, 0.5);
        ms.mulPose(Axis.YP.rotationDegrees(blockEntity.yaw));
        ms.mulPose(Axis.XP.rotationDegrees(blockEntity.pitch));
        renderVolumetricDisplay(ms, bufferSource, blockEntity, cameraView, deltaTicks, sampleCenter, magnification, color);
        ms.popPose();
    }

    private boolean isLensExtension(BlockState aboveBlockState) {
        return aboveBlockState.is(ModBlocks.LENS_EXTENSION) || aboveBlockState.is(ModBlocks.TELEPHOTO_EXTENSION)
                || aboveBlockState.is(ModBlocks.LIME_COLOR_FILTER) || aboveBlockState.is(ModBlocks.PURPLE_COLOR_FILTER)
                || aboveBlockState.is(ModBlocks.RED_COLOR_FILTER) || aboveBlockState.is(ModBlocks.WHITE_COLOR_FILTER)
                || aboveBlockState.is(ModBlocks.LIGHT_BOOST_FILTER);
    }

    private float[] blendColor(BlockState state, float[] color) {
        float[] target = null;
        float strength = 0.4f;

        if (state.is(ModBlocks.LIME_COLOR_FILTER))   target = new float[]{0.2f, 1.0f, 0.2f};
        else if (state.is(ModBlocks.PURPLE_COLOR_FILTER)) target = new float[]{0.6f, 0.0f, 1.0f};
        else if (state.is(ModBlocks.RED_COLOR_FILTER))    target = new float[]{1.0f, 0.0f, 0.0f};
        else if (state.is(ModBlocks.WHITE_COLOR_FILTER))  target = new float[]{1.0f, 1.0f, 1.0f};

        if (target == null) return color;

        // Lerp toward the target color by strength
        return new float[]{
                color[0] + (target[0] - color[0]) * strength,
                color[1] + (target[1] - color[1]) * strength,
                color[2] + (target[2] - color[2]) * strength,
                color[3]
        };
    }

    public boolean isFacingDown(BlockState state) {
        return state.hasProperty(BlockStateProperties.FACING) &&
                state.getValue(BlockStateProperties.FACING) == Direction.DOWN;
    }

    public boolean isFacingUp(BlockState state) {
        return state.hasProperty(BlockStateProperties.FACING) &&
                state.getValue(BlockStateProperties.FACING) == Direction.UP;
    }

    private void renderVolumetricDisplay(PoseStack ms, MultiBufferSource bufferSource,
                                         VolumetricDisplayBlockEntity blockEntity,
                                         Matrix4f cameraView, float deltaTicks,
                                         BlockPos sampleCenter, float magnification, float[] color) {
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
        float pulse = 1.0f + ((float) Math.sin(time * 0.02f) * 0.02f);

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
        if (blockEntity.vboDirty && blockEntity.rebuildFuture == null) {
            blockEntity.vboDirty = false;

            List<ChunkCache.VoxelData> snapshot = blockEntity.chunkCache.snapshotVoxels();
            BlockPos capCenter = sampleCenter;
            float[] capColor = color;

            if (blockEntity.pendingMesh != null) {
                blockEntity.pendingMesh.close();
                blockEntity.pendingMesh = null;
            }

            blockEntity.rebuildFuture = CompletableFuture.runAsync(() -> {
                ByteBufferBuilder byteBuffer = new ByteBufferBuilder(snapshot.size() * 5 * 8 * 28);
                BufferBuilder builder = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                for (ChunkCache.VoxelData voxel : snapshot) {
                    float relX = (voxel.x - capCenter.getX());
                    float relY = (voxel.y - capCenter.getY());
                    float relZ = (voxel.z - capCenter.getZ());

                    // Bake at neutral scale — no growth, no magnification
                    float posX = relX * VOXEL_SIZE;
                    float posY = relY * VOXEL_SIZE;
                    float posZ = relZ * VOXEL_SIZE;

                    float heightFactor = (voxel.y - capCenter.getY() + 32) / 64.0f;
                    float r = Math.min(1.0f, (0.2f + heightFactor * 0.4f) * capColor[0]);
                    float g = Math.min(1.0f, (0.5f + heightFactor * 0.5f) * capColor[1]);
                    float b = Math.min(1.0f, capColor[2]);
                    float a = Math.min(1.0f, VOXEL_ALPHA * capColor[3]);

                    float voxelH = voxel.height; // neutral height, matrix scales it
                    drawVoxelToBuilder(builder, posX, posY, posZ, voxelH, voxel, r, g, b, a);
                }

                blockEntity.pendingMesh = builder.buildOrThrow();
                blockEntity.pendingVoxels = snapshot;
            });
        }

        // Draw static VBO
        if (blockEntity.staticVBO != null) {
            ms.pushPose();
            ms.scale(growth * magnification * pulse, growth * magnification * pulse, growth * magnification * pulse);

            HOLOGRAM_RENDER_TYPE.setupRenderState();
            blockEntity.staticVBO.bind();
            Matrix4f modelView = new Matrix4f(cameraView).mul(ms.last().pose());
            blockEntity.staticVBO.drawWithShader(modelView, RenderSystem.getProjectionMatrix(),
                    GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            HOLOGRAM_RENDER_TYPE.clearRenderState();

            ms.popPose();
        }

        VertexConsumer scanBuffer = bufferSource.getBuffer(HOLOGRAM_RENDER_TYPE);
        Matrix4f matrix = ms.last().pose();
        float scale = growth * magnification * pulse;
        float scaledScan = scanPos * VOXEL_SIZE * 50f; // scan position in neutral space

        for (ChunkCache.VoxelData voxel : blockEntity.chunkCache.getVoxels()) {
            float relX = (voxel.x - sampleCenter.getX());
            float relY = (voxel.y - sampleCenter.getY());
            float relZ = (voxel.z - sampleCenter.getZ());

            float posZ = relZ * VOXEL_SIZE * scale;
            float distToScan = Math.abs(posZ - scaledScan * scale);
            if (distToScan > SCAN_BAND_WIDTH) continue;

            float posX = relX * VOXEL_SIZE * scale;
            float posY = relY * VOXEL_SIZE * scale;

            float scanHighlight = Math.max(0, 1.0f - (distToScan / SCAN_BAND_WIDTH));
            float heightFactor = (voxel.y - sampleCenter.getY() + 32) / 64.0f;

            float r = Math.min(1.0f, (0.2f + (heightFactor * 0.4f) + (scanHighlight * 0.6f)) * color[0]);
            float g = Math.min(1.0f, (0.5f + (heightFactor * 0.5f)) * color[1]);
            float b = Math.min(1.0f, color[2]);
            float a = Math.min(1.0f, VOXEL_ALPHA + (scanHighlight * 0.5f) * color[3]);

            float voxelH = voxel.height;
            drawVoxelToConsumer(matrix, scanBuffer, posX, posY, posZ, voxelH, voxel, r, g, b, a, scale);
        }
    }

    // -------------------------------------------------------------------------
    // Geometry helpers
    // -------------------------------------------------------------------------

    private void drawVoxelToConsumer(Matrix4f matrix, VertexConsumer buf,
                                     float x, float y, float z, float h,
                                     ChunkCache.VoxelData voxel,
                                     float r, float g, float b, float a, float scale) {
        float s = VOXEL_SHAPE_SIZE * scale;
        float d = h * VOXEL_SIZE * scale;
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