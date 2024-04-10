package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.FluidInputTank;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.FluidOutputTank;
import com.veteam.voluminousenergy.blocks.tiles.inventory.BatteryBoxInventoryValidator;
import com.veteam.voluminousenergy.blocks.tiles.inventory.DimensionalLaserInventoryValidator;
import com.veteam.voluminousenergy.blocks.tiles.inventory.FurnaceInventoryValidator;
import com.veteam.voluminousenergy.blocks.tiles.inventory.GasFiredFurnaceInventoryValidator;
import com.veteam.voluminousenergy.recipe.processor.*;
import com.veteam.voluminousenergy.tools.Config;

import static com.veteam.voluminousenergy.recipe.VERecipes.VERecipeTypes.*;

public class VETileEntities {

    static final int DEFAULT_TANK_CAPACITY = 4000;

    public static final VETileEntityFactory AIR_COMPRESSOR_FACTORY =
            new VETileEntityFactory(VEBlocks.AIR_COMPRESSOR_TILE, VEContainers.AIR_COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.AIR_COMPRESSOR_MAX_POWER.get(),
                            Config.AIR_COMPRESSOR_TRANSFER.get(),
                            Config.AIR_COMPRESSOR_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withCustomRecipeProcessing(new AirCompressorProcessor());

    public static final VETileEntityFactory AQUEOULIZER_TILE_FACTORY =
            new VETileEntityFactory(VEBlocks.AQUEOULIZER_TILE, VEContainers.AQUEOULIZER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.AQUEOULIZER_MAX_POWER.get(),
                            Config.AQUEOULIZER_TRANSFER.get(),
                            Config.AQUEOULIZER_POWER_USAGE.get())
                    .addTanks(new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY))
                    .countable()
                    .makesSound()
                    .withRecipe(AQUEOULIZING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory BATTERY_BOX_FACTORY =
            new VETileEntityFactory(VEBlocks.BATTERY_BOX_TILE, VEContainers.BATTERY_BOX_FACTORY)
                    .addEnergyStorage(
                            Config.BATTERY_BOX_MAX_POWER.get(),
                            Config.BATTERY_BOX_TRANSFER.get())
                    .withCustomInventoryValidator(new BatteryBoxInventoryValidator())
                    .withCustomRecipeProcessing(new BatteryBoxProcessor());

    public static final VETileEntityFactory BLAST_FURNACE_FACTORY =
            new VETileEntityFactory(VEBlocks.BLAST_FURNACE_TILE, VEContainers.BLAST_FURNACE_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.BLAST_FURNACE_MAX_POWER.get(),
                            Config.BLAST_FURNACE_TRANSFER.get(),
                            Config.BLAST_FURNACE_POWER_USAGE.get())
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .addDataFlag("temperature_kelvin")
                    .addDataFlag("temperature_celsius")
                    .addDataFlag("temperature_fahrenheit")
                    .makesSound()
                    .withRecipe(INDUSTRIAL_BLASTING)
                    .withCustomRecipeProcessing(new MultiBlockRecipeProcessor(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK));

    public static final VETileEntityFactory CENTRIFUGAL_AGITATOR_FACTORY =
            new VETileEntityFactory(VEBlocks.CENTRIFUGAL_AGITATOR_TILE, VEContainers.CENTRIFUGAL_AGITATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get(),
                            Config.CENTRIFUGAL_AGITATOR_TRANSFER.get(),
                            Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get())
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(1, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(CENTRIFUGAL_AGITATING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory CENTRIFUGAL_SEPARATOR_FACTORY =
            new VETileEntityFactory(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, VEContainers.CENTRIFUGAL_SEPARATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get(),
                            Config.CENTRIFUGAL_SEPARATOR_TRANSFER.get(),
                            Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(CENTRIFUGAL_SEPARATION)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory COMBUSTION_GENERATOR_FACTORY =
            new VETileEntityFactory(VEBlocks.COMBUSTION_GENERATOR_TILE, VEContainers.COMBUSTION_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.COMBUSTION_GENERATOR_MAX_POWER.get(),
                            Config.COMBUSTION_GENERATOR_SEND.get())
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidInputTank(1, DEFAULT_TANK_CAPACITY))
                    .countable()
                    .makesSound()
                    .sendsOutPower()
                    .withRecipe(FUEL_COMBUSTION)
                    .withCustomRecipeProcessing(new CombustionGeneratorProcessor());

    public static final VETileEntityFactory COMPRESSOR_FACTORY =
            new VETileEntityFactory(VEBlocks.COMPRESSOR_TILE, VEContainers.COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.COMPRESSOR_MAX_POWER.get(),
                            Config.COMPRESSOR_TRANSFER.get(),
                            Config.COMPRESSOR_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(COMPRESSING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory CRUSHER_FACTORY =
            new VETileEntityFactory(VEBlocks.CRUSHER_TILE, VEContainers.CRUSHER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CRUSHER_MAX_POWER.get(),
                            Config.CRUSHER_TRANSFER.get(),
                            Config.CRUSHER_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(CRUSHING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory DIMENSIONAL_LASER_FACTORY =
            new VETileEntityFactory(VEBlocks.DIMENSIONAL_LASER_TILE, VEContainers.DIMENSIONAL_LASER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.DIMENSIONAL_LASER_MAX_POWER.get(),
                            Config.DIMENSIONAL_LASER_TRANSFER.get(),
                            Config.DIMENSIONAL_LASER_POWER_USAGE.get())
                    .addTanks(
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .addDataFlag("build_tick")
                    .withRecipe(DIMENSIONAL_LASING)
                    .withInfiniteRender()
                    .withCustomInventoryValidator(new DimensionalLaserInventoryValidator())
                    .withCustomRecipeProcessing(new DimensionalLaserRecipeProcessor(VEBlocks.SOLARIUM_MACHINE_CASING_BLOCK));

    public static final VETileEntityFactory DISTILLATION_UNIT_FACTORY =
            new VETileEntityFactory(VEBlocks.DISTILLATION_UNIT_TILE, VEContainers.DISTILLATION_UNIT_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.DISTILLATION_UNIT_MAX_POWER.get(),
                            Config.DISTILLATION_UNIT_TRANSFER.get(),
                            Config.DIMENSIONAL_LASER_POWER_USAGE.get())
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(1, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(DISTILLING)
                    .withCustomRecipeProcessing(new MultiBlockRecipeProcessor(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK));

    public static final VETileEntityFactory ELECTRIC_FURNACE_FACTORY =
            new VETileEntityFactory(VEBlocks.ELECTRIC_FURNACE_TILE, VEContainers.ELECTRIC_FURNACE_FACTORY)
                    .addEnergyStorageWithConsumption(Config.ELECTROLYZER_MAX_POWER.get(),
                            Config.ELECTRIC_FURNACE_TRANSFER.get(),
                            Config.ELECTRIC_FURNACE_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withCustomInventoryValidator(new FurnaceInventoryValidator())
                    .withCustomRecipeProcessing(new ElectricFurnaceProcessor());

    public static final VETileEntityFactory ELECTROLYZER_FACTORY =
            new VETileEntityFactory(VEBlocks.ELECTROLYZER_TILE, VEContainers.ELECTROLYZER_FACTORY)
                    .addEnergyStorageWithConsumption(Config.ELECTROLYZER_MAX_POWER.get(),
                            Config.ELECTRIC_FURNACE_TRANSFER.get(),
                            Config.ELECTRIC_FURNACE_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(ELECTROLYZING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory FLUID_ELECTROLYZER_FACTORY =
            new VETileEntityFactory(VEBlocks.FLUID_ELECTROLYZER_TILE, VEContainers.FLUID_ELECTROLYZER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.FLUID_ELECTROLYZER_MAX_POWER.get(),
                            Config.FLUID_ELECTROLYZER_TRANSFER.get(),
                            Config.FLUID_ELECTROLYZER_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(ELECTROLYZING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory FLUID_MIXER_FACTORY =
            new VETileEntityFactory(VEBlocks.FLUID_MIXER_TILE, VEContainers.FLUID_MIXER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.FLUID_MIXER_MAX_POWER.get(),
                            Config.FLUID_MIXER_TRANSFER.get(),
                            Config.FLUID_MIXER_POWER_USAGE.get()
                    )
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidInputTank(1, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(FLUID_MIXING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory GAS_FIRED_FURNACE =
            new VETileEntityFactory(VEBlocks.GAS_FIRED_FURNACE_TILE, VEContainers.GAS_FIRED_FURNACE_FACTORY)
                    // We have an upgrade slot, so we add an empty energy storage. Less than ideal.
                    .addEnergyStorage(0,0)
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .addDataFlag("fuel_length")
                    .addDataFlag("fuel_counter")
                    .withCustomInventoryValidator(new GasFiredFurnaceInventoryValidator())
                    .withCustomRecipeProcessing(new GasFiredFurnaceProcessor());

    public static final VETileEntityFactory HYDROPONIC_INCUBATOR_FACTORY =
            new VETileEntityFactory(VEBlocks.HYDROPONIC_INCUBATOR_TILE, VEContainers.HYDROPONIC_INCUBATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.HYDROPONIC_INCUBATOR_MAX_POWER.get(),
                            Config.HYDROPONIC_INCUBATOR_TRANSFER.get(),
                            Config.HYDROPONIC_INCUBATOR_POWER_USAGE.get()
                    )
                    .addTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(HYDROPONIC_INCUBATING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory IMPLOSION_COMPRESSOR_FACTORY =
            new VETileEntityFactory(VEBlocks.IMPLOSION_COMPRESSOR_TILE, VEContainers.IMPLOSION_COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.IMPLOSION_COMPRESSOR_MAX_POWER.get(),
                            Config.IMPLOSION_COMPRESSOR_TRANSFER.get(),
                            Config.IMPLOSION_COMPRESSOR_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(IMPLOSION_COMPRESSING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory PRIMITIVE_BLAST_FURNACE_FACTORY =
            new VETileEntityFactory(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE, VEContainers.PRIMITIVE_BLAST_FURNACE_FACTORY)
                    .countable()
                    .makesSound()
                    .withRecipe(PRIMITIVE_BLAST_FURNACING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory PRIMITIVE_SOLAR_PANEL_FACTORY =
            new VETileEntityFactory(VEBlocks.PRIMITIVE_SOLAR_PANEL_TILE, VEContainers.PRIMITIVE_SOLAR_PANEL_FACTORY)
                    .addEnergyStorage(
                            Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get(),
                            Config.PRIMITIVE_SOLAR_PANEL_SEND.get()
                    )
                    .sendsOutPower()
                    .withCustomRecipeProcessing(new SolarPanelProcessor(Config.PRIMITIVE_SOLAR_PANEL_GENERATE.get()));

    public static final VETileEntityFactory SOLAR_PANEL_FACTORY =
            new VETileEntityFactory(VEBlocks.SOLAR_PANEL_TILE, VEContainers.SOLAR_PANEL_FACTORY)
                    .addEnergyStorage(
                            Config.SOLAR_PANEL_MAX_POWER.get(),
                            Config.SOLAR_PANEL_SEND.get()
                    )
                    .sendsOutPower()
                    .withCustomRecipeProcessing(new SolarPanelProcessor(Config.SOLAR_PANEL_GENERATE.get()));

    public static final VETileEntityFactory PRIMITIVE_STIRLING_GENERATOR_TILE_FACTORY =
            new VETileEntityFactory(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE, VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),
                            Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get())
                    .countable()
                    .makesSound()
                    .sendsOutPower()
                    .withRecipe(STIRLING)
                    .withCustomRecipeProcessing(new GeneratorProcessor(true, 4));

    public static final VETileEntityFactory PUMP_FACTORY =
            new VETileEntityFactory(VEBlocks.PUMP_TILE, VEContainers.PUMP_FACTORY)
                    .addEnergyStorage(
                            Config.PUMP_MAX_POWER.get(),
                            Config.PUMP_TRANSFER.get()
                    )
                    .addTanks(new FluidOutputTank(0, DEFAULT_TANK_CAPACITY))
                    .countable()
                    .addDataFlag("lx")
                    .addDataFlag("ly")
                    .addDataFlag("lz")
                    .addSavableTag("selected_fluid")
                    .withCustomRecipeProcessing(new PumpTileProcessor())
                    .makesSound();

    public static final VETileEntityFactory SAWMILL_FACTORY =
            new VETileEntityFactory(VEBlocks.SAWMILL_TILE, VEContainers.SAWMILL_FACTORY)
                    .addEnergyStorage(
                            Config.SAWMILL_MAX_POWER.get(),
                            Config.SAWMILL_TRANSFER.get()
                    ).addTanks(
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(SAWMILLING)
                    .withCustomRecipeProcessing(new BasicProcessor());

    public static final VETileEntityFactory STIRLING_GENERATOR_FACTORY =
            new VETileEntityFactory(VEBlocks.STIRLING_GENERATOR_TILE, VEContainers.STIRLING_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.STIRLING_GENERATOR_MAX_POWER.get(),
                            Config.STIRLING_GENERATOR_SEND.get()
                    )
                    .sendsOutPower()
                    .countable()
                    .withRecipe(STIRLING)
                    .withCustomRecipeProcessing(new GeneratorProcessor());

    // TODO needs a custom processor
    public static final VETileEntityFactory TOOLING_STATION_FACTORY =
            new VETileEntityFactory(VEBlocks.TOOLING_STATION_TILE, VEContainers.TOOLING_STATION_FACTORY)
                    .withRecipe(TOOLING)
                    .withCustomRecipeProcessing(new BasicProcessor());
}
