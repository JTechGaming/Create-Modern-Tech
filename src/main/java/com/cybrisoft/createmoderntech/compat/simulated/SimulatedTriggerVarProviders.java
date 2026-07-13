package com.cybrisoft.createmoderntech.compat.simulated;

import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.TriggerVarProvider;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity;

public class SimulatedTriggerVarProviders implements TriggerVarProvider {
    @Override
    public void register() {
        TriggerVarProviderRegistry.register(NavTableBlockEntity.class, be -> {
            if (be.currentTarget == null) return "No target";
            return be.currentTarget.toString();
        });
        TriggerVarProviderRegistry.register(LaserSensorBlockEntity.class, be -> {
            return be.currentPower + "";
        });
        TriggerVarProviderRegistry.register(VelocitySensorBlockEntity.class, be -> {
            return String.format("%.2f", be.getAdjustedVelocity()) + " meters per second"; // written out for pronunciation
        });
        TriggerVarProviderRegistry.register(AltitudeSensorBlockEntity.class, be -> {
            return String.format("%.2f", be.getWorldHeight());
        });
        TriggerVarProviderRegistry.register(GimbalSensorBlockEntity.class, be -> {
            return "X " + be.getXAngle() + " degrees, Z " + be.getZAngle() + " degrees";
        });
    }
}
