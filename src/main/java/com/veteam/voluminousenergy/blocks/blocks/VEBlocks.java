package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    //Primitive Blast
    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static PrimitiveBlastFurnaceBlock PRIMITIVE_BLAST_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitiveblastfurnace")
    public static TileEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID  + ":primitiveblastfurnace")
    public static ContainerType<PrimitiveBlastFurnaceContainer> PRIMITIVE_BLAST_FURNACE_CONTAINER;

    //Primitive Stirling
    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static PrimitiveStirlingGeneratorBlock PRIMITIVE_STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static TileEntityType<PrimitiveStirlingGeneratorTile> PRIMITIVE_STIRLING_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static ContainerType<PrimitiveStirlingGeneratorContainer> PRIMITIVE_STIRLING_GENERATOR_CONTAINER;

    //Crusher
    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static CrusherBlock CRUSHER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static TileEntityType<CrusherTile> CRUSHER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static ContainerType<CrusherContainer> CRUSHER_CONTAINER;

    //Ores
    @ObjectHolder(VoluminousEnergy.MODID  + ":saltpeterore")
    public static SaltpeterOre SALTPETER_ORE;

    @ObjectHolder(VoluminousEnergy.MODID  + ":bauxiteore")
    public static BauxiteOre BAUXITE_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":cinnabarore")
    public static CinnabarOre CINNABAR_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":rutileore")
    public static RutileOre RUTILE_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":galena_ore")
    public static GalenaOre GALENA_ORE;

}
