package com.cybrisoft.createmoderntech.block.warpgate.transponder;

import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.cybrisoft.createmoderntech.network.EndWarpTransitionPacket;
import com.cybrisoft.createmoderntech.network.StartWarpTransitionPacket;
import com.cybrisoft.createmoderntech.registry.ModSounds;
import com.cybrisoft.createmoderntech.util.ServerWarpGateManager;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WarpGateTransponderBlockEntity extends KineticBlockEntity implements BlockEntitySubLevelActor, BlockSubLevelAssemblyListener {
    public WarpGateTransponderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public volatile Vec3 warpPosition = null;
    public volatile boolean needsAxisCorrection = false;
    public volatile boolean sourceIsZ = false;
    public volatile boolean destIsZ = false;
    public volatile boolean readyToTeleport = false;
    public volatile boolean stagingTeleport = false;
    public volatile Vec3 stagingPosition = null;
    public int warpCooldown = 0;
    private int warpTransitionTicks = 0;
    private List<Player> travelingPlayers = new ArrayList<>();

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide()) return;

        if (warpTransitionTicks > 0) {
            warpTransitionTicks--;
            if (warpTransitionTicks == 25) {
                readyToTeleport = true;
                SubLevelAccess sl = SableCompanion.INSTANCE.getContaining(level, worldPosition);
                if (sl != null) {
                    level.playSound(null, worldPosition, ModSounds.WARP_TRANSITION.get(), SoundSource.AMBIENT, 8.0f, 1.0f);
                    for (Player player : level.players()) {
                        SubLevelAccess playerSL = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(player);
                        if (playerSL != null && playerSL.getUniqueId().equals(sl.getUniqueId())) {
                            PacketDistributor.sendToPlayer((ServerPlayer) player, new EndWarpTransitionPacket(playerSL.getUniqueId()));
                        }
                    }
                }
            }
        } else {
            warpCooldown = Math.max(0, warpCooldown-1);
        }

        if (warpCooldown > 0) return;

        if (getSpeed() == 0f) return;

        Set<BlockPos> warpGates = ServerWarpGateManager.getWarpGates(level.dimension());
        if (warpGates.isEmpty()) return;

        SubLevelAccess sublevel = SableCompanion.INSTANCE.getContaining(level, worldPosition);
        if (sublevel != null) {
            Vec3 projectionPos = sublevel.logicalPose().transformPosition(worldPosition.getCenter());
            for (BlockPos pos : warpGates) {
                BlockEntity ble = level.getBlockEntity(pos);
                if (ble instanceof WarpGateTerminalBlockEntity be) {
                    if (!be.wasOn) continue;

                    if (isInsidePortal(projectionPos, be)) {
                        Vec3 velocity = new Vec3(
                                sublevel.logicalPose().position().x() - sublevel.lastPose().position().x(),
                                sublevel.logicalPose().position().y() - sublevel.lastPose().position().y(),
                                sublevel.logicalPose().position().z() - sublevel.lastPose().position().z()
                        );
                        if (velocity.length() < 0.5) continue; // minimum velocity threshold

                        WarpGateTerminalBlockEntity pair = be.findPairedGate();
                        if (pair == null || !pair.wasOn) continue;
                        Vec3 sourceCenter = Vec3.atCenterOf(be.getBlockPos()).add(0, be.multiblockRadius, 0);
                        Vec3 destCenter = Vec3.atCenterOf(pair.getBlockPos()).add(0, pair.multiblockRadius, 0);
                        Vec3 offset = projectionPos.subtract(sourceCenter);

                        warnSize(be, pair); // todo move to target lock stage instead

                        warpPosition = destCenter.add(offset);
                        needsAxisCorrection = be.multiblockIsZ != pair.multiblockIsZ;
                        sourceIsZ = be.multiblockIsZ;
                        destIsZ = pair.multiblockIsZ;
                        warpCooldown = 20;
                        warpTransitionTicks = 200;
                        stagingTeleport = true;
                        stagingPosition = be.getBlockPos().getCenter().add(0, 2000, 0);

                        if (warpPosition != null) {
                            // send to all players on the sublevel
                            for (Player player : level.players()) {
                                SubLevelAccess playerSubLevel = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(player);
                                if (playerSubLevel != null && playerSubLevel.getUniqueId().equals(sublevel.getUniqueId())) {
                                    travelingPlayers.add(player);
                                    PacketDistributor.sendToPlayer((ServerPlayer) player,
                                            new StartWarpTransitionPacket(sublevel.getUniqueId(), velocity));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void warnSize(WarpGateTerminalBlockEntity start, WarpGateTerminalBlockEntity end) {
        SubLevelAccess sublevel = SableCompanion.INSTANCE.getContaining(level, worldPosition);
        if (sublevel == null) return;

        BoundingBox3dc bb = sublevel.boundingBox();
        float endRadius = end.multiblockRadius;

        // get the half-extents of the ship perpendicular to the destination portal normal
        double halfHeight = (bb.maxY() - bb.minY()) / 2.0;
        double halfWidth;
        if (end.multiblockIsZ) {
            // portal faces Z, ship width is in X
            halfWidth = (bb.maxX() - bb.minX()) / 2.0;
        } else {
            // portal faces X, ship width is in Z
            halfWidth = (bb.maxZ() - bb.minZ()) / 2.0;
        }

        // check if the ship's cross-section fits in the destination circle
        // worst case corner distance from center
        double cornerDist = Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);
        if (cornerDist > endRadius) {
            System.out.println("Warning: ship may be too large for destination gate");
        }
    }

    private boolean isInsidePortal(Vec3 globalPos, WarpGateTerminalBlockEntity gate) {
        Vec3 gateCenter = Vec3.atCenterOf(gate.getBlockPos()).add(0, gate.multiblockRadius, 0);
        Vec3 diff = globalPos.subtract(gateCenter);

        // project onto portal plane
        Vec3 normal = !gate.multiblockIsZ ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0);
        double normalDist = Math.abs(diff.dot(normal));

        if (normalDist > 1.0) return false; // too far from plane

        Vec3 projected = diff.subtract(normal.scale(diff.dot(normal)));
        return projected.length() < gate.multiblockRadius;
    }

    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {}

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        BlockEntitySubLevelActor.super.sable$physicsTick(subLevel, handle, timeStep);

        if (stagingTeleport) {
            // teleport 2000 blocks up, keep orientation
            Vector3d currentPos = new Vector3d(stagingPosition.x, stagingPosition.y, stagingPosition.z);
            handle.teleport(currentPos, subLevel.logicalPose().orientation());

            fixBranchingPlayers(subLevel.getLevel());
        }

        if (warpPosition != null && readyToTeleport) {
            stagingTeleport = false;
            readyToTeleport = false;
            Quaterniondc currentOrientation = subLevel.logicalPose().orientation();
            Quaterniondc newOrientation = currentOrientation;

            if (sourceIsZ != destIsZ) {
                Quaterniond axisCorrection = new Quaterniond().rotateY(Math.PI / 2);
                newOrientation = new Quaterniond(currentOrientation).mul(axisCorrection);
            }

            if (needsAxisCorrection) {
                Vector3d vel = handle.getLinearVelocity(new Vector3d());
                Vector3d angVel = handle.getAngularVelocity(new Vector3d());

                // zero out current velocity
                handle.addLinearAndAngularVelocity(
                        new Vector3d(-vel.x, -vel.y, -vel.z),
                        new Vector3d(-angVel.x, -angVel.y, -angVel.z)
                );

                // add rotated velocity (90 degrees around Y)
                handle.addLinearAndAngularVelocity(
                        new Vector3d(-vel.z, vel.y, vel.x),
                        new Vector3d(-angVel.z, angVel.y, angVel.x)
                );
            }

            handle.teleport(
                    new Vector3d(warpPosition.x, warpPosition.y, warpPosition.z),
                    newOrientation
            );

            fixBranchingPlayers(subLevel.getLevel());

            // reset relevant states
            warpPosition = null;
            travelingPlayers.clear();
        }
    }

    private void fixBranchingPlayers(ServerLevel level) {
        SubLevelAccess sl = SableCompanion.INSTANCE.getContaining(level, worldPosition);
        if (sl == null) return;
        Vec3 safePos = sl.logicalPose().transformPosition(worldPosition.getCenter());
        for (Player player : travelingPlayers) {
            SubLevelAccess playerSL = SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(player);
            if (playerSL == null) {
                // Player is no longer on the sublevel
                System.out.println("Player supposedly left sublevel");
                //level.getServer().execute(() -> player.teleportTo(safePos.x, safePos.y, safePos.z));
            }
        }
    }
}
