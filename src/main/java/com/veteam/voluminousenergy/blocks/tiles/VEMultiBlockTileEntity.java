package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VEMultiBlockTileEntity extends VEFluidTileEntity {


    public VEMultiBlockTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public boolean isMultiBlockValid(Block block){

        // Get Direction
        String direction = getDirection();
        // Setup range to check based on direction
        byte sX, sY, sZ, lX, lY, lZ;

        if (direction == null || direction.equals("null")){
            return false;
        } else if (direction.equals("north")){
            sX = -1;
            sY = 0;
            sZ = 1;
            lX = 1;
            lY = 2;
            lZ = 3;
        } else if (direction.equals("south")){
            sX = -1;
            sY = 0;
            sZ = -1;
            lX = 1;
            lY = 2;
            lZ = -3;
        } else if (direction.equals("east")){
            sX = -1;
            sY = 0;
            sZ = 1;
            lX = -3;
            lY = 2;
            lZ = -1;
        } else if (direction.equals("west")){
            sX = 1;
            sY = 0;
            sZ = -1;
            lX = 3;
            lY = 2;
            lZ = 1;
        } else { // Invalid Direction
            return false;
        }

        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos :  BlockPos.betweenClosed(worldPosition.offset(sX,sY,sZ),worldPosition.offset(lX,lY,lZ))){
            final BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() != block){ // Fails multiblock condition
                return false;
            }
        }
        return true;
    }

}
