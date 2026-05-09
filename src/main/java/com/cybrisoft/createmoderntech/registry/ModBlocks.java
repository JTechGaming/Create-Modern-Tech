package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings("removal")
public class ModBlocks {
    public static final BlockEntry<VolumetricDisplayBlock> VOLUMETRIC_DISPLAY_BLOCK =
            REGISTRATE.block("auto_yaw_controller", VolumetricDisplayBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(properties -> properties.isRedstoneConductor((pState, pLevel, pPos) -> false))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .properties(p -> p.noOcclusion())
                    .properties(p -> p.strength(0.8f))
                    .transform(axeOrPickaxe())
                    .blockstate((c, p) -> p.directionalBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
                    .simpleItem()
                    .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering blocks!");
    }
}
