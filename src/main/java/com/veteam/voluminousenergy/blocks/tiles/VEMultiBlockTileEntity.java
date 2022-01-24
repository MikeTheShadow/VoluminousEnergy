package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class VEMultiBlockTileEntity extends VEFluidTileEntity {


    public VEMultiBlockTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isMultiBlockValid(Block block) {


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

            if (blockState.getBlock() != block) { // Fails MultiBlock condition
                return false;
            }
        }
        return true;
    }

}
