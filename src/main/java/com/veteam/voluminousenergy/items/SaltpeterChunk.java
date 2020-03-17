package com.veteam.voluminousenergy.items;
import net.minecraft.item.Item;
import com.veteam.voluminousenergy.setup.VESetup;

public class SaltpeterChunk extends Item {
    public SaltpeterChunk (Properties properties){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("saltpeterchunk");
    }
}
