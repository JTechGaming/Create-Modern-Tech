package com.cybrisoft.createmoderntech.block.volumetric.controller.beacon;

import com.cybrisoft.createmoderntech.block.volumetric.controller.VolumetricControllerBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.shaft.VolumetricShaftBlockEntity;
import com.cybrisoft.createmoderntech.block.volumetric.display.VolumetricDisplayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class BeaconControllerBlockEntity extends VolumetricControllerBlockEntity {
    // Output slot for compass items
    public final ItemStackHandler outputInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return false; } // output only
    };

    // Assigned colors for beacons in order
    private static final int[] BEACON_COLORS = {
            0xFF4444, // red
            0x44FF44, // green
            0x4444FF, // blue
            0xFFFF44, // yellow
            0xFF44FF, // magenta
            0x44FFFF, // cyan
            0xFF8844, // orange
            0xFFFFFF, // white
    };

    private int nextColorIndex = 0;

    public BeaconControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public VolumetricShaftBlockEntity.ControllerType getControllerType() {
        // skip the standard float accumulation in tick()
        return null;
    }

    @Override
    public void tick() {
        // don't use the standard float accumulation
        if (level == null || level.isClientSide()) return;
    }

    /**
     * set or clear beacon at cursor position.
     */
    public void onSetClearTriggered() {
        VolumetricDisplayBlockEntity display = getLinkedDisplay();
        if (display == null) return;

        float cursorX = display.getBlockPos().getX() + display.panX;
        float cursorZ = display.getBlockPos().getZ() + display.panZ;

        // Check if cursor overlaps an existing beacon (within 8 blocks)
        VolumetricDisplayBlockEntity.BeaconData overlapping = null;
        for (VolumetricDisplayBlockEntity.BeaconData beacon : display.beacons) {
            float dx = beacon.x - cursorX;
            float dz = beacon.z - cursorZ;
            if (dx * dx + dz * dz < 64f) { // 8 block radius
                overlapping = beacon;
                break;
            }
        }

        if (overlapping != null) {
            // Clear the overlapping beacon
            display.beacons.remove(overlapping);
            display.notifyUpdate();
            // Emit redstone pulse on the shaft side (signal that a beacon was cleared)
            // handled by block state or separate mechanism if needed
        } else {
            // Add a new beacon at cursor position
            int color = BEACON_COLORS[nextColorIndex % BEACON_COLORS.length];
            nextColorIndex++;
            display.beacons.add(new VolumetricDisplayBlockEntity.BeaconData(cursorX, cursorZ, color));
            display.notifyUpdate();
        }
    }

    /**
     * output compass for selected beacon.
     * beacon closest to cursor within selection radius.
     */
    public void onOutputCompassTriggered() {
        VolumetricDisplayBlockEntity display = getLinkedDisplay();
        if (display == null) return;

        float cursorX = display.getBlockPos().getX() + display.smoothPanX;
        float cursorZ = display.getBlockPos().getZ() + display.smoothPanZ;

        // Find nearest beacon to cursor
        VolumetricDisplayBlockEntity.BeaconData selected = null;
        float bestDist = Float.MAX_VALUE;
        for (VolumetricDisplayBlockEntity.BeaconData beacon : display.beacons) {
            float dx = beacon.x - cursorX;
            float dz = beacon.z - cursorZ;
            float dist = dx * dx + dz * dz;
            if (dist < bestDist) {
                bestDist = dist;
                selected = beacon;
            }
        }

        if (selected == null) return;

        // Create a compass with beacon coordinates stored as NBT
        ItemStack compass = new ItemStack(Items.COMPASS);
        CompoundTag tag = new CompoundTag();
        tag.putFloat("BeaconX", selected.x);
        tag.putFloat("BeaconZ", selected.z);
        tag.putInt("BeaconColor", selected.color);
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        // Place in output slot if empty
        if (outputInventory.getStackInSlot(0).isEmpty()) {
            outputInventory.setStackInSlot(0, compass);
            setChanged();
        }
    }

    /**
     * Walks up the shaft to find the linked display block entity.
     */
    private VolumetricDisplayBlockEntity getLinkedDisplay() {
        if (shaftPos == null || level == null) return null;
        if (level.getBlockEntity(shaftPos) instanceof VolumetricShaftBlockEntity shaft) {
            if (shaft.cachedDisplayPos != null &&
                    level.getBlockEntity(shaft.cachedDisplayPos) instanceof VolumetricDisplayBlockEntity display) {
                return display;
            }
        }
        return null;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("NextColorIndex", nextColorIndex);
        tag.put("OutputInventory", outputInventory.serializeNBT(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        nextColorIndex = tag.getInt("NextColorIndex");
        if (tag.contains("OutputInventory")) {
            outputInventory.deserializeNBT(registries, tag.getCompound("OutputInventory"));
        }
    }
}