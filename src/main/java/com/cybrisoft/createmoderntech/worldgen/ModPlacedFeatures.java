package com.cybrisoft.createmoderntech.worldgen;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> YTTERBIUM_ORE_PLACED_KEY = registerKey("ytterbium_ore_placed");
    public static final ResourceKey<PlacedFeature> MYSTERIOUS_ORE_PLACED_KEY = registerKey("mysterious_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, YTTERBIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.YTTERBIUM_ORE_KEY),
                commonOrePlacement(3 , HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(127))));

        register(context, MYSTERIOUS_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MYSTERIOUS_ORE_KEY),
                commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange) {
        return List.of(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
    }
}
