package com.veteam.voluminousenergy.blocks.blocks.crops;

import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.item.Item;

public class RiceCrop extends VEWaterCrop {

    public RiceCrop(Properties properties) {
        super(properties);
        setRegistryName("rice_crop");
    }

    @Override
    public Item cropItem(){
        return VEItems.RICE_ITEM.getItem();
    }


}
