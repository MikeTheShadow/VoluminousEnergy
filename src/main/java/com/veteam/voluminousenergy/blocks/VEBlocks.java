package com.veteam.voluminousenergy.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    //Primitive Blast
    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static PrimitiveBlastFurnaceBlock PRIMITIVE_BLAST_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static TileEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE;

    //Primitive Stirling
    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static PrimitiveStirlingGeneratorBlock PRIMITIVE_STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static TileEntityType<PrimitiveStirlingGeneratorTile> PRIMITIVE_STIRLING_GENERATOR_TILE;

    //Ores
    @ObjectHolder(VoluminousEnergy.MODID  + ":saltpeterore")
    public static SaltpeterOre SALTPETER_ORE;
}
