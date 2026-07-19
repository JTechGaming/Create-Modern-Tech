package com.cybrisoft.createmoderntech.mixin;

import com.cybrisoft.createmoderntech.client.WarpTransitionRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void onRenderSky(CallbackInfo ci) {
        if (WarpTransitionRenderer.shouldRender) {
            ci.cancel();
        }
    }
}