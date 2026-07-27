package com.cybrisoft.createmoderntech;

import com.cybrisoft.createmoderntech.client.BeaconCompassClientEvents;
import com.cybrisoft.createmoderntech.compat.ModernTechTriggerVarProviders;
import com.cybrisoft.createmoderntech.compat.VanillaTriggerVarProviders;
import com.cybrisoft.createmoderntech.compat.create.CreateTriggerVarProviders;
import com.cybrisoft.createmoderntech.compat.simulated.SimulatedTriggerVarProviders;
import com.cybrisoft.createmoderntech.config.ModernTechAllConfigs;
import com.cybrisoft.createmoderntech.ponder.ModernTechPonderPlugin;
import com.cybrisoft.createmoderntech.registry.*;
import com.cybrisoft.createmoderntech.tts.FreeTTSEngine;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
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

    private static final ResourceLocation WARP_WARP_SHADER = ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, "warp_warp");

    public CreateModernTech(IEventBus modEventBus, ModContainer modContainer) {
        getLogger().info("Initializing Create: Modern Tech!");

        REGISTRATE.registerEventListeners(modEventBus);

        ModDataComponents.REGISTER.register(modEventBus);
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModCreativeTabs.register(modEventBus);
        ModPackets.register(modEventBus);
        ModSounds.register(modEventBus);

        ModernTechAllConfigs.register(ModLoadingContext.get(), modContainer);

        new VanillaTriggerVarProviders().register();
        new CreateTriggerVarProviders().register();
        new SimulatedTriggerVarProviders().register();
        new ModernTechTriggerVarProviders().register();

        modEventBus.addListener(CreateModernTech::init);
        modEventBus.addListener(CreateModernTech::onLoadComplete);
        modEventBus.addListener(CreateModernTech::registerCapabilities);
        modEventBus.addListener(ModCapabilities::register);
        modEventBus.addListener((RegisterEvent event) -> {
            ModArmInteractions.init();
        });

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(CreateModernTech::registerKeyMappings);
            modEventBus.addListener(CreateModernTech::clientInit);
            modEventBus.addListener(BeaconCompassClientEvents::registerColors);
            NeoForge.EVENT_BUS.addListener(CreateModernTech::clientTick);
        }
        NeoForge.EVENT_BUS.addListener(CreateModernTech::onLoadWorld);
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MODID, path);
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) { }

    private static void clientTick(ClientTickEvent.Post event) { }

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

    @OnlyIn(Dist.CLIENT)
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
