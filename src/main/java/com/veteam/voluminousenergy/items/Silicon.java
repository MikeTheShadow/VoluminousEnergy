package com.veteam.voluminousenergy.items;
import net.minecraft.item.Item;
import com.veteam.voluminousenergy.setup.VESetup;

public class Silicon extends Item {
    public Silicon (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("silicon");
    }
}
