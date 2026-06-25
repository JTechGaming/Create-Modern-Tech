package com.cybrisoft.createmoderntech.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientCompassHelper {
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
