package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.ponder.PonderScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonderIndex implements PonderPlugin {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateModernTech.getLogger().info("Registering Ponder!");
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER =
                helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(ModBlocks.VOLUMETRIC_DISPLAY_BLOCK)
                .addStoryBoard("volumetric_display", PonderScenes::volumetricDisplay);
    }

    @Override
    public String getModId() {
        return CreateModernTech.MODID;
    }
}
