package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.item.BeaconCompassData;
import com.cybrisoft.createmoderntech.item.BeaconCompassItem;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import com.cybrisoft.createmoderntech.registry.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

public class BeaconCompassClientEvents {
    public static void registerItemProperties() {
        ItemProperties.register(
                ModItems.BEACON_COMPASS.get(),
                ResourceLocation.withDefaultNamespace("angle"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0f;
                    return BeaconCompassItem.getNeedleAngle(stack, level, entity, seed);
                }
        );
    }

    public static void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) return 0xFFFFFF; // tint index 0 = base texture
            BeaconCompassData data = stack.get(ModDataComponents.BEACON_TARGET.get());
            if (data == null) return 0xFFFFFF;
            return data.packedRGB();
        }, ModItems.BEACON_COMPASS.get());
    }
}