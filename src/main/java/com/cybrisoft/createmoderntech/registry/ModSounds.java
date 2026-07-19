package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CreateModernTech.MODID);

    public static final Supplier<SoundEvent> PORTAL_ACTIVATE = registerSoundEvent("portal_activate");
    public static final Supplier<SoundEvent> PORTAL_CLOSE = registerSoundEvent("portal_close");
    public static final Supplier<SoundEvent> WARP_TRANSITION = registerSoundEvent("warp_transition");
    public static final Supplier<SoundEvent> WARP_AMBIANCE = registerSoundEvent("warp_ambiance");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateModernTech.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        CreateModernTech.getLogger().info("Registering sounds!");
        SOUND_EVENTS.register(eventBus);
    }
}
