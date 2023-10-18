package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public abstract class VEMultiBlockTileEntity extends VEFluidTileEntity {

    public VEMultiBlockTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<?>> recipeType) {
        super(type, pos, state, recipeType);
    }

    public boolean validity = false;

    public boolean isMultiBlockValid() {

        int rawDirection = this.getBlockState().getValue(BlockStateProperties.FACING).get2DDataValue();

        int sXMultiplier = 1;
        int sZMultiplier = 1;

        int sX = (rawDirection == 1 ? 1 : -1) * sXMultiplier;
        int sZ = (rawDirection < 2 ? -1 : 1) * sZMultiplier;

        int lxMultiplier = (rawDirection == 3 ? -1 : 1);
        int lzMultiplier = (rawDirection == 0 || rawDirection == 3 ? -1 : 1);

        int lX = sX + (lxMultiplier * 2);
        int lZ = sZ + (lzMultiplier * 2);

        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos : BlockPos.betweenClosed(worldPosition.offset(sX, 0, sZ), worldPosition.offset(lX, 2, lZ))) {
            final BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() != getCasingBlock()) { // Fails MultiBlock condition
                return false;
            }
        }
        return true;
    }

    private byte tick = 19;

    @Override
    public void tick() {
        tick++;
        if (tick == 20) {
            tick = 0;
            validity = isMultiBlockValid();
        }
        if (!(validity)) {
            return;
        }
        super.tick();
    }

    @Override
    public void load(CompoundTag tag) {
        this.validity = tag.getBoolean("validity");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putBoolean("validity", this.validity);
        super.saveAdditional(tag);
    }

    public abstract Block getCasingBlock();

}
