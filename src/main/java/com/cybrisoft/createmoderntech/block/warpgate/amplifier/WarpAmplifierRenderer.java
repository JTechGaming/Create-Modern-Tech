package com.cybrisoft.createmoderntech.block.warpgate.amplifier;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WarpAmplifierRenderer extends KineticBlockEntityRenderer<WarpAmplifierBlockEntity> {
    private static final int SEGMENTS = 6;

    public WarpAmplifierRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WarpAmplifierBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        ms.pushPose();
        ms.translate(0.5, 0, 0.5);
        if (be.isBeamOn) {
            renderBeam(ms, 1);
        }

        ms.translate(0, 0.3, 0);
        renderRings(be, ms, buffer);
        ms.popPose();
    }

    private static final ResourceLocation BEAM_SHADER =
            ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, "warp_amplifier_beam");

    private void renderBeam(PoseStack ms, float height) {
        ShaderProgram shader = VeilRenderSystem.setShader(BEAM_SHADER);
        if (shader == null) return;

        ShaderUniformAccess time = shader.getUniform("Time");
        if (time != null) time.setFloat((float)(System.currentTimeMillis() % 1000000) / 1000f);

        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        shader.bind();

        float radius = 0.32f;
        int segments = 16;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i <= segments; i++) {
            float angle = (float)(i * 2 * Math.PI / segments);
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);
            float u = (float) i / segments;

            buf.addVertex(ms.last().pose(), cos * radius, 0f, sin * radius).setUv(u, 0f);
            buf.addVertex(ms.last().pose(), cos * radius, height, sin * radius).setUv(u, 1f);
        }

        BufferUploader.drawWithShader(buf.buildOrThrow());

        ShaderProgram.unbind();
    }

    private void renderRings(WarpAmplifierBlockEntity be, PoseStack ms, MultiBufferSource buffer) {
        VertexConsumer vc = buffer.getBuffer(RenderType.debugQuads());

        float baseRadius = 0.6f;
        float expandedRadius = 1.6f;
        float radius = baseRadius + (expandedRadius - baseRadius) * be.expansionProgress;

        float gapFactor = be.expansionProgress * 0.75f;
        float segmentArc = (float)(2 * Math.PI / SEGMENTS) * (1f - gapFactor);
        float ringWidth = 0.08f;
        float innerRadius = radius - ringWidth;

        renderRing(ms, vc, be.ring1Angle, radius, innerRadius, segmentArc, 0.0f);
        renderRing(ms, vc, be.ring2Angle, radius, innerRadius, segmentArc, 0.2f);
        renderRing(ms, vc, be.ring3Angle, radius, innerRadius, segmentArc, 0.4f);
    }

    private void renderRing(PoseStack ms, VertexConsumer vc, float baseAngle,
                            float outerR, float innerR, float segmentArc, float yOffset) {
        int arcSteps = 8; // subdivisions per segment
        int color[] = {25, 25, 25, 255}; // RGBA

        float ringHeight = 0.16f;

        for (int i = 0; i < SEGMENTS; i++) {
            float segStart = baseAngle + i * (float)(2 * Math.PI / SEGMENTS);
            float segEnd = segStart + segmentArc;

            for (int j = 0; j < arcSteps; j++) {
                float a0 = segStart + (segEnd - segStart) * (j / (float) arcSteps);
                float a1 = segStart + (segEnd - segStart) * ((j + 1) / (float) arcSteps);

                float cos0 = Mth.cos(a0), sin0 = Mth.sin(a0);
                float cos1 = Mth.cos(a1), sin1 = Mth.sin(a1);

                float y0 = yOffset - ringHeight / 2f;
                float y1 = yOffset + ringHeight / 2f;

                // bottom face
                addVertex(ms, vc, innerR * cos0, y0, innerR * sin0, color);
                addVertex(ms, vc, outerR * cos0, y0, outerR * sin0, color);
                addVertex(ms, vc, outerR * cos1, y0, outerR * sin1, color);
                addVertex(ms, vc, innerR * cos1, y0, innerR * sin1, color);
                // top face
                addVertex(ms, vc, innerR * cos0, y1, innerR * sin0, color);
                addVertex(ms, vc, outerR * cos1, y1, outerR * sin1, color);
                addVertex(ms, vc, outerR * cos0, y1, outerR * sin0, color);
                addVertex(ms, vc, innerR * cos1, y1, innerR * sin1, color);
                // outer face
                addVertex(ms, vc, outerR * cos0, y0, outerR * sin0, color);
                addVertex(ms, vc, outerR * cos0, y1, outerR * sin0, color);
                addVertex(ms, vc, outerR * cos1, y1, outerR * sin1, color);
                addVertex(ms, vc, outerR * cos1, y0, outerR * sin1, color);
                // inner face
                addVertex(ms, vc, innerR * cos0, y0, innerR * sin0, color);
                addVertex(ms, vc, innerR * cos1, y0, innerR * sin1, color);
                addVertex(ms, vc, innerR * cos1, y1, innerR * sin1, color);
                addVertex(ms, vc, innerR * cos0, y1, innerR * sin0, color);
            }
        }
    }

    private void addVertex(PoseStack ms, VertexConsumer vc, float x, float y, float z, int[] rgba) {
        vc.addVertex(ms.last().pose(), x, y, z)
                .setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
}
