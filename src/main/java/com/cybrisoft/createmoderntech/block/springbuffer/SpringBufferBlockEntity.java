package com.cybrisoft.createmoderntech.block.springbuffer;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;

public class SpringBufferBlockEntity extends GeneratingKineticBlockEntity {
    private float stored = 0f;
    private boolean unwinding = false;
    private static final float WINDING_CONSUMPTION = 8f;
    private static final float UNWIND_RATE = 10f;
    private static final float MAX_STORED = 100000f;

    public SpringBufferBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (level != null && !level.isClientSide()) {
            updateGeneratedRotation();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;

        float rawSpeed = getSpeed();
        if (Math.abs(rawSpeed) < 0.001f) return;

        boolean powered = level.hasNeighborSignal(worldPosition);

        boolean newUnwinding;
        if (!unwinding) {
            newUnwinding = powered && stored > 0f;
        } else {
            newUnwinding = powered;
        }
        if (newUnwinding != unwinding) {
            unwinding = newUnwinding;
            updateGeneratedRotation();
            sendData();
        }

        if (unwinding) {
            stored = Math.max(0f, stored - Math.abs(rawSpeed) * UNWIND_RATE);

            KineticNetwork network = getOrCreateNetwork();
            if (network != null) {
                if (network.calculateStress() > network.calculateCapacity()) {
                    network.updateCapacity();
                }
            }

        } else {
            KineticNetwork network = getOrCreateNetwork();
            if (network != null) {
                float surplus = network.calculateCapacity() - network.calculateStress();
                float windRate = Math.min(surplus, Math.abs(rawSpeed) * 0.1f);
                stored = Math.min(MAX_STORED, stored + windRate);
            }
        }

        stored = Math.max(0f, stored);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        sendData();
    }

    @Override
    public float getGeneratedSpeed() {
        float rawSpeed = getSpeed();
        if (unwinding) return rawSpeed;
        return 0f;
    }

    @Override
    public float calculateStressApplied() {
        if (unwinding) return super.calculateStressApplied();
        return WINDING_CONSUMPTION;
    }

    @Override
    public float calculateAddedStressCapacity() {
        if (unwinding && stored > UNWIND_RATE) return UNWIND_RATE;
        return 0f;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.number(stored)
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.temporaryText("stored")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        CreateLang.number(UNWIND_RATE * getSpeed())
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_speed")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        return true;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("stored", stored);
        compound.putBoolean("unwinding", unwinding);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        stored = compound.getFloat("stored");
        unwinding = compound.getBoolean("unwinding");
    }
}
