package com.veteam.voluminousenergy.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static PrimitiveBlastFurnaceBlock PRIMITIVE_BLAST_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static TileEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE;

}
