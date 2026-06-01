package com.cybrisoft.createmoderntech.block.gauge;

import com.cybrisoft.createmoderntech.registry.ModBlocks;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RegionalStressGaugeBlockEntity extends GeneratingKineticBlockEntity {
    public float pairedSpeed = 0;
    public float pairedCapacity = 0;
    public float pairedDemand = 0;
    public float consumerDemandPerRpm = 0;

    private int tickCounter = 0;
    private boolean initializingSupplier = false;

    public RegionalStressGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float calculateAddedStressCapacity() {
        if (isSupplier()) return 0;
        if (Math.abs(pairedSpeed) < 0.01f) return 0;
        return pairedCapacity / Math.abs(pairedSpeed);
    }

    @Override
    public float calculateStressApplied() {
        if (!isSupplier()) return super.calculateStressApplied();
        if (initializingSupplier) return 0;
        if (Math.abs(speed) < 0.01f) return 0;
        lastStressApplied = consumerDemandPerRpm;
        return consumerDemandPerRpm;
    }

    @Override
    public float getGeneratedSpeed() {
        if (isSupplier()) return 0;
        return pairedSpeed;
    }

    @Override
    public void initialize() {
        if (level == null || level.isClientSide()) {
            super.initialize();
            return;
        }

        if (isSupplier()) {
            initializingSupplier = true;
            super.initialize();
            initializingSupplier = false;
            consumerDemandPerRpm = 0;
            lastStressApplied = 0;
        } else {
            super.initialize();
            pairedSpeed = 0;
            pairedCapacity = 0;
            pairedDemand = 0;
            updateGeneratedRotation();
        }
        tickCounter = -20;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;

        if (++tickCounter < 5) return;
        tickCounter = 0;

        RegionalStressGaugeBlockEntity paired = getPaired();
        if (paired == null) {
            if (isSupplier()) {
                consumerDemandPerRpm = 0;
                lastStressApplied = 0;
                KineticNetwork net = getOrCreateNetwork();
                if (net != null) {
                    net.updateStressFor(this, 0);
                    net.updateStress();
                }
            } else {
                pairedSpeed = 0;
                pairedCapacity = 0;
                pairedDemand = 0;
                updateGeneratedRotation();
            }
            return;
        }

        if (isSupplier()) {
            KineticNetwork net = getOrCreateNetwork();
            if (net == null) {
                paired.pairedSpeed = 0;
                paired.pairedCapacity = 0;
                paired.pairedDemand = 0;
                paired.updateGeneratedRotation();
                return;
            }

            paired.pairedSpeed = this.speed;
            paired.pairedCapacity = net.calculateCapacity();
            paired.pairedDemand = net.calculateStress();
            paired.updateGeneratedRotation();
        } else {
            KineticNetwork net = getOrCreateNetwork();
            if (net == null) return;

            float demand = net.calculateStress();
            if (Math.abs(paired.speed) < 0.01f) return;

            paired.consumerDemandPerRpm = demand / Math.abs(paired.speed);
            paired.lastStressApplied = paired.consumerDemandPerRpm;
            KineticNetwork pairedNet = paired.getOrCreateNetwork();
            if (pairedNet != null) {
                pairedNet.updateStressFor(paired, paired.consumerDemandPerRpm);
                pairedNet.updateStress();
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("gui.goggles.generator_stats").forGoggles(tooltip);
        CreateLang.translate("tooltip.capacityProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CreateLang.number(consumerDemandPerRpm * getTheoreticalSpeed())
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_speed")
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }

    private boolean isSupplier() {
        return getBlockState().getValue(RegionalStressGaugeBlock.SUPPLIER);
    }

    private RegionalStressGaugeBlockEntity getPaired() {
        Direction facing = getBlockState().getValue(RegionalStressGaugeBlock.FACING);
        BlockPos pairedPos = worldPosition.relative(facing.getOpposite());
        BlockState pairedState = level.getBlockState(pairedPos);
        if (!pairedState.is(ModBlocks.REGIONAL_STRESS_GAUGE_BLOCK.get())) return null;
        if (pairedState.getValue(RegionalStressGaugeBlock.FACING) != facing.getOpposite()) return null;
        if (pairedState.getValue(RegionalStressGaugeBlock.SUPPLIER) == isSupplier()) return null;
        return level.getBlockEntity(pairedPos) instanceof RegionalStressGaugeBlockEntity be ? be : null;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("PairedSpeed", pairedSpeed);
        tag.putFloat("PairedCapacity", pairedCapacity);
        tag.putFloat("PairedDemand", pairedDemand);
        tag.putFloat("ConsumerDemandPerRpm", consumerDemandPerRpm);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        pairedSpeed = tag.getFloat("PairedSpeed");
        pairedCapacity = tag.getFloat("PairedCapacity");
        pairedDemand = tag.getFloat("PairedDemand");
        consumerDemandPerRpm = tag.getFloat("ConsumerDemandPerRpm");
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
}