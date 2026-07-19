package com.cybrisoft.createmoderntech.block.warpgate.termimal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WarpGateFilterSlot extends ValueBoxTransform.Sided {

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = Direction.UP;
        Vec3 vec = VecHelper.voxelSpace(8f, 8f, 15.5f);

        vec = VecHelper.rotateCentered(vec, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        vec = VecHelper.rotateCentered(vec, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
        vec = vec.subtract(Vec3.atLowerCornerOf(facing.getNormal())
                .scale(2 / 16f));

        return vec;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = Direction.UP;
        if (direction.getAxis() == facing.getAxis())
            return false;
        return true;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = getSide();
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        float yRot = AngleHelper.horizontalAngle(facing) + 180;

        if (facing.getAxis() == Direction.Axis.Y)
            TransformStack.of(ms)
                    .rotateYDegrees(180 + AngleHelper.horizontalAngle(Direction.UP));

        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(xRot);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
