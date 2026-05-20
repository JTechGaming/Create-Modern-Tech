package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.item.BeaconCompassData;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTER =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CreateModernTech.MODID);

    public static final Supplier<DataComponentType<BeaconCompassData>> BEACON_TARGET =
            REGISTER.register("beacon_target", () -> DataComponentType.<BeaconCompassData>builder()
                    .persistent(BeaconCompassData.CODEC)
                    .networkSynchronized(BeaconCompassData.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> SPEAKER_NETWORK_ID =
            REGISTER.register("speaker_network_id", () -> DataComponentType.<UUID>builder()
                    .persistent(UUIDUtil.CODEC)
                    .networkSynchronized(UUIDUtil.STREAM_CODEC)
                    .build());

    public static void register() {}
}