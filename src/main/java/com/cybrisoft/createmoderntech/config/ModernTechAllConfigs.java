package com.cybrisoft.createmoderntech.config;

import com.simibubi.create.api.stress.BlockStressValues;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModernTechAllConfigs {
    private static ModernTechServer server;

    public static void register(ModLoadingContext context, ModContainer container) {
        Pair<ModernTechServer, ModConfigSpec> pair = new ModConfigSpec.Builder()
                .configure(builder -> {
                    ModernTechServer config = new ModernTechServer();
                    config.registerAll(builder);
                    return config;
                });

        server = pair.getLeft();
        server.specification = pair.getRight();
        container.registerConfig(ModConfig.Type.SERVER, server.specification);

        BlockStressValues.IMPACTS.registerProvider(server.stressValues::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(server.stressValues::getCapacity);
    }
}
