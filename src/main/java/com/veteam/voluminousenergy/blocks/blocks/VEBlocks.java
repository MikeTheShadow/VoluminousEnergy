package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    // Shells
    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_shell")
    public static AluminumShellBlock ALUMINUM_SHELL;

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

    //Electrolyzer
    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static ElectrolyzerBlock ELECTROLYZER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static TileEntityType<ElectrolyzerTile> ELECTROLYZER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static ContainerType<ElectrolyzerContainer> ELECTROLYZER_CONTAINER;

    // Centrifugal Agitator
    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static CentrifugalAgitatorBlock CENTRIFUGAL_AGITATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static TileEntityType<CentrifugalAgitatorTile> CENTRIFUGAL_AGITATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static ContainerType<CentrifugalAgitatorContainer> CENTRIFUGAL_AGITATOR_CONTAINER;

    // Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static CompressorBlock COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static TileEntityType<CompressorTile> COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static ContainerType<CompressorContainer> COMPRESSOR_CONTAINER;

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
