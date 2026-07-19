package com.cybrisoft.createmoderntech.block.warpgate.termimal;

import com.cybrisoft.createmoderntech.CreateModernTech;
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

public class WarpGateTerminalRenderer extends SmartBlockEntityRenderer<WarpGateTerminalBlockEntity> {

    public WarpGateTerminalRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WarpGateTerminalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
    }
}
