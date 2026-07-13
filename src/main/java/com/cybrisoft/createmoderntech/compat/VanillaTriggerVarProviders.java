package com.cybrisoft.createmoderntech.compat;

import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.TriggerVarProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.*;

import java.util.HashMap;
import java.util.Map;

public class VanillaTriggerVarProviders implements TriggerVarProvider {
    @Override
    public void register() {
        TriggerVarProviderRegistry.register(ChestBlockEntity.class, this::registerContainerBE);
        TriggerVarProviderRegistry.register(BarrelBlockEntity.class, this::registerContainerBE);
        TriggerVarProviderRegistry.register(DropperBlockEntity.class, this::registerContainerBE);
        TriggerVarProviderRegistry.register(DispenserBlockEntity.class, this::registerContainerBE);
        TriggerVarProviderRegistry.register(HopperBlockEntity.class, this::registerContainerBE);
        TriggerVarProviderRegistry.register(FurnaceBlockEntity.class, this::registerContainerBE);
    }

    private String registerContainerBE(BaseContainerBlockEntity be) {
        Map<Item, Integer> items = new HashMap<>();
        for (int i=0; i < be.getContainerSize(); i++) {
            ItemStack stack = be.getItem(i);
            if (stack.is(Items.AIR)) continue;

            int count = stack.getCount();
            if (items.containsKey(stack.getItem())) {
                count += items.get(stack.getItem());
            }
            items.put(stack.getItem(), count);
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Item item : items.keySet()) {
            int count = items.get(item);
            result.append(count).append(" ").append(item.getDefaultInstance().getDisplayName().getString());
            if (i == items.size() - 2) { // second to last element
                result.append(" and ");
            }
            i++;
        }
        return result.toString().isBlank() ? "Empty" : result.toString();
    }
}
