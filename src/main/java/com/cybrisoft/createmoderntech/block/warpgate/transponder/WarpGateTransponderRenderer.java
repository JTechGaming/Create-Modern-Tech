package com.cybrisoft.createmoderntech.block.warpgate.transponder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;

public class WarpGateTransponderRenderer extends SmartBlockEntityRenderer<WarpGateTransponderBlockEntity> {
    public WarpGateTransponderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private int timer = 0;

    @Override
    protected void renderSafe(WarpGateTransponderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        ms.pushPose();
        ms.translate(0.5, 1.31, 0.5);

        Direction facing = be.getBlockState().getValue(WarpGateTransponderBlock.FACING);
        ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        ms.mulPose(Axis.XP.rotationDegrees(-45));

        ms.translate(0, 0, -0.07);

        float scale = 1f / 80f;
        ms.scale(scale, -scale, scale);

        Font font = Minecraft.getInstance().font;

        String speedText = String.format("%.01f", be.shipSpeed) + " m/s";
        float speedWidth = font.width(speedText);
        int[] speedColor = be.shipSpeed >= WarpGateTransponderBlockEntity.MIN_SPEED_REQ ? new int[]{137, 243, 54} : new int[]{255, 44, 44};

        String distanceText = String.format("%.01f", be.distanceToGate) + " m";
        float distanceWidth = font.width(distanceText);
        int[] distanceColor = distanceReq(be) ? new int[]{137, 243, 54} : new int[]{255, 44, 44};

        String warpingText = "Jumping..";
        float warpingWidth = font.width(warpingText);

        String noneText = "No Target Found";
        float noneWidth = font.width(noneText);

        if (be.targetGatePos == null) {
            ms.scale(0.5f, 0.5f, 0.5f);
            font.drawInBatch(noneText, -noneWidth / 2f, 12,
                    FastColor.ARGB32.color(255, 255, 44, 44),
                    false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        } else if (be.stagingTeleport) {
            if (timer > 60) {
                if (timer >= 120) {
                    timer = 0;
                }

                font.drawInBatch(warpingText, -warpingWidth / 2f, 5,
                        FastColor.ARGB32.color(255, 137, 243, 54),
                        false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
            }
        } else {
            font.drawInBatch(speedText, -speedWidth / 2f, 0,
                    FastColor.ARGB32.color(255, speedColor[0], speedColor[1], speedColor[2]),
                    false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

            font.drawInBatch(distanceText, -distanceWidth / 2f, 10,
                    FastColor.ARGB32.color(255, distanceColor[0], distanceColor[1], distanceColor[2]),
                    false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        }

        ms.popPose();

        timer++;
    }

    private boolean distanceReq(WarpGateTransponderBlockEntity be) {
        float finalSpeedS = be.shipSpeed * be.shipSpeed + (2 * be.shipAcceleration * be.distanceToGate);
        return Math.sqrt(finalSpeedS) >= WarpGateTransponderBlockEntity.MIN_SPEED_REQ;
    }
}
