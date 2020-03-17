package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class PetCoke extends Item
{
    public PetCoke(Properties properties)
    {
        super(new Item.Properties()
        .maxStackSize(64)
        .group(VESetup.itemGroup));
        setRegistryName("petcoke");
    }
}
