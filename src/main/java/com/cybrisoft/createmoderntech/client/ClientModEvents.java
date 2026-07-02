package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CreateModernTech.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    private static boolean wasUsingSpyglass = false;

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        boolean isUsingNow = SpyglassZoomManager.isUsingAdvancedSpyglass(event.getPlayer());

        if (isUsingNow) {
            // Pas jouw scroll-modifier toe
            event.setNewFovModifier(SpyglassZoomManager.getCurrentModifier());
            wasUsingSpyglass = true;
        } else if (wasUsingSpyglass) {
            SpyglassZoomManager.reset();
            wasUsingSpyglass = false;
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (SpyglassZoomManager.isUsingAdvancedSpyglass(Minecraft.getInstance().player)) {
            SpyglassZoomManager.handleScroll(event.getScrollDeltaY());

            // Cancel event to prevent switching items in the hotbar
            event.setCanceled(true);
        }
    }
}
