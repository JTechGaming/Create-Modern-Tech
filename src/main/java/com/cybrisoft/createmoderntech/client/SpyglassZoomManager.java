package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class SpyglassZoomManager {
    private static float modifier = 0.4F;
    private static final float MAX_ZOOM_MODIFIER = 0.4F;
    private static final float MIN_ZOOM_MODIFIER = 0.01F;

    public static float getCurrentModifier() {
        return modifier;
    }

    public static void handleScroll(double scrollDelta) {
        if (scrollDelta > 0) {
            modifier = Math.max(MIN_ZOOM_MODIFIER, modifier / 2.0F);
        } else if (scrollDelta < 0) {
            modifier = Math.min(MAX_ZOOM_MODIFIER, modifier * 2.0F);
        }
    }

    public static void reset() {
        modifier = 0.2F;
    }

    public static boolean isUsingAdvancedSpyglass(Player player) {
        return player != null && player.isUsingItem() && player.getUseItem().is(ModItems.ADJUSTABLE_SPYGLASS.get());
    }
}
