package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class VESetup {

    public static CreativeModeTab itemGroup = new CreativeModeTab("voluminousenergy"){
        @Override
        public ItemStack makeIcon(){ return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK); }
    };

    public void init(){}

}
