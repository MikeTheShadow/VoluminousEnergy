package com.veteam.voluminousenergy.blocks.blocks.crops;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RiceCrop extends VEWaterCrop {

    public RiceCrop(Properties properties) {
        super(properties);
        setRegistryName("rice_crop");
    }

    @Override
    public Item cropItem(){
        return VEItems.RICE_GRAIN;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random){
        if (random.nextFloat() <= Config.RICE_TICK_CHANCE.get()) {
            super.randomTick(state, worldIn, pos, random);
        }
    }

}
