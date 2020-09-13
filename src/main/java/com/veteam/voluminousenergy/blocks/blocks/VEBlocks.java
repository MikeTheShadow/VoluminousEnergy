package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    // Shells
    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_shell")
    public static AluminumShellBlock ALUMINUM_SHELL;

    // Machine Frames
    @ObjectHolder(VoluminousEnergy.MODID + ":carbon_shielded_aluminum_machine_frame")
    public static CarbonShieldedAluminumMachineFrame CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME;

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

    // Stirling Generator
    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static StirlingGeneratorBlock STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static TileEntityType<StirlingGeneratorTile> STIRLING_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static ContainerType<StirlingGeneratorContainer> STIRLING_GENERATOR_CONTAINER;

    // Combustion Generator
    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static CombustionGeneratorBlock COMBUSTION_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static TileEntityType<CombustionGeneratorTile> COMBUSTION_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static ContainerType<CombustionGeneratorContainer> COMBUSTION_GENERATOR_CONTAINER;

    // Aqueoulizer
    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static AqueoulizerBlock AQUEOULIZER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static TileEntityType<AqueoulizerTile> AQUEOULIZER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static ContainerType<AqueoulizerContainer> AQUEOULIZER_CONTAINER;

    // Air Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static AirCompressorBlock AIR_COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static TileEntityType<AirCompressorTile> AIR_COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static ContainerType<AirCompressorContainer> AIR_COMPRESSOR_CONTAINER;

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
