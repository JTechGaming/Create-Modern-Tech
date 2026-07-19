package com.cybrisoft.createmoderntech.block.warpgate.termimal;

import com.cybrisoft.createmoderntech.block.warpgate.WarpGateBlock;
import com.cybrisoft.createmoderntech.block.warpgate.WarpGateSegment;
import com.cybrisoft.createmoderntech.block.warpgate.amplifier.WarpAmplifierBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.drive.WarpDriveBlockEntity;
import com.cybrisoft.createmoderntech.client.WarpGateRenderer;
import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.cybrisoft.createmoderntech.registry.ModSounds;
import com.cybrisoft.createmoderntech.util.ServerWarpGateManager;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WarpGateTerminalBlockEntity extends SmartBlockEntity {
    private static final int MIN_RADIUS = 8;
    private static final int MAX_RADIUS = 50;

    public int multiblockRadius = 0;
    public boolean multiblockIsZ = false;
    public boolean drawGuides = false;
    public int guideRadius = 0;

    public WarpGateTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FilteringBehaviour filtering;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filtering = new FilteringBehaviour(this, new WarpGateFilterSlot());
        behaviours.add(filtering);
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (multiblockRadius == 0) return super.getRenderBoundingBox();
        BlockPos base = getBlockPos();
        int r = multiblockRadius;
        return new AABB(
                base.getX() - r, base.getY(), base.getZ() - r,
                base.getX() + r, base.getY() + r * 2, base.getZ() + r
        );
    }

    public float activationProgress = 0f;
    public float closing = 0f;
    private static final float ACTIVATION_SPEED = 0.1f;
    private float cachedRadius = 0f;
    public boolean wasOn = false;
    private boolean wasClosed = false;
    private float prevProgress = 0f;

    private boolean isBeamOn = false;
    private float speed = 0f;
    private int amplifiers = 0;

    private boolean wasClient = false;

    @Override
    public void initialize() {
        super.initialize();
        if (level == null) return;
        wasClient = level.isClientSide();
        if (!wasClient) {
            ServerWarpGateManager.register(level.dimension(), getBlockPos());
        } else {
            WarpGateRenderer.register(getBlockPos());
        }
    }

    @Override
    public void destroy() {
        if (wasClient) {
            WarpGateRenderer.remove(getBlockPos());
        } else if (level != null) {
            ServerWarpGateManager.remove(level.dimension(), getBlockPos());
        }
        super.destroy();
    }

    public WarpGateTerminalBlockEntity findPairedGate() {
        if (level == null) return null;

        for (BlockPos pos : ServerWarpGateManager.getWarpGates(level.dimension())) {
            if (pos.equals(getBlockPos())) continue;

            BlockEntity ble = level.getBlockEntity(pos);
            if (ble instanceof WarpGateTerminalBlockEntity be) {
                if (ItemStack.isSameItemSameComponents(filtering.getFilter(), be.filtering.getFilter())) {
                    return be;
                }
            }
        }

        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;

        boolean isOn =
                multiblockRadius > 0 &&
                isBeamOn &&
                multiblockRadius <= 10 + amplifiers * 8;

        if (isOn) {
            activationProgress = Math.min(1f, activationProgress + ACTIVATION_SPEED / multiblockRadius * (speed / 16f));
            cachedRadius = multiblockRadius;
            closing = 0f;

            if (!wasClosed && activationProgress == 1f) {
                wasClosed = true;
                level.playSound(null, worldPosition, ModSounds.PORTAL_CLOSE.get(), SoundSource.AMBIENT, 2.0f, 1.0f);
            }
            if (!wasOn) {
                level.playSound(null, worldPosition, ModSounds.PORTAL_ACTIVATE.get(), SoundSource.AMBIENT, 2.0f, 1.0f);
            }
        } else {
            closing = 1f;
            activationProgress = Math.max(0f, activationProgress - ACTIVATION_SPEED * 4f / cachedRadius);
            wasClosed = false;
        }

        if (prevProgress != activationProgress) {
            sendData();
        }

        wasOn = isOn;
        prevProgress = activationProgress;
    }

    boolean wasValid = false;

    @Override
    public void lazyTick() {
        super.lazyTick();

        multiblockRadius = validateMultiBlock();
        boolean isValid = multiblockRadius > 0;
        if (isValid && !wasValid) {
            updateBlockStates(multiblockRadius);
        }
        wasValid = isValid;

        if (level == null) return;

        BlockEntity ble = level.getBlockEntity(getBlockPos().below());
        if (ble instanceof WarpAmplifierBlockEntity be) {
            this.amplifiers = be.amplifierAmount;
            this.isBeamOn = be.isBeamOn;
            this.speed = be.getSpeed();
            return;
        }
        if (ble instanceof WarpDriveBlockEntity be) {
            this.isBeamOn = be.isOn;
            this.speed = be.getSpeed();
            this.amplifiers = 0;
        }
    }

    private void updateBlockStates(int radius) {
        Level level = getLevel();
        if (level == null) return;

        BlockPos center = getBlockPos().above(radius);
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        boolean isZ = multiblockIsZ;

        int x = 0, y = radius, d = 1 - radius;
        while (x <= y) {
            applySegments(level, cx, cy, cz, x, y, isZ, center);
            if (d < 0) d += 2 * x + 3;
            else { d += 2 * (x - y) + 5; y--; }
            x++;
        }
    }

    private void applySegments(Level level, int cx, int cy, int cz, int x, int y, boolean isZ, BlockPos center) {
        if (isZ) {
            setSegment(level, new BlockPos(cx, cy + y, cz + x), center, isZ);
            setSegment(level, new BlockPos(cx, cy - y, cz + x), center, isZ);
            setSegment(level, new BlockPos(cx, cy + y, cz - x), center, isZ);
            setSegment(level, new BlockPos(cx, cy - y, cz - x), center, isZ);
            setSegment(level, new BlockPos(cx, cy + x, cz + y), center, isZ);
            setSegment(level, new BlockPos(cx, cy - x, cz + y), center, isZ);
            setSegment(level, new BlockPos(cx, cy + x, cz - y), center, isZ);
            setSegment(level, new BlockPos(cx, cy - x, cz - y), center, isZ);
        } else {
            setSegment(level, new BlockPos(cx + y, cy + x, cz), center, isZ);
            setSegment(level, new BlockPos(cx - y, cy + x, cz), center, isZ);
            setSegment(level, new BlockPos(cx + y, cy - x, cz), center, isZ);
            setSegment(level, new BlockPos(cx - y, cy - x, cz), center, isZ);
            setSegment(level, new BlockPos(cx + x, cy + y, cz), center, isZ);
            setSegment(level, new BlockPos(cx - x, cy + y, cz), center, isZ);
            setSegment(level, new BlockPos(cx + x, cy - y, cz), center, isZ);
            setSegment(level, new BlockPos(cx - x, cy - y, cz), center, isZ);
        }
    }

    private void setSegment(Level level, BlockPos pos, BlockPos center, boolean isZ) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.WARP_GATE_BLOCK)) return;
        WarpGateSegment segment = WarpGateBlock.computeSegment(pos, center, isZ);
        level.setBlock(pos, state.setValue(WarpGateBlock.SEGMENT, segment), 3);
    }

    public int validateMultiBlock() {
        BlockPos base = getBlockPos();
        Level level = getLevel();
        guideRadius = 0;
        if (level == null) return 0;

        // walk upward through air until top ring block
        int steps = 0;
        while (!level.getBlockState(base.above(steps + 1)).is(ModBlocks.WARP_GATE_BLOCK)) {
            steps++;
            if (steps > MAX_RADIUS * 2) return 0;
        }
        int r = (steps + 1) / 2; // radius
        if (r < MIN_RADIUS) return 0;
        guideRadius = r;

        boolean isZ = level.getBlockState(base.north()).is(ModBlocks.WARP_GATE_BLOCK);
        int cx = base.getX();
        int cy = base.getY() + r; // ring center Y
        int cz = base.getZ();

        // midpoint circle algorithm
        int x = 0, y = r, d = 1 - r;
        while (x <= y) {
            if (!checkOctants(level, cx, cy, cz, x, y, isZ)) return 0;
            if (d < 0) d += 2 * x + 3;
            else { d += 2 * (x - y) + 5; y--; }
            x++;
        }

        multiblockIsZ = isZ;
        return r;
    }

    private boolean checkOctants(Level level, int cx, int cy, int cz, int x, int y, boolean isZ) {
        if (isZ) {
            return check(level, cx, cy + y, cz + x) && check(level, cx, cy - y, cz + x)
                    && check(level, cx, cy + y, cz - x) && check(level, cx, cy - y, cz - x)
                    && check(level, cx, cy + x, cz + y) && check(level, cx, cy - x, cz + y)
                    && check(level, cx, cy + x, cz - y) && check(level, cx, cy - x, cz - y);
        } else {
            return check(level, cx + y, cy + x, cz) && check(level, cx - y, cy + x, cz)
                    && check(level, cx + y, cy - x, cz) && check(level, cx - y, cy - x, cz)
                    && check(level, cx + x, cy + y, cz) && check(level, cx - x, cy + y, cz)
                    && check(level, cx + x, cy - y, cz) && check(level, cx - x, cy - y, cz);
        }
    }

    private boolean check(Level level, int x, int y, int z) {
        BlockState state = level.getBlockState(new BlockPos(x, y, z));
        return state.is(ModBlocks.WARP_GATE_BLOCK) || state.is(ModBlocks.WARP_GATE_TERMINAL_BLOCK);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("activationProgress", activationProgress);
        tag.putFloat("closing", closing);
        tag.putInt("radius", multiblockRadius);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        activationProgress = tag.getFloat("activationProgress");
        closing = tag.getFloat("closing");
        multiblockRadius = tag.getInt("radius");
    }
}
