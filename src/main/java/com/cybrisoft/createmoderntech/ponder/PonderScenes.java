package com.cybrisoft.createmoderntech.ponder;

import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PonderScenes {
    public static void volumetricDisplay(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("volumetric_display", "Displaying the Surrounding Terrain");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.75f);
        scene.setSceneOffsetY(-2.0f);
        scene.removeShadow();

        var gearbox       = util.select().position(2, 0, 2);
        var eastShafts    = util.select().fromTo(3, 0, 2, 4, 0, 2);
        var shaftColumn   = util.select().fromTo(2, 1, 2, 2, 2, 2);
        var display       = util.select().position(2, 3, 2);

        // temporarily delete the lens blocks
        scene.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 7, 2), Blocks.AIR.defaultBlockState(), false);

        scene.world().showSection(gearbox, Direction.UP);
        scene.idle(5);
        scene.world().showSection(eastShafts, Direction.WEST);
        scene.idle(5);
        scene.world().setBlocks(shaftColumn, AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y), false);
        scene.world().showSection(shaftColumn, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(display, Direction.DOWN);
        scene.idle(20);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));
        scene.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-64f));
        scene.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-64f));

        scene.overlay().showText(60)
                .text("The Volumetric Display reads the surrounding terrain and projects a hologram")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        scene.idle(70);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(256f)));
        scene.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-256f));
        scene.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-256f));

        scene.overlay().showText(40)
                .text("The faster the input RPM, the more geometry the hologram will show")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        scene.idle(50);

        var lensBottom    = util.select().position(2, 4, 2);
        var lensExtension1 = util.select().position(2, 5, 2);
        var lensExtension2 = util.select().position(2, 6, 2);
        var lensTop       = util.select().position(2, 7, 2);

        scene.overlay().showText(40)
                .text("Lenses can be placed on top to magnify or zoom the projection")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();

        scene.world().restoreBlocks(lensBottom);
        scene.world().showSection(lensBottom, Direction.DOWN);

        scene.idle(50);

        scene.overlay().showText(40)
                .text("A lens setup can be extended using lens extensions")
                .pointAt(util.vector().topOf(2, 5, 2))
                .attachKeyFrame();

        scene.idle(10);
        scene.world().restoreBlocks(lensExtension1);
        scene.world().showSection(lensExtension1, Direction.DOWN);
        scene.idle(10);
        scene.world().restoreBlocks(lensExtension2);
        scene.world().showSection(lensExtension2, Direction.DOWN);

        scene.idle(30);

        scene.overlay().showText(90)
                .text("A lens at the bottom will scale the projection down by the strength")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();

        scene.world().restoreBlocks(lensTop);
        scene.world().showSection(lensTop, Direction.DOWN);
        scene.world().restoreBlocks(lensBottom);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_2X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_4X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_10X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.LENS_16X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.DOWN), false);
        scene.idle(20);

        scene.overlay().showText(90)
                .text("A lens at the top will scale the projection by the strength")
                .pointAt(util.vector().topOf(2, 6, 2))
                .attachKeyFrame();

        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_2X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_4X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_10X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_16X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);

        // hide lenses first
        scene.world().hideSection(lensTop, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension2, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension1, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensBottom, Direction.UP);
        scene.idle(10);

        // now delete them, because we no longer need lens blocks
        scene.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 7, 2), Blocks.AIR.defaultBlockState(), false);

        scene.idle(20);

        scene.overlay().showText(30)
                .text("Instead of regular shafts, volumetric shafts can be used")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        scene.idle(20);
        scene.world().hideSection(shaftColumn, Direction.EAST);
        scene.idle(10);
        scene.world().restoreBlocks(shaftColumn);
        scene.idle(10);
        scene.world().showSection(shaftColumn, Direction.EAST);
        scene.idle(10);

        scene.overlay().showText(30)
                .text("Controllers can be attached anywhere on the shaft column to manipulate the projection")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        scene.idle(30);

        var yawController   = util.select().position(3, 2, 2);
        var yawShaft        = util.select().fromTo(3, 2, 1, 3, 2, 0);
        var pitchController = util.select().position(2, 2, 1);
        var pitchShaft      = util.select().fromTo(1, 2, 1, 0, 2, 1);
        var panXController  = util.select().position(2, 1, 3);
        var panXShaft       = util.select().fromTo(3, 1, 3, 4, 1, 3);
        var panZController  = util.select().position(1, 1, 2);
        var panZShaft       = util.select().fromTo(1, 1, 3, 1, 1, 4);

        // Yaw
        scene.world().showSection(yawController, Direction.SOUTH);
        scene.world().showSection(yawShaft, Direction.SOUTH);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Yaw rotates the projection horizontally")
                .pointAt(util.vector().topOf(3, 2, 2))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(3, 2, 1), // shaft
                new BlockPos(3, 2, 0) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.yaw = fi * 3f;
                be.vboDirty = true;
            }));
            scene.idle(1);
        }

        List.of(
                new BlockPos(3, 2, 1), // shaft
                new BlockPos(3, 2, 0) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        scene.idle(20);

        // Pitch
        scene.world().showSection(pitchController, Direction.EAST);
        scene.world().showSection(pitchShaft, Direction.EAST);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Pitch tilts the projection forward and back")
                .pointAt(util.vector().topOf(2, 2, 1))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(1, 2, 1), // shaft
                new BlockPos(0, 2, 1) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.pitch = fi * 1.5f;
                be.vboDirty = true;
            }));
            scene.idle(1);
        }

        List.of(
                new BlockPos(1, 2, 1), // shaft
                new BlockPos(0, 2, 1) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        scene.idle(20);

        // Reset both
        scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
            be.yaw = 0f;
            be.pitch = 0f;
            be.vboDirty = true;
        }));

        // Pan X and Pan Z
        scene.world().showSection(panXController, Direction.WEST);
        scene.world().showSection(panXShaft, Direction.WEST);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Pan X shifts the sampled area east or west")
                .pointAt(util.vector().topOf(2, 1, 3))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(3, 1, 3), // shaft
                new BlockPos(4, 1, 3) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

        List.of(
                new BlockPos(3, 1, 3), // shaft
                new BlockPos(4, 1, 3) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        scene.world().showSection(panZController, Direction.NORTH);
        scene.world().showSection(panZShaft, Direction.NORTH);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Pan Z shifts the sampled area north or south")
                .pointAt(util.vector().topOf(1, 1, 2))
                .colored(PonderPalette.BLUE);

        List.of(
                new BlockPos(1, 1, 3), // shaft
                new BlockPos(1, 1, 4) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(64f)));

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panZ = fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

        List.of(
                new BlockPos(1, 1, 3), // shaft
                new BlockPos(1, 1, 4) // shaft
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));

        scene.overlay().showText(40)
                .text("Each controller's shaft speed determines how fast the parameter changes")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();
        scene.idle(50);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2), // shaft
                new BlockPos(2, 2, 2), // shaft
                new BlockPos(2, 3, 2)  // display
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(0f)));
        scene.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(0f));
        scene.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(0f));
    }

    public static void lensExtensions(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("lens_extensions", "Using Different Lens Extensions");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.75f);
        scene.setSceneOffsetY(-2.0f);
        scene.removeShadow();

        var gearbox = util.select().position(2, 0, 2);
        var eastShafts = util.select().fromTo(3, 0, 2, 4, 0, 2);
        var display = util.select().position(2, 1, 2);

        // temporarily delete the lens blocks
        scene.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LENS_EXTENSION.getDefaultState(), false);
        scene.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 6, 2), Blocks.AIR.defaultBlockState(), false);

        scene.world().showSection(gearbox, Direction.UP);
        scene.idle(5);
        scene.world().showSection(eastShafts, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(display, Direction.DOWN);
        scene.idle(20);

        List.of(
                new BlockPos(2, 0, 2), // gearbox
                new BlockPos(2, 1, 2)  // display
        ).forEach(pos -> scene.world().modifyBlockEntity(pos, KineticBlockEntity.class,
                be -> be.setSpeed(128f)));
        scene.world().modifyBlockEntity(new BlockPos(3, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-128f));
        scene.world().modifyBlockEntity(new BlockPos(4, 0, 2), KineticBlockEntity.class, be -> be.setSpeed(-128f));

        var lensBottom    = util.select().position(2, 2, 2);
        var lensExtension1 = util.select().position(2, 3, 2);
        var lensExtension2 = util.select().position(2, 4, 2);
        var lensExtension3 = util.select().position(2, 5, 2);
        var lensExtension4 = util.select().position(2, 6, 2);

        scene.world().showSection(lensBottom, Direction.DOWN);
        scene.world().showSection(lensExtension1, Direction.DOWN);

        scene.overlay().showText(40)
                .text("Instead of regular lens extensions, special ones can be used")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        scene.idle(50);

        scene.overlay().showText(40)
                .text("The first of these special extensions is the telephoto extension")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        scene.world().restoreBlocks(lensExtension1);

        scene.idle(50);

        scene.overlay().showText(80)
                .text("It offsets the projection by 0.5 blocks per extension")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        scene.idle(40);
        scene.world().restoreBlocks(lensExtension2);
        scene.world().showSection(lensExtension2, Direction.DOWN);
        scene.idle(20);
        scene.world().restoreBlocks(lensExtension3);
        scene.world().showSection(lensExtension3, Direction.DOWN);
        scene.idle(20);

        scene.world().hideSection(lensExtension3, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension2, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension1, Direction.UP);
        scene.idle(10);

        scene.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);

        scene.idle(20);

        scene.overlay().showText(20)
                .text("Next up is the light boost filter")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        scene.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LIGHT_BOOST_FILTER.getDefaultState(), false);
        scene.world().showSection(lensExtension1, Direction.DOWN);

        scene.idle(30);

        scene.overlay().showText(30)
                .text("It will boost the opacity of the projection by 20 percent")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        scene.idle(40);

        scene.world().hideSection(lensExtension1, Direction.UP);
        scene.idle(10);
        scene.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);

        scene.idle(20);

        scene.overlay().showText(40)
                .text("Finally, color filters can be used to tune the color of the projection")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();

        scene.idle(50);

        scene.overlay().showText(90)
                .text("There are...")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        scene.idle(15);
        scene.overlay().showText(75)
                .text("Lime...")
                .pointAt(util.vector().topOf(2, 3, 2))
                .attachKeyFrame();
        scene.world().showSection(lensExtension1, Direction.DOWN);
        scene.world().setBlock(new BlockPos(2, 3, 2), ModBlocks.LIME_COLOR_FILTER.getDefaultState(), false);

        scene.idle(15);
        scene.overlay().showText(50)
                .text("Red...")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();
        scene.world().showSection(lensExtension2, Direction.DOWN);
        scene.world().setBlock(new BlockPos(2, 4, 2), ModBlocks.RED_COLOR_FILTER.getDefaultState(), false);

        scene.idle(15);
        scene.overlay().showText(35)
                .text("Purple...")
                .pointAt(util.vector().topOf(2, 5, 2))
                .attachKeyFrame();
        scene.world().showSection(lensExtension3, Direction.DOWN);
        scene.world().setBlock(new BlockPos(2, 5, 2), ModBlocks.PURPLE_COLOR_FILTER.getDefaultState(), false);

        scene.idle(15);
        scene.overlay().showText(20)
                .text("And white filters")
                .pointAt(util.vector().topOf(2, 6, 2))
                .attachKeyFrame();
        scene.world().showSection(lensExtension4, Direction.DOWN);
        scene.world().setBlock(new BlockPos(2, 6, 2), ModBlocks.WHITE_COLOR_FILTER.getDefaultState(), false);

        scene.idle(30);

        scene.overlay().showText(90)
                .text("Color filters can be mixed to create many different color combinations")
                .pointAt(util.vector().topOf(2, 2, 2))
                .attachKeyFrame();

        scene.idle(40);

        scene.world().hideSection(lensExtension4, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension3, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension2, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensExtension1, Direction.UP);
        scene.idle(3);
        scene.world().hideSection(lensBottom, Direction.UP);
        scene.idle(10);

        scene.world().setBlock(new BlockPos(2, 2, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 3, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 4, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(new BlockPos(2, 5, 2), Blocks.AIR.defaultBlockState(), false);
    }

    public static void beaconController(SceneBuilder scene, SceneBuildingUtil util) {
        CreateSceneBuilder createScene = new CreateSceneBuilder(scene);

        scene.title("beacon_controller", "Using The Beacon Controller");
        scene.configureBasePlate(0, 0, 4);
        scene.scaleSceneView(1.0f);
        scene.setSceneOffsetY(-1.0f);
        scene.removeShadow();

        var gearbox = util.select().position(1, 0, 3);
        var eastShafts = util.select().fromTo(2, 0, 3, 3, 0, 3);
        var shaft = util.select().position(1, 1, 3);
        var display = util.select().position(1, 2, 3);
        var beaconController = util.select().position(0, 1, 3);
        var topReceiver = util.select().position(0, 2, 3);
        var bottomReceiver = util.select().position(0, 0, 3);

        scene.world().setBlock(new BlockPos(2, 0, 3), AllBlocks.SHAFT.getDefaultState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X), false);

        scene.world().showSection(gearbox, Direction.UP);
        scene.idle(5);
        scene.world().showSection(eastShafts, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(shaft, Direction.UP);
        scene.idle(5);
        scene.world().showSection(display, Direction.UP);
        scene.idle(5);
        scene.world().showSection(beaconController, Direction.SOUTH);
        scene.addInstruction(s -> {
            if (s.getWorld().getBlockEntity(new BlockPos(1, 1, 3))
                    instanceof VolumetricShaftBlockEntity sh) {
                sh.markLayoutDirty();
                sh.refreshLayout();
            }
        });
        scene.idle(20);

        createScene.world().setKineticSpeed(util.select().fromTo(1, 0, 3, 1, 2, 3), 128f);
        createScene.world().setKineticSpeed(util.select().fromTo(2, 0, 3, 3, 0, 3), -128f);

        scene.overlay().showText(40)
                .text("The beacon controller attaches to a volumetric shaft")
                .pointAt(util.vector().topOf(0, 1, 3))
                .attachKeyFrame();
        scene.idle(50);

        scene.world().showSection(topReceiver, Direction.DOWN);
        scene.overlay().showText(80)
                .text("When powered from the top, a beacon will be placed in the volume")
                .pointAt(util.vector().topOf(0, 2, 3))
                .attachKeyFrame();
        scene.idle(50);

        scene.world().toggleRedstonePower(topReceiver);
        scene.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                                ModBlockEntityTypes.BEACON_CONTROLLER.get())
                        .ifPresent(BeaconControllerBlockEntity::onSetClearTriggered));

        scene.idle(10);

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(1, 2, 3),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

        scene.idle(10);
        scene.world().toggleRedstonePower(topReceiver);
        scene.idle(20);

        scene.world().showSection(bottomReceiver, Direction.UP);
        scene.overlay().showText(60)
                .text("If the bottom is powered when a beacon is not hovered, nothing happens")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        scene.idle(40);

        scene.world().toggleRedstonePower(bottomReceiver);
        scene.idle(20);
        scene.world().toggleRedstonePower(bottomReceiver);
        scene.idle(10);

        for (int i = 0; i < 10; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(1, 2, 3),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = -fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

        scene.idle(10);

        scene.overlay().showText(60)
                .text("However, if a beacon is hovered, powering the bottom of the beacon controller...")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        scene.idle(50);

        scene.world().toggleRedstonePower(bottomReceiver);
        scene.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                                ModBlockEntityTypes.BEACON_CONTROLLER.get())
                        .ifPresent(BeaconControllerBlockEntity::onOutputCompassTriggered));

        scene.idle(20);
        scene.world().toggleRedstonePower(bottomReceiver);

        scene.overlay().showText(40)
                .text("...will create a beacon compass")
                .pointAt(util.vector().topOf(0, 0, 3))
                .attachKeyFrame();
        scene.idle(50);

        scene.world().restoreBlocks(eastShafts);

        var cog = util.select().position(2, 1, 3);
        var arm = util.select().position(2, 1, 2);
        var armPos = new BlockPos(2, 1, 2);
        var navTable = util.select().position(2, 0, 1);

        scene.idle(5);
        scene.world().showSection(cog, Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(arm, Direction.EAST);
        scene.idle(10);

        createScene.world().setKineticSpeed(util.select().position(3, 0, 3), 128f);
        createScene.world().setKineticSpeed(util.select().fromTo(1, 0, 3, 1, 2, 3), 128f);
        createScene.world().setKineticSpeed(util.select().position(2, 0, 3), -128f);
        createScene.world().setKineticSpeed(util.select().position(2, 1, 3), -128f);

        scene.overlay().showText(60)
                .text("This compass can be extracted by something like a mechanical arm")
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame();
        scene.idle(20);

        scene.world().showSection(navTable, Direction.DOWN);

        scene.world().modifyBlockEntity(armPos, KineticBlockEntity.class, be -> {
            be.setSpeed(-48f);
            be.updateSpeed = false;
        });
        scene.idle(5);

        ItemStack compass = new ItemStack(ModItems.BEACON_COMPASS.get());

        scene.addInstruction(s -> {
            if (s.getWorld().getBlockEntity(armPos) instanceof ArmBlockEntity be) {
                System.out.println("arm speed=" + be.getSpeed() + " updateSpeed=" + be.updateSpeed);
            }
        });

        createScene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 1);
        driveArmProgress(scene, armPos, 24);

        scene.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(0, 1, 3),
                        ModBlockEntityTypes.BEACON_CONTROLLER.get()).ifPresent(be ->
                        be.outputInventory.setItem(0, ItemStack.EMPTY)));

        createScene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, compass, -1);
        driveArmProgress(scene, armPos, 20);
        createScene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, compass, 1);
        driveArmProgress(scene, armPos, 24);

        scene.addInstruction(s ->
                s.getWorld().getBlockEntity(new BlockPos(2, 0, 1),
                        SimBlockEntityTypes.NAVIGATION_TABLE.get()).ifPresent(be ->
                        be.inventory.slot.setStack(compass)));

        createScene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.idle(5);

        scene.overlay().showText(50)
                .text("The compass will then point to the selected beacon")
                .pointAt(util.vector().topOf(2, 1, 2))
                .attachKeyFrame();
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
