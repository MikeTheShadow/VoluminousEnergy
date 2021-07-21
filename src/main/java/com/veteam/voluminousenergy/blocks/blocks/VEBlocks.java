package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
import com.veteam.voluminousenergy.blocks.blocks.crops.VELandCrop;
import com.veteam.voluminousenergy.blocks.blocks.storage.materials.*;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.blocks.storage.raw.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VEBlocks {

    // Shells
    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_shell")
    public static AluminumShellBlock ALUMINUM_SHELL;

    // Machine Frames
    @ObjectHolder(VoluminousEnergy.MODID + ":carbon_shielded_aluminum_machine_frame")
    public static CarbonShieldedAluminumMachineFrame CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME;

    // Casings (For multiblocks)
    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_machine_casing")
    public static AluminumMachineCasingBlock ALUMINUM_MACHINE_CASING_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":titanium_machine_casing")
    public static TitaniumMachineCasingBlock TITANIUM_MACHINE_CASING_BLOCK;

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

    // Distillation Unit
    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static DistillationUnitBlock DISTILLATION_UNIT_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static TileEntityType<DistillationUnitTile> DISTILLATION_UNIT_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static ContainerType<DistillationUnitContainer> DISTILLATION_UNIT_CONTAINER;

    // Pump
    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static PumpBlock PUMP_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static TileEntityType<PumpTile> PUMP_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static ContainerType<PumpContainer> PUMP_CONTAINER;

    // Gas Fired Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static GasFiredFurnaceBlock GAS_FIRED_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static TileEntityType<GasFiredFurnaceTile> GAS_FIRED_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static ContainerType<GasFiredFurnaceContainer> GAS_FIRED_FURNACE_CONTAINER;

    // Electric Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static ElectricFurnaceBlock ELECTRIC_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static TileEntityType<ElectricFurnaceTile> ELECTRIC_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static ContainerType<ElectricFurnaceContainer> ELECTRIC_FURNACE_CONTAINER;

    // Battery Box
    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static BatteryBoxBlock BATTERY_BOX_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static TileEntityType<BatteryBoxTile> BATTERY_BOX_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static ContainerType<BatteryBoxContainer> BATTERY_BOX_CONTAINER;

    // Primitive Solar Panel
    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static PrimitiveSolarPanelBlock PRIMITIVE_SOLAR_PANEL_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static TileEntityType<PrimitiveSolarPanelTile> PRIMITIVE_SOLAR_PANEL_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static ContainerType<PrimitiveSolarPanelContainer> PRIMITIVE_SOLAR_PANEL_CONTAINER;

    // Solar Panel
    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static SolarPanelBlock SOLAR_PANEL_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static TileEntityType<SolarPanelTile> SOLAR_PANEL_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static ContainerType<SolarPanelContainer> SOLAR_PANEL_CONTAINER;

    // Centrifugal Separator
    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static CentrifugalSeparatorBlock CENTRIFUGAL_SEPARATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static TileEntityType<CentrifugalSeparatorTile> CENTRIFUGAL_SEPARATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static ContainerType<CentrifugalSeparatorContainer> CENTRIFUGAL_SEPARATOR_CONTAINER;

    // Implosion Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static ImplosionCompressorBlock IMPLOSION_COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static TileEntityType<ImplosionCompressorTile> IMPLOSION_COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static ContainerType<ImplosionCompressorContainer> IMPLOSION_COMPRESSOR_CONTAINER;

    // Blast Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static BlastFurnaceBlock BLAST_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static TileEntityType<BlastFurnaceTile> BLAST_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static ContainerType<BlastFurnaceContainer> BLAST_FURNACE_CONTAINER;

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

    @ObjectHolder(VoluminousEnergy.MODID + ":eighzo_ore")
    public static EighzoOre EIGHZO_ORE;

    //Crops
    //@ObjectHolder(VoluminousEnergy.MODID + ":water_crop")
    //public static VEWaterCrop WATER_CROP;

    @ObjectHolder(VoluminousEnergy.MODID + ":land_crop")
    public static VELandCrop LAND_CROP;

    @ObjectHolder(VoluminousEnergy.MODID + ":rice_crop")
    public static RiceCrop RICE_CROP;

    // Material Storage Blocks
    @ObjectHolder(VoluminousEnergy.MODID + ":solarium_block")
    public static SolariumBlock SOLARIUM_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_block")
    public static AluminumBlock ALUMINUM_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":carbon_block")
    public static CarbonBlock CARBON_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":eighzo_block")
    public static EighzoBlock EIGHZO_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":nighalite_block")
    public static NighaliteBlock NIGHALITE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":saltpeter_block")
    public static SaltpeterBlock SALTPETER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":titanium_block")
    public static TitaniumBlock TITANIUM_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":tungsten_block")
    public static TungstenBlock TUNGSTEN_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":tungsten_steel_block")
    public static TungstenSteelBlock TUNGSTEN_STEEL_BLOCK;

    // Raw Material Storage Blocks
    @ObjectHolder(VoluminousEnergy.MODID + ":raw_bauxite_block")
    public static RawBauxiteBlock RAW_BAUXITE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":raw_cinnabar_block")
    public static RawCinnabarBlock RAW_CINNABAR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":raw_eighzo_block")
    public static RawEighzoBlock RAW_EIGHZO_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":raw_galena_block")
    public static RawGalenaBlock RAW_GALENA_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":raw_rutile_block")
    public static RawRutileBlock RAW_RUTILE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":raw_bone_block")
    public static RawBoneBlock RAW_BONE_BLOCK;
}
