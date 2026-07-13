package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.aicore.AICoreBlock;
import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockItem;
import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlock;
import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockItem;
import com.cybrisoft.createmoderntech.block.gauge.RegionalStressGaugeBlock;
import com.cybrisoft.createmoderntech.block.lens.AngledLensExtensionBlock;
import com.cybrisoft.createmoderntech.block.lens.VerticalAngledLensExtensionBlock;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlock;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockItem;
import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlock;
import com.cybrisoft.createmoderntech.block.warpgate.WarpGateBlock;
import com.cybrisoft.createmoderntech.block.warpgate.WarpGateTerminalBlock;
import com.cybrisoft.createmoderntech.block.warpgate.WarpGateTransponderBlock;
import com.cybrisoft.createmoderntech.config.ModernTechStress;
import com.cybrisoft.createmoderntech.block.lens.VerticalLensBlock;
import com.cybrisoft.createmoderntech.block.lens.LensExtensionBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanXControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanZControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.YawControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlock;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.lang.FontHelper;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings("removal")
public class ModBlocks {
    public static final BlockEntry<VolumetricDisplayBlock> VOLUMETRIC_DISPLAY_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
                    .block("volumetric_display", VolumetricDisplayBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .transform(ModernTechStress.setImpact(4.0))
                    .simpleItem()
                    .register();

    public static final BlockEntry<VolumetricShaftBlock> VOLUMETRIC_SHAFT_BLOCK =
            REGISTRATE.block("volumetric_shaft", VolumetricShaftBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static final BlockEntry<PanXControllerBlock> PAN_X_CONTROLLER_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
                    .block("pan_x_controller", PanXControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();
    public static final BlockEntry<PanZControllerBlock> PAN_Z_CONTROLLER_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
                    .block("pan_z_controller", PanZControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();
    public static final BlockEntry<PitchControllerBlock> PITCH_CONTROLLER_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
                    .block("pitch_controller", PitchControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();
    public static final BlockEntry<YawControllerBlock> YAW_CONTROLLER_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)))).block("yaw_controller", YawControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();
    public static final BlockEntry<BeaconControllerBlock> BEACON_CONTROLLER_BLOCK =
            REGISTRATE.block("beacon_controller", BeaconControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.noOcclusion().strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static final BlockEntry<RegionalStressGaugeBlock> REGIONAL_STRESS_GAUGE_BLOCK =
            REGISTRATE.block("regional_stress_gauge", RegionalStressGaugeBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.noOcclusion().strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static final BlockEntry<SpeakerBlock> SPEAKER_BLOCK =
            REGISTRATE.block("speaker", SpeakerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .item(SpeakerBlockItem::new).build()
                    .register();

    public static final BlockEntry<AudioTriggerBlock> AUDIO_TRIGGER_BLOCK =
            REGISTRATE.block("audio_trigger", AudioTriggerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .item(AudioTriggerBlockItem::new).build()
                    .register();

    public static final BlockEntry<AICoreBlock> AI_CORE_BLOCK =
            REGISTRATE.block("ai_core_block", AICoreBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .item(AICoreBlockItem::new).build()
                    .register();

    public static final BlockEntry<WarpGateTransponderBlock> WARP_GATE_TRANSPONDER_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))).block("warp_gate_transponder", WarpGateTransponderBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();

    public static final BlockEntry<WarpGateBlock> WARP_GATE_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))).block("warp_gate", WarpGateBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static final BlockEntry<WarpGateTerminalBlock> WARP_GATE_TERMINAL_BLOCK =
            REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))).block("warp_gate_terminal", WarpGateTerminalBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static final BlockEntry<VerticalLensBlock> LENS_1X = registerLens("lens_1x");
    public static final BlockEntry<VerticalLensBlock> LENS_2X = registerLens("lens_2x");
    public static final BlockEntry<VerticalLensBlock> LENS_4X = registerLens("lens_4x");
    public static final BlockEntry<VerticalLensBlock> LENS_8X = registerLens("lens_8x");
    public static final BlockEntry<VerticalLensBlock> LENS_16X = registerLens("lens_16x");
    public static final BlockEntry<LensExtensionBlock> LENS_EXTENSION = registerExtension("lens_extension");
    public static final BlockEntry<LensExtensionBlock> TELEPHOTO_EXTENSION = registerExtension("telephoto_extension");
    public static final BlockEntry<LensExtensionBlock> LIME_COLOR_FILTER = registerExtension("lime_color_filter");
    public static final BlockEntry<LensExtensionBlock> RED_COLOR_FILTER = registerExtension("red_color_filter");
    public static final BlockEntry<LensExtensionBlock> PURPLE_COLOR_FILTER = registerExtension("purple_color_filter");
    public static final BlockEntry<LensExtensionBlock> WHITE_COLOR_FILTER = registerExtension("white_color_filter");
    public static final BlockEntry<LensExtensionBlock> LIGHT_BOOST_FILTER = registerExtension("light_boost_filter");

    public static final BlockEntry<AngledLensExtensionBlock> ANGLED_LENS_EXTENSION = REGISTRATE.block("angled_lens_extension", AngledLensExtensionBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().strength(0.5f))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.directionalBlock(c.getEntry(),AssetLookup.standardModel(c, p)))
            .simpleItem()
            .register();
    public static final BlockEntry<VerticalAngledLensExtensionBlock> VERTICAL_ANGLED_LENS_EXTENSION = REGISTRATE.block("vertical_angled_lens_extension", VerticalAngledLensExtensionBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().strength(0.5f))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.directionalBlock(c.getEntry(),AssetLookup.standardModel(c, p)))
            .simpleItem()
            .register();

    private static BlockEntry<LensExtensionBlock> registerExtension(String name) {
        return REGISTRATE.block(name, LensExtensionBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().strength(0.5f))
                .transform(axeOrPickaxe())
                .blockstate((c, p) -> p.directionalBlock(c.getEntry(), p.models().getExistingFile(p.modLoc("block/" + name))))
                .simpleItem()
                .register();
    }

    private static BlockEntry<VerticalLensBlock> registerLens(String name) {
        return REGISTRATE.block(name, VerticalLensBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().strength(0.5f))
                .transform(axeOrPickaxe())
                .blockstate((c, p) -> p.directionalBlock(c.getEntry(), p.models().getExistingFile(p.modLoc("block/" + name))))
                .simpleItem()
                .register();
    }

    public static void register() {
        CreateModernTech.getLogger().info("Registering blocks!");
    }
}
