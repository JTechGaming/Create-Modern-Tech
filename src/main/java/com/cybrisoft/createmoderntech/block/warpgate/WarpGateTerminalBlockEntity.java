package com.cybrisoft.createmoderntech.block.warpgate;

import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.List;

public class WarpGateTerminalBlockEntity extends SmartBlockEntity {
    private static final int MIN_RADIUS = 8;
    private static final int MAX_RADIUS = 40;

    public int multiblockRadius = 0;
    public boolean multiblockIsZ = false;
    public boolean drawGuides = false;
    public int guideRadius = 0;

    public WarpGateTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

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

    private boolean hasRingBlock(Level level, int bx, int by, int bz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (level.getBlockState(new BlockPos(bx + dx, by + dy, bz + dz))
                            .is(ModBlocks.WARP_GATE_BLOCK)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        multiblockRadius = validateMultiBlock();
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
}
