package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;

public class ModCreativeTabs {
    public static DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateModernTech.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MODERN_TECH_CREATIVE_TAB = addTab("modern_tech", "Create: Modern Tech",
            ModBlocks.VOLUMETRIC_DISPLAY_BLOCK::asStack);


    public static DeferredHolder<CreativeModeTab, CreativeModeTab> addTab(String id, String name, Supplier<ItemStack> icon) {
        String itemGroupId = "itemGroup." + CreateModernTech.MODID + "." + id;
        REGISTRATE.addRawLang(itemGroupId, name);

        CreativeModeTab.Builder tabBuilder = CreativeModeTab.builder()
                .icon(icon)
                .displayItems(ModCreativeTabs::displayItems)
                .title(Component.translatable(itemGroupId))
                .withTabsBefore(getCreateTabOrFallback());

        return CREATIVE_TABS.register(id, tabBuilder::build);
    }

    private static ResourceKey<CreativeModeTab> getCreateTabOrFallback() {
        try {
            Class<?> clazz = Class.forName("com.simibubi.create.AllCreativeModeTabs");
            var field = clazz.getField("PALETTES_CREATIVE_TAB");
            Object palettesTab = field.get(null);

            var getKeyMethod = palettesTab.getClass().getMethod("getKey");
            @SuppressWarnings("unchecked")
            ResourceKey<CreativeModeTab> key =
                    (ResourceKey<CreativeModeTab>) getKeyMethod.invoke(palettesTab);

            return key;
        } catch (Throwable t) {
            return CreativeModeTabs.REDSTONE_BLOCKS;
        }
    }

    private static void displayItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(ModBlocks.VOLUMETRIC_DISPLAY_BLOCK);
        pOutput.accept(ModBlocks.VOLUMETRIC_SHAFT_BLOCK);
        pOutput.accept(ModBlocks.YAW_CONTROLLER_BLOCK);
        pOutput.accept(ModBlocks.PITCH_CONTROLLER_BLOCK);
        pOutput.accept(ModBlocks.PAN_X_CONTROLLER_BLOCK);
        pOutput.accept(ModBlocks.PAN_Z_CONTROLLER_BLOCK);
        pOutput.accept(ModBlocks.BEACON_CONTROLLER_BLOCK);

        pOutput.accept(ModBlocks.LENS_1X);
        pOutput.accept(ModBlocks.LENS_2X);
        pOutput.accept(ModBlocks.LENS_4X);
        pOutput.accept(ModBlocks.LENS_8X);
        pOutput.accept(ModBlocks.LENS_16X);
        pOutput.accept(ModBlocks.LENS_EXTENSION);
        pOutput.accept(ModBlocks.ANGLED_LENS_EXTENSION);
        pOutput.accept(ModBlocks.VERTICAL_ANGLED_LENS_EXTENSION);
        pOutput.accept(ModBlocks.TELEPHOTO_EXTENSION);
        pOutput.accept(ModBlocks.LIGHT_BOOST_FILTER);
        pOutput.accept(ModBlocks.LIME_COLOR_FILTER);
        pOutput.accept(ModBlocks.RED_COLOR_FILTER);
        pOutput.accept(ModBlocks.PURPLE_COLOR_FILTER);
        pOutput.accept(ModBlocks.WHITE_COLOR_FILTER);

        pOutput.accept(ModBlocks.REGIONAL_STRESS_GAUGE_BLOCK);
//
//        pOutput.accept(ModBlocks.SPRING_BUFFER_BLOCK);

        pOutput.accept(ModBlocks.SPEAKER_BLOCK);
        pOutput.accept(ModBlocks.AUDIO_TRIGGER_BLOCK);
        pOutput.accept(ModBlocks.AI_CORE_BLOCK);

        pOutput.accept(ModBlocks.WARP_GATE_BLOCK);
        pOutput.accept(ModBlocks.WARP_GATE_TERMINAL_BLOCK);
        pOutput.accept(ModBlocks.WARP_GATE_TRANSPONDER_BLOCK);
        pOutput.accept(ModBlocks.WARP_DRIVE_BLOCK);
        pOutput.accept(ModBlocks.WARP_AMPLIFIER_BLOCK);

        pOutput.accept(ModBlocks.YTTERBIUM_ORE);
        pOutput.accept(ModBlocks.MYSTERIOUS_ORE);

        pOutput.accept(ModItems.BEACON_COMPASS);
        pOutput.accept(ModItems.ADJUSTABLE_SPYGLASS);

        pOutput.accept(ModItems.AI_PROCESSOR);
        pOutput.accept(ModItems.LENS_ELEMENT);
        pOutput.accept(ModItems.OPTICAL_DRIVE);
        pOutput.accept(ModItems.DIAPHRAGM);
        pOutput.accept(ModItems.MEMBRANE);

        pOutput.accept(ModItems.RAW_YTTERBIUM);
        pOutput.accept(ModItems.YTTERBIUM_INGOT);
        pOutput.accept(ModItems.YTTERBIUM_NUGGET);
        pOutput.accept(ModItems.PURIFIED_YTTERBIUM_NUGGET);
        pOutput.accept(ModItems.YTTERBIUM_SHEET);
        pOutput.accept(ModItems.HIGH_FREQUENCY_PLATING);

        pOutput.accept(ModItems.RAW_MYSTERIOUS_CRYSTAL);
        pOutput.accept(ModItems.ROUGH_MYSTERIOUS_CRYSTAL);
        pOutput.accept(ModItems.MYSTERIOUS_CRYSTAL);
    }


    public static void register(IEventBus eventBus) {
        CreateModernTech.getLogger().info("Registering CreativeTabs!");
        CREATIVE_TABS.register(eventBus);
    }
}
