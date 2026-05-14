package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.gauge.RegionalStressGaugeBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanXControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanZControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.YawControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayRenderer;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
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

    public static final BlockEntityEntry<VolumetricShaftBlockEntity> VOLUMETRIC_SHAFT = REGISTRATE
            .blockEntity("volumetric_shaft", VolumetricShaftBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.VOLUMETRIC_SHAFT_BLOCK)
            .register();

    public static final BlockEntityEntry<PanXControllerBlockEntity> PAN_X_CONTROLLER = REGISTRATE
            .blockEntity("pan_x_controller", PanXControllerBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.PAN_X_CONTROLLER_BLOCK)
            .register();
    public static final BlockEntityEntry<PanZControllerBlockEntity> PAN_Z_CONTROLLER = REGISTRATE
            .blockEntity("pan_z_controller", PanZControllerBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.PAN_Z_CONTROLLER_BLOCK)
            .register();
    public static final BlockEntityEntry<YawControllerBlockEntity> YAW_CONTROLLER = REGISTRATE
            .blockEntity("yaw_controller", YawControllerBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.YAW_CONTROLLER_BLOCK)
            .register();
    public static final BlockEntityEntry<PitchControllerBlockEntity> PITCH_CONTROLLER = REGISTRATE
            .blockEntity("pitch_controller", PitchControllerBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.PITCH_CONTROLLER_BLOCK)
            .register();
    public static final BlockEntityEntry<BeaconControllerBlockEntity> BEACON_CONTROLLER = REGISTRATE
            .blockEntity("beacon_controller", BeaconControllerBlockEntity::new)
            .validBlocks(ModBlocks.BEACON_CONTROLLER_BLOCK)
            .register();

    public static final BlockEntityEntry<RegionalStressGaugeBlockEntity> REGIONAL_STRESS_GAUGE = REGISTRATE
            .blockEntity("regional_stress_gauge", RegionalStressGaugeBlockEntity::new)
            .validBlocks(ModBlocks.REGIONAL_STRESS_GAUGE_BLOCK)
            .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering block entity types!");
    }
}
