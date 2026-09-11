package com.cybrisoft.createmoderntech.block.starbreech;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.command.SablePhysicsCommands;
import dev.ryanhcode.sable.command.SableSpawnCommands;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StarBreechBlockEntity extends KineticBlockEntity {
    public StarBreechBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    List<UUID> createdSublevels = new ArrayList<>();

    @Override
    public void tick() {
        super.tick();

        if (level == null) return;
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, getBlockPos());
        Vec3 globalPos;
        if (subLevel != null) {
            globalPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(getBlockPos()));
        } else {
            globalPos = Vec3.atCenterOf(getBlockPos());
        }

        Random randomizer = new Random();

        Vec3 random = new Vec3(
                randomizer.nextInt(10) - 5,
                randomizer.nextInt(10) - 5,
                randomizer.nextInt(10) - 5
        );
        Vec3 chosenPos = globalPos.add(random);
        BlockPos pos = BlockPos.containing(chosenPos);
        if (pos.equals(getBlockPos())) return;

        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock().equals(Blocks.AIR)) return;

        final ServerSubLevelContainer plotContainer = ServerSubLevelContainer.getContainer(serverLevel);
        if (plotContainer == null) return;

        final SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get(level);
        if (physicsSystem == null) return;

        final Pose3d pose = new Pose3d();
        pose.position().set(chosenPos.x, chosenPos.y, chosenPos.z);

        final SubLevel newSubLevel = plotContainer.allocateNewSubLevel(pose);
        final LevelPlot plot = newSubLevel.getPlot();

        final ChunkPos center = plot.getCenterChunk();
        plot.newEmptyChunk(center);

        plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, blockState, 3);
        newSubLevel.updateLastPose();

        createdSublevels.add(newSubLevel.getUniqueId());

        for (ServerSubLevel serverSubLevel : plotContainer.getAllSubLevels()) {
            if (serverSubLevel.getUniqueId().equals(newSubLevel.getUniqueId())) {
                physicsSystem.getPhysicsHandle(serverSubLevel).applyLinearImpulse(
                        JOMLConversion.toJOML(serverSubLevel.logicalPose().transformNormalInverse(new Vec3(0, 10, 0)))
                );
                break;
            }
        }
    }
}
