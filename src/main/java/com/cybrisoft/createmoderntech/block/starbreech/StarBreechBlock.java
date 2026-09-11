package com.cybrisoft.createmoderntech.block.starbreech;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StarBreechBlock extends Block implements IBE<StarBreechBlockEntity> {
    public StarBreechBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<StarBreechBlockEntity> getBlockEntityClass() {
        return StarBreechBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends StarBreechBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.STAR_BREECH.get();
    }
}
