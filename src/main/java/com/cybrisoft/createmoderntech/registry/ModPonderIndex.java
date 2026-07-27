package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.ponder.PonderScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

public class ModPonderIndex implements PonderPlugin {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateModernTech.getLogger().info("Registering Ponder!");
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER =
                helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(ModBlocks.VOLUMETRIC_DISPLAY_BLOCK)
                .addStoryBoard("volumetric_display", PonderScenes::volumetricDisplay)
                .addStoryBoard("lens_extensions", PonderScenes::lensExtensions)
                .addStoryBoard("beacon_controller", PonderScenes::beaconController);

        HELPER.forComponents(ModBlocks.LIGHT_BOOST_FILTER, ModBlocks.TELEPHOTO_EXTENSION, ModBlocks.LIME_COLOR_FILTER,
                ModBlocks.RED_COLOR_FILTER, ModBlocks.PURPLE_COLOR_FILTER, ModBlocks.WHITE_COLOR_FILTER
        ).addStoryBoard("lens_extensions", PonderScenes::lensExtensions);

        HELPER.forComponents(ModBlocks.BEACON_CONTROLLER_BLOCK)
                .addStoryBoard("beacon_controller", PonderScenes::beaconController);

        HELPER.forComponents(ModBlocks.AI_CORE_BLOCK, ModBlocks.AUDIO_TRIGGER_BLOCK, ModBlocks.SPEAKER_BLOCK)
                .addStoryBoard("ai_system", PonderScenes::aiSystem)
                .addStoryBoard("audio_trigger", PonderScenes::audioTrigger);

        HELPER.forComponents(ModBlocks.WARP_GATE_BLOCK, ModBlocks.WARP_AMPLIFIER_BLOCK, ModBlocks.WARP_DRIVE_BLOCK, ModBlocks.WARP_GATE_TERMINAL_BLOCK, ModBlocks.WARP_GATE_TRANSPONDER_BLOCK)
                .addStoryBoard("warp_gate_intro", PonderScenes::warpGate)
                .addStoryBoard("gate_working", PonderScenes::workingGate);
    }

    @Override
    public String getModId() {
        return CreateModernTech.MODID;
    }
}
