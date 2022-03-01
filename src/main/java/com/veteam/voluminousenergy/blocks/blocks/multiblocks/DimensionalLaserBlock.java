package com.veteam.voluminousenergy.blocks.blocks.multiblocks;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.DimensionalLaserTile;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DimensionalLaserBlock extends BeaconBlock {

    public DimensionalLaserBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(value -> 7)
                .noOcclusion()
        );
        setRegistryName("dimensional_laser");
        VETagDataGenerator.setRequiresIron(this);
        VETagDataGenerator.setRequiresPickaxe(this);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends DimensionalLaserTile> tile) {
        return level.isClientSide ? null : createTickerHelperT(passedBlockEntity, tile, DimensionalLaserTile::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new DimensionalLaserTile(blockPos,blockState);
    }

    @Override
    public @Nonnull DyeColor getColor() {
        return DyeColor.PURPLE;
    }


    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelperT(BlockEntityType<T> blockEntityType, BlockEntityType<? extends DimensionalLaserTile> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.DIMENSIONAL_LASER_TILE);
    }

}
