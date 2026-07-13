package com.cybrisoft.createmoderntech.block.warpgate;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class WarpGateTransponderBlock extends KineticBlock implements IBE<WarpGateTransponderBlockEntity> {
    public WarpGateTransponderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<WarpGateTransponderBlockEntity> getBlockEntityClass() {
        return WarpGateTransponderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WarpGateTransponderBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.WARP_GATE_TRANSPONDER.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof WarpGateTransponderBlockEntity be) {
            BlockPos blockPos = context.getClickedPos();
            SubLevelAccess sublevel = SableCompanion.INSTANCE.getContaining(level, blockPos);
            Vec3 transformedPos;
            if (sublevel != null) {
                transformedPos = sublevel.logicalPose().transformPosition(blockPos.getCenter());
            } else {
                transformedPos = blockPos.getCenter();
            }

            transformedPos = transformedPos.add(10, 0, 0);

            be.warpPosition = new Vector3d(transformedPos.x, transformedPos.y, transformedPos.z);
        }
        return InteractionResult.FAIL;
    }
}
