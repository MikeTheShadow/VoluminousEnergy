package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.blocks.VEBlocks;
import javafx.scene.paint.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class VESetup {

    public static ItemGroup itemGroup = new ItemGroup("voluminousenergy"){
        @Override
        public ItemStack createIcon(){
            return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK);
        }
    };

    public void init(){

    }

}
