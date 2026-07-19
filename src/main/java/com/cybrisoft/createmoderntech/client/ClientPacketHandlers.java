package com.cybrisoft.createmoderntech.client;

import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.network.*;
import com.cybrisoft.createmoderntech.registry.ModSounds;
import com.cybrisoft.createmoderntech.tts.FreeTTSEngine;
import com.cybrisoft.createmoderntech.ui.AudioTriggerScreen;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {

    public static void handleOpenAudioTriggerScreen(OpenAudioTriggerScreenPacket packet,
                                                    IPayloadContext context) {
        context.enqueueWork(() ->
                Minecraft.getInstance().setScreen(
                        new AudioTriggerScreen(packet.pos(), packet.message(), packet.vars())));
    }

    public static void handlePlaySpeaker(PlaySpeakerPacket packet,
                                         IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            Vec3 playerPos = mc.player.position();
            boolean played = false;
            for (BlockPos speakerPos : packet.speakerPositions()) {
                if (mc.level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity speaker) {
                    Vec3 pos = Vec3.atCenterOf(speakerPos);
                    if (pos.distanceTo(playerPos) <= speaker.range) {
                        FreeTTSEngine.speak(packet.message(), pos, speaker.range);
                        played = true;
                    }
                }
            }
            if (!played) FreeTTSEngine.speak(packet.message(), playerPos, 32f);
        });
    }

    public static void handleHeightmapData(HeightmapDataPacket packet,
                                           IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            if (mc.level.getBlockEntity(packet.blockEntityPos()) instanceof VolumetricDisplayBlockEntity be) {
                be.heightmapCache.putAll(packet.heightmaps());
                be.vboDirty = true;
                be.lastCacheUpdate = 0;
            }
        });
    }

    public static void handleStart(StartWarpTransitionPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                WarpTransitionRenderer.sublevel = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(mc.player);
            }
            WarpTransitionRenderer.shouldRender = true;
            WarpTransitionRenderer.travelDirection = packet.velocity().normalize();
            WarpTransitionRenderer.ramping = true;
            WarpTransitionRenderer.transitionProgress = 0f;

            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            Level level = player.level();

            level.playLocalSound(player, ModSounds.WARP_TRANSITION.get(), SoundSource.AMBIENT, 8.0f, 1.0f);
            level.playLocalSound(player, ModSounds.WARP_AMBIANCE.get(), SoundSource.AMBIENT, 8.0f, 1.0f);
        });
    }

    public static void handleEnd(EndWarpTransitionPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            WarpTransitionRenderer.ramping = false;
        });
    }

    // -------------------------------------------------------------------------
    // Client-side: request chunks not already cached
    // -------------------------------------------------------------------------

    public static void requestMissingChunks(VolumetricDisplayBlockEntity be,
                                            BlockPos sampleCenter, int chunkRadius) {
        int centerCX = sampleCenter.getX() >> 4;
        int centerCZ = sampleCenter.getZ() >> 4;

        List<Long> toRequest = new ArrayList<>();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int cx = centerCX + dx;
                int cz = centerCZ + dz;
                long key = packChunkPos(cx, cz);

                if (!be.heightmapCache.containsKey(key)) {
                    toRequest.add(key);
                }
            }
        }

        if (!toRequest.isEmpty()) {
            PacketDistributor.sendToServer(new RequestHeightmapPacket(be.getBlockPos(), toRequest));
        }
    }

    // -------------------------------------------------------------------------
    // Helper: pack chunk coords the same way on both sides
    // -------------------------------------------------------------------------

    public static long packChunkPos(int cx, int cz) {
        return ((long) cz << 32) | (cx & 0xFFFFFFFFL);
    }
}
