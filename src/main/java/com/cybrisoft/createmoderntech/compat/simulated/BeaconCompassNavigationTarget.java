package com.cybrisoft.createmoderntech.compat.simulated;

import com.cybrisoft.createmoderntech.item.BeaconCompassData;
import com.cybrisoft.createmoderntech.registry.ModDataComponents;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeaconCompassNavigationTarget implements NavigationTarget {
    @Override
    public @Nullable Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        final BeaconCompassData data = self.getComponents().get(ModDataComponents.BEACON_TARGET.get());
        if (data != null) {
            final Vec3 pos = navBE.getProjectedSelfPos();
            return new Vec3(data.x(), pos.y(), data.z());
        }

        return null;
    }

    @Override
    public float getMaxRange() {
        return 0;
    }
}
