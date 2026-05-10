package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.lens.VerticalLensBlock;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlock;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.RotatedPillarBlock;

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
                    .onRegister((block) -> BlockStressValues.IMPACTS.register(block, () -> 4.0))
                    .simpleItem()
                    .register();

    public static final BlockEntry<VerticalLensBlock> LENS_1X = registerLens("lens_1x");
    public static final BlockEntry<VerticalLensBlock> LENS_2X = registerLens("lens_2x");
    public static final BlockEntry<VerticalLensBlock> LENS_4X = registerLens("lens_4x");
    public static final BlockEntry<VerticalLensBlock> LENS_10X = registerLens("lens_10x");
    public static final BlockEntry<VerticalLensBlock> LENS_16X = registerLens("lens_16x");
    public static final BlockEntry<VerticalLensBlock> LENS_EXTENSION = registerLens("lens_extension");

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
