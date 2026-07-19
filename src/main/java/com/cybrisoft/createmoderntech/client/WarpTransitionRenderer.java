package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.mojang.blaze3d.vertex.*;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Random;

@EventBusSubscriber(modid = CreateModernTech.MODID, value = Dist.CLIENT)
public class WarpTransitionRenderer {
    public static boolean shouldRender = false;
    public static SubLevelAccess sublevel = null;
    public static float transitionProgress = 0f; // 0=start, 1=fully in warp
    public static Vec3 travelDirection = Vec3.ZERO;

    private static final int STREAK_COUNT = 200;
    private static final Random RNG = new Random(42); // fixed seed for consistent pattern

    // pre-generate streak origins on a sphere
    private static final Vec3[] STREAK_ORIGINS = new Vec3[STREAK_COUNT];
    static {
        for (int i = 0; i < STREAK_COUNT; i++) {
            float theta = (float)(RNG.nextFloat() * 2 * Math.PI);
            float phi = (float)(Math.acos(2 * RNG.nextFloat() - 1));
            float r = 20f + RNG.nextFloat() * 60f;
            STREAK_ORIGINS[i] = new Vec3(
                    r * Math.sin(phi) * Math.cos(theta),
                    r * Math.sin(phi) * Math.sin(theta),
                    r * Math.cos(phi)
            );
        }
    }

    private static final RenderType WARP_RENDER_TYPE = RenderType.create(
            "warp_transition",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
    );

    public static boolean ramping = false; // true = ramping up, false = ramping down

    private static Vec3 targetVisualCenter = null;
    private static Vec3 currentVisualCenter = null;
    private static final float LERP_SPEED = 0.1f;
    public static final int tickDelay = 10;
    public static int delay = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!shouldRender) return;

        if (Minecraft.getInstance().player != null) {
            targetVisualCenter = Minecraft.getInstance().player.position();
            if (delay > 0) currentVisualCenter = targetVisualCenter;
            delay = Math.max(0, delay - 1);
            if (currentVisualCenter == null) currentVisualCenter = targetVisualCenter;
            currentVisualCenter = currentVisualCenter.add(
                    targetVisualCenter.subtract(currentVisualCenter).scale(LERP_SPEED));
        }

        if (ramping) {
            transitionProgress = Math.min(6f, transitionProgress + 0.04f);
        } else {
            shouldRender = false;
            sublevel = null;
        }
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!shouldRender || sublevel == null) return;

        PoseStack ms = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();

        ms.pushPose();
        ms.translate(-cam.x, -cam.y, -cam.z);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vc = bufferSource.getBuffer(WARP_RENDER_TYPE);

        if (currentVisualCenter == null && Minecraft.getInstance().player != null) currentVisualCenter = Minecraft.getInstance().player.position();

        for (int i = 0; i < STREAK_COUNT; i++) {
            Vec3 origin = STREAK_ORIGINS[i].add(currentVisualCenter);
            renderStreak(ms, vc, origin, transitionProgress, travelDirection, i);
        }

        bufferSource.endBatch(RenderType.translucent());
        ms.popPose();
    }

    private static void renderStreak(PoseStack ms, VertexConsumer vc, Vec3 origin,
                                     float progress, Vec3 dir, int index) {
        float stretchAmount = progress * 30f; // how long streaks get
        float width = 0.15f;

        Vec3 stretched = dir.scale(stretchAmount);
        Vec3 perp = origin.cross(dir).normalize().scale(width);

        // color cycling through rainbow based on streak index
        float hue = (index / (float) STREAK_COUNT + progress * 0.1f) % 1.0f;
        int[] color = hsvToRgb(hue, 0.7f, 1.0f);

        Vec3 p0 = origin.subtract(perp);
        Vec3 p1 = origin.add(perp);
        Vec3 p2 = origin.add(stretched).add(perp);
        Vec3 p3 = origin.add(stretched).subtract(perp);

        // forward winding
        addVertex(ms, vc, p0, color, 0);
        addVertex(ms, vc, p1, color, 0);
        addVertex(ms, vc, p2, color, 255); // fade out at tip
        addVertex(ms, vc, p3, color, 255);
        // reverse winding
        addVertex(ms, vc, p3, color, 255);
        addVertex(ms, vc, p2, color, 255);
        addVertex(ms, vc, p1, color, 0);
        addVertex(ms, vc, p0, color, 0);
    }

    private static int[] hsvToRgb(float h, float s, float v) {
        int i = (int)(h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s), q = v * (1 - f * s), t = v * (1 - (1 - f) * s);
        float r, g, b;
        switch (i % 6) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default -> { r = v; g = p; b = q; }
        }
        return new int[]{(int)(r*255), (int)(g*255), (int)(b*255)};
    }

    private static void addVertex(PoseStack ms, VertexConsumer vc, Vec3 pos, int[] rgba, int alpha) {
        vc.addVertex(ms.last().pose(), (float)pos.x, (float)pos.y, (float)pos.z)
                .setColor(rgba[0], rgba[1], rgba[2], alpha);
    }
}
