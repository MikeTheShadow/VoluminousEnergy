package com.veteam.voluminousenergy.blocks.blocks.crops;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RiceCrop extends VEWaterCrop {

    public RiceCrop() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)); // TODO: Rice Properties
    }

    @Override
    public Item cropItem(){
        return VEItems.RICE_GRAIN.get();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random){
        if (random.nextFloat() <= Config.RICE_TICK_CHANCE.get()) {
            super.randomTick(state, worldIn, pos, random);
        }
    }

}
