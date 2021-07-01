package com.veteam.voluminousenergy.items.crops;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.crops.VEWaterCrop;

public class RiceItem extends WaterCropItem {

    public RiceItem(Properties properties) {
        super(VEBlocks.RICE_CROP.getBlock(), properties);
        setRegistryName("rice_grain");
    }

    @Override
    public VEWaterCrop getWaterCrop(){
        return (VEWaterCrop) VEBlocks.RICE_CROP.getBlock();
    }

}
