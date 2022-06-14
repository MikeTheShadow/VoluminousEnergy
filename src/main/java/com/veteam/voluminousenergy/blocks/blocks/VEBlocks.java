package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
import com.veteam.voluminousenergy.blocks.blocks.crops.VELandCrop;
import com.veteam.voluminousenergy.blocks.blocks.machines.*;
import com.veteam.voluminousenergy.blocks.blocks.machines.tanks.*;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod(VoluminousEnergy.MODID)
public class VEBlocks {

    // Shells
    @ObjectHolder(registryName = "block", value = "aluminum_shell")
    public static AluminumShellBlock ALUMINUM_SHELL;

    // Machine Frames
    @ObjectHolder(registryName = "block", value = "carbon_shielded_aluminum_machine_frame")
    public static CarbonShieldedAluminumMachineFrame CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME;

    // Casings (For multiblocks)
    @ObjectHolder(registryName = "block", value = "aluminum_machine_casing")
    public static AluminumMachineCasingBlock ALUMINUM_MACHINE_CASING_BLOCK;

    @ObjectHolder(registryName = "block", value = "titanium_machine_casing")
    public static TitaniumMachineCasingBlock TITANIUM_MACHINE_CASING_BLOCK;

    //Primitive Blast
    @ObjectHolder(registryName = "block", value = "primitiveblastfurnace")
    public static PrimitiveBlastFurnaceBlock PRIMITIVE_BLAST_FURNACE_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value = "primitiveblastfurnace")
    public static BlockEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE;

    @ObjectHolder(registryName = "menu", value = "primitiveblastfurnace")
    public static MenuType<PrimitiveBlastFurnaceContainer> PRIMITIVE_BLAST_FURNACE_CONTAINER;

    //Primitive Stirling
    @ObjectHolder(registryName = "block", value =  "primitivestirlinggenerator")
    public static PrimitiveStirlingGeneratorBlock PRIMITIVE_STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "primitivestirlinggenerator")
    public static BlockEntityType<PrimitiveStirlingGeneratorTile> PRIMITIVE_STIRLING_GENERATOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "primitivestirlinggenerator")
    public static MenuType<PrimitiveStirlingGeneratorContainer> PRIMITIVE_STIRLING_GENERATOR_CONTAINER;

    //Crusher
    @ObjectHolder(registryName = "block", value =  "crusher")
    public static CrusherBlock CRUSHER_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "crusher")
    public static BlockEntityType<CrusherTile> CRUSHER_TILE;

    @ObjectHolder(registryName = "menu", value =  "crusher")
    public static MenuType<CrusherContainer> CRUSHER_CONTAINER;

    //Electrolyzer
    @ObjectHolder(registryName = "block", value =  "electrolyzer")
    public static ElectrolyzerBlock ELECTROLYZER_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "electrolyzer")
    public static BlockEntityType<ElectrolyzerTile> ELECTROLYZER_TILE;

    @ObjectHolder(registryName = "menu", value =  "electrolyzer")
    public static MenuType<ElectrolyzerContainer> ELECTROLYZER_CONTAINER;

    // Centrifugal Agitator
    @ObjectHolder(registryName = "block", value =  "centrifugal_agitator")
    public static CentrifugalAgitatorBlock CENTRIFUGAL_AGITATOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "centrifugal_agitator")
    public static BlockEntityType<CentrifugalAgitatorTile> CENTRIFUGAL_AGITATOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "centrifugal_agitator")
    public static MenuType<CentrifugalAgitatorContainer> CENTRIFUGAL_AGITATOR_CONTAINER;

    // Compressor
    @ObjectHolder(registryName = "block", value =  "compressor")
    public static CompressorBlock COMPRESSOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "compressor")
    public static BlockEntityType<CompressorTile> COMPRESSOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "compressor")
    public static MenuType<CompressorContainer> COMPRESSOR_CONTAINER;

    // Stirling Generator
    @ObjectHolder(registryName = "block", value =  "stirling_generator")
    public static StirlingGeneratorBlock STIRLING_GENERATOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "stirling_generator")
    public static BlockEntityType<StirlingGeneratorTile> STIRLING_GENERATOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "stirling_generator")
    public static MenuType<StirlingGeneratorContainer> STIRLING_GENERATOR_CONTAINER;

    // Combustion Generator
    @ObjectHolder(registryName = "block", value =  "combustion_generator")
    public static CombustionGeneratorBlock COMBUSTION_GENERATOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "combustion_generator")
    public static BlockEntityType<CombustionGeneratorTile> COMBUSTION_GENERATOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "combustion_generator")
    public static MenuType<CombustionGeneratorContainer> COMBUSTION_GENERATOR_CONTAINER;

    // Aqueoulizer
    @ObjectHolder(registryName = "block", value =  "aqueoulizer")
    public static AqueoulizerBlock AQUEOULIZER_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "aqueoulizer")
    public static BlockEntityType<AqueoulizerTile> AQUEOULIZER_TILE;

    @ObjectHolder(registryName = "menu", value =  "aqueoulizer")
    public static MenuType<AqueoulizerContainer> AQUEOULIZER_CONTAINER;

    // Air Compressor
    @ObjectHolder(registryName = "block", value =  "air_compressor")
    public static AirCompressorBlock AIR_COMPRESSOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "air_compressor")
    public static BlockEntityType<AirCompressorTile> AIR_COMPRESSOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "air_compressor")
    public static MenuType<AirCompressorContainer> AIR_COMPRESSOR_CONTAINER;

    // Distillation Unit
    @ObjectHolder(registryName = "block", value =  "distillation_unit")
    public static DistillationUnitBlock DISTILLATION_UNIT_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "distillation_unit")
    public static BlockEntityType<DistillationUnitTile> DISTILLATION_UNIT_TILE;

    @ObjectHolder(registryName = "menu", value =  "distillation_unit")
    public static MenuType<DistillationUnitContainer> DISTILLATION_UNIT_CONTAINER;

    // Pump
    @ObjectHolder(registryName = "block", value =  "pump")
    public static PumpBlock PUMP_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "pump")
    public static BlockEntityType<PumpTile> PUMP_TILE;

    @ObjectHolder(registryName = "menu", value =  "pump")
    public static MenuType<PumpContainer> PUMP_CONTAINER;

    // Gas Fired Furnace
    @ObjectHolder(registryName = "block", value =  "gas_fired_furnace")
    public static GasFiredFurnaceBlock GAS_FIRED_FURNACE_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "gas_fired_furnace")
    public static BlockEntityType<GasFiredFurnaceTile> GAS_FIRED_FURNACE_TILE;

    @ObjectHolder(registryName = "menu", value =  "gas_fired_furnace")
    public static MenuType<GasFiredFurnaceContainer> GAS_FIRED_FURNACE_CONTAINER;

    // Electric Furnace
    @ObjectHolder(registryName = "block", value =  "electric_furnace")
    public static ElectricFurnaceBlock ELECTRIC_FURNACE_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "electric_furnace")
    public static BlockEntityType<ElectricFurnaceTile> ELECTRIC_FURNACE_TILE;

    @ObjectHolder(registryName = "menu", value =  "electric_furnace")
    public static MenuType<ElectricFurnaceContainer> ELECTRIC_FURNACE_CONTAINER;

    // Battery Box
    @ObjectHolder(registryName = "block", value =  "battery_box")
    public static BatteryBoxBlock BATTERY_BOX_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "battery_box")
    public static BlockEntityType<BatteryBoxTile> BATTERY_BOX_TILE;

    @ObjectHolder(registryName = "menu", value =  "battery_box")
    public static MenuType<BatteryBoxContainer> BATTERY_BOX_CONTAINER;

    // Primitive Solar Panel
    @ObjectHolder(registryName = "block", value =  "primitive_solar_panel")
    public static PrimitiveSolarPanelBlock PRIMITIVE_SOLAR_PANEL_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "primitive_solar_panel")
    public static BlockEntityType<PrimitiveSolarPanelTile> PRIMITIVE_SOLAR_PANEL_TILE;

    @ObjectHolder(registryName = "menu", value =  "primitive_solar_panel")
    public static MenuType<PrimitiveSolarPanelContainer> PRIMITIVE_SOLAR_PANEL_CONTAINER;

    // Solar Panel
    @ObjectHolder(registryName = "block", value =  "solar_panel")
    public static SolarPanelBlock SOLAR_PANEL_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "solar_panel")
    public static BlockEntityType<SolarPanelTile> SOLAR_PANEL_TILE;

    @ObjectHolder(registryName = "menu", value =  "solar_panel")
    public static MenuType<SolarPanelContainer> SOLAR_PANEL_CONTAINER;

    // Centrifugal Separator
    @ObjectHolder(registryName = "block", value =  "centrifugal_separator")
    public static CentrifugalSeparatorBlock CENTRIFUGAL_SEPARATOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "centrifugal_separator")
    public static BlockEntityType<CentrifugalSeparatorTile> CENTRIFUGAL_SEPARATOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "centrifugal_separator")
    public static MenuType<CentrifugalSeparatorContainer> CENTRIFUGAL_SEPARATOR_CONTAINER;

    // Implosion Compressor
    @ObjectHolder(registryName = "block", value =  "implosion_compressor")
    public static ImplosionCompressorBlock IMPLOSION_COMPRESSOR_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "implosion_compressor")
    public static BlockEntityType<ImplosionCompressorTile> IMPLOSION_COMPRESSOR_TILE;

    @ObjectHolder(registryName = "menu", value =  "implosion_compressor")
    public static MenuType<ImplosionCompressorContainer> IMPLOSION_COMPRESSOR_CONTAINER;

    // Blast Furnace
    @ObjectHolder(registryName = "block", value =  "blast_furnace")
    public static BlastFurnaceBlock BLAST_FURNACE_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "blast_furnace")
    public static BlockEntityType<BlastFurnaceTile> BLAST_FURNACE_TILE;

    @ObjectHolder(registryName = "menu", value =  "blast_furnace")
    public static MenuType<BlastFurnaceContainer> BLAST_FURNACE_CONTAINER;

    // Tooling Station
    @ObjectHolder(registryName = "block", value =  "tooling_station")
    public static ToolingStationBlock TOOLING_STATION_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "tooling_station")
    public static BlockEntityType<ToolingStationTile> TOOLING_STATION_TILE;

    @ObjectHolder(registryName = "menu", value =  "tooling_station")
    public static MenuType<ToolingStationContainer> TOOLING_STATION_CONTAINER;

    // Sawmill
    @ObjectHolder(registryName = "block", value =  "sawmill")
    public static SawmillBlock SAWMILL_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "sawmill")
    public static BlockEntityType<SawmillTile> SAWMILL_TILE;

    @ObjectHolder(registryName = "menu", value =  "sawmill")
    public static MenuType<SawmillContainer> SAWMILL_CONTAINER;

    // Tanks (Tile/Block)

    // Aluminum Tank
    @ObjectHolder(registryName = "block", value =  "aluminum_tank")
    public static AluminumTankBlock ALUMINUM_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "aluminum_tank")
    public static BlockEntityType<AluminumTankTile> ALUMINUM_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "aluminum_tank")
    public static MenuType<AluminumTankContainer> ALUMINUM_TANK_CONTAINER;

    // Titanium Tank
    @ObjectHolder(registryName = "block", value =  "titanium_tank")
    public static TitaniumTankBlock TITANIUM_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "titanium_tank")
    public static BlockEntityType<TitaniumTankTile> TITANIUM_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "titanium_tank")
    public static MenuType<TitaniumTankContainer> TITANIUM_TANK_CONTAINER;

    // Netherite Tank
    @ObjectHolder(registryName = "block", value =  "netherite_tank")
    public static NetheriteTankBlock NETHERITE_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "netherite_tank")
    public static BlockEntityType<NetheriteTankTile> NETHERITE_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "netherite_tank")
    public static MenuType<NetheriteTankContainer> NETHERITE_TANK_CONTAINER;

    // Netherite Tank
    @ObjectHolder(registryName = "block", value =  "nighalite_tank")
    public static NighaliteTankBlock NIGHALITE_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "nighalite_tank")
    public static BlockEntityType<NighaliteTankTile> NIGHALITE_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "nighalite_tank")
    public static MenuType<NighaliteTankContainer> NIGHALITE_TANK_CONTAINER;

    // Eighzo Tank
    @ObjectHolder(registryName = "block", value =  "eighzo_tank")
    public static EighzoTankBlock EIGHZO_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "eighzo_tank")
    public static BlockEntityType<EighzoTankTile> EIGHZO_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "eighzo_tank")
    public static MenuType<EighzoTankContainer> EIGHZO_TANK_CONTAINER;

    // Solarium Tank
    @ObjectHolder(registryName = "block", value =  "solarium_tank")
    public static SolariumTankBlock SOLARIUM_TANK_BLOCK;

    @ObjectHolder(registryName = "block_entity_type", value =  "solarium_tank")
    public static BlockEntityType<SolariumTankTile> SOLARIUM_TANK_TILE;

    @ObjectHolder(registryName = "menu", value =  "solarium_tank")
    public static MenuType<SolariumTankContainer> SOLARIUM_TANK_CONTAINER;

    //Ores
    @ObjectHolder(registryName = "block", value =  "saltpeterore")
    public static SaltpeterOre SALTPETER_ORE;

    @ObjectHolder(registryName = "block", value =  "bauxiteore")
    public static BauxiteOre BAUXITE_ORE;

    @ObjectHolder(registryName = "block", value =  "cinnabarore")
    public static CinnabarOre CINNABAR_ORE;

    @ObjectHolder(registryName = "block", value =  "rutileore")
    public static RutileOre RUTILE_ORE;

    @ObjectHolder(registryName = "block", value =  "galena_ore")
    public static GalenaOre GALENA_ORE;

    @ObjectHolder(registryName = "block", value =  "eighzo_ore")
    public static EighzoOre EIGHZO_ORE;

    // Deepslate ores
    @ObjectHolder(registryName = "block", value =  "deepslate_bauxite_ore")
    public static DeepslateBauxiteOre DEEPSLATE_BAUXITE_ORE;

    @ObjectHolder(registryName = "block", value =  "deepslate_cinnabar_ore")
    public static DeepslateCinnabarOre DEEPSLATE_CINNABAR_ORE;

    @ObjectHolder(registryName = "block", value =  "deepslate_rutile_ore")
    public static DeepslateRutileOre DEEPSLATE_RUTILE_ORE;

    @ObjectHolder(registryName = "block", value =  "deepslate_galena_ore")
    public static DeepslateGalenaOre DEEPSLATE_GALENA_ORE;

    @ObjectHolder(registryName = "block", value =  "red_saltpeter_ore")
    public static RedSaltpeterOre RED_SALTPETER_ORE;

    //Crops
    //@ObjectHolder(registryName = "", value =  "water_crop")
    //public static VEWaterCrop WATER_CROP;

    @ObjectHolder(registryName = "block", value =  "land_crop")
    public static VELandCrop LAND_CROP;

    @ObjectHolder(registryName = "block", value =  "rice_crop")
    public static RiceCrop RICE_CROP;

    // Material Storage Blocks
    @ObjectHolder(registryName = "block", value =  "solarium_block")
    public static SolariumBlock SOLARIUM_BLOCK;

    @ObjectHolder(registryName = "block", value =  "aluminum_block")
    public static AluminumBlock ALUMINUM_BLOCK;

    @ObjectHolder(registryName = "block", value =  "carbon_block")
    public static CarbonBlock CARBON_BLOCK;

    @ObjectHolder(registryName = "block", value =  "eighzo_block")
    public static EighzoBlock EIGHZO_BLOCK;

    @ObjectHolder(registryName = "block", value =  "nighalite_block")
    public static NighaliteBlock NIGHALITE_BLOCK;

    @ObjectHolder(registryName = "block", value =  "saltpeter_block")
    public static SaltpeterBlock SALTPETER_BLOCK;

    @ObjectHolder(registryName = "block", value =  "titanium_block")
    public static TitaniumBlock TITANIUM_BLOCK;

    @ObjectHolder(registryName = "block", value =  "tungsten_block")
    public static TungstenBlock TUNGSTEN_BLOCK;

    @ObjectHolder(registryName = "block", value =  "tungsten_steel_block")
    public static TungstenSteelBlock TUNGSTEN_STEEL_BLOCK;

    // Raw Material Storage Blocks
    @ObjectHolder(registryName = "block", value =  "raw_bauxite_block")
    public static RawBauxiteBlock RAW_BAUXITE_BLOCK;

    @ObjectHolder(registryName = "block", value =  "raw_cinnabar_block")
    public static RawCinnabarBlock RAW_CINNABAR_BLOCK;

    @ObjectHolder(registryName = "block", value =  "raw_eighzo_block")
    public static RawEighzoBlock RAW_EIGHZO_BLOCK;

    @ObjectHolder(registryName = "block", value =  "raw_galena_block")
    public static RawGalenaBlock RAW_GALENA_BLOCK;

    @ObjectHolder(registryName = "block", value =  "raw_rutile_block")
    public static RawRutileBlock RAW_RUTILE_BLOCK;

    @ObjectHolder(registryName = "block", value =  "raw_bone_block")
    public static RawBoneBlock RAW_BONE_BLOCK;

    @ObjectHolder(registryName = "block", value =  "pressure_ladder")
    public static PressureLadder PRESSURE_LADDER;
}
