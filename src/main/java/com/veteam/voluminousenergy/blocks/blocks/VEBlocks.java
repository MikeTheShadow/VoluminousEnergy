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
import com.veteam.voluminousenergy.util.VEClientSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(VoluminousEnergy.MODID)
public class VEBlocks {

    public static final List<RegistryWithName> REGISTERED_BLOCKS = new ArrayList<>();

    public static final DeferredRegister<Block> VE_BLOCKS_REGISTRY = DeferredRegister.create(Registries.BLOCK, VoluminousEnergy.MODID);
    public static final DeferredRegister<BlockEntityType<?>> VE_TILE_REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VoluminousEnergy.MODID);
    public static final DeferredRegister<MenuType<?>> VE_CONTAINER_REGISTRY = DeferredRegister.create(Registries.MENU, VoluminousEnergy.MODID);

    // Shells
    public static Supplier<Block> ALUMINUM_SHELL = registerWithBlockItemSupport("aluminum_shell", AluminumShellBlock::new);

    // Machine Frames
    public static Supplier<Block> CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME = registerWithBlockItemSupport("carbon_shielded_aluminum_machine_frame", CarbonShieldedAluminumMachineFrame::new);

    // Casings (For multiblocks)
    public static Supplier<Block> ALUMINUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("aluminum_machine_casing", AluminumMachineCasingBlock::new);

    public static Supplier<Block> TITANIUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("titanium_machine_casing", TitaniumMachineCasingBlock::new);

    public static Supplier<Block> SOLARIUM_MACHINE_CASING_BLOCK = registerWithBlockItemSupport("solarium_machine_casing", SolariumMachineCasingBlock::new);

    //Primitive Blast
    public static final BlockTileMenuRegistry PRIMITIVE_BLAST_FURNACE =
            new BlockTileMenuRegistry("primitiveblastfurnace",
                    PrimitiveBlastFurnaceBlock::new,
                    () -> VETileEntities.PRIMITIVE_BLAST_FURNACE_FACTORY,
                    () -> VEContainers.PRIMITIVE_BLAST_FURNACE_FACTORY
                    ,false,false,true);

    //Primitive Stirling
    public static final BlockTileMenuRegistry PRIMITIVE_STIRLING_GENERATOR =
            new BlockTileMenuRegistry("primitivestirlinggenerator",
                    PrimitiveStirlingGeneratorBlock::new,
                    () -> VETileEntities.PRIMITIVE_STIRLING_GENERATOR_TILE_FACTORY,
                    () -> VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY
                    ,true,false,true);

    //Crusher
    public static final BlockTileMenuRegistry CRUSHER =
            new BlockTileMenuRegistry("crusher",
                    CrusherBlock::new,
                    () -> VETileEntities.CRUSHER_FACTORY,
                    () -> VEContainers.CRUSHER_FACTORY
                    ,true,false,true);

    //Electrolyzer
    public static final BlockTileMenuRegistry ELECTROLYZER =
            new BlockTileMenuRegistry("electrolyzer",
                    ElectrolyzerBlock::new,
                    () -> VETileEntities.ELECTROLYZER_FACTORY,
                    () -> VEContainers.ELECTROLYZER_FACTORY
                    ,true,false,true);

    // Centrifugal Agitator
    public static final BlockTileMenuRegistry CENTRIFUGAL_AGITATOR =
            new BlockTileMenuRegistry("centrifugal_agitator",
                    CentrifugalAgitatorBlock::new,
                    () -> VETileEntities.CENTRIFUGAL_AGITATOR_FACTORY,
                    () -> VEContainers.CENTRIFUGAL_AGITATOR_FACTORY
                    ,true,true,true);

    // Compressor
    public static final BlockTileMenuRegistry COMPRESSOR =
            new BlockTileMenuRegistry("compressor",
                    CompressorBlock::new,
                    () -> VETileEntities.COMPRESSOR_FACTORY,
                    () -> VEContainers.COMPRESSOR_FACTORY
                    ,true,false,true);

    // Stirling Generator
    public static final BlockTileMenuRegistry STIRLING_GENERATOR =
            new BlockTileMenuRegistry("stirling_generator",
                    StirlingGeneratorBlock::new,
                    () -> VETileEntities.STIRLING_GENERATOR_FACTORY,
                    () -> VEContainers.STIRLING_GENERATOR_FACTORY
                    ,true,false,true);

    // Combustion Generator
    public static final BlockTileMenuRegistry COMBUSTION_GENERATOR =
            new BlockTileMenuRegistry("combustion_generator",
                    CombustionGeneratorBlock::new,
                    () -> VETileEntities.COMBUSTION_GENERATOR_FACTORY,
                    () -> VEContainers.COMBUSTION_GENERATOR_FACTORY
                    ,true,true,true);

    // Aqueoulizer
    public static final BlockTileMenuRegistry AQUEOULIZER =
            new BlockTileMenuRegistry("aqueoulizer",
                    AqueoulizerBlock::new,
                    () -> VETileEntities.AQUEOULIZER_TILE_FACTORY,
                    () -> VEContainers.AQUEOULIZER_FACTORY
                    ,true,true,true);

    // Air Compressor
    public static final BlockTileMenuRegistry AIR_COMPRESSOR =
            new BlockTileMenuRegistry("air_compressor",
                    AirCompressorBlock::new,
                    () -> VETileEntities.AIR_COMPRESSOR_FACTORY,
                    () -> VEContainers.AIR_COMPRESSOR_FACTORY
                    ,true,true,true);

    // Distillation Unit
    public static final BlockTileMenuRegistry DISTILLATION_UNIT =
            new BlockTileMenuRegistry("distillation_unit",
                    DistillationUnitBlock::new,
                    () -> VETileEntities.DISTILLATION_UNIT_FACTORY,
                    () -> VEContainers.DISTILLATION_UNIT_FACTORY
                    ,true,true,true);

    // Pump
    public static final BlockTileMenuRegistry PUMP =
            new BlockTileMenuRegistry("pump",
                    PumpBlock::new,
                    () -> VETileEntities.PUMP_FACTORY,
                    () -> VEContainers.PUMP_FACTORY
                    ,true,true,true);

    // Gas Fired Furnace
    public static final BlockTileMenuRegistry GAS_FIRED_FURNACE =
            new BlockTileMenuRegistry("gas_fired_furnace",
                    GasFiredFurnaceBlock::new,
                    () -> VETileEntities.GAS_FIRED_FURNACE,
                    () -> VEContainers.GAS_FIRED_FURNACE_FACTORY
                    ,false,true,true);

    // Electric Furnace
    public static final BlockTileMenuRegistry ELECTRIC_FURNACE =
            new BlockTileMenuRegistry("electric_furnace",
                    ElectricFurnaceBlock::new,
                    () -> VETileEntities.ELECTRIC_FURNACE_FACTORY,
                    () -> VEContainers.ELECTRIC_FURNACE_FACTORY
                    ,true,false,true);

    // Battery Box
    public static final BlockTileMenuRegistry BATTERY_BOX =
            new BlockTileMenuRegistry("battery_box",
                    BatteryBoxBlock::new,
                    () -> VETileEntities.BATTERY_BOX_FACTORY,
                    () -> VEContainers.BATTERY_BOX_FACTORY
                    ,true,false,true);

    // Primitive Solar Panel
    public static final BlockTileMenuRegistry PRIMITIVE_SOLAR_PANEL =
            new BlockTileMenuRegistry("primitive_solar_panel",
                    PrimitiveSolarPanelBlock::new,
                    () -> VETileEntities.PRIMITIVE_SOLAR_PANEL_FACTORY,
                    () -> VEContainers.PRIMITIVE_SOLAR_PANEL_FACTORY
                    ,true,false,false);

    // Solar Panel
    public static final BlockTileMenuRegistry SOLAR_PANEL =
            new BlockTileMenuRegistry("solar_panel",
                    SolarPanelBlock::new,
                    () -> VETileEntities.SOLAR_PANEL_FACTORY,
                    () -> VEContainers.SOLAR_PANEL_FACTORY
                    ,true,false,false);

    // Centrifugal Separator
    public static final BlockTileMenuRegistry CENTRIFUGAL_SEPARATOR =
            new BlockTileMenuRegistry("centrifugal_separator",
                    CentrifugalSeparatorBlock::new,
                    () -> VETileEntities.CENTRIFUGAL_SEPARATOR_FACTORY,
                    () -> VEContainers.CENTRIFUGAL_SEPARATOR_FACTORY
                    ,true,false,true);

    // Implosion Compressor
    public static final BlockTileMenuRegistry IMPLOSION_COMPRESSOR =
            new BlockTileMenuRegistry("implosion_compressor",
                    ImplosionCompressorBlock::new,
                    () -> VETileEntities.IMPLOSION_COMPRESSOR_FACTORY,
                    () -> VEContainers.IMPLOSION_COMPRESSOR_FACTORY
                    ,true,false,true);

    // Blast Furnace
    public static final BlockTileMenuRegistry BLAST_FURNACE =
            new BlockTileMenuRegistry("blast_furnace",
                    BlastFurnaceBlock::new,
                    () -> VETileEntities.BLAST_FURNACE_FACTORY,
                    () -> VEContainers.BLAST_FURNACE_FACTORY
                    ,true,true,true);

    // Tooling Station
    public static final BlockTileMenuRegistry TOOLING_STATION =
            new BlockTileMenuRegistry("tooling_station",
                    ToolingStationBlock::new,
                    () -> VETileEntities.TOOLING_STATION_FACTORY,
                    () -> VEContainers.TOOLING_STATION_FACTORY
                    ,true,true,true);

    // Sawmill
    public static final BlockTileMenuRegistry SAWMILL =
            new BlockTileMenuRegistry("sawmill",
                    SawmillBlock::new,
                    () -> VETileEntities.SAWMILL_FACTORY,
                    () -> VEContainers.SAWMILL_FACTORY
                    ,true,true,true);

    // Dimensional Laser
    public static final BlockTileMenuRegistry DIMENSIONAL_LASER =
            new BlockTileMenuRegistry("dimensional_laser",
                    DimensionalLaserBlock::new,
                    () -> VETileEntities.DIMENSIONAL_LASER_FACTORY,
                    () -> VEContainers.DIMENSIONAL_LASER_FACTORY
                    ,true,true,true);

    // Fluid Electrolyzer
    public static final BlockTileMenuRegistry FLUID_ELECTROLYZER =
            new BlockTileMenuRegistry("fluid_electrolyzer",
                    FluidElectrolyzerBlock::new,
                    () -> VETileEntities.FLUID_ELECTROLYZER_FACTORY,
                    () -> VEContainers.FLUID_ELECTROLYZER_FACTORY
                    ,true,true,true);

    // Fluid Mixer
    public static final BlockTileMenuRegistry FLUID_MIXER =
            new BlockTileMenuRegistry("fluid_mixer",
                    FluidMixerBlock::new,
                    () -> VETileEntities.FLUID_MIXER_FACTORY,
                    () -> VEContainers.FLUID_MIXER_FACTORY
                    ,true,true,true);

    // Hydroponic Incubator
    public static final BlockTileMenuRegistry HYDROPONIC_INCUBATOR =
            new BlockTileMenuRegistry("hydroponic_incubator",
                    HydroponicIncubatorBlock::new,
                    () -> VETileEntities.HYDROPONIC_INCUBATOR_FACTORY,
                    () -> VEContainers.HYDROPONIC_INCUBATOR_FACTORY
                    ,true,true,true);


    // Tanks (Tile/Block)

    // Aluminum Tank
    public static final BlockTileMenuRegistry ALUMINUM_TANK =
            new BlockTileMenuRegistry("aluminum_tank",
                    AluminumTankBlock::new,
                    () -> VETileEntities.ALUMINUM_TANK_FACTORY,
                    () -> VEContainers.ALUMINUM_TANK_FACTORY
                    ,false,true,true);

    // Titanium Tank
    public static final BlockTileMenuRegistry TITANIUM_TANK =
            new BlockTileMenuRegistry("titanium_tank",
                    TitaniumTankBlock::new,
                    () -> VETileEntities.TITANIUM_TANK_FACTORY,
                    () -> VEContainers.TITANIUM_TANK_FACTORY
                    ,false,true,true);

    // Netherite Tank
    public static final BlockTileMenuRegistry NETHERITE_TANK =
            new BlockTileMenuRegistry("netherite_tank",
                    NetheriteTankBlock::new,
                    () -> VETileEntities.NETHERITE_TANK_FACTORY,
                    () -> VEContainers.NETHERITE_TANK_FACTORY
                    ,false,true,true);

    // Nighalite Tank
    public static final BlockTileMenuRegistry NIGHALITE_TANK =
            new BlockTileMenuRegistry("nighalite_tank",
                    NighaliteTankBlock::new,
                    () -> VETileEntities.NIGHALITE_TANK_FACTORY,
                    () -> VEContainers.NIGHALITE_TANK_FACTORY
                    ,false,true,true);

    // Eighzo Tank
    public static final BlockTileMenuRegistry EIGHZO_TANK =
            new BlockTileMenuRegistry("eighzo_tank",
                    EighzoTankBlock::new,
                    () -> VETileEntities.EIGHZO_TANK_FACTORY,
                    () -> VEContainers.EIGHZO_TANK_FACTORY
                    ,false,true,true);

    // Solarium Tank
    public static final BlockTileMenuRegistry SOLARIUM_TANK =
            new BlockTileMenuRegistry("solarium_tank",
                    SolariumTankBlock::new,
                    () -> VETileEntities.SOLARIUM_TANK_FACTORY,
                    () -> VEContainers.SOLARIUM_TANK_FACTORY
            ,false,true,true);

    // Dimensional Laser Stuff

    public static Supplier<Block> DIMENSIONAL_LASER_PYLON = registerWithBlockItemSupport("laser_pylon", () -> new FaceableBlock(
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
    public static Supplier<Block> SALTPETER_ORE = registerWithBlockItemSupport("saltpeterore", SaltpeterOre::new);

    public static Supplier<Block> BAUXITE_ORE = registerWithBlockItemSupport("bauxiteore", BauxiteOre::new);

    public static Supplier<Block> CINNABAR_ORE = registerWithBlockItemSupport("cinnabarore", CinnabarOre::new);

    public static Supplier<Block> RUTILE_ORE = registerWithBlockItemSupport("rutileore", RutileOre::new);

    public static Supplier<Block> GALENA_ORE = registerWithBlockItemSupport("galena_ore", GalenaOre::new);

    public static Supplier<Block> EIGHZO_ORE = registerWithBlockItemSupport("eighzo_ore", EighzoOre::new);

    // Deepslate ores
    public static Supplier<Block> DEEPSLATE_BAUXITE_ORE = registerWithBlockItemSupport("deepslate_bauxite_ore", DeepslateBauxiteOre::new);

    public static Supplier<Block> DEEPSLATE_CINNABAR_ORE = registerWithBlockItemSupport("deepslate_cinnabar_ore", DeepslateCinnabarOre::new);

    public static Supplier<Block> DEEPSLATE_RUTILE_ORE = registerWithBlockItemSupport("deepslate_rutile_ore", DeepslateRutileOre::new);

    public static Supplier<Block> DEEPSLATE_GALENA_ORE = registerWithBlockItemSupport("deepslate_galena_ore", DeepslateGalenaOre::new);

    public static Supplier<Block> RED_SALTPETER_ORE = registerWithBlockItemSupport("red_saltpeter_ore", RedSaltpeterOre::new);

    //Crops
    //public static VEWaterCrop WATER_CROP;

    //public static VELandCrop LAND_CROP;

    public static Supplier<Block> RICE_CROP = registerWithNoBlockItemSupport("rice_crop", RiceCrop::new);

    // Material Storage Blocks
    public static Supplier<Block> SOLARIUM_BLOCK = registerWithBlockItemSupport("solarium_block", SolariumBlock::new);

    public static Supplier<Block> ALUMINUM_BLOCK = registerWithBlockItemSupport("aluminum_block", AluminumBlock::new);

    public static Supplier<Block> CARBON_BLOCK = registerWithBlockItemSupport("carbon_block", CarbonBlock::new);

    public static Supplier<Block> EIGHZO_BLOCK = registerWithBlockItemSupport("eighzo_block", EighzoBlock::new);

    public static Supplier<Block> NIGHALITE_BLOCK = registerWithBlockItemSupport("nighalite_block", NighaliteBlock::new);

    public static Supplier<Block> SALTPETER_BLOCK = registerWithBlockItemSupport("saltpeter_block", SaltpeterBlock::new);

    public static Supplier<Block> TITANIUM_BLOCK = registerWithBlockItemSupport("titanium_block", TitaniumBlock::new);

    public static Supplier<Block> TUNGSTEN_BLOCK = registerWithBlockItemSupport("tungsten_block", TungstenBlock::new);

    public static Supplier<Block> TUNGSTEN_STEEL_BLOCK = registerWithBlockItemSupport("tungsten_steel_block", TungstenSteelBlock::new);

    // Raw Material Storage Blocks
    public static Supplier<Block> RAW_BAUXITE_BLOCK = registerWithBlockItemSupport("raw_bauxite_block", RawBauxiteBlock::new);

    public static Supplier<Block> RAW_CINNABAR_BLOCK = registerWithBlockItemSupport("raw_cinnabar_block", RawCinnabarBlock::new);

    public static Supplier<Block> RAW_EIGHZO_BLOCK = registerWithBlockItemSupport("raw_eighzo_block", RawEighzoBlock::new);

    public static Supplier<Block> RAW_GALENA_BLOCK = registerWithBlockItemSupport("raw_galena_block", RawGalenaBlock::new);

    public static Supplier<Block> RAW_RUTILE_BLOCK = registerWithBlockItemSupport("raw_rutile_block", RawRutileBlock::new);

    @Deprecated
    public static Supplier<Block> RAW_BONE_BLOCK = registerWithBlockItemSupport("raw_bone_block", RawBoneBlock::new); // Unused

    public static Supplier<Block> PRESSURE_LADDER = registerWithBlockItemSupport("pressure_ladder", PressureLadder::new);

    public static class BlockTileMenuRegistry {

        Supplier<Block> block;
        Supplier<BlockEntityType<VETileEntity>> tile;
        Supplier<MenuType<VEContainer>> container;

        public BlockTileMenuRegistry(String name, Supplier<Block> blockSupplier, Supplier<VETileEntityFactory> tileEntityFactory, Supplier<VEContainerFactory> containerFactory,boolean hasEnergy, boolean hasFluids, boolean hasInventory) {
            block = registerWithBlockItemSupport(name, blockSupplier,hasEnergy,hasFluids,hasInventory);
            tile = VE_TILE_REGISTRY.register(name,
                    () -> BlockEntityType.Builder.of(tileEntityFactory.get()::create, block.get()).build(null));
            container = VE_CONTAINER_REGISTRY.register(name, () ->
                    IMenuTypeExtension.create((id, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        return containerFactory.get().create(id, VEClientSide.getClientWorld(), pos, inv, VEClientSide.getPlayer());
                    }));

        }

        public Supplier<Block> block() {
            return this.block;
        }

        public Supplier<BlockEntityType<VETileEntity>> tile() {
            return this.tile;
        }

        public Supplier<MenuType<VEContainer>> container() {
            return this.container;
        }
    }

    public record RegistryWithName(String name, Supplier<Block> block,boolean hasEnergy, boolean hasFluids, boolean hasInventory) {
        public RegistryWithName(String name, Supplier<Block> block) {
            this(name,block,false,false,false);
        }
    }

    public static Supplier<Block> registerWithBlockItemSupport(String name, Supplier<Block> blockSupplier) {
        Supplier<Block> registryObject = VE_BLOCKS_REGISTRY.register(name, blockSupplier);
        REGISTERED_BLOCKS.add(new RegistryWithName(name, registryObject));
        return registryObject;
    }

    public static Supplier<Block> registerWithBlockItemSupport(String name, Supplier<Block> blockSupplier,boolean hasEnergy, boolean hasFluids, boolean hasInventory) {
        Supplier<Block> registryObject = VE_BLOCKS_REGISTRY.register(name, blockSupplier);
        REGISTERED_BLOCKS.add(new RegistryWithName(name, registryObject,hasEnergy, hasFluids, hasInventory));
        return registryObject;
    }

    public static Supplier<Block> registerWithNoBlockItemSupport(String name, Supplier<Block> blockSupplier) {
        Supplier<Block> registryObject = VE_BLOCKS_REGISTRY.register(name, blockSupplier);
        return registryObject;
    }
}
