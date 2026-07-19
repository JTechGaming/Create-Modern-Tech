package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockEntity;
import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockEntity;
import com.cybrisoft.createmoderntech.block.gauge.RegionalStressGaugeBlockEntity;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockEntity;
import com.cybrisoft.createmoderntech.block.springbuffer.SpringBufferBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanXControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.pan.PanZControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.PitchControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.YawControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerRenderer;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayRenderer;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.amplifier.WarpAmplifierBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.amplifier.WarpAmplifierRenderer;
import com.cybrisoft.createmoderntech.block.warpgate.drive.WarpDriveBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalRenderer;
import com.cybrisoft.createmoderntech.block.warpgate.transponder.WarpGateTransponderBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
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
            .renderer(() -> BeaconControllerRenderer::new)
            .register();

    public static final BlockEntityEntry<RegionalStressGaugeBlockEntity> REGIONAL_STRESS_GAUGE = REGISTRATE
            .blockEntity("regional_stress_gauge", RegionalStressGaugeBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .renderer(() -> ShaftRenderer::new)
            .validBlocks(ModBlocks.REGIONAL_STRESS_GAUGE_BLOCK)
            .register();

    public static final BlockEntityEntry<SpringBufferBlockEntity> SPRING_BUFFER = REGISTRATE
            .blockEntity("spring_buffer", SpringBufferBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.SPRING_BUFFER_BLOCK)
            .register();

    public static final BlockEntityEntry<SpeakerBlockEntity> SPEAKER = REGISTRATE
            .blockEntity("speaker", SpeakerBlockEntity::new)
            .validBlocks(ModBlocks.SPEAKER_BLOCK)
            .register();

    public static final BlockEntityEntry<AudioTriggerBlockEntity> AUDIO_TRIGGER = REGISTRATE
            .blockEntity("audio_trigger", AudioTriggerBlockEntity::new)
            .validBlocks(ModBlocks.AUDIO_TRIGGER_BLOCK)
            .register();

    public static final BlockEntityEntry<AICoreBlockEntity> AI_CORE_BLOCK = REGISTRATE
            .blockEntity("ai_core_block", AICoreBlockEntity::new)
            .validBlocks(ModBlocks.AI_CORE_BLOCK)
            .register();

    public static final BlockEntityEntry<WarpGateTransponderBlockEntity> WARP_GATE_TRANSPONDER = REGISTRATE
            .blockEntity("warp_gate_transponder", WarpGateTransponderBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.WARP_GATE_TRANSPONDER_BLOCK)
            .register();

    public static final BlockEntityEntry<WarpGateTerminalBlockEntity> WARP_GATE_TERMINAL = REGISTRATE
            .blockEntity("warp_gate_terminal", WarpGateTerminalBlockEntity::new)
            .validBlocks(ModBlocks.WARP_GATE_TERMINAL_BLOCK)
            .renderer(() -> WarpGateTerminalRenderer::new)
            .register();

    public static final BlockEntityEntry<WarpDriveBlockEntity> WARP_DRIVE = REGISTRATE
            .blockEntity("warp_drive", WarpDriveBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlocks(ModBlocks.WARP_DRIVE_BLOCK)
            .register();

    public static final BlockEntityEntry<WarpAmplifierBlockEntity> WARP_AMPLIFIER = REGISTRATE
            .blockEntity("warp_amplifier", WarpAmplifierBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .renderer(() -> WarpAmplifierRenderer::new)
            .validBlocks(ModBlocks.WARP_AMPLIFIER_BLOCK)
            .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering block entity types!");
    }
}
