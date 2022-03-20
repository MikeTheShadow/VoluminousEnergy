package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class RFIDChip extends Item {

    public RFIDChip (){
        super(new Item.Properties()
                .stacksTo(16)
                .tab(VESetup.itemGroup)
                .rarity(Rarity.create("ELECTRONIC", ChatFormatting.GREEN))
        );
        setRegistryName("rfid_chip");
    }

}
