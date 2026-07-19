package com.cybrisoft.createmoderntech.datagen;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Map;
import java.util.Set;

public class BlockLootProvider extends BlockLootSubProvider {
    protected BlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.VOLUMETRIC_DISPLAY_BLOCK.get());
        dropSelf(ModBlocks.VOLUMETRIC_SHAFT_BLOCK.get());
        dropSelf(ModBlocks.PAN_X_CONTROLLER_BLOCK.get());
        dropSelf(ModBlocks.PAN_Z_CONTROLLER_BLOCK.get());
        dropSelf(ModBlocks.PITCH_CONTROLLER_BLOCK.get());
        dropSelf(ModBlocks.YAW_CONTROLLER_BLOCK.get());
        dropSelf(ModBlocks.BEACON_CONTROLLER_BLOCK.get());
        dropSelf(ModBlocks.REGIONAL_STRESS_GAUGE_BLOCK.get());
        dropSelf(ModBlocks.SPRING_BUFFER_BLOCK.get());
        dropSelf(ModBlocks.SPEAKER_BLOCK.get());
        dropSelf(ModBlocks.AUDIO_TRIGGER_BLOCK.get());
        dropSelf(ModBlocks.AI_CORE_BLOCK.get());
        dropSelf(ModBlocks.LENS_1X.get());
        dropSelf(ModBlocks.LENS_2X.get());
        dropSelf(ModBlocks.LENS_4X.get());
        dropSelf(ModBlocks.LENS_8X.get());
        dropSelf(ModBlocks.LENS_16X.get());
        dropSelf(ModBlocks.LENS_EXTENSION.get());
        dropSelf(ModBlocks.TELEPHOTO_EXTENSION.get());
        dropSelf(ModBlocks.LIME_COLOR_FILTER.get());
        dropSelf(ModBlocks.RED_COLOR_FILTER.get());
        dropSelf(ModBlocks.PURPLE_COLOR_FILTER.get());
        dropSelf(ModBlocks.WHITE_COLOR_FILTER.get());
        dropSelf(ModBlocks.LIGHT_BOOST_FILTER.get());
        dropSelf(ModBlocks.ANGLED_LENS_EXTENSION.get());
        dropSelf(ModBlocks.VERTICAL_ANGLED_LENS_EXTENSION.get());

        dropSelf(ModBlocks.WARP_GATE_BLOCK.get());
        dropSelf(ModBlocks.WARP_DRIVE_BLOCK.get());
        dropSelf(ModBlocks.WARP_AMPLIFIER_BLOCK.get());
        dropSelf(ModBlocks.WARP_GATE_TERMINAL_BLOCK.get());
        dropSelf(ModBlocks.WARP_GATE_TRANSPONDER_BLOCK.get());
        dropSelf(ModBlocks.SPRING_BUFFER_BLOCK.get());

        add(ModBlocks.YTTERBIUM_ORE.get(),
                block -> createVariableOreDrops(ModBlocks.YTTERBIUM_ORE.get(), ModItems.RAW_YTTERBIUM.get(), 1, 3));
        add(ModBlocks.MYSTERIOUS_ORE.get(),
                block -> createVariableOreDrops(ModBlocks.MYSTERIOUS_ORE.get(), ModItems.RAW_MYSTERIOUS_CRYSTAL.get(), 1, 1));
    }

    protected LootTable.Builder createVariableOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(CreateModernTech.MODID))
                .map(Map.Entry::getValue)
                .toList();
    }
}
