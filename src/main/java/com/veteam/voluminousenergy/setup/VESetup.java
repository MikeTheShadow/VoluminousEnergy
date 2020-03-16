package com.veteam.voluminousenergy.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class VESetup {

    public static ItemGroup itemGroup = new ItemGroup("voluminousenergy"){
        @Override
        public ItemStack createIcon(){
            return null; //TODO: Make a proper icon for the Voluminous Energy item group
        }
    };

    public void init(){

    }

}
