package com.cybrisoft.createmoderntech.mixin;

import com.cybrisoft.createmoderntech.client.SpyglassZoomManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class GuiMixin {
    @Redirect(
            method = "renderCameraOverlays",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z")
    )
    private boolean modifyIsScoping(net.minecraft.client.player.LocalPlayer player) {
        return player.isScoping() || SpyglassZoomManager.isUsingAdvancedSpyglass(player);
    }
}