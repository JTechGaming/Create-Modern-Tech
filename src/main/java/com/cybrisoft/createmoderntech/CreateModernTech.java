package com.cybrisoft.createmoderntech;

import com.cybrisoft.createmoderntech.client.BeaconCompassClientEvents;
import com.cybrisoft.createmoderntech.config.ModernTechAllConfigs;
import com.cybrisoft.createmoderntech.ponder.ModernTechPonderPlugin;
import com.cybrisoft.createmoderntech.registry.*;
import com.cybrisoft.createmoderntech.tts.FreeTTSEngine;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(CreateModernTech.MODID)
public class CreateModernTech {
    public static final String MODID = "createmoderntech";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public static final KeyMapping TEST_SPEAK = new KeyMapping(
            "key.createmoderntech.test_speak",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "key.categories.createmoderntech"
    );

    public CreateModernTech(IEventBus modEventBus, ModContainer modContainer) {
        getLogger().info("Initializing Create: Modern Tech!");

        REGISTRATE.registerEventListeners(modEventBus);

        ModDataComponents.REGISTER.register(modEventBus);
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModCreativeTabs.register(modEventBus);
        ModPackets.register(modEventBus);

        ModernTechAllConfigs.register(ModLoadingContext.get(), modContainer);

        modEventBus.addListener(CreateModernTech::init);
        modEventBus.addListener(CreateModernTech::clientInit);
        modEventBus.addListener(CreateModernTech::onLoadComplete);
        modEventBus.addListener(CreateModernTech::registerCapabilities);
        modEventBus.addListener(CreateModernTech::registerKeyMappings);
        modEventBus.addListener(ModCapabilities::register);
        modEventBus.addListener((RegisterEvent event) -> {
            ModArmInteractions.init();
        });
        modEventBus.addListener(BeaconCompassClientEvents::registerColors);

        NeoForge.EVENT_BUS.addListener(CreateModernTech::clientTick);
        NeoForge.EVENT_BUS.addListener(CreateModernTech::onLoadWorld);
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MODID, path);
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TEST_SPEAK);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        if (TEST_SPEAK.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                Vec3 pos = mc.player.position();
                FreeTTSEngine.speak("critical engine failure", pos, 32f);
            }
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    public static void onLoadWorld(LevelEvent.Load event) {
    }

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
        });
    }

    public static void clientInit(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ModernTechPonderPlugin());
        BeaconCompassClientEvents.registerItemProperties();
        FreeTTSEngine.initialize();
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    static {
        REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));
    }
}
