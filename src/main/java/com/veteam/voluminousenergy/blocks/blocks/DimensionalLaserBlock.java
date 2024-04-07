package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.blocks.blocks.machines.VEFaceableMachineBlock;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntities;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DimensionalLaserBlock extends VEFaceableMachineBlock {

    public DimensionalLaserBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(value -> 7)
                .noOcclusion()
        );
        VETagDataGenerator.setRequiresSolarium(this);
        VETagDataGenerator.setRequiresPickaxe(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return VETileEntities.DIMENSIONAL_LASER_FACTORY.create(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.DIMENSIONAL_LASER_TILE.get());
    }
}
