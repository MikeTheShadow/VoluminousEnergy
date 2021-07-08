package com.veteam.voluminousenergy.blocks.blocks.crops;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class RiceCrop extends VEWaterCrop {

    public RiceCrop(Properties properties) {
        super(properties);
        setRegistryName("rice_crop");
    }

    @Override
    public Item cropItem(){
        return VEItems.RICE_GRAIN.getItem();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random){
        if (random.nextFloat() <= Config.RICE_TICK_CHANCE.get()) {
            super.randomTick(state, worldIn, pos, random);
        }
    }

}
