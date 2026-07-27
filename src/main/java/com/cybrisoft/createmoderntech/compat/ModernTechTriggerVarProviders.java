package com.cybrisoft.createmoderntech.compat;

import com.cybrisoft.createmoderntech.block.volumetric.controller.beacon.BeaconControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.termimal.WarpGateTerminalBlockEntity;
import com.cybrisoft.createmoderntech.block.warpgate.transponder.WarpGateTransponderBlockEntity;
import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.TriggerVarProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModernTechTriggerVarProviders implements TriggerVarProvider {
    @Override
    public void register() {
        TriggerVarProviderRegistry.register(BeaconControllerBlockEntity.class, (be) -> {
            VolumetricDisplayBlockEntity.BeaconData beacon = BeaconControllerBlockEntity.getNearestBeacon(be.getLinkedDisplay());

            if (beacon == null) return "No Beacon Found";

            return beacon.name.isBlank() ? "Unnamed Beacon" : beacon.name;
        });

        TriggerVarProviderRegistry.register(WarpGateTransponderBlockEntity.class, (be) -> {
            if (be.oldTargetPos == null && be.targetGatePos != null) {
                be.oldTargetPos = be.targetGatePos;
                return "Locked onto warp target on channel " + be.filtering.getFilter().getDisplayName().getString();
            }
            if (be.stagingTeleport) return "Jumping to destination";
            //if (be.shipSpeed >= WarpGateTransponderBlockEntity.MIN_SPEED_REQ) return "Required speed reached";

            if (be.getLevel() == null || be.targetGatePos == null) return "";
            BlockEntity ble = be.getLevel().getBlockEntity(be.targetGatePos);
            if (ble instanceof WarpGateTerminalBlockEntity gateBle) {
                if (gateBle.wasOn) {
                    return "Ready for jump";
                }
                return "Waiting for gate to turn on";
            }
            return "";
        });
    }
}
