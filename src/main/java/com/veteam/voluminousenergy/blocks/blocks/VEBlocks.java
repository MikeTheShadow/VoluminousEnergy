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
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;

@Mod(VoluminousEnergy.MODID)
public class VEBlocks {

    // Shells
    //@ObjectHolder(registryName = "block", value = "aluminum_shell")
    public static AluminumShellBlock ALUMINUM_SHELL = new AluminumShellBlock(); // TODO: Unused?

    // Machine Frames
    //@ObjectHolder(registryName = "block", value = "carbon_shielded_aluminum_machine_frame")
    public static CarbonShieldedAluminumMachineFrame CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME = new CarbonShieldedAluminumMachineFrame(); // TODO: Unused?

    // Casings (For multiblocks)
    public static AluminumMachineCasingBlock ALUMINUM_MACHINE_CASING_BLOCK = new AluminumMachineCasingBlock();

    public static TitaniumMachineCasingBlock TITANIUM_MACHINE_CASING_BLOCK = new TitaniumMachineCasingBlock();

    //Primitive Blast
    public static PrimitiveBlastFurnaceBlock PRIMITIVE_BLAST_FURNACE_BLOCK = new PrimitiveBlastFurnaceBlock();
    public static BlockEntityType<PrimitiveBlastFurnaceTile> PRIMITIVE_BLAST_FURNACE_TILE = BlockEntityType.Builder.of(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null);
    public static MenuType<PrimitiveBlastFurnaceContainer> PRIMITIVE_BLAST_FURNACE_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveBlastFurnaceContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            });

    //Primitive Stirling
    public static PrimitiveStirlingGeneratorBlock PRIMITIVE_STIRLING_GENERATOR_BLOCK = new PrimitiveStirlingGeneratorBlock();
    public static BlockEntityType<PrimitiveStirlingGeneratorTile> PRIMITIVE_STIRLING_GENERATOR_TILE = BlockEntityType.Builder.of(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null);
    public static MenuType<PrimitiveStirlingGeneratorContainer> PRIMITIVE_STIRLING_GENERATOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveStirlingGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    //Crusher
    public static CrusherBlock CRUSHER_BLOCK = new CrusherBlock();
    public static BlockEntityType<CrusherTile> CRUSHER_TILE = BlockEntityType.Builder.of(CrusherTile::new,VEBlocks.CRUSHER_BLOCK).build(null);
    public static MenuType<CrusherContainer> CRUSHER_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CrusherContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            });

    //Electrolyzer
    public static ElectrolyzerBlock ELECTROLYZER_BLOCK = new ElectrolyzerBlock();
    public static BlockEntityType<ElectrolyzerTile> ELECTROLYZER_TILE = BlockEntityType.Builder.of(ElectrolyzerTile::new,VEBlocks.ELECTROLYZER_BLOCK).build(null);
    public static MenuType<ElectrolyzerContainer> ELECTROLYZER_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectrolyzerContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            });

    // Centrifugal Agitator
    public static CentrifugalAgitatorBlock CENTRIFUGAL_AGITATOR_BLOCK = new CentrifugalAgitatorBlock();
    public static BlockEntityType<CentrifugalAgitatorTile> CENTRIFUGAL_AGITATOR_TILE = BlockEntityType.Builder.of(CentrifugalAgitatorTile::new,VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK).build(null);
    public static MenuType<CentrifugalAgitatorContainer> CENTRIFUGAL_AGITATOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalAgitatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Compressor
    public static CompressorBlock COMPRESSOR_BLOCK = new CompressorBlock();
    public static BlockEntityType<CompressorTile> COMPRESSOR_TILE = BlockEntityType.Builder.of(CompressorTile::new,VEBlocks.COMPRESSOR_BLOCK).build(null);
    public static MenuType<CompressorContainer> COMPRESSOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Stirling Generator
    public static StirlingGeneratorBlock STIRLING_GENERATOR_BLOCK = new StirlingGeneratorBlock();
    public static BlockEntityType<StirlingGeneratorTile> STIRLING_GENERATOR_TILE = BlockEntityType.Builder.of(StirlingGeneratorTile::new,VEBlocks.STIRLING_GENERATOR_BLOCK).build(null);
    public static MenuType<StirlingGeneratorContainer> STIRLING_GENERATOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new StirlingGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Combustion Generator
    public static CombustionGeneratorBlock COMBUSTION_GENERATOR_BLOCK = new CombustionGeneratorBlock();
    public static BlockEntityType<CombustionGeneratorTile> COMBUSTION_GENERATOR_TILE = BlockEntityType.Builder.of(CombustionGeneratorTile::new,VEBlocks.COMBUSTION_GENERATOR_BLOCK).build(null);
    public static MenuType<CombustionGeneratorContainer> COMBUSTION_GENERATOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CombustionGeneratorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Aqueoulizer
    public static AqueoulizerBlock AQUEOULIZER_BLOCK = new AqueoulizerBlock();
    public static BlockEntityType<AqueoulizerTile> AQUEOULIZER_TILE = BlockEntityType.Builder.of(AqueoulizerTile::new,VEBlocks.AQUEOULIZER_BLOCK).build(null);
    public static MenuType<AqueoulizerContainer> AQUEOULIZER_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AqueoulizerContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Air Compressor
    public static AirCompressorBlock AIR_COMPRESSOR_BLOCK = new AirCompressorBlock();
    public static BlockEntityType<AirCompressorTile> AIR_COMPRESSOR_TILE = BlockEntityType.Builder.of(AirCompressorTile::new,VEBlocks.AIR_COMPRESSOR_BLOCK).build(null);
    public static MenuType<AirCompressorContainer> AIR_COMPRESSOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AirCompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Distillation Unit
    public static DistillationUnitBlock DISTILLATION_UNIT_BLOCK = new DistillationUnitBlock();
    public static BlockEntityType<DistillationUnitTile> DISTILLATION_UNIT_TILE = BlockEntityType.Builder.of(DistillationUnitTile::new,VEBlocks.DISTILLATION_UNIT_BLOCK).build(null);
    public static MenuType<DistillationUnitContainer> DISTILLATION_UNIT_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new DistillationUnitContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Pump
    public static PumpBlock PUMP_BLOCK = new PumpBlock();
    public static BlockEntityType<PumpTile> PUMP_TILE = BlockEntityType.Builder.of(PumpTile::new,VEBlocks.PUMP_BLOCK).build(null);
    public static MenuType<PumpContainer> PUMP_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PumpContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Gas Fired Furnace
    public static GasFiredFurnaceBlock GAS_FIRED_FURNACE_BLOCK = new GasFiredFurnaceBlock();
    public static BlockEntityType<GasFiredFurnaceTile> GAS_FIRED_FURNACE_TILE = BlockEntityType.Builder.of(GasFiredFurnaceTile::new,VEBlocks.GAS_FIRED_FURNACE_BLOCK).build(null);
    public static MenuType<GasFiredFurnaceContainer> GAS_FIRED_FURNACE_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new GasFiredFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Electric Furnace
    public static ElectricFurnaceBlock ELECTRIC_FURNACE_BLOCK = new ElectricFurnaceBlock();
    public static BlockEntityType<ElectricFurnaceTile> ELECTRIC_FURNACE_TILE = BlockEntityType.Builder.of(ElectricFurnaceTile::new,VEBlocks.ELECTRIC_FURNACE_BLOCK).build(null);
    public static MenuType<ElectricFurnaceContainer> ELECTRIC_FURNACE_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectricFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Battery Box
    public static BatteryBoxBlock BATTERY_BOX_BLOCK = new BatteryBoxBlock();
    public static BlockEntityType<BatteryBoxTile> BATTERY_BOX_TILE = BlockEntityType.Builder.of(BatteryBoxTile::new,VEBlocks.BATTERY_BOX_BLOCK).build(null);
    public static MenuType<BatteryBoxContainer> BATTERY_BOX_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new BatteryBoxContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Primitive Solar Panel
    public static PrimitiveSolarPanelBlock PRIMITIVE_SOLAR_PANEL_BLOCK = new PrimitiveSolarPanelBlock();
    public static BlockEntityType<PrimitiveSolarPanelTile> PRIMITIVE_SOLAR_PANEL_TILE = BlockEntityType.Builder.of(PrimitiveSolarPanelTile::new,VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK).build(null);
    public static MenuType<PrimitiveSolarPanelContainer> PRIMITIVE_SOLAR_PANEL_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveSolarPanelContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Solar Panel
    public static SolarPanelBlock SOLAR_PANEL_BLOCK = new SolarPanelBlock();
    public static BlockEntityType<SolarPanelTile> SOLAR_PANEL_TILE = BlockEntityType.Builder.of(SolarPanelTile::new,VEBlocks.SOLAR_PANEL_BLOCK).build(null);
    public static MenuType<SolarPanelContainer> SOLAR_PANEL_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SolarPanelContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Centrifugal Separator
    public static CentrifugalSeparatorBlock CENTRIFUGAL_SEPARATOR_BLOCK = new CentrifugalSeparatorBlock();
    public static BlockEntityType<CentrifugalSeparatorTile> CENTRIFUGAL_SEPARATOR_TILE = BlockEntityType.Builder.of(CentrifugalSeparatorTile::new,VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK).build(null);
    public static MenuType<CentrifugalSeparatorContainer> CENTRIFUGAL_SEPARATOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalSeparatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Implosion Compressor
    public static ImplosionCompressorBlock IMPLOSION_COMPRESSOR_BLOCK = new ImplosionCompressorBlock();
    public static BlockEntityType<ImplosionCompressorTile> IMPLOSION_COMPRESSOR_TILE = BlockEntityType.Builder.of(ImplosionCompressorTile::new,VEBlocks.IMPLOSION_COMPRESSOR_BLOCK).build(null);
    public static MenuType<ImplosionCompressorContainer> IMPLOSION_COMPRESSOR_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ImplosionCompressorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Blast Furnace
    public static BlastFurnaceBlock BLAST_FURNACE_BLOCK = new BlastFurnaceBlock();
    public static BlockEntityType<BlastFurnaceTile> BLAST_FURNACE_TILE = BlockEntityType.Builder.of(BlastFurnaceTile::new,VEBlocks.BLAST_FURNACE_BLOCK).build(null);
    public static MenuType<BlastFurnaceContainer> BLAST_FURNACE_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new BlastFurnaceContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Tooling Station
    public static ToolingStationBlock TOOLING_STATION_BLOCK = new ToolingStationBlock();
    public static BlockEntityType<ToolingStationTile> TOOLING_STATION_TILE = BlockEntityType.Builder.of(ToolingStationTile::new,VEBlocks.TOOLING_STATION_BLOCK).build(null);
    public static MenuType<ToolingStationContainer> TOOLING_STATION_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ToolingStationContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Sawmill
    public static SawmillBlock SAWMILL_BLOCK = new SawmillBlock();
    public static BlockEntityType<SawmillTile> SAWMILL_TILE = BlockEntityType.Builder.of(SawmillTile::new,VEBlocks.SAWMILL_BLOCK).build(null);
    public static MenuType<SawmillContainer> SAWMILL_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SawmillContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Tanks (Tile/Block)

    // Aluminum Tank
    public static AluminumTankBlock ALUMINUM_TANK_BLOCK = new AluminumTankBlock();
    public static BlockEntityType<AluminumTankTile> ALUMINUM_TANK_TILE = BlockEntityType.Builder.of(AluminumTankTile::new, VEBlocks.ALUMINUM_TANK_BLOCK).build(null);
    public static MenuType<AluminumTankContainer> ALUMINUM_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new AluminumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Titanium Tank
    public static TitaniumTankBlock TITANIUM_TANK_BLOCK = new TitaniumTankBlock();
    public static BlockEntityType<TitaniumTankTile> TITANIUM_TANK_TILE = BlockEntityType.Builder.of(TitaniumTankTile::new,VEBlocks.TITANIUM_TANK_BLOCK).build(null);
    public static MenuType<TitaniumTankContainer> TITANIUM_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new TitaniumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Netherite Tank
    public static NetheriteTankBlock NETHERITE_TANK_BLOCK = new NetheriteTankBlock();
    public static BlockEntityType<NetheriteTankTile> NETHERITE_TANK_TILE = BlockEntityType.Builder.of(NetheriteTankTile::new,VEBlocks.NETHERITE_TANK_BLOCK).build(null);
    public static MenuType<NetheriteTankContainer> NETHERITE_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new NetheriteTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Nighalite Tank
    public static NighaliteTankBlock NIGHALITE_TANK_BLOCK = new NighaliteTankBlock();
    public static BlockEntityType<NighaliteTankTile> NIGHALITE_TANK_TILE = BlockEntityType.Builder.of(NighaliteTankTile::new,VEBlocks.NIGHALITE_TANK_BLOCK).build(null);
    public static MenuType<NighaliteTankContainer> NIGHALITE_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new NighaliteTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Eighzo Tank
    public static EighzoTankBlock EIGHZO_TANK_BLOCK = new EighzoTankBlock();
    public static BlockEntityType<EighzoTankTile> EIGHZO_TANK_TILE = BlockEntityType.Builder.of(EighzoTankTile::new,VEBlocks.EIGHZO_TANK_BLOCK).build(null);
    public static MenuType<EighzoTankContainer> EIGHZO_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new EighzoTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    // Solarium Tank
    public static SolariumTankBlock SOLARIUM_TANK_BLOCK = new SolariumTankBlock();
    public static BlockEntityType<SolariumTankTile> SOLARIUM_TANK_TILE = BlockEntityType.Builder.of(SolariumTankTile::new,VEBlocks.SOLARIUM_TANK_BLOCK).build(null);
    public static MenuType<SolariumTankContainer> SOLARIUM_TANK_CONTAINER =
            IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new SolariumTankContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            });

    //Ores
    public static SaltpeterOre SALTPETER_ORE = new SaltpeterOre();

    public static BauxiteOre BAUXITE_ORE = new BauxiteOre();

    public static CinnabarOre CINNABAR_ORE = new CinnabarOre();

    public static RutileOre RUTILE_ORE = new RutileOre();

    public static GalenaOre GALENA_ORE = new GalenaOre();

    public static EighzoOre EIGHZO_ORE = new EighzoOre();

    // Deepslate ores
    public static DeepslateBauxiteOre DEEPSLATE_BAUXITE_ORE = new DeepslateBauxiteOre();

    public static DeepslateCinnabarOre DEEPSLATE_CINNABAR_ORE = new DeepslateCinnabarOre();

    public static DeepslateRutileOre DEEPSLATE_RUTILE_ORE = new DeepslateRutileOre();

    public static DeepslateGalenaOre DEEPSLATE_GALENA_ORE = new DeepslateGalenaOre();

    public static RedSaltpeterOre RED_SALTPETER_ORE = new RedSaltpeterOre();

    //Crops
    //@ObjectHolder(registryName = "", value =  "water_crop")
    //public static VEWaterCrop WATER_CROP;

    @Deprecated
    public static VELandCrop LAND_CROP; // Unused

    public static RiceCrop RICE_CROP = new RiceCrop();

    // Material Storage Blocks
    public static SolariumBlock SOLARIUM_BLOCK = new SolariumBlock();

    public static AluminumBlock ALUMINUM_BLOCK = new AluminumBlock();

    public static CarbonBlock CARBON_BLOCK = new CarbonBlock();

    public static EighzoBlock EIGHZO_BLOCK = new EighzoBlock();

    public static NighaliteBlock NIGHALITE_BLOCK = new NighaliteBlock();

    public static SaltpeterBlock SALTPETER_BLOCK = new SaltpeterBlock();

    public static TitaniumBlock TITANIUM_BLOCK = new TitaniumBlock();

    public static TungstenBlock TUNGSTEN_BLOCK = new TungstenBlock();

    public static TungstenSteelBlock TUNGSTEN_STEEL_BLOCK = new TungstenSteelBlock();

    // Raw Material Storage Blocks
    public static RawBauxiteBlock RAW_BAUXITE_BLOCK = new RawBauxiteBlock();

    public static RawCinnabarBlock RAW_CINNABAR_BLOCK = new RawCinnabarBlock();

    public static RawEighzoBlock RAW_EIGHZO_BLOCK = new RawEighzoBlock();

    public static RawGalenaBlock RAW_GALENA_BLOCK = new RawGalenaBlock();

    public static RawRutileBlock RAW_RUTILE_BLOCK = new RawRutileBlock();

    @Deprecated
    public static RawBoneBlock RAW_BONE_BLOCK = new RawBoneBlock(); // Unused

    public static PressureLadder PRESSURE_LADDER = new PressureLadder();
}
