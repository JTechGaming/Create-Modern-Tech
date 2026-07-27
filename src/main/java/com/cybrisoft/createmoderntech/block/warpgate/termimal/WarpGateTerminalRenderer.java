package com.cybrisoft.createmoderntech.block.warpgate.termimal;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.client.WarpGateRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WarpGateTerminalRenderer extends SmartBlockEntityRenderer<WarpGateTerminalBlockEntity> {

    public WarpGateTerminalRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }



    @Override
    protected void renderSafe(WarpGateTerminalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);

        if (be.isVirtual()) {
            ms.pushPose();
            Vec3 offset = be.ponderRenderOffset;
            ms.translate(offset.x, offset.y, offset.z);
            WarpGateRenderer.drawGate(be, ms, Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
            ms.popPose();
        }
    }
}
