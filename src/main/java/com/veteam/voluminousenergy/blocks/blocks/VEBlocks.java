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
import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntities;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(VoluminousEnergy.MODID)
public class VEBlocks {

    public static final List<RegistryWithName> REGISTERED_BLOCKS = new ArrayList<>();

    public static final DeferredRegister<Block> VE_BLOCKS_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, VoluminousEnergy.MODID);
    public static final DeferredRegister<BlockEntityType<?>> VE_TILE_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoluminousEnergy.MODID);
    public static final DeferredRegister<MenuType<?>> VE_CONTAINER_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoluminousEnergy.MODID);

    // Shells
    public static RegistryObject<Block> ALUMINUM_SHELL = registerWithBlockItemSupport("aluminum_shell", AluminumShellBlock::new);

    // Machine Frames
    public static RegistryObject<Block> CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME = registerWithBlockItemSupport("carbon_shielded_aluminum_machine_frame", CarbonShieldedAluminumMachineFrame::new);

    // Casings (For multiblocks)
    public static RegistryObject<Block> ALUMINUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("aluminum_machine_casing", AluminumMachineCasingBlock::new);

    public static RegistryObject<Block> TITANIUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("titanium_machine_casing", TitaniumMachineCasingBlock::new);

    public static RegistryObject<Block> SOLARIUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("solarium_machine_casing", SolariumMachineCasingBlock::new);

    //Primitive Blast
    public static final BlockTileMenuRegistry PRIMITIVE_BLAST_FURNACE =
            new BlockTileMenuRegistry("primitiveblastfurnace",
                    PrimitiveBlastFurnaceBlock::new,
                    () -> VETileEntities.PRIMITIVE_BLAST_FURNACE_FACTORY,
                    () -> VEContainers.PRIMITIVE_BLAST_FURNACE_FACTORY);

    //Primitive Stirling
    public static final BlockTileMenuRegistry PRIMITIVE_STIRLING_GENERATOR =
            new BlockTileMenuRegistry("primitivestirlinggenerator",
                    PrimitiveStirlingGeneratorBlock::new,
                    () -> VETileEntities.PRIMITIVE_STIRLING_GENERATOR_TILE_FACTORY,
                    () -> VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY);

    //Crusher
    public static final BlockTileMenuRegistry CRUSHER =
            new BlockTileMenuRegistry("crusher",
                    CrusherBlock::new,
                    () -> VETileEntities.CRUSHER_FACTORY,
                    () -> VEContainers.CRUSHER_FACTORY);

    //Electrolyzer
    public static final BlockTileMenuRegistry ELECTROLYZER =
            new BlockTileMenuRegistry("electrolyzer",
                    ElectrolyzerBlock::new,
                    () -> VETileEntities.ELECTROLYZER_FACTORY,
                    () -> VEContainers.ELECTROLYZER_FACTORY);

    // Centrifugal Agitator
    public static final BlockTileMenuRegistry CENTRIFUGAL_AGITATOR =
            new BlockTileMenuRegistry("centrifugal_agitator",
                    CentrifugalAgitatorBlock::new,
                    () -> VETileEntities.CENTRIFUGAL_AGITATOR_FACTORY,
                    () -> VEContainers.CENTRIFUGAL_AGITATOR_FACTORY);

    // Compressor
    public static final BlockTileMenuRegistry COMPRESSOR =
            new BlockTileMenuRegistry("compressor",
                    CompressorBlock::new,
                    () -> VETileEntities.COMPRESSOR_FACTORY,
                    () -> VEContainers.COMPRESSOR_FACTORY);

    // Stirling Generator
    public static final BlockTileMenuRegistry STIRLING_GENERATOR =
            new BlockTileMenuRegistry("stirling_generator",
                    StirlingGeneratorBlock::new,
                    () -> VETileEntities.STIRLING_GENERATOR_FACTORY,
                    () -> VEContainers.STIRLING_GENERATOR_FACTORY);

    // Combustion Generator
    public static final BlockTileMenuRegistry COMBUSTION_GENERATOR =
            new BlockTileMenuRegistry("combustion_generator",
                    CombustionGeneratorBlock::new,
                    () -> VETileEntities.COMBUSTION_GENERATOR_FACTORY,
                    () -> VEContainers.COMBUSTION_GENERATOR_FACTORY);

    // Aqueoulizer
    public static final BlockTileMenuRegistry AQUEOULIZER =
            new BlockTileMenuRegistry("aqueoulizer",
                    AqueoulizerBlock::new,
                    () -> VETileEntities.AQUEOULIZER_TILE_FACTORY,
                    () -> VEContainers.AQUEOULIZER_FACTORY);

    // Air Compressor
    public static final BlockTileMenuRegistry AIR_COMPRESSOR =
            new BlockTileMenuRegistry("air_compressor",
                    AirCompressorBlock::new,
                    () -> VETileEntities.AIR_COMPRESSOR_FACTORY,
                    () -> VEContainers.AIR_COMPRESSOR_FACTORY);

    // Distillation Unit
    public static final BlockTileMenuRegistry DISTILLATION_UNIT =
            new BlockTileMenuRegistry("distillation_unit",
                    DistillationUnitBlock::new,
                    () -> VETileEntities.DISTILLATION_UNIT_FACTORY,
                    () -> VEContainers.DISTILLATION_UNIT_FACTORY);

    // Pump
    public static final BlockTileMenuRegistry PUMP =
            new BlockTileMenuRegistry("pump",
                    PumpBlock::new,
                    () -> VETileEntities.PUMP_FACTORY,
                    () -> VEContainers.PUMP_FACTORY);

    // Gas Fired Furnace
    public static final BlockTileMenuRegistry GAS_FIRED_FURNACE =
            new BlockTileMenuRegistry("gas_fired_furnace",
                    GasFiredFurnaceBlock::new,
                    () -> VETileEntities.GAS_FIRED_FURNACE,
                    () -> VEContainers.GAS_FIRED_FURNACE_FACTORY);

    // Electric Furnace
    public static final BlockTileMenuRegistry ELECTRIC_FURNACE =
            new BlockTileMenuRegistry("electric_furnace",
                    ElectricFurnaceBlock::new,
                    () -> VETileEntities.ELECTRIC_FURNACE_FACTORY,
                    () -> VEContainers.ELECTRIC_FURNACE_FACTORY);

    // Battery Box
    public static final BlockTileMenuRegistry BATTERY_BOX =
            new BlockTileMenuRegistry("battery_box",
                    BatteryBoxBlock::new,
                    () -> VETileEntities.BATTERY_BOX_FACTORY,
                    () -> VEContainers.BATTERY_BOX_FACTORY);

    // Primitive Solar Panel
    public static final BlockTileMenuRegistry PRIMITIVE_SOLAR_PANEL =
            new BlockTileMenuRegistry("primitive_solar_panel",
                    PrimitiveSolarPanelBlock::new,
                    () -> VETileEntities.PRIMITIVE_SOLAR_PANEL_FACTORY,
                    () -> VEContainers.PRIMITIVE_SOLAR_PANEL_FACTORY);

    // Solar Panel
    public static final BlockTileMenuRegistry SOLAR_PANEL =
            new BlockTileMenuRegistry("solar_panel",
                    SolarPanelBlock::new,
                    () -> VETileEntities.SOLAR_PANEL_FACTORY,
                    () -> VEContainers.SOLAR_PANEL_FACTORY);

    // Centrifugal Separator
    public static final BlockTileMenuRegistry CENTRIFUGAL_SEPARATOR =
            new BlockTileMenuRegistry("centrifugal_separator",
                    CentrifugalSeparatorBlock::new,
                    () -> VETileEntities.CENTRIFUGAL_SEPARATOR_FACTORY,
                    () -> VEContainers.CENTRIFUGAL_SEPARATOR_FACTORY);

    // Implosion Compressor
    public static final BlockTileMenuRegistry IMPLOSION_COMPRESSOR =
            new BlockTileMenuRegistry("implosion_compressor",
                    ImplosionCompressorBlock::new,
                    () -> VETileEntities.IMPLOSION_COMPRESSOR_FACTORY,
                    () -> VEContainers.IMPLOSION_COMPRESSOR_FACTORY);

    // Blast Furnace
    public static final BlockTileMenuRegistry BLAST_FURNACE =
            new BlockTileMenuRegistry("blast_furnace",
                    BlastFurnaceBlock::new,
                    () -> VETileEntities.BLAST_FURNACE_FACTORY,
                    () -> VEContainers.BLAST_FURNACE_FACTORY);

    // Tooling Station
    public static final BlockTileMenuRegistry TOOLING_STATION =
            new BlockTileMenuRegistry("tooling_station",
                    ToolingStationBlock::new,
                    () -> VETileEntities.TOOLING_STATION_FACTORY,
                    () -> VEContainers.TOOLING_STATION_FACTORY);

    // Sawmill
    public static final BlockTileMenuRegistry SAWMILL =
            new BlockTileMenuRegistry("sawmill",
                    SawmillBlock::new,
                    () -> VETileEntities.SAWMILL_FACTORY,
                    () -> VEContainers.SAWMILL_FACTORY);

    // Dimensional Laser
    public static final BlockTileMenuRegistry DIMENSIONAL_LASER =
            new BlockTileMenuRegistry("dimensional_laser",
                    DimensionalLaserBlock::new,
                    () -> VETileEntities.DIMENSIONAL_LASER_FACTORY,
                    () -> VEContainers.DIMENSIONAL_LASER_FACTORY);

    // Fluid Electrolyzer
    public static final BlockTileMenuRegistry FLUID_ELECTROLYZER =
            new BlockTileMenuRegistry("fluid_electrolyzer",
                    FluidElectrolyzerBlock::new,
                    () -> VETileEntities.FLUID_ELECTROLYZER_FACTORY,
                    () -> VEContainers.FLUID_ELECTROLYZER_FACTORY);

    // Fluid Mixer
    public static final BlockTileMenuRegistry FLUID_MIXER =
            new BlockTileMenuRegistry("fluid_mixer",
                    FluidMixerBlock::new,
                    () -> VETileEntities.FLUID_MIXER_FACTORY,
                    () -> VEContainers.FLUID_MIXER_FACTORY);

    // Hydroponic Incubator
    public static final BlockTileMenuRegistry HYDROPONIC_INCUBATOR =
            new BlockTileMenuRegistry("hydroponic_incubator",
                    HydroponicIncubatorBlock::new,
                    () -> VETileEntities.HYDROPONIC_INCUBATOR_FACTORY,
                    () -> VEContainers.HYDROPONIC_INCUBATOR_FACTORY);


    // Tanks (Tile/Block)

    // Aluminum Tank
    public static final BlockTileMenuRegistry ALUMINUM_TANK =
            new BlockTileMenuRegistry("aluminum_tank",
                    AluminumTankBlock::new,
                    () -> VETileEntities.ALUMINUM_TANK_FACTORY,
                    () -> VEContainers.ALUMINUM_TANK_FACTORY);

    // Titanium Tank
    public static final BlockTileMenuRegistry TITANIUM_TANK =
            new BlockTileMenuRegistry("titanium_tank",
                    TitaniumTankBlock::new,
                    () -> VETileEntities.TITANIUM_TANK_FACTORY,
                    () -> VEContainers.TITANIUM_TANK_FACTORY);

    // Netherite Tank
    public static final BlockTileMenuRegistry NETHERITE_TANK =
            new BlockTileMenuRegistry("netherite_tank",
                    NetheriteTankBlock::new,
                    () -> VETileEntities.NETHERITE_TANK_FACTORY,
                    () -> VEContainers.NETHERITE_TANK_FACTORY);

    // Nighalite Tank
    public static final BlockTileMenuRegistry NIGHALITE_TANK =
            new BlockTileMenuRegistry("nighalite_tank",
                    NighaliteTankBlock::new,
                    () -> VETileEntities.NIGHALITE_TANK_FACTORY,
                    () -> VEContainers.NIGHALITE_TANK_FACTORY);

    // Eighzo Tank
    public static final BlockTileMenuRegistry EIGHZO_TANK =
            new BlockTileMenuRegistry("eighzo_tank",
                    EighzoTankBlock::new,
                    () -> VETileEntities.EIGHZO_TANK_FACTORY,
                    () -> VEContainers.EIGHZO_TANK_FACTORY);

    // Solarium Tank
    public static final BlockTileMenuRegistry SOLARIUM_TANK =
            new BlockTileMenuRegistry("solarium_tank",
                    SolariumTankBlock::new,
                    () -> VETileEntities.SOLARIUM_TANK_FACTORY,
                    () -> VEContainers.SOLARIUM_TANK_FACTORY);

    // Dimensional Laser Stuff

    public static RegistryObject<Block> DIMENSIONAL_LASER_PYLON = registerWithBlockItemSupport("laser_pylon", () -> new FaceableBlock(
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.METAL)
                            .strength(2.0f)
                            .lightLevel(l -> 0)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                    , "laser_pylon") {
            }
    );

    //Ores
    public static RegistryObject<Block> SALTPETER_ORE = registerWithBlockItemSupport("saltpeterore", SaltpeterOre::new);

    public static RegistryObject<Block> BAUXITE_ORE = registerWithBlockItemSupport("bauxiteore", BauxiteOre::new);

    public static RegistryObject<Block> CINNABAR_ORE = registerWithBlockItemSupport("cinnabarore", CinnabarOre::new);

    public static RegistryObject<Block> RUTILE_ORE = registerWithBlockItemSupport("rutileore", RutileOre::new);

    public static RegistryObject<Block> GALENA_ORE = registerWithBlockItemSupport("galena_ore", GalenaOre::new);

    public static RegistryObject<Block> EIGHZO_ORE = registerWithBlockItemSupport("eighzo_ore", EighzoOre::new);

    // Deepslate ores
    public static RegistryObject<Block> DEEPSLATE_BAUXITE_ORE = registerWithBlockItemSupport("deepslate_bauxite_ore", DeepslateBauxiteOre::new);

    public static RegistryObject<Block> DEEPSLATE_CINNABAR_ORE = registerWithBlockItemSupport("deepslate_cinnabar_ore", DeepslateCinnabarOre::new);

    public static RegistryObject<Block> DEEPSLATE_RUTILE_ORE = registerWithBlockItemSupport("deepslate_rutile_ore", DeepslateRutileOre::new);

    public static RegistryObject<Block> DEEPSLATE_GALENA_ORE = registerWithBlockItemSupport("deepslate_galena_ore", DeepslateGalenaOre::new);

    public static RegistryObject<Block> RED_SALTPETER_ORE = registerWithBlockItemSupport("red_saltpeter_ore", RedSaltpeterOre::new);

    //Crops
    //public static VEWaterCrop WATER_CROP;

    //public static VELandCrop LAND_CROP;

    public static RegistryObject<Block> RICE_CROP = registerWithBlockItemSupport("rice_crop", RiceCrop::new);

    // Material Storage Blocks
    public static RegistryObject<Block> SOLARIUM_BLOCK = registerWithBlockItemSupport("solarium_block", SolariumBlock::new);

    public static RegistryObject<Block> ALUMINUM_BLOCK = registerWithBlockItemSupport("aluminum_block", AluminumBlock::new);

    public static RegistryObject<Block> CARBON_BLOCK = registerWithBlockItemSupport("carbon_block", CarbonBlock::new);

    public static RegistryObject<Block> EIGHZO_BLOCK = registerWithBlockItemSupport("eighzo_block", EighzoBlock::new);

    public static RegistryObject<Block> NIGHALITE_BLOCK = registerWithBlockItemSupport("nighalite_block", NighaliteBlock::new);

    public static RegistryObject<Block> SALTPETER_BLOCK = registerWithBlockItemSupport("saltpeter_block", SaltpeterBlock::new);

    public static RegistryObject<Block> TITANIUM_BLOCK = registerWithBlockItemSupport("titanium_block", TitaniumBlock::new);

    public static RegistryObject<Block> TUNGSTEN_BLOCK = registerWithBlockItemSupport("tungsten_block", TungstenBlock::new);

    public static RegistryObject<Block> TUNGSTEN_STEEL_BLOCK = registerWithBlockItemSupport("tungsten_steel_block", TungstenSteelBlock::new);

    // Raw Material Storage Blocks
    public static RegistryObject<Block> RAW_BAUXITE_BLOCK = registerWithBlockItemSupport("raw_bauxite_block", RawBauxiteBlock::new);

    public static RegistryObject<Block> RAW_CINNABAR_BLOCK = registerWithBlockItemSupport("raw_cinnabar_block", RawCinnabarBlock::new);

    public static RegistryObject<Block> RAW_EIGHZO_BLOCK = registerWithBlockItemSupport("raw_eighzo_block", RawEighzoBlock::new);

    public static RegistryObject<Block> RAW_GALENA_BLOCK = registerWithBlockItemSupport("raw_galena_block", RawGalenaBlock::new);

    public static RegistryObject<Block> RAW_RUTILE_BLOCK = registerWithBlockItemSupport("raw_rutile_block", RawRutileBlock::new);

    @Deprecated
    public static RegistryObject<Block> RAW_BONE_BLOCK = registerWithBlockItemSupport("raw_bone_block", RawBoneBlock::new); // Unused

    public static RegistryObject<Block> PRESSURE_LADDER = registerWithBlockItemSupport("pressure_ladder", PressureLadder::new);

    public static class BlockTileMenuRegistry {

        RegistryObject<Block> block;
        RegistryObject<BlockEntityType<VETileEntity>> tile;
        RegistryObject<MenuType<VEContainer>> container;

        public BlockTileMenuRegistry(String name, Supplier<Block> blockSupplier, Supplier<VETileEntityFactory> tileEntityFactory, Supplier<VEContainerFactory> containerFactory) {
            block = registerWithBlockItemSupport(name, blockSupplier);
            tile = VE_TILE_REGISTRY.register(name,
                    () -> BlockEntityType.Builder.of(tileEntityFactory.get()::create, block.get()).build(null));
            container = VE_CONTAINER_REGISTRY.register(name, () ->
                    IForgeMenuType.create((id, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        return containerFactory.get().create(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
                    }));
        }

        public RegistryObject<Block> block() {
            return this.block;
        }

        public RegistryObject<BlockEntityType<VETileEntity>> tile() {
            return this.tile;
        }

        public RegistryObject<MenuType<VEContainer>> container() {
            return this.container;
        }
    }

    public record RegistryWithName(String name, RegistryObject<Block> block) {
    }

    public static RegistryObject<Block> registerWithBlockItemSupport(String name, Supplier<Block> blockSupplier) {
        RegistryObject<Block> registryObject = VE_BLOCKS_REGISTRY.register(name, blockSupplier);
        REGISTERED_BLOCKS.add(new RegistryWithName(name, registryObject));
        return registryObject;
    }
}
