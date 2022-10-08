package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
import com.veteam.voluminousenergy.blocks.blocks.crops.VELandCrop;
import com.veteam.voluminousenergy.blocks.blocks.machines.*;
import com.veteam.voluminousenergy.blocks.blocks.machines.tanks.*;
import com.veteam.voluminousenergy.blocks.blocks.multiblocks.DimensionalLaserBlock;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateBauxiteOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateCinnabarOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateGalenaOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateRutileOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.red_sand.RedSaltpeterOre;
import com.veteam.voluminousenergy.blocks.blocks.storage.materials.*;
import com.veteam.voluminousenergy.blocks.blocks.storage.raw.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.containers.tank.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import com.veteam.voluminousenergy.blocks.tiles.tank.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    public static BlockEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID  + ":primitiveblastfurnace")
    public static MenuType<PrimitiveBlastFurnaceContainer> PRIMITIVE_BLAST_FURNACE_CONTAINER;

    //Primitive Stirling
    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static PrimitiveStirlingGeneratorBlock PRIMITIVE_STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static BlockEntityType<PrimitiveStirlingGeneratorTile> PRIMITIVE_STIRLING_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitivestirlinggenerator")
    public static MenuType<PrimitiveStirlingGeneratorContainer> PRIMITIVE_STIRLING_GENERATOR_CONTAINER;

    //Crusher
    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static CrusherBlock CRUSHER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static BlockEntityType<CrusherTile> CRUSHER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":crusher")
    public static MenuType<CrusherContainer> CRUSHER_CONTAINER;

    //Electrolyzer
    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static ElectrolyzerBlock ELECTROLYZER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static BlockEntityType<ElectrolyzerTile> ELECTROLYZER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":electrolyzer")
    public static MenuType<ElectrolyzerContainer> ELECTROLYZER_CONTAINER;

    // Centrifugal Agitator
    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static CentrifugalAgitatorBlock CENTRIFUGAL_AGITATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static BlockEntityType<CentrifugalAgitatorTile> CENTRIFUGAL_AGITATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_agitator")
    public static MenuType<CentrifugalAgitatorContainer> CENTRIFUGAL_AGITATOR_CONTAINER;

    // Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static CompressorBlock COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static BlockEntityType<CompressorTile> COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":compressor")
    public static MenuType<CompressorContainer> COMPRESSOR_CONTAINER;

    // Stirling Generator
    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static StirlingGeneratorBlock STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static BlockEntityType<StirlingGeneratorTile> STIRLING_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":stirling_generator")
    public static MenuType<StirlingGeneratorContainer> STIRLING_GENERATOR_CONTAINER;

    // Combustion Generator
    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static CombustionGeneratorBlock COMBUSTION_GENERATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static BlockEntityType<CombustionGeneratorTile> COMBUSTION_GENERATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":combustion_generator")
    public static MenuType<CombustionGeneratorContainer> COMBUSTION_GENERATOR_CONTAINER;

    // Aqueoulizer
    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static AqueoulizerBlock AQUEOULIZER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static BlockEntityType<AqueoulizerTile> AQUEOULIZER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":aqueoulizer")
    public static MenuType<AqueoulizerContainer> AQUEOULIZER_CONTAINER;

    // Air Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static AirCompressorBlock AIR_COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static BlockEntityType<AirCompressorTile> AIR_COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":air_compressor")
    public static MenuType<AirCompressorContainer> AIR_COMPRESSOR_CONTAINER;

    // Distillation Unit
    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static DistillationUnitBlock DISTILLATION_UNIT_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static BlockEntityType<DistillationUnitTile> DISTILLATION_UNIT_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":distillation_unit")
    public static MenuType<DistillationUnitContainer> DISTILLATION_UNIT_CONTAINER;

    // Pump
    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static PumpBlock PUMP_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static BlockEntityType<PumpTile> PUMP_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":pump")
    public static MenuType<PumpContainer> PUMP_CONTAINER;

    // Gas Fired Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static GasFiredFurnaceBlock GAS_FIRED_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static BlockEntityType<GasFiredFurnaceTile> GAS_FIRED_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":gas_fired_furnace")
    public static MenuType<GasFiredFurnaceContainer> GAS_FIRED_FURNACE_CONTAINER;

    // Electric Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static ElectricFurnaceBlock ELECTRIC_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static BlockEntityType<ElectricFurnaceTile> ELECTRIC_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":electric_furnace")
    public static MenuType<ElectricFurnaceContainer> ELECTRIC_FURNACE_CONTAINER;

    // Battery Box
    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static BatteryBoxBlock BATTERY_BOX_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static BlockEntityType<BatteryBoxTile> BATTERY_BOX_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":battery_box")
    public static MenuType<BatteryBoxContainer> BATTERY_BOX_CONTAINER;

    // Primitive Solar Panel
    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static PrimitiveSolarPanelBlock PRIMITIVE_SOLAR_PANEL_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static BlockEntityType<PrimitiveSolarPanelTile> PRIMITIVE_SOLAR_PANEL_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":primitive_solar_panel")
    public static MenuType<PrimitiveSolarPanelContainer> PRIMITIVE_SOLAR_PANEL_CONTAINER;

    // Solar Panel
    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static SolarPanelBlock SOLAR_PANEL_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static BlockEntityType<SolarPanelTile> SOLAR_PANEL_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":solar_panel")
    public static MenuType<SolarPanelContainer> SOLAR_PANEL_CONTAINER;

    // Centrifugal Separator
    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static CentrifugalSeparatorBlock CENTRIFUGAL_SEPARATOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static BlockEntityType<CentrifugalSeparatorTile> CENTRIFUGAL_SEPARATOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":centrifugal_separator")
    public static MenuType<CentrifugalSeparatorContainer> CENTRIFUGAL_SEPARATOR_CONTAINER;

    // Implosion Compressor
    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static ImplosionCompressorBlock IMPLOSION_COMPRESSOR_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static BlockEntityType<ImplosionCompressorTile> IMPLOSION_COMPRESSOR_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":implosion_compressor")
    public static MenuType<ImplosionCompressorContainer> IMPLOSION_COMPRESSOR_CONTAINER;

    // Blast Furnace
    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static BlastFurnaceBlock BLAST_FURNACE_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static BlockEntityType<BlastFurnaceTile> BLAST_FURNACE_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":blast_furnace")
    public static MenuType<BlastFurnaceContainer> BLAST_FURNACE_CONTAINER;

    // Tooling Station
    @ObjectHolder(VoluminousEnergy.MODID + ":tooling_station")
    public static ToolingStationBlock TOOLING_STATION_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":tooling_station")
    public static BlockEntityType<ToolingStationTile> TOOLING_STATION_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":tooling_station")
    public static MenuType<ToolingStationContainer> TOOLING_STATION_CONTAINER;

    // Sawmill
    @ObjectHolder(VoluminousEnergy.MODID + ":sawmill")
    public static SawmillBlock SAWMILL_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":sawmill")
    public static BlockEntityType<SawmillTile> SAWMILL_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":sawmill")
    public static MenuType<SawmillContainer> SAWMILL_CONTAINER;

    // Fluid Electrolyzer
    @ObjectHolder(VoluminousEnergy.MODID + ":fluid_electrolyzer")
    public static FluidElectrolyzerBlock FLUID_ELECTROLYZER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":fluid_electrolyzer")
    public static BlockEntityType<FluidElectrolyzerTile> FLUID_ELECTROLYZER_TILE;

    // TODO: Container
    @ObjectHolder(VoluminousEnergy.MODID + ":fluid_electrolyzer")
    public static MenuType<FluidElectrolyzerContainer> FLUID_ELECTROLYZER_CONTAINER;

    // Tanks (Tile/Block)

    // Aluminum Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_tank")
    public static AluminumTankBlock ALUMINUM_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_tank")
    public static BlockEntityType<AluminumTankTile> ALUMINUM_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":aluminum_tank")
    public static MenuType<AluminumTankContainer> ALUMINUM_TANK_CONTAINER;

    // Titanium Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":titanium_tank")
    public static TitaniumTankBlock TITANIUM_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":titanium_tank")
    public static BlockEntityType<TitaniumTankTile> TITANIUM_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":titanium_tank")
    public static MenuType<TitaniumTankContainer> TITANIUM_TANK_CONTAINER;

    // Netherite Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":netherite_tank")
    public static NetheriteTankBlock NETHERITE_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":netherite_tank")
    public static BlockEntityType<NetheriteTankTile> NETHERITE_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":netherite_tank")
    public static MenuType<NetheriteTankContainer> NETHERITE_TANK_CONTAINER;

    // Netherite Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":nighalite_tank")
    public static NighaliteTankBlock NIGHALITE_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":nighalite_tank")
    public static BlockEntityType<NighaliteTankTile> NIGHALITE_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":nighalite_tank")
    public static MenuType<NighaliteTankContainer> NIGHALITE_TANK_CONTAINER;

    // Eighzo Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":eighzo_tank")
    public static EighzoTankBlock EIGHZO_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":eighzo_tank")
    public static BlockEntityType<EighzoTankTile> EIGHZO_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":eighzo_tank")
    public static MenuType<EighzoTankContainer> EIGHZO_TANK_CONTAINER;

    // Solarium Tank
    @ObjectHolder(VoluminousEnergy.MODID + ":solarium_tank")
    public static SolariumTankBlock SOLARIUM_TANK_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":solarium_tank")
    public static BlockEntityType<SolariumTankTile> SOLARIUM_TANK_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":solarium_tank")
    public static MenuType<SolariumTankContainer> SOLARIUM_TANK_CONTAINER;

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

    // Deepslate ores
    @ObjectHolder(VoluminousEnergy.MODID  + ":deepslate_bauxite_ore")
    public static DeepslateBauxiteOre DEEPSLATE_BAUXITE_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":deepslate_cinnabar_ore")
    public static DeepslateCinnabarOre DEEPSLATE_CINNABAR_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":deepslate_rutile_ore")
    public static DeepslateRutileOre DEEPSLATE_RUTILE_ORE;

    @ObjectHolder(VoluminousEnergy.MODID + ":deepslate_galena_ore")
    public static DeepslateGalenaOre DEEPSLATE_GALENA_ORE;

    @ObjectHolder(VoluminousEnergy.MODID  + ":red_saltpeter_ore")
    public static RedSaltpeterOre RED_SALTPETER_ORE;

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

    @ObjectHolder(VoluminousEnergy.MODID + ":pressure_ladder")
    public static PressureLadder PRESSURE_LADDER;

    // Dimensional Laser
    @ObjectHolder(VoluminousEnergy.MODID + ":dimensional_laser")
    public static DimensionalLaserBlock DIMENSIONAL_LASER_BLOCK;

    @ObjectHolder(VoluminousEnergy.MODID + ":dimensional_laser")
    public static BlockEntityType<DimensionalLaserTile> DIMENSIONAL_LASER_TILE;

    @ObjectHolder(VoluminousEnergy.MODID + ":dimensional_laser")
    public static MenuType<DimensionalLaserContainer> DIMENSIONAL_LASER_CONTAINER;
}
