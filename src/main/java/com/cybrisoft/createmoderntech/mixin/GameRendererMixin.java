package com.cybrisoft.createmoderntech.mixin;

import com.cybrisoft.createmoderntech.client.SpyglassZoomManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;
    @Shadow private float fov;

    @Inject(method = "tickFov", at = @At("TAIL"))
    private void bypassMinimumFovLimit(CallbackInfo ci) {
        if (SpyglassZoomManager.isUsingAdvancedSpyglass(this.minecraft.player)) {
            this.fov = SpyglassZoomManager.getCurrentModifier();
        }
    }
}
