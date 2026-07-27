package com.cybrisoft.createmoderntech.block.volumetric.display;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.lens.AngledLensExtensionBlock;
import com.cybrisoft.createmoderntech.block.lens.LensExtensionBlock;
import com.cybrisoft.createmoderntech.block.lens.VerticalAngledLensExtensionBlock;
import com.cybrisoft.createmoderntech.block.lens.VerticalLensBlock;
import com.cybrisoft.createmoderntech.client.ClientPacketHandlers;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModPackets;
import com.cybrisoft.createmoderntech.util.ChunkCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.optics.Lens;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class VolumetricDisplayRenderer extends SmartBlockEntityRenderer<VolumetricDisplayBlockEntity> {
    // Maximum chunk radius (16 = 32 chunk diameter)
    private static final int MAX_CHUNK_RADIUS = 16;
    // RPM per chunk radius unit — 256 RPM / 16 chunks = 16 RPM per chunk
    private static final float RPM_PER_CHUNK = 16f;
    // Lerp duration in ticks for radius expansion
    private static final int RADIUS_LERP_SPEED = 40;

    private static final float DISPLAY_HEIGHT = 2.0f;
    private static final float VOXEL_SIZE = 0.01f;
    private static final float VOXEL_SHAPE_SIZE = 0.005f;
    private static final float VOXEL_ALPHA = 0.5f;
    private static final float SCAN_BAND_WIDTH = 0.02f;

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

        BlockPos rawPos = blockEntity.getBlockPos();
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, rawPos);
        Vec3 projectedPos;
        if (subLevel != null) {
            projectedPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(rawPos));
        } else {
            projectedPos = Vec3.atCenterOf(rawPos);
        }

        float panLerpSpeed = 0.1f;

        if (blockEntity.smoothPanX == 0 && blockEntity.smoothPanZ == 0
                && (blockEntity.panX != 0 || blockEntity.panZ != 0)) {
            blockEntity.smoothPanX = blockEntity.panX;
            blockEntity.smoothPanZ = blockEntity.panZ;
        }

        blockEntity.smoothPanX += (blockEntity.panX - blockEntity.smoothPanX) * panLerpSpeed;
        blockEntity.smoothPanZ += (blockEntity.panZ - blockEntity.smoothPanZ) * panLerpSpeed;

        BlockPos centerPos = BlockPos.containing(projectedPos);
        BlockPos sampleCenter = centerPos.offset((int) blockEntity.smoothPanX, 0, (int) blockEntity.smoothPanZ);

        float renderCenterX = centerPos.getX() + blockEntity.smoothPanX;
        float renderCenterZ = centerPos.getZ() + blockEntity.smoothPanZ;

        Level worldLevel = Minecraft.getInstance().level;
        if (worldLevel == null) return;

        // --- Radius lerp ---
        float rawSpeed = Math.abs(blockEntity.getSpeed());
        float targetRadius = Math.min(rawSpeed / RPM_PER_CHUNK, MAX_CHUNK_RADIUS);

        if (blockEntity.targetRadius != targetRadius) {
            blockEntity.startRadius = blockEntity.currentRadius;
            blockEntity.targetRadius = targetRadius;
            blockEntity.radiusLerp = 0f;
        }

        if (blockEntity.radiusLerp < 1f) {
            blockEntity.radiusLerp += deltaTicks / RADIUS_LERP_SPEED;
            if (blockEntity.radiusLerp > 1f) blockEntity.radiusLerp = 1f;
            blockEntity.currentRadius = blockEntity.startRadius +
                    (blockEntity.targetRadius - blockEntity.startRadius) * easeOutQuart(blockEntity.radiusLerp);
        }

        // Invisible at 0 radius
        if (blockEntity.currentRadius < 0.01f) return;

        // ceil for sampling (stay one step ahead), smooth for rendering
        int sampleRadius = (int) Math.ceil(blockEntity.currentRadius);
        sampleRadius = Math.max(1, Math.min(sampleRadius, MAX_CHUNK_RADIUS));

        // Request missing chunks using ceil radius
        if (blockEntity.chunkRequestDirty) {
            ClientPacketHandlers.requestMissingChunks(blockEntity, sampleCenter, sampleRadius);
            blockEntity.chunkRequestDirty = false;
        }

        // Cache update triggers when ceil radius changes or center moves
        boolean chunkChanged = false;
        if (blockEntity.lastCenterPos != null) {
            int lastCX = blockEntity.lastCenterPos.getX() >> 4;
            int lastCZ = blockEntity.lastCenterPos.getZ() >> 4;
            int newCX = sampleCenter.getX() >> 4;
            int newCZ = sampleCenter.getZ() >> 4;
            chunkChanged = lastCX != newCX || lastCZ != newCZ;
        }
        if (blockEntity.lastIntRadius != sampleRadius ||
                blockEntity.lastCenterPos == null ||
                chunkChanged) {
            blockEntity.pendingIntCenter = sampleCenter;
            blockEntity.lastIntRadius = sampleRadius;
            blockEntity.vboDirty = true;

            // Evict heightmap cache entries too far from current center
            if (blockEntity.heightmapCache.size() > 1024) {
                int cx = sampleCenter.getX() >> 4;
                int cz = sampleCenter.getZ() >> 4;
                int maxDist = MAX_CHUNK_RADIUS + 4;
                blockEntity.heightmapCache.entrySet().removeIf(entry -> {
                    long key = entry.getKey();
                    int ecx = (int)(key & 0xFFFFFFFFL);
                    int ecz = (int)(key >> 32);
                    int dx = ecx - cx;
                    int dz = ecz - cz;
                    return dx * dx + dz * dz > maxDist * maxDist;
                });
            }
        }

        if (blockEntity.lensConfig == null || isInvalidated(level, blockEntity)) {
            rebuildLensConfig(level, rawPos, blockEntity);
        }

        if (blockEntity.cachedMagnification != blockEntity.lensConfig.magnification ||
                blockEntity.cachedOffset != blockEntity.lensConfig.offset ||
                !Arrays.equals(blockEntity.cachedColor, blockEntity.lensConfig.color)) {
            blockEntity.cachedMagnification = blockEntity.lensConfig.magnification;
            blockEntity.cachedOffset = blockEntity.lensConfig.offset;
            blockEntity.cachedColor = blockEntity.lensConfig.color;
            blockEntity.vboDirty = true;
        }

        Matrix4f cameraView = new Matrix4f(RenderSystem.getModelViewMatrix());
        ms.pushPose();
        ms.translate(0.5 + blockEntity.lensConfig.offset.x, DISPLAY_HEIGHT + blockEntity.lensConfig.offset.y, 0.5 + blockEntity.lensConfig.offset.z);
        ms.mulPose(Axis.YP.rotationDegrees(blockEntity.yaw));
        ms.mulPose(Axis.XP.rotationDegrees(blockEntity.pitch));
        renderVolumetricDisplay(ms, bufferSource, worldLevel, blockEntity, cameraView, deltaTicks,
                new Vector3f(renderCenterX, sampleCenter.getY(), renderCenterZ), sampleRadius, blockEntity.lensConfig.magnification, blockEntity.lensConfig.color);
        ms.popPose();
    }

    private boolean isInvalidated(Level level, VolumetricDisplayBlockEntity blockEntity) {
        if (blockEntity.lensConfig == null) return true;
        for (BlockPos pos : blockEntity.lensConfig.lensCache.keySet()) {
            BlockState state = blockEntity.lensConfig.lensCache.get(pos);
            if (level.getBlockState(pos) != state) {
                return true;
            }
        }
        if (level.getBlockState(blockEntity.lensConfig.startPos) != blockEntity.lensConfig.startState) return true;
        if (blockEntity.lensConfig.endPos != null && level.getBlockState(blockEntity.lensConfig.endPos) != blockEntity.lensConfig.endState) return true;
        return false;
    }

    private void rebuildLensConfig(Level level, BlockPos rawPos, VolumetricDisplayBlockEntity blockEntity) {
        VolumetricDisplayBlockEntity.LensConfig config = new VolumetricDisplayBlockEntity.LensConfig();

        BlockState lensState = level.getBlockState(rawPos.above()); // start at block above display
        int startMagLevel = 0;
        if (isFacingDown(lensState)) {
            if (lensState.is(ModBlocks.LENS_1X.get()))       startMagLevel = 1;
            else if (lensState.is(ModBlocks.LENS_2X.get()))  startMagLevel = 2;
            else if (lensState.is(ModBlocks.LENS_4X.get()))  startMagLevel = 4;
            else if (lensState.is(ModBlocks.LENS_8X.get()))  startMagLevel = 8;
            else if (lensState.is(ModBlocks.LENS_16X.get())) startMagLevel = 16;
            config.offset.y += 1;
        }
        config.startPos = rawPos.above();
        config.startState = lensState;
        BlockPos nextPos = rawPos.above().above();
        Direction currentDirection = Direction.UP;
        if (startMagLevel != 0) {
            lensState = level.getBlockState(nextPos);
            while (isLensExtension(lensState)) {
                BlockPos currentPos = nextPos;

                if (config.lensCache.containsKey(currentPos)) {
                    break;
                }

                config.color = blendColor(lensState, config.color);

                lensState = level.getBlockState(currentPos);
                if (lensState.is(ModBlocks.TELEPHOTO_EXTENSION)) config.offset.y += 0.5f;
                if (lensState.is(ModBlocks.LIGHT_BOOST_FILTER)) config.color[3] *= 1.2f;

                // determine where next block in the lens stack will be
                if (lensState.getBlock() instanceof LensExtensionBlock) {
                    Direction.Axis axis = lensState.getValue(BlockStateProperties.AXIS);

                    if (currentDirection.getAxis() != axis) {
                        break;
                    }

                    nextPos = currentPos.relative(currentDirection);
                    config.offset.add(currentDirection.getStepX(), currentDirection.getStepY(), currentDirection.getStepZ());
                } else if (lensState.getBlock() instanceof AngledLensExtensionBlock) {
                    Direction direction = lensState.getValue(BlockStateProperties.FACING);
                    // horizontal angled has lenses on its direction and its counter-clockwise direction
                    if (currentDirection == direction.getOpposite()) {
                        currentDirection = direction.getClockWise().getOpposite();
                    } else if (currentDirection.getOpposite() == direction.getCounterClockWise()) {
                        currentDirection = direction;
                    } else {
                        break;
                    }
                    nextPos = currentPos.relative(currentDirection);
                    config.offset.add(currentDirection.getStepX(), currentDirection.getStepY(), currentDirection.getStepZ());
                } else if (lensState.getBlock() instanceof VerticalAngledLensExtensionBlock) {
                    FrontAndTop orientation = lensState.getValue(BlockStateProperties.ORIENTATION);
                    // vertical angled has lenses on its top and its front direction
                    Direction topDir = orientation.top();
                    Direction frontDir = orientation.front();

                    if (currentDirection == frontDir.getOpposite()) {
                        currentDirection = topDir;
                    } else if (currentDirection == topDir.getOpposite()) {
                        currentDirection = frontDir;
                    } else {
                        break;
                    }
                    nextPos = currentPos.relative(currentDirection);
                    config.offset.add(currentDirection.getStepX(), currentDirection.getStepY(), currentDirection.getStepZ());
                }

                config.lensCache.put(currentPos, lensState);
            }
            lensState = level.getBlockState(nextPos);
            if (isFacingUp(lensState) && currentDirection == Direction.UP) {
                int endMagLevel = 0;
                if (lensState.is(ModBlocks.LENS_1X.get()))       endMagLevel = 1;
                else if (lensState.is(ModBlocks.LENS_2X.get()))  endMagLevel = 2;
                else if (lensState.is(ModBlocks.LENS_4X.get()))  endMagLevel = 4;
                else if (lensState.is(ModBlocks.LENS_8X.get()))  endMagLevel = 8;
                else if (lensState.is(ModBlocks.LENS_16X.get())) endMagLevel = 16;
                config.magnification = (float) endMagLevel / startMagLevel;
                config.offset.y += 1;
            }
            config.endPos = nextPos;
            config.endState = lensState;
        }

        blockEntity.lensConfig = config;
    }

    private boolean isLensExtension(BlockState state) {
        return state.is(ModBlocks.LENS_EXTENSION) || state.is(ModBlocks.ANGLED_LENS_EXTENSION) || state.is(ModBlocks.VERTICAL_ANGLED_LENS_EXTENSION)
                || state.is(ModBlocks.TELEPHOTO_EXTENSION) || state.is(ModBlocks.LIME_COLOR_FILTER)
                || state.is(ModBlocks.PURPLE_COLOR_FILTER) || state.is(ModBlocks.RED_COLOR_FILTER)
                || state.is(ModBlocks.WHITE_COLOR_FILTER)  || state.is(ModBlocks.LIGHT_BOOST_FILTER);
    }

    private float[] blendColor(BlockState state, float[] color) {
        float[] target = null;
        float strength = 0.4f;

        if (state.is(ModBlocks.LIME_COLOR_FILTER))        target = new float[]{0.2f, 1.0f, 0.2f};
        else if (state.is(ModBlocks.PURPLE_COLOR_FILTER)) target = new float[]{0.6f, 0.0f, 1.0f};
        else if (state.is(ModBlocks.RED_COLOR_FILTER))    target = new float[]{1.0f, 0.0f, 0.0f};
        else if (state.is(ModBlocks.WHITE_COLOR_FILTER))  target = new float[]{1.0f, 1.0f, 1.0f};

        if (target == null) return color;

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
                                         Level worldLevel, VolumetricDisplayBlockEntity blockEntity,
                                         Matrix4f cameraView, float deltaTicks,
                                         Vector3f sampleCenter, int sampleRadius, float magnification, float[] color) {
        float time = blockEntity.getLevel().getGameTime() + AnimationTickHolder.getPartialTicks();

        float scanSpeed = (blockEntity.targetRadius / MAX_CHUNK_RADIUS) * 0.1f;
        float scanPos = (time * scanSpeed) % 10.0f;

        // Upload finished async build if ready
        if (blockEntity.rebuildFuture != null && blockEntity.rebuildFuture.isDone()) {
            if (blockEntity.pendingMesh != null) {
                if (blockEntity.staticVBO == null)
                    blockEntity.staticVBO = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
                blockEntity.staticVBO.bind();
                blockEntity.staticVBO.upload(blockEntity.pendingMesh);
                VertexBuffer.unbind();
                blockEntity.pendingMesh.close();
                blockEntity.pendingMesh = null;
                blockEntity.lastCenterPos = blockEntity.bakingIntCenter;
            }
            if (blockEntity.pendingVoxels != null) {
                if (blockEntity.chunkCache == null) {
                    blockEntity.chunkCache = new ChunkCache();
                }
                ChunkCache cache = (ChunkCache) blockEntity.chunkCache;
                cache.swapVoxels((List<ChunkCache.VoxelData>) blockEntity.pendingVoxels);
                blockEntity.pendingVoxels = null;
            }
            blockEntity.rebuildFuture = null;
        }

        // Kick off new async build if needed and none is running
        if (blockEntity.vboDirty && blockEntity.rebuildFuture == null) {
            blockEntity.vboDirty = false;

            blockEntity.bakingIntCenter = blockEntity.pendingIntCenter;
            BlockPos capIntCenter = blockEntity.bakingIntCenter;
            float[] capColor = color;
            int capSampleRadius = sampleRadius;

            if (blockEntity.pendingMesh != null) {
                blockEntity.pendingMesh.close();
                blockEntity.pendingMesh = null;
            }

            blockEntity.rebuildFuture = CompletableFuture.runAsync(() -> {
                if (blockEntity.chunkCache == null) {
                    blockEntity.chunkCache = new ChunkCache();
                }
                ChunkCache cache = (ChunkCache) blockEntity.chunkCache;
                cache.update(worldLevel, capIntCenter, capSampleRadius, blockEntity.heightmapCache);

                List<ChunkCache.VoxelData> snapshot = cache.snapshotVoxels();

                if (blockEntity.sharedByteBuffer == null) {
                    blockEntity.sharedByteBuffer = new ByteBufferBuilder(8 * 1024 * 1024);
                }
                ByteBufferBuilder byteBuffer = blockEntity.sharedByteBuffer;
                BufferBuilder builder = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS,
                        DefaultVertexFormat.POSITION_COLOR);

                for (ChunkCache.VoxelData voxel : snapshot) {
                    float relX = (voxel.x - capIntCenter.getX());
                    float relY = (voxel.y - capIntCenter.getY());
                    float relZ = (voxel.z - capIntCenter.getZ());

                    float posX = relX * VOXEL_SIZE;
                    float posY = relY * VOXEL_SIZE;
                    float posZ = relZ * VOXEL_SIZE;

                    float heightFactor = (voxel.y - capIntCenter.getY() + 32) / 64.0f;
                    float r = Math.min(1.0f, (0.2f + heightFactor * 0.4f) * capColor[0]);
                    float g = Math.min(1.0f, (0.5f + heightFactor * 0.5f) * capColor[1]);
                    float b = Math.min(1.0f, capColor[2]);
                    float a = Math.min(1.0f, VOXEL_ALPHA * capColor[3]);

                    drawVoxelToBuilder(builder, posX, posY, posZ, voxel.height, voxel, r, g, b, a);
                }

                blockEntity.pendingMesh = builder.buildOrThrow();
                blockEntity.pendingVoxels = snapshot;
            });
        }

        // Draw static VBO
        if (blockEntity.staticVBO != null) {
            ms.pushPose();
            float subOffsetX = (sampleCenter.x - blockEntity.lastCenterPos.getX()) * VOXEL_SIZE;
            float subOffsetZ = (sampleCenter.z - blockEntity.lastCenterPos.getZ()) * VOXEL_SIZE;
            ms.translate(-subOffsetX * magnification, 0, -subOffsetZ * magnification);
            ms.scale(magnification, magnification, magnification);

            HOLOGRAM_RENDER_TYPE.setupRenderState();
            blockEntity.staticVBO.bind();
            Matrix4f modelView = new Matrix4f(cameraView).mul(ms.last().pose());
            blockEntity.staticVBO.drawWithShader(modelView, RenderSystem.getProjectionMatrix(),
                    GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            HOLOGRAM_RENDER_TYPE.clearRenderState();

            ms.popPose();
        }

        // Inline scan pass
        VertexConsumer scanBuffer = bufferSource.getBuffer(HOLOGRAM_RENDER_TYPE);
        Matrix4f matrix = ms.last().pose();
        float scale = magnification;
        float scaledScan = scanPos * VOXEL_SIZE * 50f;

        if (blockEntity.chunkCache == null) {
            blockEntity.chunkCache = new ChunkCache();
        }
        ChunkCache cache = (ChunkCache)  blockEntity.chunkCache;
        for (ChunkCache.VoxelData voxel : cache.getVoxels()) {
            float relX = (voxel.x - sampleCenter.x());
            float relY = (voxel.y - sampleCenter.y());
            float relZ = (voxel.z - sampleCenter.z());

            float posX = relX * VOXEL_SIZE * scale;
            float posY = relY * VOXEL_SIZE * scale;
            float posZ = relZ * VOXEL_SIZE * scale;

            float minR2 = (scaledScan*scale - SCAN_BAND_WIDTH) * (scaledScan*scale - SCAN_BAND_WIDTH);
            float maxR2 = (scaledScan*scale + SCAN_BAND_WIDTH) * (scaledScan*scale + SCAN_BAND_WIDTH);
            float distSq = posX*posX + posZ*posZ;
            if (distSq < minR2 || distSq > maxR2) continue;

            float distFromCenter = (float) Math.sqrt(posX * posX + posZ * posZ);
            float distToScan = Math.abs(distFromCenter - scaledScan * scale);
            if (distToScan > SCAN_BAND_WIDTH) continue;

            float scanHighlight = Math.max(0, 1.0f - (distToScan / SCAN_BAND_WIDTH));
            float heightFactor = (voxel.y - sampleCenter.y() + 32) / 64.0f;

            float r = Math.min(1.0f, (0.2f + (heightFactor * 0.4f) + (scanHighlight * 0.6f)) * color[0]);
            float g = Math.min(1.0f, (0.5f + (heightFactor * 0.5f)) * color[1]);
            float b = Math.min(1.0f, color[2]);
            float a = Math.min(1.0f, (VOXEL_ALPHA + (scanHighlight * 0.5f)) * color[3]);

            drawVoxelToConsumer(matrix, scanBuffer, posX, posY, posZ, voxel.height, voxel, r, g, b, a, scale);
        }

        drawCursor(ms.last().pose(), scanBuffer, scale, color, time, false, true);

        List<BeaconLabel> labels = new ArrayList<>();

        for (VolumetricDisplayBlockEntity.BeaconData beacon : blockEntity.beacons) {
            float relX = beacon.x - sampleCenter.x();
            float relZ = beacon.z - sampleCenter.z();
            float[] beaconColor = {beacon.r(), beacon.g(), beacon.b(), 1.0f};

            float radiusInBlocks = blockEntity.currentRadius * 16f;
            boolean xOutOfRange = relX > radiusInBlocks || relX < -radiusInBlocks;
            boolean zOutOfRange = relZ > radiusInBlocks || relZ < -radiusInBlocks;
            if (xOutOfRange) relX = Math.signum(relX) * radiusInBlocks;
            if (zOutOfRange) relZ = Math.signum(relZ) * radiusInBlocks;

            ms.pushPose();
            ms.translate(relX * VOXEL_SIZE * scale, 0, relZ * VOXEL_SIZE * scale);
            drawCursor(ms.last().pose(), scanBuffer, scale, beaconColor, time, xOutOfRange || zOutOfRange, false);

            if (beacon.name != null && !beacon.name.isBlank()) {
                labels.add(new BeaconLabel(relX, relZ, beacon.name, beaconColor));
            }

            ms.popPose();
        }

        // origin
        BlockPos rawPos = blockEntity.getBlockPos();
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(worldLevel, rawPos);
        Vec3 projectedPos;
        if (subLevel != null) {
            projectedPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(rawPos));
        } else {
            projectedPos = Vec3.atCenterOf(rawPos);
        }

        float relX = (float) (projectedPos.x() - sampleCenter.x());
        float relZ = (float) (projectedPos.z() - sampleCenter.z());

        float radiusInBlocks = blockEntity.currentRadius * 16f;
        boolean xOutOfRange = relX > radiusInBlocks || relX < -radiusInBlocks;
        boolean zOutOfRange = relZ > radiusInBlocks || relZ < -radiusInBlocks;
        if (xOutOfRange) relX = Math.signum(relX) * radiusInBlocks;
        if (zOutOfRange) relZ = Math.signum(relZ) * radiusInBlocks;

        ms.pushPose();
        ms.translate(relX * VOXEL_SIZE * scale, 0, relZ * VOXEL_SIZE * scale);
        drawCursor(ms.last().pose(), scanBuffer, scale, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, time, xOutOfRange || zOutOfRange, true);
        ms.popPose();

        for (BeaconLabel label : labels) {
            ms.pushPose();

            ms.translate(label.relX * VOXEL_SIZE * scale, 0, label.relZ * VOXEL_SIZE * scale);

            // offset slightly above the cursor
            ms.translate(0, 0.45f * scale + 0.02f, 0);

            ms.mulPose(Axis.XP.rotationDegrees(-90));

            ms.scale(0.5f, 0.5f, 0.5f);

            // scale down to fit the display
            float textScale = scale * 0.02f;
            ms.scale(textScale, -textScale, textScale);

            Font font = Minecraft.getInstance().font;
            String name = label.name;
            float textWidth = font.width(name);

            int light = LightTexture.FULL_BRIGHT;

            // center the text on the cursor
            font.drawInBatch(name, -textWidth / 2f, 0,
                    FastColor.ARGB32.color(255, (int)(label.color[0]*255), (int)(label.color[1]*255), (int)(label.color[2]*255)),
                    false, ms.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);

            ms.popPose();
        }
    }

    static class BeaconLabel {
        public float relX;
        public float relZ;
        public String name;
        public float[] color;

        public BeaconLabel(float relX, float relZ, String name, float[] color) {
            this.relX = relX;
            this.relZ = relZ;
            this.name = name;
            this.color = color;
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
        if (h > 64) {
            h = 0;
        }
        float d = h * VOXEL_SIZE * scale;
        quadC(matrix, buf, x-s,y,z-s, x-s,y,z+s, x+s,y,z+s, x+s,y,z-s, r,g,b,a); // draw top

        if (voxel.isPlaceholder) {
            // 16 blocks * VOXEL_SIZE / 2, minus gap
            float chunkHalf = 16 * VOXEL_SIZE * 0.5f - 0.003f;
            quadC(matrix, buf, x - chunkHalf, y, z - chunkHalf,
                    x - chunkHalf, y, z + chunkHalf,
                    x + chunkHalf, y, z + chunkHalf,
                    x + chunkHalf, y, z - chunkHalf,
                    r, g, b, a);
            return; // no sides
        }
        if (h <= 0) return;
        // draw sides
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
        if (h > 64) {
            h = 0;
        }
        float d = h * VOXEL_SIZE;
        quadB(buf, x-s,y,z-s, x-s,y,z+s, x+s,y,z+s, x+s,y,z-s, r,g,b,a); // draw top face
        if (voxel.isPlaceholder) {
            // 16 blocks * VOXEL_SIZE / 2, minus gap
            float chunkHalf = 16 * VOXEL_SIZE * 0.5f - 0.003f;
            quadB(buf, x - chunkHalf, y, z - chunkHalf,
                    x - chunkHalf, y, z + chunkHalf,
                    x + chunkHalf, y, z + chunkHalf,
                    x + chunkHalf, y, z - chunkHalf,
                    r, g, b, a);
            return; // no sides
        }
        if (h <= 0) return;
        // draw sides
        if (voxel.exposedWest)  quadB(buf, x-s,y,z+s, x-s,y-d,z+s, x-s,y-d,z-s, x-s,y,z-s, r,g,b,a);
        if (voxel.exposedEast)  quadB(buf, x+s,y,z-s, x+s,y-d,z-s, x+s,y-d,z+s, x+s,y,z+s, r,g,b,a);
        if (voxel.exposedNorth) quadB(buf, x-s,y,z-s, x-s,y-d,z-s, x+s,y-d,z-s, x+s,y,z-s, r,g,b,a);
        if (voxel.exposedSouth) quadB(buf, x+s,y,z+s, x+s,y-d,z+s, x-s,y-d,z+s, x-s,y,z+s, r,g,b,a);
    }

    private void drawCursor(Matrix4f matrix, VertexConsumer buf, float scale, float[] color, float time, boolean outOfRange, boolean isCenter) {
        float r = color[0];
        float g = color[1];
        float b = color[2];
        float a = 1.0f; // cursor always fully opaque for visibility

        // Pulsing size
        float cursorRadius = 0.045f * scale * (1.0f + (float) Math.sin(time * 0.3f) * 0.15f);
        float lineHeight = isCenter ? 0.26f * scale : 0.45f * scale;
        float thickness = 0.002f * scale;

        // approximated circle with 16 line segments as thin quads
        if (!outOfRange) {
            int segments = 16;
            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (i * 2 * Math.PI / segments);
                float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);
                float x1 = (float) Math.cos(angle1) * cursorRadius;
                float z1 = (float) Math.sin(angle1) * cursorRadius;
                float x2 = (float) Math.cos(angle2) * cursorRadius;
                float z2 = (float) Math.sin(angle2) * cursorRadius;
                // draw thin quad between arc points
                quadC(matrix, buf,
                        x1 - thickness, 0, z1 - thickness,
                        x1 + thickness, 0, z1 + thickness,
                        x2 + thickness, 0, z2 + thickness,
                        x2 - thickness, 0, z2 - thickness,
                        r, g, b, a);
            }
            if (isCenter) {
                float armLength = 0.05f * scale;
                float armThickness = 0.003f * scale;
                quadC(matrix, buf, -armLength, 0, -armThickness, -armLength, 0, armThickness,
                        armLength, 0, armThickness, armLength, 0, -armThickness, r, g, b, a);
                quadC(matrix, buf, -armThickness, 0, -armLength, armThickness, 0, -armLength,
                        armThickness, 0, armLength, -armThickness, 0, armLength, r, g, b, a);
            }
        }

        // Vertical line
        quadC(matrix, buf,
                -thickness, 0,          -thickness,
                -thickness, lineHeight,  -thickness,
                thickness, lineHeight,   thickness,
                thickness, 0,            thickness,
                r, g, b, a);
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