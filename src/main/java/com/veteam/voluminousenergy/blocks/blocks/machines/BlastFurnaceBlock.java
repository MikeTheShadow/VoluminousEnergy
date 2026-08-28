package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntities;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class BlastFurnaceBlock extends VEFaceableMachineBlock {

    public BlastFurnaceBlock() {
        super(Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(3.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );
        setRName("blast_furnace");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresDiamondAndBlacklistLowerTiers(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { // Replaces old createBlockEntity method
        return VETileEntities.BLAST_FURNACE_FACTORY.create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.BLAST_FURNACE.tile().get());
    }
}
