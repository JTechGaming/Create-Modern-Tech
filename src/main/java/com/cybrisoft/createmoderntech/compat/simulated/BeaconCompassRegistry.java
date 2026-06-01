package com.cybrisoft.createmoderntech.compat.simulated;

import com.cybrisoft.createmoderntech.registry.ModItems;
import dev.simulated_team.simulated.Simulated;

public class BeaconCompassRegistry {
    public static void init() {
        Simulated.getRegistrate().navTarget("beacon_compass", BeaconCompassNavigationTarget::new, () -> ModItems.BEACON_COMPASS);
    }
}
