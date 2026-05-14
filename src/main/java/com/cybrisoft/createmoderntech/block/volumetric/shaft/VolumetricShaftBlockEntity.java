package com.cybrisoft.createmoderntech.block.volumetric.shaft;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlock;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class VolumetricShaftBlockEntity extends KineticBlockEntity {
    public float panX = 0f;
    public float panZ = 0f;
    public float yaw = 0f;
    public float pitch = 0f;

    public BlockPos cachedDisplayPos = null;
    private boolean layoutDirty = true;

    public final List<VolumetricDisplayBlockEntity.BeaconData> beacons = new ArrayList<>();

    public VolumetricShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide()) return;

        if (layoutDirty) {
            refreshLayout();
            layoutDirty = false;
        }
    }

    /**
     * Scans upward to find the display and outward at each shaft Y level to find controllers.
     * Called lazily when the layout is marked dirty by a neighbor change.
     */
    private void refreshLayout() {
        cachedDisplayPos = null;

        // Walk upward through the shaft column
        BlockPos cursor = worldPosition.above();
        while (level.getBlockState(cursor).getBlock() instanceof VolumetricShaftBlock) {
            checkNeighborsAt(cursor);
            cursor = cursor.above();
        }

        BlockState top = level.getBlockState(cursor);
        if (top.getBlock() instanceof VolumetricDisplayBlock) {
            cachedDisplayPos = cursor;
        }

        // Also check neighbors at this block own position
        checkNeighborsAt(worldPosition);
    }

    /**
     * Checks the 4 horizontal neighbors at a given position for controller blocks
     * and registers them so they know which shaft they belong to.
     */
    private void checkNeighborsAt(BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);
            if (level.getBlockEntity(neighborPos) instanceof VolumetricControllerBlockEntity controller) {
                controller.setShaftPos(worldPosition);
            }
        }
    }

    /**
     * Called by controllers to update their specific parameter.
     */
    public void setControllerValue(ControllerType type, float value) {
        switch (type) {
            case PAN_X -> panX = value;
            case PAN_Z -> panZ = value;
            case YAW   -> yaw = value;
            case PITCH -> pitch = value;
        }

        // Propagate to the display
        if (cachedDisplayPos != null && level.getBlockEntity(cachedDisplayPos) instanceof VolumetricDisplayBlockEntity display) {
            display.panX = panX;
            display.panZ = panZ;
            display.yaw = yaw;
            display.pitch = pitch;
            display.chunkRequestDirty = true;

            display.notifyUpdate();
        }
    }

    public void markLayoutDirty() {
        layoutDirty = true;
    }

    public enum ControllerType {
        PAN_X, PAN_Z, YAW, PITCH
    }
}