package com.cybrisoft.createmoderntech.worldgen;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> YTTERBIUM_ORE_KEY = registerKey("ytterbium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTERIOUS_ORE_KEY = registerKey("mysterious_ore");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest blackstoneReplaceables = new BlockMatchTest(Blocks.BLACKSTONE);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> ytterbiumOres = List.of(
                OreConfiguration.target(blackstoneReplaceables, ModBlocks.YTTERBIUM_ORE.getDefaultState())
        );

        register(context, YTTERBIUM_ORE_KEY, Feature.ORE, new OreConfiguration(ytterbiumOres, 5));
        register(context, MYSTERIOUS_ORE_KEY, Feature.ORE, new OreConfiguration(endReplaceables,
                ModBlocks.MYSTERIOUS_ORE.get().defaultBlockState(), 3));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
