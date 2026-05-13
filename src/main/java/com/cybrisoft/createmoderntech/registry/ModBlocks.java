package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.config.ModernTechStress;
import com.cybrisoft.createmoderntech.block.lens.VerticalLensBlock;
import com.cybrisoft.createmoderntech.block.lens.VerticalLensExtensionBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanXControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanZControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.YawControllerBlock;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlock;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings("removal")
public class ModBlocks {
    public static final BlockEntry<VolumetricDisplayBlock> VOLUMETRIC_DISPLAY_BLOCK =
            REGISTRATE.block("volumetric_display", VolumetricDisplayBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
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
            REGISTRATE.block("pan_x_controller", PanXControllerBlock::new)
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
            REGISTRATE.block("pan_z_controller", PanZControllerBlock::new)
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
            REGISTRATE.block("pitch_controller", PitchControllerBlock::new)
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
            REGISTRATE.block("yaw_controller", YawControllerBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .transform(ModernTechStress.setImpact(1.0))
                    .simpleItem()
                    .register();

    public static final BlockEntry<VerticalLensBlock> LENS_1X = registerLens("lens_1x");
    public static final BlockEntry<VerticalLensBlock> LENS_2X = registerLens("lens_2x");
    public static final BlockEntry<VerticalLensBlock> LENS_4X = registerLens("lens_4x");
    public static final BlockEntry<VerticalLensBlock> LENS_10X = registerLens("lens_10x");
    public static final BlockEntry<VerticalLensBlock> LENS_16X = registerLens("lens_16x");
    public static final BlockEntry<VerticalLensExtensionBlock> LENS_EXTENSION = registerExtension("lens_extension");
    public static final BlockEntry<VerticalLensExtensionBlock> TELEPHOTO_EXTENSION = registerExtension("telephoto_extension");
    public static final BlockEntry<VerticalLensExtensionBlock> LIME_COLOR_FILTER = registerExtension("lime_color_filter");
    public static final BlockEntry<VerticalLensExtensionBlock> RED_COLOR_FILTER = registerExtension("red_color_filter");
    public static final BlockEntry<VerticalLensExtensionBlock> PURPLE_COLOR_FILTER = registerExtension("purple_color_filter");
    public static final BlockEntry<VerticalLensExtensionBlock> WHITE_COLOR_FILTER = registerExtension("white_color_filter");
    public static final BlockEntry<VerticalLensExtensionBlock> LIGHT_BOOST_FILTER = registerExtension("light_boost_filter");

    private static BlockEntry<VerticalLensExtensionBlock> registerExtension(String name) {
        return REGISTRATE.block(name, VerticalLensExtensionBlock::new)
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
