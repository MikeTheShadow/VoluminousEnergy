package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.RegistryObject;

public class MultiBlockRecipeProcessor extends DefaultProcessor {

    private RegistryObject<? extends Block> block;

    public MultiBlockRecipeProcessor(RegistryObject<? extends Block> block) {
        this.block = block;
    }

    public boolean isMultiBlockValid(VETileEntity tile) {
        int rawDirection = tile.getBlockState().getValue(BlockStateProperties.FACING).get2DDataValue();

        int sXMultiplier = 1;
        int sZMultiplier = 1;

        int sX = (rawDirection == 1 ? 1 : -1) * sXMultiplier;
        int sZ = (rawDirection < 2 ? -1 : 1) * sZMultiplier;

        int lxMultiplier = (rawDirection == 3 ? -1 : 1);
        int lzMultiplier = (rawDirection == 0 || rawDirection == 3 ? -1 : 1);

        int lX = sX + (lxMultiplier * 2);
        int lZ = sZ + (lzMultiplier * 2);

        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos : BlockPos.betweenClosed(tile.getBlockPos().offset(sX, 0, sZ), tile.getBlockPos().offset(lX, 2, lZ))) {
            final BlockState blockState = tile.getLevel().getBlockState(blockPos);

            if (blockState.getBlock() != block.get()) { // Fails MultiBlock condition
                return false;
            }
        }
        return true;
    }

}
