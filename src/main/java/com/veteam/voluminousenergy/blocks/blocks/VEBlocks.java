package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(VoluminousEnergy.MODID)
public class VEBlocks {

    public static final DeferredRegister<Block> VE_BLOCKS_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, VoluminousEnergy.MODID);
    public static final DeferredRegister<BlockEntityType<?>> VE_TILE_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoluminousEnergy.MODID);
    public static final DeferredRegister<MenuType<?>> VE_CONTAINER_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoluminousEnergy.MODID);

    // Shells
    public static RegistryObject<AluminumShellBlock> ALUMINUM_SHELL = VE_BLOCKS_REGISTRY.register("aluminum_shell", AluminumShellBlock::new);

    // Machine Frames
    public static RegistryObject<CarbonShieldedAluminumMachineFrame> CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME = VE_BLOCKS_REGISTRY.register("carbon_shielded_aluminum_machine_frame", CarbonShieldedAluminumMachineFrame::new);

    // Casings (For multiblocks)
    public static RegistryObject<AluminumMachineCasingBlock> ALUMINUM_MACHINE_CASING_BLOCK = VE_BLOCKS_REGISTRY.register("aluminum_machine_casing", AluminumMachineCasingBlock::new);

    public static RegistryObject<TitaniumMachineCasingBlock> TITANIUM_MACHINE_CASING_BLOCK = VE_BLOCKS_REGISTRY.register("titanium_machine_casing", TitaniumMachineCasingBlock::new);

    //Primitive Blast
    public static RegistryObject<PrimitiveBlastFurnaceBlock> PRIMITIVE_BLAST_FURNACE_BLOCK = VE_BLOCKS_REGISTRY.register("primitiveblastfurnace", PrimitiveBlastFurnaceBlock::new);
    public static RegistryObject<BlockEntityType<PrimitiveBlastFurnaceTile>> PRIMITIVE_BLAST_FURNACE_TILE = VE_TILE_REGISTRY.register("primitiveblastfurnace",
            () -> BlockEntityType.Builder.of(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get()).build(null) );
    public static RegistryObject<MenuType<PrimitiveBlastFurnaceContainer>> PRIMITIVE_BLAST_FURNACE_CONTAINER = VE_CONTAINER_REGISTRY.register("primitiveblastfurnace", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveBlastFurnaceContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }));

    //Primitive Stirling
    public static RegistryObject<PrimitiveStirlingGeneratorBlock> PRIMITIVE_STIRLING_GENERATOR_BLOCK = VE_BLOCKS_REGISTRY.register("primitivestirlinggenerator", PrimitiveStirlingGeneratorBlock::new);
    public static RegistryObject<BlockEntityType<PrimitiveStirlingGeneratorTile>> PRIMITIVE_STIRLING_GENERATOR_TILE = VE_TILE_REGISTRY.register("primitivestirlinggenerator",
            () -> BlockEntityType.Builder.of(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<PrimitiveStirlingGeneratorContainer>> PRIMITIVE_STIRLING_GENERATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("primitivestirlinggenerator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveStirlingGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    //Crusher
    public static RegistryObject<CrusherBlock> CRUSHER_BLOCK = VE_BLOCKS_REGISTRY.register("crusher", CrusherBlock::new);
    public static RegistryObject<BlockEntityType<CrusherTile>> CRUSHER_TILE = VE_TILE_REGISTRY.register("crusher",
            () -> BlockEntityType.Builder.of(CrusherTile::new,VEBlocks.CRUSHER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<CrusherContainer>> CRUSHER_CONTAINER = VE_CONTAINER_REGISTRY.register("crusher", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CrusherContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }));

    //Electrolyzer
    public static RegistryObject<ElectrolyzerBlock> ELECTROLYZER_BLOCK = VE_BLOCKS_REGISTRY.register("electrolyzer", ElectrolyzerBlock::new);
    public static RegistryObject<BlockEntityType<ElectrolyzerTile>> ELECTROLYZER_TILE = VE_TILE_REGISTRY.register("electrolyzer",
            () -> BlockEntityType.Builder.of(ElectrolyzerTile::new,VEBlocks.ELECTROLYZER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<ElectrolyzerContainer>> ELECTROLYZER_CONTAINER = VE_CONTAINER_REGISTRY.register("electrolyzer", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectrolyzerContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Centrifugal Agitator
    public static RegistryObject<CentrifugalAgitatorBlock> CENTRIFUGAL_AGITATOR_BLOCK = VE_BLOCKS_REGISTRY.register("centrifugal_agitator", CentrifugalAgitatorBlock::new);
    public static RegistryObject<BlockEntityType<CentrifugalAgitatorTile>> CENTRIFUGAL_AGITATOR_TILE = VE_TILE_REGISTRY.register("centrifugal_agitator",
            () -> BlockEntityType.Builder.of(CentrifugalAgitatorTile::new,VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<CentrifugalAgitatorContainer>> CENTRIFUGAL_AGITATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("centrifugal_agitator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalAgitatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Compressor
    public static RegistryObject<CompressorBlock> COMPRESSOR_BLOCK = VE_BLOCKS_REGISTRY.register("compressor", CompressorBlock::new);
    public static RegistryObject<BlockEntityType<CompressorTile>> COMPRESSOR_TILE = VE_TILE_REGISTRY.register("compressor",
            () -> BlockEntityType.Builder.of(CompressorTile::new,VEBlocks.COMPRESSOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<CompressorContainer>> COMPRESSOR_CONTAINER = VE_CONTAINER_REGISTRY.register("compressor", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Stirling Generator
    public static RegistryObject<StirlingGeneratorBlock> STIRLING_GENERATOR_BLOCK = VE_BLOCKS_REGISTRY.register("stirling_generator", StirlingGeneratorBlock::new);
    public static RegistryObject<BlockEntityType<StirlingGeneratorTile>> STIRLING_GENERATOR_TILE = VE_TILE_REGISTRY.register("stirling_generator",
            () -> BlockEntityType.Builder.of(StirlingGeneratorTile::new,VEBlocks.STIRLING_GENERATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<StirlingGeneratorContainer>> STIRLING_GENERATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("stirling_generator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new StirlingGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Combustion Generator
    public static RegistryObject<CombustionGeneratorBlock> COMBUSTION_GENERATOR_BLOCK = VE_BLOCKS_REGISTRY.register("combustion_generator", CombustionGeneratorBlock::new);
    public static RegistryObject<BlockEntityType<CombustionGeneratorTile>> COMBUSTION_GENERATOR_TILE = VE_TILE_REGISTRY.register("combustion_generator",
            () -> BlockEntityType.Builder.of(CombustionGeneratorTile::new,VEBlocks.COMBUSTION_GENERATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<CombustionGeneratorContainer>> COMBUSTION_GENERATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("combustion_generator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CombustionGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Aqueoulizer
    public static RegistryObject<AqueoulizerBlock> AQUEOULIZER_BLOCK = VE_BLOCKS_REGISTRY.register("aqueoulizer", AqueoulizerBlock::new);
    public static RegistryObject<BlockEntityType<AqueoulizerTile>> AQUEOULIZER_TILE = VE_TILE_REGISTRY.register("aqueoulizer",
            () -> BlockEntityType.Builder.of(AqueoulizerTile::new,VEBlocks.AQUEOULIZER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<AqueoulizerContainer>> AQUEOULIZER_CONTAINER = VE_CONTAINER_REGISTRY.register("aqueoulizer", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AqueoulizerContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Air Compressor
    public static RegistryObject<AirCompressorBlock> AIR_COMPRESSOR_BLOCK = VE_BLOCKS_REGISTRY.register("air_compressor", AirCompressorBlock::new);
    public static RegistryObject<BlockEntityType<AirCompressorTile>> AIR_COMPRESSOR_TILE = VE_TILE_REGISTRY.register("air_compressor",
            () -> BlockEntityType.Builder.of(AirCompressorTile::new,VEBlocks.AIR_COMPRESSOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<AirCompressorContainer>> AIR_COMPRESSOR_CONTAINER = VE_CONTAINER_REGISTRY.register("air_compressor", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AirCompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Distillation Unit
    public static RegistryObject<DistillationUnitBlock> DISTILLATION_UNIT_BLOCK = VE_BLOCKS_REGISTRY.register("distillation_unit", DistillationUnitBlock::new);
    public static RegistryObject<BlockEntityType<DistillationUnitTile>> DISTILLATION_UNIT_TILE = VE_TILE_REGISTRY.register("distillation_unit",
            () -> BlockEntityType.Builder.of(DistillationUnitTile::new,VEBlocks.DISTILLATION_UNIT_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<DistillationUnitContainer>> DISTILLATION_UNIT_CONTAINER = VE_CONTAINER_REGISTRY.register("distillation_unit", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new DistillationUnitContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Pump
    public static RegistryObject<PumpBlock> PUMP_BLOCK = VE_BLOCKS_REGISTRY.register("pump", PumpBlock::new);
    public static RegistryObject<BlockEntityType<PumpTile>> PUMP_TILE = VE_TILE_REGISTRY.register("pump",
            () -> BlockEntityType.Builder.of(PumpTile::new,VEBlocks.PUMP_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<PumpContainer>> PUMP_CONTAINER = VE_CONTAINER_REGISTRY.register("pump", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PumpContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Gas Fired Furnace
    public static RegistryObject<GasFiredFurnaceBlock> GAS_FIRED_FURNACE_BLOCK = VE_BLOCKS_REGISTRY.register("gas_fired_furnace", GasFiredFurnaceBlock::new);
    public static RegistryObject<BlockEntityType<GasFiredFurnaceTile>> GAS_FIRED_FURNACE_TILE = VE_TILE_REGISTRY.register("gas_fired_furnace",
            () -> BlockEntityType.Builder.of(GasFiredFurnaceTile::new,VEBlocks.GAS_FIRED_FURNACE_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<GasFiredFurnaceContainer>> GAS_FIRED_FURNACE_CONTAINER = VE_CONTAINER_REGISTRY.register("gas_fired_furnace", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new GasFiredFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Electric Furnace
    public static RegistryObject<ElectricFurnaceBlock> ELECTRIC_FURNACE_BLOCK = VE_BLOCKS_REGISTRY.register("electric_furnace", ElectricFurnaceBlock::new);
    public static RegistryObject<BlockEntityType<ElectricFurnaceTile>> ELECTRIC_FURNACE_TILE = VE_TILE_REGISTRY.register("electric_furnace",
            () -> BlockEntityType.Builder.of(ElectricFurnaceTile::new,VEBlocks.ELECTRIC_FURNACE_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<ElectricFurnaceContainer>> ELECTRIC_FURNACE_CONTAINER = VE_CONTAINER_REGISTRY.register("electric_furnace", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectricFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Battery Box
    public static RegistryObject<BatteryBoxBlock> BATTERY_BOX_BLOCK = VE_BLOCKS_REGISTRY.register("battery_box", BatteryBoxBlock::new);
    public static RegistryObject<BlockEntityType<BatteryBoxTile>> BATTERY_BOX_TILE = VE_TILE_REGISTRY.register("battery_box",
            () -> BlockEntityType.Builder.of(BatteryBoxTile::new,VEBlocks.BATTERY_BOX_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<BatteryBoxContainer>> BATTERY_BOX_CONTAINER = VE_CONTAINER_REGISTRY.register("battery_box", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new BatteryBoxContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Primitive Solar Panel
    public static RegistryObject<PrimitiveSolarPanelBlock> PRIMITIVE_SOLAR_PANEL_BLOCK = VE_BLOCKS_REGISTRY.register("primitive_solar_panel", PrimitiveSolarPanelBlock::new);
    public static RegistryObject<BlockEntityType<PrimitiveSolarPanelTile>> PRIMITIVE_SOLAR_PANEL_TILE = VE_TILE_REGISTRY.register("primitive_solar_panel",
            () -> BlockEntityType.Builder.of(PrimitiveSolarPanelTile::new,VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<PrimitiveSolarPanelContainer>> PRIMITIVE_SOLAR_PANEL_CONTAINER = VE_CONTAINER_REGISTRY.register("primitive_solar_panel", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveSolarPanelContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Solar Panel
    public static RegistryObject<SolarPanelBlock> SOLAR_PANEL_BLOCK = VE_BLOCKS_REGISTRY.register("solar_panel", SolarPanelBlock::new);
    public static RegistryObject<BlockEntityType<SolarPanelTile>> SOLAR_PANEL_TILE = VE_TILE_REGISTRY.register("solar_panel",
            () -> BlockEntityType.Builder.of(SolarPanelTile::new,VEBlocks.SOLAR_PANEL_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<SolarPanelContainer>> SOLAR_PANEL_CONTAINER = VE_CONTAINER_REGISTRY.register("solar_panel", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SolarPanelContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Centrifugal Separator
    public static RegistryObject<CentrifugalSeparatorBlock> CENTRIFUGAL_SEPARATOR_BLOCK = VE_BLOCKS_REGISTRY.register("centrifugal_separator", CentrifugalSeparatorBlock::new);
    public static RegistryObject<BlockEntityType<CentrifugalSeparatorTile>> CENTRIFUGAL_SEPARATOR_TILE = VE_TILE_REGISTRY.register("centrifugal_separator",
            () -> BlockEntityType.Builder.of(CentrifugalSeparatorTile::new,VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<CentrifugalSeparatorContainer>> CENTRIFUGAL_SEPARATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("centrifugal_separator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalSeparatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Implosion Compressor
    public static RegistryObject<ImplosionCompressorBlock> IMPLOSION_COMPRESSOR_BLOCK = VE_BLOCKS_REGISTRY.register("implosion_compressor", ImplosionCompressorBlock::new);
    public static RegistryObject<BlockEntityType<ImplosionCompressorTile>> IMPLOSION_COMPRESSOR_TILE = VE_TILE_REGISTRY.register("implosion_compressor",
            () -> BlockEntityType.Builder.of(ImplosionCompressorTile::new,VEBlocks.IMPLOSION_COMPRESSOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<ImplosionCompressorContainer>> IMPLOSION_COMPRESSOR_CONTAINER = VE_CONTAINER_REGISTRY.register("implosion_compressor", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ImplosionCompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Blast Furnace
    public static RegistryObject<BlastFurnaceBlock> BLAST_FURNACE_BLOCK = VE_BLOCKS_REGISTRY.register("blast_furnace", BlastFurnaceBlock::new);
    public static RegistryObject<BlockEntityType<BlastFurnaceTile>> BLAST_FURNACE_TILE = VE_TILE_REGISTRY.register("blast_furnace",
            () -> BlockEntityType.Builder.of(BlastFurnaceTile::new,VEBlocks.BLAST_FURNACE_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<BlastFurnaceContainer>> BLAST_FURNACE_CONTAINER = VE_CONTAINER_REGISTRY.register("blast_furnace", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new BlastFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Tooling Station
    public static RegistryObject<ToolingStationBlock> TOOLING_STATION_BLOCK = VE_BLOCKS_REGISTRY.register("tooling_station", ToolingStationBlock::new);
    public static RegistryObject<BlockEntityType<ToolingStationTile>> TOOLING_STATION_TILE = VE_TILE_REGISTRY.register("tooling_station",
            () -> BlockEntityType.Builder.of(ToolingStationTile::new,VEBlocks.TOOLING_STATION_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<ToolingStationContainer>> TOOLING_STATION_CONTAINER = VE_CONTAINER_REGISTRY.register("tooling_station", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ToolingStationContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Sawmill
    public static RegistryObject<SawmillBlock> SAWMILL_BLOCK = VE_BLOCKS_REGISTRY.register("sawmill", SawmillBlock::new);
    public static RegistryObject<BlockEntityType<SawmillTile>> SAWMILL_TILE = VE_TILE_REGISTRY.register("sawmill",
            () -> BlockEntityType.Builder.of(SawmillTile::new,VEBlocks.SAWMILL_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<SawmillContainer>> SAWMILL_CONTAINER = VE_CONTAINER_REGISTRY.register("sawmill", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SawmillContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Dimensional Laser
    public static RegistryObject<DimensionalLaserBlock> DIMENSIONAL_LASER_BLOCK = VE_BLOCKS_REGISTRY.register("dimensional_laser", DimensionalLaserBlock::new);
    public static RegistryObject<BlockEntityType<DimensionalLaserTile>> DIMENSIONAL_LASER_TILE = VE_TILE_REGISTRY.register("dimensional_laser",
            () -> BlockEntityType.Builder.of(DimensionalLaserTile::new,VEBlocks.DIMENSIONAL_LASER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<DimensionalLaserContainer>> DIMENSIONAL_LASER_CONTAINER = VE_CONTAINER_REGISTRY.register("dimensional_laser", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new DimensionalLaserContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Fluid Electrolyzer
    public static RegistryObject<FluidElectrolyzerBlock> FLUID_ELECTROLYZER_BLOCK = VE_BLOCKS_REGISTRY.register("fluid_electrolyzer", FluidElectrolyzerBlock::new);
    public static RegistryObject<BlockEntityType<FluidElectrolyzerTile>> FLUID_ELECTROLYZER_TILE = VE_TILE_REGISTRY.register("fluid_electrolyzer",
            () -> BlockEntityType.Builder.of(FluidElectrolyzerTile::new,VEBlocks.FLUID_ELECTROLYZER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<FluidElectrolyzerContainer>> FLUID_ELECTROLYZER_CONTAINER = VE_CONTAINER_REGISTRY.register("fluid_electrolyzer", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new FluidElectrolyzerContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Fluid Mixer
    public static RegistryObject<FluidMixerBlock> FLUID_MIXER_BLOCK = VE_BLOCKS_REGISTRY.register("fluid_mixer", FluidMixerBlock::new);
    public static RegistryObject<BlockEntityType<FluidMixerTile>> FLUID_MIXER_TILE = VE_TILE_REGISTRY.register("fluid_mixer",
            () -> BlockEntityType.Builder.of(FluidMixerTile::new,VEBlocks.FLUID_MIXER_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<FluidMixerContainer>> FLUID_MIXER_CONTAINER = VE_CONTAINER_REGISTRY.register("fluid_mixer", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new FluidMixerContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Hydroponic Incubator
    public static RegistryObject<HydroponicIncubatorBlock> HYDROPONIC_INCUBATOR_BLOCK = VE_BLOCKS_REGISTRY.register("hydroponic_incubator", HydroponicIncubatorBlock::new);
    public static RegistryObject<BlockEntityType<HydroponicIncubatorTile>> HYDROPONIC_INCUBATOR_TILE = VE_TILE_REGISTRY.register("hydroponic_incubator",
            () -> BlockEntityType.Builder.of(HydroponicIncubatorTile::new,VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<HydroponicIncubatorContainer>> HYDROPONIC_INCUBATOR_CONTAINER = VE_CONTAINER_REGISTRY.register("hydroponic_incubator", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new HydroponicIncubatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Tanks (Tile/Block)

    // Aluminum Tank
    public static RegistryObject<AluminumTankBlock> ALUMINUM_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("aluminum_tank", AluminumTankBlock::new);
    public static RegistryObject<BlockEntityType<AluminumTankTile>> ALUMINUM_TANK_TILE = VE_TILE_REGISTRY.register("aluminum_tank",
            () -> BlockEntityType.Builder.of(AluminumTankTile::new, VEBlocks.ALUMINUM_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<AluminumTankContainer>> ALUMINUM_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("aluminum_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AluminumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Titanium Tank
    public static RegistryObject<TitaniumTankBlock> TITANIUM_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("titanium_tank", TitaniumTankBlock::new);
    public static RegistryObject<BlockEntityType<TitaniumTankTile>> TITANIUM_TANK_TILE = VE_TILE_REGISTRY.register("titanium_tank",
            () -> BlockEntityType.Builder.of(TitaniumTankTile::new,VEBlocks.TITANIUM_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<TitaniumTankContainer>> TITANIUM_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("titanium_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new TitaniumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Netherite Tank
    public static RegistryObject<NetheriteTankBlock> NETHERITE_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("netherite_tank", NetheriteTankBlock::new);
    public static RegistryObject<BlockEntityType<NetheriteTankTile>> NETHERITE_TANK_TILE = VE_TILE_REGISTRY.register("netherite_tank",
            () -> BlockEntityType.Builder.of(NetheriteTankTile::new,VEBlocks.NETHERITE_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<NetheriteTankContainer>> NETHERITE_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("netherite_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new NetheriteTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Nighalite Tank
    public static RegistryObject<NighaliteTankBlock> NIGHALITE_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("nighalite_tank", NighaliteTankBlock::new);
    public static RegistryObject<BlockEntityType<NighaliteTankTile>> NIGHALITE_TANK_TILE = VE_TILE_REGISTRY.register("nighalite_tank",
            () -> BlockEntityType.Builder.of(NighaliteTankTile::new,VEBlocks.NIGHALITE_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<NighaliteTankContainer>> NIGHALITE_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("nighalite_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new NighaliteTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Eighzo Tank
    public static RegistryObject<EighzoTankBlock> EIGHZO_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("eighzo_tank", EighzoTankBlock::new);
    public static RegistryObject<BlockEntityType<EighzoTankTile>> EIGHZO_TANK_TILE = VE_TILE_REGISTRY.register("eighzo_tank",
            () -> BlockEntityType.Builder.of(EighzoTankTile::new,VEBlocks.EIGHZO_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<EighzoTankContainer>> EIGHZO_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("eighzo_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new EighzoTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    // Solarium Tank
    public static RegistryObject<SolariumTankBlock> SOLARIUM_TANK_BLOCK = VE_BLOCKS_REGISTRY.register("solarium_tank", SolariumTankBlock::new);
    public static RegistryObject<BlockEntityType<SolariumTankTile>> SOLARIUM_TANK_TILE = VE_TILE_REGISTRY.register("solarium_tank",
            () -> BlockEntityType.Builder.of(SolariumTankTile::new,VEBlocks.SOLARIUM_TANK_BLOCK.get()).build(null));
    public static RegistryObject<MenuType<SolariumTankContainer>> SOLARIUM_TANK_CONTAINER = VE_CONTAINER_REGISTRY.register("solarium_tank", () ->
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SolariumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }));

    //Ores
    public static RegistryObject<SaltpeterOre> SALTPETER_ORE = VE_BLOCKS_REGISTRY.register("saltpeterore", SaltpeterOre::new);

    public static RegistryObject<BauxiteOre> BAUXITE_ORE = VE_BLOCKS_REGISTRY.register("bauxiteore", BauxiteOre::new);

    public static RegistryObject<CinnabarOre> CINNABAR_ORE = VE_BLOCKS_REGISTRY.register("cinnabarore", CinnabarOre::new);

    public static RegistryObject<RutileOre> RUTILE_ORE = VE_BLOCKS_REGISTRY.register("rutileore", RutileOre::new);

    public static RegistryObject<GalenaOre> GALENA_ORE = VE_BLOCKS_REGISTRY.register("galena_ore", GalenaOre::new);

    public static RegistryObject<EighzoOre> EIGHZO_ORE = VE_BLOCKS_REGISTRY.register("eighzo_ore", EighzoOre::new);

    // Deepslate ores
    public static RegistryObject<DeepslateBauxiteOre> DEEPSLATE_BAUXITE_ORE = VE_BLOCKS_REGISTRY.register("deepslate_bauxite_ore", DeepslateBauxiteOre::new);

    public static RegistryObject<DeepslateCinnabarOre> DEEPSLATE_CINNABAR_ORE = VE_BLOCKS_REGISTRY.register("deepslate_cinnabar_ore", DeepslateCinnabarOre::new);

    public static RegistryObject<DeepslateRutileOre> DEEPSLATE_RUTILE_ORE = VE_BLOCKS_REGISTRY.register("deepslate_rutile_ore", DeepslateRutileOre::new);

    public static RegistryObject<DeepslateGalenaOre> DEEPSLATE_GALENA_ORE = VE_BLOCKS_REGISTRY.register("deepslate_galena_ore", DeepslateGalenaOre::new);

    public static RegistryObject<RedSaltpeterOre> RED_SALTPETER_ORE = VE_BLOCKS_REGISTRY.register("red_saltpeter_ore", RedSaltpeterOre::new);

    //Crops
    //public static VEWaterCrop WATER_CROP;

    //public static VELandCrop LAND_CROP;

    public static RegistryObject<RiceCrop> RICE_CROP = VE_BLOCKS_REGISTRY.register("rice_crop", RiceCrop::new);

    // Material Storage Blocks
    public static RegistryObject<SolariumBlock> SOLARIUM_BLOCK = VE_BLOCKS_REGISTRY.register("solarium_block", SolariumBlock::new);

    public static RegistryObject<AluminumBlock> ALUMINUM_BLOCK = VE_BLOCKS_REGISTRY.register("aluminum_block", AluminumBlock::new);

    public static RegistryObject<CarbonBlock> CARBON_BLOCK = VE_BLOCKS_REGISTRY.register("carbon_block", CarbonBlock::new);

    public static RegistryObject<EighzoBlock> EIGHZO_BLOCK = VE_BLOCKS_REGISTRY.register("eighzo_block", EighzoBlock::new);

    public static RegistryObject<NighaliteBlock> NIGHALITE_BLOCK = VE_BLOCKS_REGISTRY.register("nighalite_block", NighaliteBlock::new);

    public static RegistryObject<SaltpeterBlock> SALTPETER_BLOCK = VE_BLOCKS_REGISTRY.register("saltpeter_block", SaltpeterBlock::new);

    public static RegistryObject<TitaniumBlock> TITANIUM_BLOCK = VE_BLOCKS_REGISTRY.register("titanium_block", TitaniumBlock::new);

    public static RegistryObject<TungstenBlock> TUNGSTEN_BLOCK = VE_BLOCKS_REGISTRY.register("tungsten_block", TungstenBlock::new);

    public static RegistryObject<TungstenSteelBlock> TUNGSTEN_STEEL_BLOCK = VE_BLOCKS_REGISTRY.register("tungsten_steel_block", TungstenSteelBlock::new);

    // Raw Material Storage Blocks
    public static RegistryObject<RawBauxiteBlock> RAW_BAUXITE_BLOCK = VE_BLOCKS_REGISTRY.register("raw_bauxite_block", RawBauxiteBlock::new);

    public static RegistryObject<RawCinnabarBlock> RAW_CINNABAR_BLOCK = VE_BLOCKS_REGISTRY.register("raw_cinnabar_block", RawCinnabarBlock::new);

    public static RegistryObject<RawEighzoBlock> RAW_EIGHZO_BLOCK = VE_BLOCKS_REGISTRY.register("raw_eighzo_block", RawEighzoBlock::new);

    public static RegistryObject<RawGalenaBlock> RAW_GALENA_BLOCK = VE_BLOCKS_REGISTRY.register("raw_galena_block", RawGalenaBlock::new);

    public static RegistryObject<RawRutileBlock> RAW_RUTILE_BLOCK = VE_BLOCKS_REGISTRY.register("raw_rutile_block", RawRutileBlock::new);

    @Deprecated
    public static RegistryObject<RawBoneBlock> RAW_BONE_BLOCK = VE_BLOCKS_REGISTRY.register("raw_bone_block", RawBoneBlock::new); // Unused

    public static RegistryObject<PressureLadder> PRESSURE_LADDER = VE_BLOCKS_REGISTRY.register("pressure_ladder", PressureLadder::new);
}
