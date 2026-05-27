package com.cybrisoft.createmoderntech.block.aicore;

import com.cybrisoft.createmoderntech.block.audiotrigger.AudioTriggerBlockEntity;
import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AICoreBlock  extends Block implements IBE<AICoreBlockEntity> {
    public AICoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<AICoreBlockEntity> getBlockEntityClass() {
        return AICoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AICoreBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.AI_CORE_BLOCK.get();
    }
}
