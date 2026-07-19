package com.cybrisoft.createmoderntech.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

public class ServerWarpGateManager {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> warpGates = new HashMap<>();

    public static void register(ResourceKey<Level> level, BlockPos blockPos) {
        warpGates.computeIfAbsent(level, k -> new HashSet<>()).add(blockPos);
    }

    public static void remove(ResourceKey<Level> level, BlockPos blockPos) {
        if (!warpGates.containsKey(level)) return;
        warpGates.get(level).remove(blockPos);
    }

    public static Set<BlockPos> getWarpGates(ResourceKey<Level> level) {
        return warpGates.getOrDefault(level, Collections.emptySet());
    }
}
