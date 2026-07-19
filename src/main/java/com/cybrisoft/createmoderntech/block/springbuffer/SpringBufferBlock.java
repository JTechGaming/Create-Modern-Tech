package com.cybrisoft.createmoderntech.block.springbuffer;

import com.cybrisoft.createmoderntech.registry.ModBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpringBufferBlock extends DirectionalKineticBlock implements IBE<SpringBufferBlockEntity> {
    public SpringBufferBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING) || face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Class<SpringBufferBlockEntity> getBlockEntityClass() {
        return SpringBufferBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpringBufferBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SPRING_BUFFER.get();
    }
}
