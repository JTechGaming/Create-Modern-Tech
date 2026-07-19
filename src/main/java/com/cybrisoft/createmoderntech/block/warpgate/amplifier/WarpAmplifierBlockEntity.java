package com.cybrisoft.createmoderntech.block.warpgate.amplifier;

import com.cybrisoft.createmoderntech.block.warpgate.drive.WarpDriveBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class WarpAmplifierBlockEntity extends KineticBlockEntity {
    public float ring1Angle = 0f;
    public float ring2Angle = 0f;
    public float ring3Angle = 0f;
    public float expansionProgress = 0f; // 0=collapsed, 1=expanded
    public boolean isBeamOn = false;
    public int amplifierAmount = 0;

    public float currentRotSpeed = 0f;
    private static final float ROT_ACCEL = 0.002f;

    public WarpAmplifierBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        float targetRotSpeed = Math.abs(getSpeed()) * 0.01f;
        currentRotSpeed += (targetRotSpeed - currentRotSpeed) * ROT_ACCEL;

        ring1Angle += currentRotSpeed;
        ring2Angle -= currentRotSpeed;
        ring3Angle += currentRotSpeed;
        expansionProgress = Math.min(1f, Math.abs(getSpeed()) / 256f);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if (level == null) return;
        BlockPos pos = getBlockPos();
        BlockEntity ble = level.getBlockEntity(pos.below());
        if (ble instanceof WarpAmplifierBlockEntity be) {
            this.isBeamOn = be.isBeamOn;
            this.amplifierAmount = be.amplifierAmount + 1;
            return;
        }
        if (ble instanceof WarpDriveBlockEntity be) {
            this.isBeamOn = be.isOn;
            this.amplifierAmount = 1;
        }
    }
}
