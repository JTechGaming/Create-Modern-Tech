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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!shouldRender) return;

        if (Minecraft.getInstance().player != null) {
            targetVisualCenter = Minecraft.getInstance().player.position();
            if (currentVisualCenter == null) currentVisualCenter = targetVisualCenter;
            currentVisualCenter = currentVisualCenter.add(
                    targetVisualCenter.subtract(currentVisualCenter).scale(LERP_SPEED));
        }

        if (ramping) {
            transitionProgress = Math.min(1f, transitionProgress + 0.02f);
        } else {
            transitionProgress = Math.max(0f, transitionProgress - 0.05f);
            if (transitionProgress <= 0f) {
                shouldRender = false;
                sublevel = null;
            }
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

        //renderTunnel(ms, vc, visualCenter);
        for (int i = 0; i < STREAK_COUNT; i++) {
            Vec3 origin = STREAK_ORIGINS[i].add(currentVisualCenter);
            renderStreak(ms, vc, origin, transitionProgress, travelDirection, i);
        }

        bufferSource.endBatch(RenderType.translucent());
        ms.popPose();
    }

    private static void renderTunnel(PoseStack ms, VertexConsumer vc, Vec3 visualCenter) {
        BoundingBox3dc bb = sublevel.boundingBox();

        // compute half extents from bounding box size
        float halfX = (float)(bb.maxX() - bb.minX()) / 2f + 2f;
        float halfY = (float)(bb.maxY() - bb.minY()) / 2f + 2f;
        float halfZ = (float)(bb.maxZ() - bb.minZ()) / 2f + 2f;

        float cx = (float) visualCenter.x;
        float cy = (float) visualCenter.y;
        float cz = (float) visualCenter.z;

        float minX = cx - halfX, maxX = cx + halfX;
        float minY = cy - halfY, maxY = cy + halfY;
        float minZ = cz - halfZ, maxZ = cz + halfZ;

        float r = 0.02f, g = 0.02f, b = 0.05f, a = 0.95f;

        // bottom
        quadVC(ms, vc, minX,minY,minZ, maxX,minY,minZ, maxX,minY,maxZ, minX,minY,maxZ, r,g,b,a);
        // top
        quadVC(ms, vc, minX,maxY,minZ, minX,maxY,maxZ, maxX,maxY,maxZ, maxX,maxY,minZ, r,g,b,a);
        // north
        quadVC(ms, vc, minX,minY,minZ, minX,maxY,minZ, maxX,maxY,minZ, maxX,minY,minZ, r,g,b,a);
        // south
        quadVC(ms, vc, minX,minY,maxZ, maxX,minY,maxZ, maxX,maxY,maxZ, minX,maxY,maxZ, r,g,b,a);
        // west
        quadVC(ms, vc, minX,minY,minZ, minX,minY,maxZ, minX,maxY,maxZ, minX,maxY,minZ, r,g,b,a);
        // east
        quadVC(ms, vc, maxX,minY,minZ, maxX,maxY,minZ, maxX,maxY,maxZ, maxX,minY,maxZ, r,g,b,a);
    }

    private static void quadVC(PoseStack ms, VertexConsumer vc,
                               float x1, float y1, float z1, float x2, float y2, float z2,
                               float x3, float y3, float z3, float x4, float y4, float z4,
                               float r, float g, float b, float a) {
        vc.addVertex(ms.last().pose(), x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(ms.last().pose(), x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(ms.last().pose(), x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(ms.last().pose(), x4, y4, z4).setColor(r, g, b, a);
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
