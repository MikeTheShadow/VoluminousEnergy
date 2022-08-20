package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.fluids.VEFluids;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class VESetup {

    public static CreativeModeTab itemGroup = new CreativeModeTab("voluminousenergy"){
        @Override
        public ItemStack makeIcon(){ return new ItemStack(VEFluids.RFNA_BUCKET_REG.get()); }
    };

    public void init(){}

}
