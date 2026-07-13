package com.cybrisoft.createmoderntech.compat.create;

import com.cybrisoft.createmoderntech.registry.TriggerVarProviderRegistry;
import com.cybrisoft.createmoderntech.util.TriggerVarProvider;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class CreateTriggerVarProviders implements TriggerVarProvider {
    @Override
    public void register() {
        TriggerVarProviderRegistry.register(NixieTubeBlockEntity.class, be -> {
            return "\"" + be.getFullText().getString() + "\"";
        });
        TriggerVarProviderRegistry.register(SpeedGaugeBlockEntity.class, be -> {
            return IRotate.SpeedLevel.of(be.getSpeed()).toString();
        });
        TriggerVarProviderRegistry.register(StressGaugeBlockEntity.class, be -> {
            double stressFraction = be.getNetworkStress() / (be.getNetworkCapacity() == 0 ? 1 : be.getNetworkCapacity());
            return (stressFraction * 100) + "%";
        });
        TriggerVarProviderRegistry.register(FluidTankBlockEntity.class, be -> {
            FluidStack fluidStack = be.getFluid(0); // tank index does nothing here
            return fluidStack.getAmount() + " milli buckets of " + CreateLang.fluidName(fluidStack).string();
        });
        TriggerVarProviderRegistry.register(DepotBlockEntity.class, be -> {
            return be.getHeldItem().toString();
        });
        TriggerVarProviderRegistry.register(ItemVaultBlockEntity.class, be -> {
            ItemStackHandler inv = be.getInventoryOfBlock();
            Map<Item, Integer> items = new HashMap<>();
            for (int i=0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
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
        });
        TriggerVarProviderRegistry.register(FlapDisplayBlockEntity.class, be -> {
            StringBuilder result = new StringBuilder();
            for (FlapDisplayLayout layout : be.lines) {
                for (FlapDisplaySection section : layout.getSections()) {
                    result.append(section.getText().getString()).append("\n");
                }
            }
            return result.toString().isBlank() ? "Empty" : result.toString();
        });
        TriggerVarProviderRegistry.register(CuckooClockBlockEntity.class, be -> {
            Level level = be.getLevel();
            if (level == null) return "00:00";
            boolean isNatural = level.dimensionType().natural();
            int dayTime = (int) ((level.getDayTime() * (isNatural ? 1 : 24)) % 24000);
            int hours = (dayTime / 1000 + 6) % 24;
            int minutes = (dayTime % 1000) * 60 / 1000;
            return hours + ":" + minutes;
        });
    }
}
