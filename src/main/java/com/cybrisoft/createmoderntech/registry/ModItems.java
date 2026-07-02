package com.cybrisoft.createmoderntech.registry;

import com.cybrisoft.createmoderntech.CreateModernTech;
import com.cybrisoft.createmoderntech.item.AdjustableSpyglassItem;
import com.cybrisoft.createmoderntech.item.BeaconCompassItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.cybrisoft.createmoderntech.CreateModernTech.REGISTRATE;

public class ModItems {
    public static final ItemEntry<BeaconCompassItem> BEACON_COMPASS =
            REGISTRATE.item("beacon_compass", BeaconCompassItem::new)
                    .register();

    public static final ItemEntry<Item> AI_PROCESSOR =
            REGISTRATE.item("ai_processor", Item::new)
                    .register();
    public static final ItemEntry<Item> LENS_ELEMENT =
            REGISTRATE.item("lens_element", Item::new)
                    .register();
    public static final ItemEntry<Item> OPTICAL_DRIVE =
            REGISTRATE.item("optical_drive", Item::new)
                    .register();
    public static final ItemEntry<Item> DIAPHRAGM =
            REGISTRATE.item("diaphragm", Item::new)
                    .register();
    public static final ItemEntry<Item> MEMBRANE =
            REGISTRATE.item("membrane", Item::new)
                    .register();
    public static final ItemEntry<AdjustableSpyglassItem> ADJUSTABLE_SPYGLASS =
            REGISTRATE.item("adjustable_spyglass", AdjustableSpyglassItem::new)
                    .register();

    public static void register() {
        CreateModernTech.getLogger().info("Registering Items!");
    }
}
