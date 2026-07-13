package com.cybrisoft.createmoderntech.block.warpgate;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class WarpGateTerminalRenderer extends SmartBlockEntityRenderer<WarpGateTerminalBlockEntity> {
    private static final ResourceLocation WARPGATE_SHADER = ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, "warpgate");

    public WarpGateTerminalRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WarpGateTerminalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        if (be.multiblockRadius > 0) {
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

            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(true);
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
                guideDot(ms, buffer, cx + y, cy + x, cz);
                guideDot(ms, buffer, cx - y, cy + x, cz);
                guideDot(ms, buffer, cx + y, cy - x, cz);
                guideDot(ms, buffer, cx - y, cy - x, cz);
                guideDot(ms, buffer, cx + x, cy + y, cz);
                guideDot(ms, buffer, cx - x, cy + y, cz);
                guideDot(ms, buffer, cx + x, cy - y, cz);
                guideDot(ms, buffer, cx - x, cy - y, cz);
                if (d < 0) d += 2 * x + 3;
                else {
                    d += 2 * (x - y) + 5;
                    y--;
                }
                x++;
            }
        }
    }

    private void guideDot(PoseStack ms, MultiBufferSource buffer, int x, int y, int z) {
        ms.pushPose();
        ms.translate(x + 0.25, y + 0.25, z + 0.25);
        ms.scale(0.5f, 0.5f, 0.5f);
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(ms, vc, 0, 0, 0, 1, 1, 1, 1f, 1f, 1f, 1f);
        ms.popPose();
    }
}
