package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.item.BeaconCompassItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;

public class ModItems {
    public static final ItemEntry<BeaconCompassItem> BEACON_COMPASS =
            REGISTRATE.item("beacon_compass", BeaconCompassItem::new)
                    .register();

    public static final ItemEntry<Item> AI_CORE =
            REGISTRATE.item("ai_core", Item::new)
                    .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering Items!");
    }
}
