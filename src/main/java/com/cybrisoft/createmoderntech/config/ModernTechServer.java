package com.cybrisoft.createmoderntech.config;

import net.createmod.catnip.config.ConfigBase;

public class ModernTechServer extends ConfigBase {
    public final ModernTechStress stressValues = nested(0, ModernTechStress::new, "Stress values");

    @Override
    public String getName() { return "server"; }
}
