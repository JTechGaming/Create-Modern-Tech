package com.cybrisoft.createmoderntech.util;

import net.minecraft.core.BlockPos;

import java.util.*;

public class ServerAINetworkManager {
    private static final Map<UUID, BlockPos> networks = new HashMap<>();

    public static void register(UUID networkID, BlockPos pos) {
        networks.put(networkID, pos);
    }

    public static void remove(UUID networkID) {
        networks.remove(networkID);
    }

    public static BlockPos getNetworkCorePos(UUID networkID) {
        return networks.get(networkID);
    }
}
