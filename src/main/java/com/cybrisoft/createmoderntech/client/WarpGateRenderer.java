package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(modid = CreateModernTech.MODID, value = Dist.CLIENT)
public class WarpGateRenderer {
    private static final List<BlockPos> gates = new CopyOnWriteArrayList<>();

    public static void register(BlockPos pos) {
        if (!gates.contains(pos)) {
            gates.add(pos);
        }
    }

    public static void remove(BlockPos pos) {
        if (!gates.remove(pos)) {
            for (BlockPos gate : gates) {
                if (gate.equals(pos)) {
                    gates.remove(gate);
                    return;
                }
            }
        }
    }

    private static final ResourceLocation WARPGATE_SHADER = ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, "warpgate");

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        PoseStack ms = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        ms.pushPose();
        ms.translate(-cam.x, -cam.y, -cam.z);

        for (BlockPos gatePos : gates) {
            BlockEntity ble = Minecraft.getInstance().player.level().getBlockEntity(gatePos);

            if (ble instanceof WarpGateTerminalBlockEntity be) {
                Vec3 gateCenter = Vec3.atCenterOf(be.getBlockPos());
                double renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16.0;
                if (cam.distanceToSqr(gateCenter) > renderDistance * renderDistance) continue;

                ms.pushPose();
                BlockPos pos = be.getBlockPos();
                ms.translate(pos.getX(), pos.getY(), pos.getZ());

                if (be.activationProgress > 0) {
                    final float offset = 0.2f;
                    float r = be.multiblockRadius + offset;
                    boolean isZ = be.multiblockIsZ;

                    ms.pushPose();
                    ms.translate(0.5, r + 0.5 - offset, 0.5);

                    ShaderProgram shader = VeilRenderSystem.setShader(WARPGATE_SHADER);
                    if (shader == null) return;

                    ShaderUniformAccess time = shader.getUniform("Time");
                    if (time != null) {
                        time.setFloat((float) (System.currentTimeMillis() % 1000000) / 1000f);
                    }
                    ShaderUniformAccess progress = shader.getUniform("Progress");
                    if (progress != null) {
                        progress.setFloat(be.activationProgress);
                    }
                    ShaderUniformAccess closing = shader.getUniform("Closing");
                    if (closing != null) {
                        closing.setFloat(be.closing);
                    }

                    RenderSystem.enableDepthTest();
                    RenderSystem.disableCull();
                    RenderSystem.depthMask(true);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO
                    );
                    shader.bind();

                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder buf = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);

                    // center vertex
                    buf.addVertex(ms.last().pose(), 0f, 0f, 0f).setUv(0.5f, 0.5f);

                    // perimeter vertices
                    int segments = 64;
                    for (int i = 0; i <= segments; i++) {
                        float angle = (float) (i * 2 * Math.PI / segments);
                        float cos = Mth.cos(angle);
                        float sin = Mth.sin(angle);
                        float px = isZ ? 0 : cos * r;
                        float py = sin * r;
                        float pz = isZ ? cos * r : 0;
                        buf.addVertex(ms.last().pose(), px, py, pz).setUv(cos * 0.5f + 0.5f, sin * 0.5f + 0.5f);
                    }

                    BufferUploader.drawWithShader(buf.buildOrThrow());

                    ShaderProgram.unbind();

                    RenderSystem.enableCull();
                    RenderSystem.depthMask(false);
                    RenderSystem.disableBlend();

                    ms.popPose();
                }

                if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && be.multiblockRadius > 0) {
                    int r = be.multiblockRadius;
                    boolean isZ = be.multiblockIsZ;

                    ms.pushPose();
                    VertexConsumer vc = bufferSource.getBuffer(RenderType.lines());

                    if (!isZ) {
                        LevelRenderer.renderLineBox(ms, vc,
                                -r + 0.5, 0.5, -1.0,
                                r + 0.5, r * 2 + 0.5, 1.0,
                                1f, 1f, 0f, 1f);
                    } else {
                        LevelRenderer.renderLineBox(ms, vc,
                                -1.0, 0.5, -r + 0.5,
                                1.0, r * 2 + 0.5, r + 0.5,
                                1f, 1f, 0f, 1f);
                    }

                    ms.popPose();
                }

                if (be.drawGuides) {
                    Level level = be.getLevel();
                    if (level == null) return;

                    int r = be.guideRadius;
                    int cx = 0, cy = r, cz = 0;
                    boolean isZ = be.multiblockIsZ;

                    int x = 0, y = r, d = 1 - r;
                    while (x <= y) {
                        guideDot(ms, bufferSource, cx + y, cy + x, cz);
                        guideDot(ms, bufferSource, cx - y, cy + x, cz);
                        guideDot(ms, bufferSource, cx + y, cy - x, cz);
                        guideDot(ms, bufferSource, cx - y, cy - x, cz);
                        guideDot(ms, bufferSource, cx + x, cy + y, cz);
                        guideDot(ms, bufferSource, cx - x, cy + y, cz);
                        guideDot(ms, bufferSource, cx + x, cy - y, cz);
                        guideDot(ms, bufferSource, cx - x, cy - y, cz);
                        if (d < 0) d += 2 * x + 3;
                        else {
                            d += 2 * (x - y) + 5;
                            y--;
                        }
                        x++;
                    }
                }
                ms.popPose();

            }
        }

        ms.popPose();
    }

    private static void guideDot(PoseStack ms, MultiBufferSource buffer, int x, int y, int z) {
        ms.pushPose();
        ms.translate(x + 0.25, y + 0.25, z + 0.25);
        ms.scale(0.5f, 0.5f, 0.5f);
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(ms, vc, 0, 0, 0, 1, 1, 1, 1f, 1f, 1f, 1f);
        ms.popPose();
    }
}
