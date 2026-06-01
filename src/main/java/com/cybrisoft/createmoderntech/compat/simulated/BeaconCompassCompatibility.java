package com.cybrisoft.createmoderntech.compat.simulated;

import dev.simulated_team.simulated.service.SimModCompatibilityService;

public class BeaconCompassCompatibility implements SimModCompatibilityService {
    @Override
    public void init() {
        BeaconCompassRegistry.init();
    }

    @Override
    public String getModId() {
        return "createmoderntech";
    }
}
