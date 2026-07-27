package com.cybrisoft.createmoderntech.block.audiotrigger;

import com.cybrisoft.createmoderntech.block.aicore.AICoreBlockEntity;
import com.cybrisoft.createmoderntech.block.speaker.SpeakerBlockEntity;
import com.cybrisoft.createmoderntech.network.PlaySpeakerPacket;
import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.ServerAINetworkManager;
import com.cybrisoft.createmoderntech.util.TriggerVariableEntry;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AudioTriggerBlockEntity extends SmartBlockEntity {
    public UUID networkId = null;
    public String message = "";
    private boolean wasPowered = false;

    public AudioTriggerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();

        if (networkId == null) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null || getLevel() == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.registerDevicePosition(getBlockPos());
        }
    }

    @Override
    public void destroy() {
        if (networkId == null) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null || getLevel() == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.unregisterDevicePosition(getBlockPos());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    boolean registered = false;

    public void tick() {
        tryRegister();
        if (level == null || level.isClientSide()) return;

        boolean isPowered = level.hasNeighborSignal(worldPosition);
        if (isPowered && !wasPowered) {
            trigger();
        }
        wasPowered = isPowered;
    }

    private void tryRegister() {
        if (registered || networkId == null || getLevel() == null || getLevel().isClientSide()) return;
        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null) return;
        if (getLevel().getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            be.registerDevicePosition(getBlockPos());
            registered = true;
        }
    }

    public List<TriggerVariableEntry> getVars() {
        List<TriggerVariableEntry> vars = new ArrayList<>();
        if (level == null) return vars;

        for (Direction dir : Direction.values()) {
            BlockPos dirPos = getBlockPos().relative(dir);
            BlockEntity dirBe = level.getBlockEntity(dirPos);

            if (dirBe != null && TriggerVarProviderRegistry.isProvider(dirBe)) {
                String value = TriggerVarProviderRegistry.resolve(dirBe);

                if (value.isBlank()) {
                    value = "No value";
                }

                BlockState dirState = level.getBlockState(dirPos);
                String identifier = BuiltInRegistries.BLOCK.getKey(dirState.getBlock()).toString();

                vars.add(new TriggerVariableEntry(dir, value, identifier));
            }
        }
        return vars;
    }

    public void trigger() {
        if (networkId == null || message.isBlank()) return;

        List<BlockPos> speakerPositions = new ArrayList<>();
        ServerLevel serverLevel = (ServerLevel) level;

        BlockPos corePos = ServerAINetworkManager.getNetworkCorePos(networkId);
        if (corePos == null) return;
        if (serverLevel == null) return;
        if (serverLevel.getBlockEntity(corePos) instanceof AICoreBlockEntity be) {
            for (BlockPos pos : be.getConnectedDevices()) {
                if (serverLevel.getBlockEntity(pos) instanceof SpeakerBlockEntity) {
                    speakerPositions.add(pos);
                }
            }
        }

        UUID netId = this.networkId;
        String msg = buildMessageString();

        for (ServerPlayer player : serverLevel.players()) {
            boolean inRange = speakerPositions.stream().anyMatch(speakerPos -> {
                Vec3 speakerGlobalPos;
                SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, speakerPos);
                if (subLevel != null) {
                    speakerGlobalPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(speakerPos));
                } else {
                    speakerGlobalPos = Vec3.atCenterOf(speakerPos);
                }

                Vec3 playerPos = player.position();
                double dist = SableCompanion.INSTANCE.distanceSquaredWithSubLevels(
                        level,
                        new Vector3d(playerPos.x, playerPos.y, playerPos.z),
                        new Vector3d(speakerGlobalPos.x, speakerGlobalPos.y, speakerGlobalPos.z)
                );

                float range = ((SpeakerBlockEntity) serverLevel.getBlockEntity(speakerPos)).range;
                return dist <= range * range;
            });

            if (inRange) {
                PacketDistributor.sendToPlayer(player,
                        new PlaySpeakerPacket(netId, msg, speakerPositions));
            }
        }
    }

    private @NonNull String buildMessageString() {
        String msg = this.message;

        List<TriggerVariableEntry> vars = getVars();
        TriggerVariableEntry upEntry = null, downEntry = null, northEntry = null, southEntry = null, westEntry = null, eastEntry = null;

        for (TriggerVariableEntry var : vars) {
            switch (var.getDirection()) {
                case DOWN -> downEntry = var;
                case UP -> upEntry = var;
                case NORTH -> northEntry = var;
                case SOUTH -> southEntry = var;
                case WEST -> westEntry = var;
                case EAST -> eastEntry = var;
            }
        }

        msg = msg.replace("%D", downEntry != null ? downEntry.getValue() : "");
        msg = msg.replace("%U", upEntry != null ? upEntry.getValue() : "");
        msg = msg.replace("%N", northEntry != null ? northEntry.getValue() : "");
        msg = msg.replace("%S", southEntry != null ? southEntry.getValue() : "");
        msg = msg.replace("%W", westEntry != null ? westEntry.getValue() : "");
        msg = msg.replace("%E", eastEntry != null ? eastEntry.getValue() : "");
        return msg;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (networkId != null) tag.putUUID("NetworkId", networkId);
        tag.putString("Message", message);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        networkId = tag.hasUUID("NetworkId") ? tag.getUUID("NetworkId") : null;
        message = tag.getString("Message");
    }
}
