package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class VEItems
{
    public static PetCoke PETCOKE = new PetCoke(new Item.Properties().group(VESetup.itemGroup));
    public static CoalCoke COALCOKE = new CoalCoke(new Item.Properties().group(VESetup.itemGroup));
    public static SaltpeterChunk SALTPETERCHUNK = new SaltpeterChunk(new Item.Properties().group(VESetup.itemGroup));

    //Dusts
    public static CoalDust COALDUST = new CoalDust(new Item.Properties().group(VESetup.itemGroup));
    public static CokeDust COKEDUST = new CokeDust(new Item.Properties().group(VESetup.itemGroup));
    public static LapisDust LAPISDUST = new LapisDust(new Item.Properties().group(VESetup.itemGroup));
    public static SulfurDust SULFURDUST = new SulfurDust(new Item.Properties().group(VESetup.itemGroup));
    public static CarbonDust CARBONDUST = new CarbonDust(new Item.Properties().group(VESetup.itemGroup));
    public static SaltpeterDust SALTPETERDUST = new SaltpeterDust(new Item.Properties().group(VESetup.itemGroup));
}
