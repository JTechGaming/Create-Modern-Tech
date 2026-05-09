package com.cybrisoft.createmoderntech;

import com.cybrisoft.createmoderntech.ponder.ModernTechPonderPlugin;
import com.cybrisoft.createmoderntech.registry.*;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateModernTech.MODID)
public class CreateModernTech {
    public static final String MODID = "createmoderntech";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public CreateModernTech(IEventBus modEventBus, ModContainer modContainer) {
        getLogger().info("Initializing Create: Modern Tech!");

        NeoForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);

        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModCreativeTabs.register(modEventBus);
        ModPackets.register(modEventBus);

        modEventBus.addListener(CreateModernTech::init);
        modEventBus.addListener(CreateModernTech::clientInit);
        modEventBus.addListener(CreateModernTech::onLoadComplete);
        modEventBus.addListener(CreateModernTech::registerCapabilities);

        NeoForge.EVENT_BUS.addListener(CreateModernTech::clientTick);
        NeoForge.EVENT_BUS.addListener(CreateModernTech::onLoadWorld);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
    }

    private static void clientTick(ClientTickEvent.Post event) {
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
