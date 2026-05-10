package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayRenderer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;

public class ModBlockEntityTypes {
    public static final BlockEntityEntry<VolumetricDisplayBlockEntity> VOLUMETRIC_DISPLAY = REGISTRATE
            .blockEntity("volumetric_display", VolumetricDisplayBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .validBlocks(ModBlocks.VOLUMETRIC_DISPLAY_BLOCK)
            .renderer(() -> VolumetricDisplayRenderer::new)
            .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering block entity types!");
    }
}
