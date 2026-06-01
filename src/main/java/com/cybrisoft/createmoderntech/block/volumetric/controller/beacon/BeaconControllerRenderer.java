package com.cybrisoft.createmoderntech.block.volumetric.controller.beacon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BeaconControllerRenderer extends SmartBlockEntityRenderer<BeaconControllerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public BeaconControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    protected void renderSafe(BeaconControllerBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        ItemStack stackToRender = blockEntity.outputInventory.getItem(0);
        if (!stackToRender.isEmpty() && blockEntity.shouldRenderItem) {
            ms.pushPose();

            ms.translate(0.5, 0.25, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(90));
            ms.scale(1.0f, 1.0f, 1.0f);

            BakedModel bakedmodel = this.itemRenderer.getModel(stackToRender, blockEntity.getLevel(), null, 1);
            this.itemRenderer.render(
                    stackToRender,
                    ItemDisplayContext.GROUND,
                    false,
                    ms,
                    buffer,
                    light,
                    overlay,
                    bakedmodel
            );

            ms.popPose();
        }
    }
}
