package com.cybrisoft.createmoderntech.ponder;

import com.cybrisoft.createmoderntech.block.volumetric.controller.rotation.YawControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

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
                .text("The faster the input RPM, the bigger the hologram will be")
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
                .text("A lens at the top will scale the projection by the strength")
                .pointAt(util.vector().topOf(2, 6, 2))
                .attachKeyFrame();

        scene.world().restoreBlocks(lensTop);
        scene.world().showSection(lensTop, Direction.DOWN);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_2X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_4X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_10X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);
        scene.world().setBlock(new BlockPos(2, 7, 2), ModBlocks.LENS_16X.getDefaultState().setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.idle(20);

        scene.overlay().showText(90)
                .text("A lens at the bottom will scale the projection down by the strength")
                .pointAt(util.vector().topOf(2, 4, 2))
                .attachKeyFrame();

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

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.yaw = fi * 3f;
                be.vboDirty = true;
            }));
            scene.idle(1);
        }

        scene.idle(20);

        // Pitch
        scene.world().showSection(pitchController, Direction.EAST);
        scene.world().showSection(pitchShaft, Direction.EAST);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Pitch tilts the projection forward and back")
                .pointAt(util.vector().topOf(2, 2, 1))
                .colored(PonderPalette.BLUE);

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.pitch = fi * 1.5f;
                be.vboDirty = true;
            }));
            scene.idle(1);
        }

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

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panX = fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

        scene.world().showSection(panZController, Direction.NORTH);
        scene.world().showSection(panZShaft, Direction.NORTH);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("Pan Z shifts the sampled area north or south")
                .pointAt(util.vector().topOf(1, 1, 2))
                .colored(PonderPalette.BLUE);

        for (int i = 0; i < 60; i++) {
            final int fi = i;
            scene.addInstruction(s -> s.getWorld().getBlockEntity(new BlockPos(2, 3, 2),
                    ModBlockEntityTypes.VOLUMETRIC_DISPLAY.get()).ifPresent(be -> {
                be.panZ = fi;
                be.chunkRequestDirty = true;
            }));
            scene.idle(1);
        }

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
}
