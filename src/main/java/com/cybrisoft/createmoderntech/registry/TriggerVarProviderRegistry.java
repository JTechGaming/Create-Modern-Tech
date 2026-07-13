package com.cybrisoft.createmoderntech.registry;

import net.minecraft.world.level.block.entity.BlockEntity;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TriggerVarProviderRegistry {
    private static final Map<Class<? extends BlockEntity>, Function<BlockEntity, String>> REGISTRY = new HashMap<>();

    public static <T extends BlockEntity> void register(Class<T> type, Function<T, String> entry) {
        if (REGISTRY.containsKey(type)) {
            throw new KeyAlreadyExistsException(type + " already has a provider registered!");
        }
        REGISTRY.put(type, be -> entry.apply(type.cast(be)));
    }

    public static String resolve(BlockEntity be) {
        Class<? extends BlockEntity> type = be.getClass();
        if (!REGISTRY.containsKey(type)) {
            return "";
        }
        return REGISTRY.get(type).apply(be);
    }

    public static boolean isProvider(BlockEntity be) {
        return REGISTRY.containsKey(be.getClass());
    }
}
