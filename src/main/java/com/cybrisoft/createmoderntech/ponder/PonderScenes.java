package com.cybrisoft.createmoderntech.ponder;

import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.amplifier.WarpAmplifierBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.transponder.WarpGateTransponderBlockEntity;
import com.cybrisoft.createmoderntech.client.WarpGateRenderer;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.*;
import net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PonderScenes {
    public static void volumetricDisplay(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("volumetric_display", "Displaying the Surrounding Terrain");
        builder.configureBasePlate(0, 0, 5);
        builder.scaleSceneView(0.75f);
        builder.setSceneOffsetY(-2.0f);
        builder.removeShadow();

        var gearbox       = util.select().position(2, 0, 2);
        var eastShafts    = util.select().fromTo(3, 0, 2, 4, 0, 2);
        var shaftColumn   = util.select().fromTo(2, 1, 2, 2, 2, 2);
        var display       = util.select().position(2, 3, 2);

        // temporarily delete the lens blocks
        builder.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 7, 2), Blocks.AIR.defaultBlockState(), false);

        builder.world().showSection(gearbox, Direction.UP);
        builder.idle(5);
        builder.world().showSection(eastShafts, Direction.WEST);
        builder.idle(5);
        builder.world().setBlocks(shaftColumn, AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y), false);
        builder.world().showSection(shaftColumn, Direction.DOWN);
        builder.idle(5);
        builder.world().showSection(display, Direction.DOWN);
        builder.idle(20);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));
        builder.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-64f));
        builder.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-64f));

        builder.overlay().showText(60)
                .text("The Volumetric Display reads the surrounding terrain and projects a hologram")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        builder.idle(70);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(256f)));
        builder.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-256f));
        builder.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-256f));

        builder.overlay().showText(40)
                .text("The faster the input RPM, the more geometry the hologram will show")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        builder.idle(50);

        var lensBottom    = util.select().position(2, 4, 2);
        var lensExtension1 = util.select().position(2, 5, 2);
        var lensExtension2 = util.select().position(2, 6, 2);
        var lensTop       = util.select().position(2, 7, 2);

        builder.overlay().showText(40)
                .text("Lenses can be placed on top to magnify or zoom the projection")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();

        builder.world().restoreBlocks(lensBottom);
        builder.world().showSection(lensBottom, Direction.DOWN);

        builder.idle(50);

        builder.overlay().showText(40)
                .text("A lens setup can be extended using lens extensions")
                .pointAt(util.vector().topOf(2, 5, 2))
                .attachKeyFrame();

        builder.idle(10);
        builder.world().restoreBlocks(lensExtension1);
        builder.world().showSection(lensExtension1, Direction.DOWN);
        builder.idle(10);
        builder.world().restoreBlocks(lensExtension2);
        builder.world().showSection(lensExtension2, Direction.DOWN);

        builder.idle(30);

        builder.overlay().showText(90)
                .text("A lens at the bottom will scale the projection down by the strength")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();

        builder.world().restoreBlocks(lensTop);
        builder.world().showSection(lensTop, Direction.DOWN);
        builder.world().restoreBlocks(lensBottom);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_2X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_4X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_8X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_16X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        builder.idle(20);

        builder.overlay().showText(90)
                .text("A lens at the top will scale the projection by the strength")
                .pointAt(util.vector().topOf(2, 6, 2))
                .attachKeyFrame();

        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_2X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_4X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_8X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        builder.idle(20);
        builder.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_16X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        builder.idle(20);

        // hide lenses first
        builder.world().hideSection(lensTop, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension2, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension1, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensBottom, Direction.UP);
        builder.idle(10);

        // now delete them, because we no longer need lens blocks
        builder.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 7, 2), Blocks.AIR.defaultBlockState(), false);

        builder.idle(20);

        builder.overlay().showText(30)
                .text("Instead of regular shafts, volumetric shafts can be used")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        builder.idle(20);
        builder.world().hideSection(shaftColumn, Direction.EAST);
        builder.idle(10);
        builder.world().restoreBlocks(shaftColumn);
        builder.idle(10);
        builder.world().showSection(shaftColumn, Direction.EAST);
        builder.idle(10);

        builder.overlay().showText(30)
                .text("Controllers can be attached anywhere on the shaft column to manipulate the projection")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        builder.idle(30);

        var yawController   = util.select().position(3, 2, 2);
        var yawShaft        = util.select().fromTo(3, 2, 1, 3, 2, 0);
        var pitchController = util.select().position(2, 2, 1);
        var pitchShaft      = util.select().fromTo(1, 2, 1, 0, 2, 1);
        var panXController  = util.select().position(2, 1, 3);
        var panXShaft       = util.select().fromTo(3, 1, 3, 4, 1, 3);
        var panZController  = util.select().position(1, 1, 2);
        var panZShaft       = util.select().fromTo(1, 1, 3, 1, 1, 4);

        // Yaw
        builder.world().showSection(yawController, Direction.SOUTH);
        builder.world().showSection(yawShaft, Direction.SOUTH);
        builder.idle(5);
        builder.overlay().showText(50)
                .text("Yaw rotates the projection horizontally")
                .pointAt(util.vector().topOf(3, 2, 2))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(3, 2, 1), // shaft
                new BlockPos(3, 2, 0) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.yaw = fi * 3f;
                be.vboDirty = true;
            }));
            builder.idle(1);
        }

        List.of(
                new BlockPos(3, 2, 1), // shaft
                new BlockPos(3, 2, 0) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        builder.idle(20);

        // Pitch
        builder.world().showSection(pitchController, Direction.EAST);
        builder.world().showSection(pitchShaft, Direction.EAST);
        builder.idle(5);
        builder.overlay().showText(50)
                .text("Pitch tilts the projection forward and back")
                .pointAt(util.vector().topOf(2, 2, 1))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(1, 2, 1), // shaft
                new BlockPos(0, 2, 1) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.pitch = fi * 1.5f;
                be.vboDirty = true;
            }));
            builder.idle(1);
        }

        List.of(
                new BlockPos(1, 2, 1), // shaft
                new BlockPos(0, 2, 1) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        builder.idle(20);

        // Reset both
        builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
            be.yaw = 0f;
            be.pitch = 0f;
            be.vboDirty = true;
        }));

        // Pan X and Pan Z
        builder.world().showSection(panXController, Direction.WEST);
        builder.world().showSection(panXShaft, Direction.WEST);
        builder.idle(5);
        builder.overlay().showText(50)
                .text("Pan X shifts the sampled area east or west")
                .pointAt(util.vector().topOf(2, 1, 3))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(3, 1, 3), // shaft
                new BlockPos(4, 1, 3) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = fi;
                be.chunkRequestDirty = true;
            }));
            builder.idle(1);
        }

        List.of(
                new BlockPos(3, 1, 3), // shaft
                new BlockPos(4, 1, 3) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        builder.world().showSection(panZController, Direction.NORTH);
        builder.world().showSection(panZShaft, Direction.NORTH);
        builder.idle(5);
        builder.overlay().showText(50)
                .text("Pan Z shifts the sampled area north or south")
                .pointAt(util.vector().topOf(1, 1, 2))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(1, 1, 3), // shaft
                new BlockPos(1, 1, 4) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panZ = fi;
                be.chunkRequestDirty = true;
            }));
            builder.idle(1);
        }

        List.of(
                new BlockPos(1, 1, 3), // shaft
                new BlockPos(1, 1, 4) // shaft
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        builder.overlay().showText(40)
                .text("Each controller's shaft speed determines how fast the parameter changes")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();
        builder.idle(50);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));
        builder.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(0f));
        builder.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(0f));
    }

    public static void lensExtensions(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("lens_extensions", "Using Different Lens Extensions");
        builder.configureBasePlate(0, 0, 5);
        builder.scaleSceneView(0.75f);
        builder.setSceneOffsetY(-2.0f);
        builder.removeShadow();

        var gearbox = util.select().position(2, 0, 2);
        var eastShafts = util.select().fromTo(3, 0, 2, 4, 0, 2);
        var display = util.select().position(2, 1, 2);

        // temporarily delete the lens blocks
        builder.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LENS_EXTENSION.getDefaultState(), false);
        builder.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);

        builder.world().showSection(gearbox, Direction.UP);
        builder.idle(5);
        builder.world().showSection(eastShafts, Direction.WEST);
        builder.idle(5);
        builder.world().showSection(display, Direction.DOWN);
        builder.idle(20);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2)  // display
        ).forEach(pos -> builder.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(128f)));
        builder.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-128f));
        builder.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-128f));

        var lensBottom    = util.select().position(2, 2, 2);
        var lensExtension1 = util.select().position(2, 3, 2);
        var lensExtension2 = util.select().position(2, 4, 2);
        var lensExtension3 = util.select().position(2, 5, 2);
        var lensExtension4 = util.select().position(2, 6, 2);

        builder.world().showSection(lensBottom, Direction.DOWN);
        builder.world().showSection(lensExtension1, Direction.DOWN);

        builder.overlay().showText(40)
                .text("Instead of regular lens extensions, special ones can be used")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        builder.idle(50);

        builder.overlay().showText(40)
                .text("The first of these special extensions is the telephoto extension")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        builder.world().restoreBlocks(lensExtension1);

        builder.idle(50);

        builder.overlay().showText(80)
                .text("It offsets the projection by 0.5 blocks per extension")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        builder.idle(40);
        builder.world().restoreBlocks(lensExtension2);
        builder.world().showSection(lensExtension2, Direction.DOWN);
        builder.idle(20);
        builder.world().restoreBlocks(lensExtension3);
        builder.world().showSection(lensExtension3, Direction.DOWN);
        builder.idle(20);

        builder.world().hideSection(lensExtension3, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension2, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension1, Direction.UP);
        builder.idle(10);

        builder.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);

        builder.idle(20);

        builder.overlay().showText(20)
                .text("Next up is the light boost filter")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        builder.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LIGHT_BOOST_FILTER.getDefaultState(), false);
        builder.world().showSection(lensExtension1, Direction.DOWN);

        builder.idle(30);

        builder.overlay().showText(30)
                .text("It will boost the opacity of the projection by 20 percent")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        builder.idle(40);

        builder.world().hideSection(lensExtension1, Direction.UP);
        builder.idle(10);
        builder.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);

        builder.idle(20);

        builder.overlay().showText(40)
                .text("Finally, color filters can be used to tune the color of the projection")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        builder.idle(50);

        builder.overlay().showText(90)
                .text("There are...")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        builder.idle(15);
        builder.overlay().showText(75)
                .text("Lime...")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        builder.world().showSection(lensExtension1, Direction.DOWN);
        builder.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LIME_COLOR_FILTER.getDefaultState(), false);

        builder.idle(15);
        builder.overlay().showText(50)
                .text("Red...")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();
        builder.world().showSection(lensExtension2, Direction.DOWN);
        builder.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.RED_COLOR_FILTER.getDefaultState(), false);

        builder.idle(15);
        builder.overlay().showText(35)
                .text("Purple...")
                .pointAt(util.vector().topOf(2, 5, 2))
                .attachKeyFrame();
        builder.world().showSection(lensExtension3, Direction.DOWN);
        builder.world().setBlock(new BlockPos(2, 5, 2), ModBlocks.PURPLE_COLOR_FILTER.getDefaultState(), false);

        builder.idle(15);
        builder.overlay().showText(20)
                .text("And white filters")
                .pointAt(util.vector().topOf(2, 6, 2))
                .attachKeyFrame();
        builder.world().showSection(lensExtension4, Direction.DOWN);
        builder.world().setBlock(new BlockPos(2, 6, 2), ModBlocks.WHITE_COLOR_FILTER.getDefaultState(), false);

        builder.idle(30);

        builder.overlay().showText(90)
                .text("Color filters can be mixed to create many different color combinations")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        builder.idle(40);

        builder.world().hideSection(lensExtension4, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension3, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension2, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensExtension1, Direction.UP);
        builder.idle(3);
        builder.world().hideSection(lensBottom, Direction.UP);
        builder.idle(10);

        builder.world().setBlock(new BlockPos(2, 2, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        builder.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
    }

    public static void beaconController(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        builder.title("beacon_controller", "Using The Beacon Controller");
        builder.configureBasePlate(0, 0, 4);
        builder.scaleSceneView(1.0f);
        builder.setSceneOffsetY(-1.0f);
        builder.removeShadow();

        var gearbox = util.select().position(1, 0, 3);
        var eastShafts = util.select().fromTo(2, 0, 3, 3, 0, 3);
        var shaft = util.select().position(1, 1, 3);
        var display = util.select().position(1, 2, 3);
        var beaconController = util.select().position(0, 1, 3);
        var topReceiver = util.select().position(0, 2, 3);
        var bottomReceiver = util.select().position(0, 0, 3);

        builder.world().setBlock(new BlockPos(2, 0, 3), AllBlocks.SHAFT.getDefaultState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X), false);

        builder.world().showSection(gearbox, Direction.UP);
        builder.idle(5);
        builder.world().showSection(eastShafts, Direction.WEST);
        builder.idle(5);
        builder.world().showSection(shaft, Direction.UP);
        builder.idle(5);
        builder.world().showSection(display, Direction.UP);
        builder.idle(5);
        builder.world().showSection(beaconController, Direction.SOUTH);
        builder.addInstruction(s -> {
            if (s.getWorld().getBlockEntity(new BlockPos(1, 1, 3))
                    instanceof VolumetricShaftBlockEntity sh) {
                sh.markLayoutDirty();
                sh.refreshLayout();
            }
        });
        builder.idle(20);

        scene.world().setKineticSpeed(util.select().fromTo(1, 0, 3, 1, 2, 3), 128f);
        scene.world().setKineticSpeed(util.select().fromTo(2, 0, 3, 3, 0, 3), -128f);

        builder.overlay().showText(40)
                .text("The beacon controller attaches to a volumetric shaft")
                .pointAt(util.vector().topOf(0, 1, 3))
                .attachKeyFrame();
        builder.idle(50);

        builder.world().showSection(topReceiver, Direction.DOWN);
        builder.overlay().showText(80)
                .text("When powered from the top, a beacon will be placed in the volume")
                .pointAt(util.vector().topOf(0, 2, 3))
                .attachKeyFrame();
        builder.idle(50);

        builder.world().toggleRedstonePower(topReceiver);
        builder.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                                ModBlockEntityTypes.BEACON_CONTROLLER.get())
                        .ifPresent(BeaconControllerBlockEntity::onSetClearTriggered));

        builder.idle(10);

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(1, 2, 3),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = fi;
                be.chunkRequestDirty = true;
            }));
            builder.idle(1);
        }

        builder.idle(10);
        builder.world().toggleRedstonePower(topReceiver);
        builder.idle(20);

        builder.world().showSection(bottomReceiver, Direction.UP);
        builder.overlay().showText(60)
                .text("If the bottom is powered when a beacon is not hovered, nothing happens")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        builder.idle(40);

        builder.world().toggleRedstonePower(bottomReceiver);
        builder.idle(20);
        builder.world().toggleRedstonePower(bottomReceiver);
        builder.idle(10);

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            builder.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(1, 2, 3),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = -fi;
                be.chunkRequestDirty = true;
            }));
            builder.idle(1);
        }

        builder.idle(10);

        builder.overlay().showText(60)
                .text("However, if a beacon is hovered, powering the bottom of the beacon controller...")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        builder.idle(50);

        builder.world().toggleRedstonePower(bottomReceiver);
        builder.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                                ModBlockEntityTypes.BEACON_CONTROLLER.get())
                        .ifPresent(BeaconControllerBlockEntity::onOutputCompassTriggered));

        builder.idle(20);
        builder.world().toggleRedstonePower(bottomReceiver);

        builder.overlay().showText(40)
                .text("...will create a beacon compass")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        builder.idle(50);

        builder.world().restoreBlocks(eastShafts);

        var cog = util.select().position(2, 1, 3);
        var arm = util.select().position(2, 1, 2);
        var armPos = new BlockPos(2, 1, 2);
        var navTable = util.select().position(2, 0, 1);

        builder.idle(5);
        builder.world().showSection(cog, Direction.NORTH);
        builder.idle(5);
        builder.world().showSection(arm, Direction.EAST);
        builder.idle(10);

        scene.world().setKineticSpeed(util.select().position(3, 0, 3), 128f);
        scene.world().setKineticSpeed(util.select().fromTo(1, 0, 3, 1, 2, 3), 128f);
        scene.world().setKineticSpeed(util.select().position(2, 0, 3), -128f);
        scene.world().setKineticSpeed(util.select().position(2, 1, 3), -128f);

        builder.overlay().showText(60)
                .text("This compass can be extracted by something like a mechanical arm")
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame();
        builder.idle(20);

        builder.world().showSection(navTable, Direction.DOWN);

        builder.world().modifyBlockEntity(armPos, KineticBlockEntity.class, be -> {
            be.setSpeed(-48f);
            be.updateSpeed = false;
        });
        builder.idle(5);

        ItemStack compass = new ItemStack(ModItems.BEACON_COMPASS.get());

        builder.addInstruction(s -> {
            if (s.getWorld().getBlockEntity(armPos) instanceof ArmBlockEntity be) {
                System.out.println("arm speed=" + be.getSpeed() + " updateSpeed=" + be.updateSpeed);
            }
        });

        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 1);
        driveArmProgress(builder, armPos, 24);

        builder.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                        ModBlockEntityTypes.BEACON_CONTROLLER.get()).ifPresent(be ->
                        be.outputInventory.setItem(0, ItemStack.EMPTY)));

        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, compass, -1);
        driveArmProgress(builder, armPos, 20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, compass, 1);
        driveArmProgress(builder, armPos, 24);

        builder.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(2, 0, 1),
                        SimBlockEntityTypes.NAVIGATION_TABLE.get()).ifPresent(be ->
                        be.inventory.slot.setStack(compass)));

        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        builder.idle(5);

        builder.overlay().showText(50)
                .text("The compass will then point to the selected beacon")
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame();
        builder.idle(60);
    }

    public static void aiSystem(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final EffectInstructions effects = scene.effects();

        builder.title("ai_system", "Setting Up An AI System");
        builder.configureBasePlate(2, 0, 4);
        builder.scaleSceneView(1.0f);
        builder.setSceneOffsetY(-1.0f);
        builder.removeShadow();

        var trigger = new BlockPos(1, 1, 1);
        var core = new BlockPos(3, 1, 1);
        var speaker = new BlockPos(5, 1, 1);

        scene.idle(5);
        final ElementLink<WorldSectionElement> coreElem = world.showIndependentSection(select.position(core), Direction.UP);
        scene.idle(10);

        builder.overlay().showText(60)
                .text("The ai core is the center of an ai network")
                .pointAt(util.vector().centerOf(core))
                .attachKeyFrame();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("Clicking the ai core block with an audio trigger or speaker item will link it to the network")
                .pointAt(util.vector().centerOf(core))
                .attachKeyFrame();
        scene.idle(10);
        scene.overlay().showControls(core.getCenter(), Pointing.UP, 20).rightClick()
                .withItem(ModBlocks.AUDIO_TRIGGER_BLOCK.asStack());
        scene.idle(40);
        scene.overlay().showControls(core.getCenter(), Pointing.UP, 20).rightClick()
                .withItem(ModBlocks.SPEAKER_BLOCK.asStack());
        scene.idle(40);

        final ElementLink<WorldSectionElement> triggerElem = world.showIndependentSection(select.position(trigger), Direction.UP);
        scene.idle(10);
        final ElementLink<WorldSectionElement> speakerElem = world.showIndependentSection(select.position(speaker), Direction.UP);
        scene.idle(10);
        world.setBlock(trigger.offset(0, 0, -1), Blocks.STONE_BUTTON.defaultBlockState(), false);
        var button = select.position(trigger.offset(0, 0, -1));
        final ElementLink<WorldSectionElement> buttonElem = world.showIndependentSection(button, Direction.UP);

        builder.overlay().showText(40)
                .text("The ai network also works on simulated contraptions")
                .pointAt(util.vector().centerOf(core))
                .attachKeyFrame();
        scene.idle(20);

        var assembler = new BlockPos(3, 1, 0);
        var shipSelection = select.fromTo(0, 0, 0, 6, 1, 2);

        final ElementLink<WorldSectionElement> ship = world.showIndependentSection(shipSelection, Direction.WEST);

        scene.idle(30);

        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, null, coreElem));
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, null, triggerElem));
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, null, speakerElem));
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, null, buttonElem));

        scene.addInstruction(new PullTheAssemblerKronkInstruction(assembler, true, true));

        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(0, -2, 0), 40, SmoothMovementUtils.quadraticRise()));

        scene.idle(50);
    }

    public static void audioTrigger(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final EffectInstructions effects = scene.effects();

        builder.title("audio_trigger", "Using the Audio Trigger");
        builder.configureBasePlate(0, 0, 4);
        builder.scaleSceneView(1.0f);
        builder.setSceneOffsetY(-1.0f);
        builder.removeShadow();

        var trigger = new BlockPos(1, 1, 1);

        world.showSection(select.position(trigger), Direction.UP);

        scene.idle(5);

        builder.overlay().showText(60)
                .text("The audio trigger will transmit a message for the ai to read out")
                .pointAt(util.vector().centerOf(trigger))
                .attachKeyFrame();
        builder.idle(80);

        world.setBlock(trigger.offset(0, 0, -1), Blocks.STONE_BUTTON.defaultBlockState(), false);
        var button = select.position(trigger.offset(0, 0, -1));
        final ElementLink<WorldSectionElement> buttonElem = world.showIndependentSection(button, Direction.UP);

        scene.idle(20);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);

        scene.idle(7);

        builder.overlay().showText(20)
                .text("Test Message");

        scene.idle(10);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, false);
        }, false);

        scene.idle(30);

        builder.overlay().showText(60)
                .text("When surrounded by certain blocks, the audio trigger can read information from them")
                .pointAt(util.vector().centerOf(trigger))
                .attachKeyFrame();
        builder.idle(80);

        var provider = trigger.offset(1, 0, 0);

        // Nav table

        world.setBlock(provider, SimBlocks.NAVIGATION_TABLE.getDefaultState(), false);
        final ElementLink<WorldSectionElement> providerElem = world.showIndependentSection(select.position(provider), Direction.UP);

        scene.idle(15);

        builder.overlay().showText(60)
                .text("To read from a block, provide the direction like this: %N, %S etc.")
                .attachKeyFrame();
        builder.idle(70);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);

        scene.idle(7);

        builder.overlay().showText(20)
                .text("No Target");

        scene.idle(15);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, false);
        }, false);

        scene.idle(15);

        // Altitude

        world.setBlock(provider, SimBlocks.ALTITUDE_SENSOR.getDefaultState(), false);

        scene.idle(15);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);

        scene.idle(7);

        builder.overlay().showText(20)
                .text("96.0");

        scene.idle(15);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, false);
        }, false);

        scene.idle(15);

        // Barrel

        world.setBlock(provider, Blocks.BARREL.defaultBlockState(), false);

        scene.idle(15);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);

        scene.idle(7);

        builder.overlay().showText(20)
                .text("16 dirt and 7 iron ingot");

        scene.idle(15);

        world.modifyBlock(trigger.offset(0, 0, -1), (s) -> {
            return s.setValue(BlockStateProperties.POWERED, false);
        }, false);

        scene.idle(10);

        world.hideSection(select.position(new BlockPos(-1, 0, 0)), Direction.DOWN);
    }

    public static void warpGate(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final EffectInstructions effects = scene.effects();

        scene.title("warp_gate_intro", "Building A Warp Gate");
        scene.configureBasePlate(0, -4, 15);
        scene.scaleSceneView(0.5f);
        scene.setSceneOffsetY(-5.0f);
        scene.rotateCameraY(180);
        scene.showBasePlate();

        var shipSelection = select.fromTo(7, 9, 16, 9, 10, 11);
        var assembler = new BlockPos(8, 10, 11);

        var gearbox = new BlockPos(8, 4, 5);
        var topCog = new BlockPos(8, 4, 0);
        var bottomCog = new BlockPos(8, 3, 0);
        var warpDrive = new BlockPos(8, 5, 5);
        var lever = new BlockPos(8, 5, 6);
        var warpTerminal = new BlockPos(8, 6, 5);
        var topGateBlock = new BlockPos(8, 22, 5);

        final BlockPos[] warpGate = {
                new BlockPos(7, 6, 5),
                new BlockPos(6, 6, 5),
                new BlockPos(5, 7, 5),
                new BlockPos(4, 7, 5),
                new BlockPos(3, 8, 5),
                new BlockPos(2, 9, 5),
                new BlockPos(1, 10, 5),
                new BlockPos(1, 11, 5),
                new BlockPos(0, 12, 5),
                new BlockPos(0, 13, 5),
                new BlockPos(0, 14, 5),
                new BlockPos(0, 15, 5),
                new BlockPos(0, 16, 5),
                new BlockPos(1, 17, 5),
                new BlockPos(1, 18, 5),
                new BlockPos(2, 19, 5),
                new BlockPos(3, 20, 5),
                new BlockPos(4, 21, 5),
                new BlockPos(5, 21, 5),
                new BlockPos(6, 22, 5),
                new BlockPos(7, 22, 5),
                new BlockPos(8, 22, 5),
                new BlockPos(9, 22, 5),
                new BlockPos(10, 22, 5),
                new BlockPos(11, 21, 5),
                new BlockPos(12, 21, 5),
                new BlockPos(13, 20, 5),
                new BlockPos(14, 19, 5),
                new BlockPos(15, 18, 5),
                new BlockPos(15, 17, 5),
                new BlockPos(16, 16, 5),
                new BlockPos(16, 15, 5),
                new BlockPos(16, 14, 5),
                new BlockPos(16, 13, 5),
                new BlockPos(16, 12, 5),
                new BlockPos(15, 11, 5),
                new BlockPos(15, 10, 5),
                new BlockPos(14, 9, 5),
                new BlockPos(13, 8, 5),
                new BlockPos(12, 7, 5),
                new BlockPos(11, 7, 5),
                new BlockPos(10, 6, 5),
                new BlockPos(9, 6, 5),
        };

        Selection[] warpGateSelection = new Selection[warpGate.length];
        for (int i = 0; i < warpGate.length; i++) {
            warpGateSelection[i] = select.position(warpGate[i]);
        }

        world.showSection(select.fromTo(gearbox, bottomCog), Direction.UP);
        world.showSection(select.position(warpDrive), Direction.UP);

        world.setKineticSpeed(select.fromTo(gearbox, topCog), 32);
        world.setKineticSpeed(select.position(bottomCog), -32);
        world.setKineticSpeed(select.position(warpDrive), -32);

        scene.idle(20);

        builder.overlay().showText(50)
                .text("A warp gate starts with a warp drive which supplies the energy")
                .pointAt(util.vector().centerOf(warpDrive))
                .attachKeyFrame();
        scene.idle(60);

        world.showSection(select.position(warpTerminal), Direction.UP);
        scene.idle(10);

        builder.overlay().showText(50)
                .text("Then, a warp gate terminal forms the base of the warp gate")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(60);

        builder.overlay().showText(50)
                .text("The warp gate terminal can support a max gate radius of 10 blocks. If the radius of the gate is larger than that...")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(60);

        world.setBlock(warpTerminal, ModBlocks.WARP_AMPLIFIER_BLOCK.getDefaultState(), false);
        world.setBlock(warpTerminal.above(), ModBlocks.WARP_GATE_TERMINAL_BLOCK.getDefaultState(), false);
        world.showSection(select.position(warpTerminal.above()), Direction.UP);

        scene.idle(10);
        builder.overlay().showText(50)
                .text("Warp amplifiers can be added in between to extend the radius by 8 blocks each")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(60);
        builder.overlay().showText(50)
                .text("The maximum gate radius is 50 blocks (with 5 amplifiers), and the minimum radius is 8 blocks")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(40);

        world.hideSection(select.position(warpTerminal.above()), Direction.UP);

        scene.idle(10);

        world.setBlock(warpTerminal, ModBlocks.WARP_GATE_TERMINAL_BLOCK.getDefaultState(), false);
        world.setBlock(warpTerminal.above(), Blocks.AIR.defaultBlockState(), false);

        scene.idle(20);

        world.showSection(select.position(topGateBlock.below(2)), Direction.DOWN);
        scene.idle(10);
        builder.overlay().showText(50)
                .text("By placing a gate block directly above the terminal at the desired diameter and clicking the terminal with a wrench...")
                .pointAt(util.vector().centerOf(topGateBlock))
                .attachKeyFrame();

        scene.idle(50);
        scene.overlay().showControls(warpTerminal.getCenter(), Pointing.UP, 40).rightClick()
                .withItem(AllItems.WRENCH.asStack());
        scene.idle(7);
        scene.world().modifyBlockEntity(warpTerminal, WarpGateTerminalBlockEntity.class, (be) -> {
            be.drawGuides = true;
        });
        scene.idle(40);
        builder.overlay().showText(50)
                .text("It will show exactly where warp gate blocks need to be placed")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(50);

        scene.world().modifyBlockEntity(warpTerminal, WarpGateTerminalBlockEntity.class, (be) -> {
            be.drawGuides = false;
        });

        for (Selection selection : warpGateSelection) {
            world.showSection(selection, Direction.DOWN);
            scene.idle(1);
        }

        scene.idle(20);

        world.showSection(select.position(lever), Direction.UP);

        scene.idle(10);

        builder.overlay().showText(50)
                .text("If the shape is fully built, powering the warp drive will activate the gate")
                .pointAt(util.vector().centerOf(lever))
                .attachKeyFrame();
        scene.idle(60);

        scene.overlay().showControls(lever.getCenter(), Pointing.UP, 40).rightClick();

        scene.idle(10);

        world.modifyBlock(lever, (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);
        world.modifyBlockEntity(warpTerminal, WarpGateTerminalBlockEntity.class, (be) -> {
            be.ponderRenderOffset = new Vec3(-3, -1.7, -1.2);
        });

        scene.idle(60);
    }

    public static void workingGate(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final EffectInstructions effects = scene.effects();

        scene.title("gate_working", "Using A Warp Gate");
        scene.configureBasePlate(10, 0, 15);
        scene.scaleSceneView(0.4f);
        scene.setSceneOffsetY(-5.0f);
        scene.rotateCameraY(180);
        scene.showBasePlate();

        var shipSelection = select.fromTo(7, 8, 18, 9, 9, 9);
        var assembler = new BlockPos(8, 9, 9);
        var gearbox = new BlockPos(8, 3, 7);
        var topCog = new BlockPos(8, 3, 2);
        var bottomCog = new BlockPos(8, 2, 2);
        var warpDrive = new BlockPos(8, 4, 7);

        var gearboxGate2 = new BlockPos(8+19, 3, 7);
        var topCogGate2 = new BlockPos(8+19, 3, 2);
        var bottomCogGate2 = new BlockPos(8+19, 2, 2);
        var warpDriveGate2 = new BlockPos(8+19, 4, 7);

        var lever = new BlockPos(8, 4, 8);
        var warpTerminal = new BlockPos(8, 5, 7);
        var topGateBlock = new BlockPos(8, 20, 7);

        var lever2 = new BlockPos(8+19, 4, 8);
        var warpTerminalGate2 = new BlockPos(8+19, 5, 7);
        var topGateBlockGate2 = new BlockPos(8+19, 20, 7);

        world.setBlock(lever, Blocks.LEVER.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH), false);
        world.setBlock(lever2, Blocks.LEVER.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH), false);

        world.modifyBlockEntity(warpTerminal, WarpGateTerminalBlockEntity.class, (be) -> {
            be.ponderRenderOffset = new Vec3(-2, -0.3, -1);
        });

        world.modifyBlockEntity(warpTerminalGate2, WarpGateTerminalBlockEntity.class, (be) -> {
            be.ponderRenderOffset = new Vec3(-11.75, -0.4, -1);
        });

        final BlockPos[] warpGate = {
                new BlockPos(7, 6, 5),
                new BlockPos(6, 6, 5),
                new BlockPos(5, 7, 5),
                new BlockPos(4, 7, 5),
                new BlockPos(3, 8, 5),
                new BlockPos(2, 9, 5),
                new BlockPos(1, 10, 5),
                new BlockPos(1, 11, 5),
                new BlockPos(0, 12, 5),
                new BlockPos(0, 13, 5),
                new BlockPos(0, 14, 5),
                new BlockPos(0, 15, 5),
                new BlockPos(0, 16, 5),
                new BlockPos(1, 17, 5),
                new BlockPos(1, 18, 5),
                new BlockPos(2, 19, 5),
                new BlockPos(3, 20, 5),
                new BlockPos(4, 21, 5),
                new BlockPos(5, 21, 5),
                new BlockPos(6, 22, 5),
                new BlockPos(7, 22, 5),
                new BlockPos(8, 22, 5),
                new BlockPos(9, 22, 5),
                new BlockPos(10, 22, 5),
                new BlockPos(11, 21, 5),
                new BlockPos(12, 21, 5),
                new BlockPos(13, 20, 5),
                new BlockPos(14, 19, 5),
                new BlockPos(15, 18, 5),
                new BlockPos(15, 17, 5),
                new BlockPos(16, 16, 5),
                new BlockPos(16, 15, 5),
                new BlockPos(16, 14, 5),
                new BlockPos(16, 13, 5),
                new BlockPos(16, 12, 5),
                new BlockPos(15, 11, 5),
                new BlockPos(15, 10, 5),
                new BlockPos(14, 9, 5),
                new BlockPos(13, 8, 5),
                new BlockPos(12, 7, 5),
                new BlockPos(11, 7, 5),
                new BlockPos(10, 6, 5),
                new BlockPos(9, 6, 5),
        };

        Selection[] warpGateSelection = new Selection[warpGate.length];
        for (int i = 0; i < warpGate.length; i++) {
            warpGateSelection[i] = select.position(warpGate[i].offset(new Vec3i(0, -1, 2)));
        }

        Selection[] warpGate2Selection = new Selection[warpGate.length];
        for (int i = 0; i < warpGate.length; i++) {
            warpGate2Selection[i] = select.position(warpGate[warpGate.length - 1 - i].offset(new Vec3i(19, -1, 2)));
        }

        world.showSection(select.fromTo(gearbox, bottomCog), Direction.UP);
        world.showSection(select.position(warpDrive), Direction.UP);

        world.showSection(select.fromTo(gearboxGate2, bottomCogGate2), Direction.UP);
        world.showSection(select.position(warpDriveGate2), Direction.UP);

        world.showSection(select.fromTo(warpDrive, warpTerminal), Direction.DOWN);
        world.showSection(select.fromTo(warpDriveGate2, warpTerminalGate2), Direction.DOWN);

        world.showSection(select.position(lever), Direction.DOWN);
        world.showSection(select.position(lever2), Direction.DOWN);

        world.setKineticSpeed(select.fromTo(gearbox, topCog), 32);
        world.setKineticSpeed(select.position(bottomCog), -32);
        world.setKineticSpeed(select.position(warpDrive), -32);

        world.setKineticSpeed(select.fromTo(gearboxGate2, topCogGate2), 32);
        world.setKineticSpeed(select.position(bottomCogGate2), -32);
        world.setKineticSpeed(select.position(warpDriveGate2), -32);

        world.setBlock(new BlockPos(19, 3, 7), Blocks.AIR.defaultBlockState(), false);

        scene.addInstruction(new PullTheAssemblerKronkInstruction(assembler, true, true));

        final ElementLink<WorldSectionElement> ship = world.showIndependentSection(shipSelection, Direction.WEST);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(5, 8, 20), 0, SmoothMovementUtils.quadraticRiseDual()));

        scene.idle(5);

        for (int i=0; i < warpGateSelection.length; i++) {
            Selection selectionGate1 = warpGateSelection[i];
            Selection selectionGate2 = warpGate2Selection[i];
            world.showSection(selectionGate1, Direction.DOWN);
            world.showSection(selectionGate2, Direction.DOWN);
            scene.idle(1);
        }

        scene.idle(10);

        builder.overlay().showText(50)
                .text("Using the filter slots, a gate frequency can be set up.")
                .pointAt(util.vector().centerOf(warpTerminal))
                .attachKeyFrame();
        scene.idle(60);

        scene.overlay().showControls(warpTerminal.getCenter(), Pointing.UP, 20).rightClick().withItem(Items.GRASS_BLOCK.getDefaultInstance());
        scene.idle(10);

        world.modifyBlockEntity(warpTerminal, WarpGateTerminalBlockEntity.class, (be) -> {
            be.filtering.setFilter(Items.GRASS_BLOCK.getDefaultInstance());
        });

        scene.idle(20);

        scene.overlay().showControls(warpTerminalGate2.getCenter(), Pointing.UP, 20).rightClick().withItem(Items.GRASS_BLOCK.getDefaultInstance());
        scene.idle(10);

        world.modifyBlockEntity(warpTerminalGate2, WarpGateTerminalBlockEntity.class, (be) -> {
            be.filtering.setFilter(Items.GRASS_BLOCK.getDefaultInstance());
        });

        scene.idle(20);

        scene.overlay().showControls(lever.getCenter(), Pointing.UP, 20).rightClick();
        scene.overlay().showControls(lever2.getCenter(), Pointing.UP, 20).rightClick();

        scene.idle(10);

        world.modifyBlock(lever, (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);
        world.modifyBlock(lever2, (s) -> {
            return s.setValue(BlockStateProperties.POWERED, true);
        }, false);

        scene.idle(20);

        var transponder = new BlockPos(8, 9, 10);

        builder.overlay().showText(50)
                .text("For a ship to travel through a gate, a transponder needs to be on board that is powered by rotational force")
                .pointAt(util.vector().centerOf(transponder))
                .attachKeyFrame();
        scene.idle(60);

        builder.overlay().showText(30)
                .text("If the ship travels fast enough...");
        scene.idle(40);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(0, 0, -27), 40, SmoothMovementUtils.quadraticRiseDual()));

        scene.idle(40);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(0, 50, 0), 0, SmoothMovementUtils.quadraticRiseDual()));

        builder.overlay().showText(30)
                .text("It will travel to the warp dimension");
        scene.idle(40);


        builder.overlay().showText(30)
                .text("It will stay there for a little while, until...");
        scene.idle(40);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(18, -50, 0), 0, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, new Vec3(0, 180, 0), 0, SmoothMovementUtils.quadraticRiseDual()));

        scene.idle(2);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, new Vec3(0, 0, 30), 40, SmoothMovementUtils.quadraticRiseDual()));

        builder.overlay().showText(30)
                .text("...the ship comes out of the destination gate");
        scene.idle(60);
    }

    private static void driveArmProgress(SceneBuilder scene, BlockPos armPos, int ticks) {
        for (int i = 0; i <= ticks; i++) {
            final float progress = i / (float) ticks;
            scene.addInstruction(s -> {
                if (s.getWorld().getBlockEntity(armPos) instanceof ArmBlockEntity be) {
                    try {
                        var field = ArmBlockEntity.class.getDeclaredField("chasedPointProgress");
                        field.setAccessible(true);
                        field.set(be, progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            scene.idle(1);
        }
    }
}
