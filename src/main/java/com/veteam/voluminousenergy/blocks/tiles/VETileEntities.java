package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.FluidInputTank;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.FluidOutputTank;
import com.veteam.voluminousenergy.recipe.processor.AirCompressorProcessor;
import com.veteam.voluminousenergy.recipe.processor.DefaultProcessor;
import com.veteam.voluminousenergy.recipe.processor.GeneratorProcessor;
import com.veteam.voluminousenergy.tools.Config;

import static com.veteam.voluminousenergy.recipe.VERecipes.VERecipeTypes.*;

public class VETileEntities {

    static final int DEFAULT_TANK_CAPACITY = 4000;

    public static final VETileFactory AIR_COMPRESSOR_FACTORY =
            new VETileFactory(VEBlocks.AIR_COMPRESSOR_TILE,VEContainers.AIR_COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.AIR_COMPRESSOR_MAX_POWER.get(),
                            Config.AIR_COMPRESSOR_TRANSFER.get(),
                            Config.AIR_COMPRESSOR_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withCustomRecipeProcessing(new AirCompressorProcessor());

    public static final VETileFactory AQUEOULIZER_TILE_FACTORY =
            new VETileFactory(VEBlocks.AQUEOULIZER_TILE, VEContainers.AQUEOULIZER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.AQUEOULIZER_MAX_POWER.get(),
                            Config.AQUEOULIZER_TRANSFER.get(),
                            Config.AQUEOULIZER_POWER_USAGE.get())
                    .withTanks(new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY))
                    .countable()
                    .makesSound()
                    .withRecipe(AQUEOULIZING)
                    .withCustomRecipeProcessing(new DefaultProcessor());


    // TODO processing
    public static final VETileFactory BATTERY_BOX_FACTORY =
            new VETileFactory(VEBlocks.BATTERY_BOX_TILE, VEContainers.BATTERY_BOX_FACTORY)
                    .addEnergyStorage(
                            Config.BATTERY_BOX_MAX_POWER.get(),
                            Config.BATTERY_BOX_TRANSFER.get());

    // TODO processing
    public static final VETileFactory BLAST_FURNACE_FACTORY =
            new VETileFactory(VEBlocks.BLAST_FURNACE_TILE, VEContainers.BLAST_FURNACE_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.BLAST_FURNACE_MAX_POWER.get(),
                            Config.BLAST_FURNACE_TRANSFER.get(),
                            Config.BLAST_FURNACE_POWER_USAGE.get())
                    .withTanks(
                            new FluidInputTank(0,DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(INDUSTRIAL_BLASTING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory CENTRIFUGAL_AGITATOR_FACTORY =
            new VETileFactory(VEBlocks.CENTRIFUGAL_AGITATOR_TILE, VEContainers.CENTRIFUGAL_AGITATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get(),
                            Config.CENTRIFUGAL_AGITATOR_TRANSFER.get(),
                            Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get())
                    .withTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(1, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(CENTRIFUGAL_AGITATING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory CENTRIFUGAL_SEPARATOR_FACTORY =
            new VETileFactory(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, VEContainers.CENTRIFUGAL_SEPARATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get(),
                            Config.CENTRIFUGAL_SEPARATOR_TRANSFER.get(),
                            Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(CENTRIFUGAL_SEPARATION)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO processing

    public static final VETileFactory COMBUSTION_GENERATOR_FACTORY =
            new VETileFactory(VEBlocks.COMBUSTION_GENERATOR_TILE, VEContainers.COMBUSTION_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.COMBUSTION_GENERATOR_MAX_POWER.get(),
                            Config.COMBUSTION_GENERATOR_SEND.get())
                    .withTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidInputTank(1, DEFAULT_TANK_CAPACITY))
                    .countable()
                    .makesSound()
                    .sendsOutPower()
                    .withRecipe(FUEL_COMBUSTION)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory COMPRESSOR_FACTORY =
            new VETileFactory(VEBlocks.COMPRESSOR_TILE, VEContainers.COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.COMPRESSOR_MAX_POWER.get(),
                            Config.COMPRESSOR_TRANSFER.get(),
                            Config.COMPRESSOR_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(COMPRESSING)
                    .withCustomRecipeProcessing(new DefaultProcessor());


    // TODO needs the rng impl for the processor
    public static final VETileFactory CRUSHER_FACTORY =
            new VETileFactory(VEBlocks.CRUSHER_TILE, VEContainers.CRUSHER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.CRUSHER_MAX_POWER.get(),
                            Config.CRUSHER_TRANSFER.get(),
                            Config.CRUSHER_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(CRUSHING)
                    .withCustomRecipeProcessing(new DefaultProcessor());


    // TODO needs some custom processing
    public static final VETileFactory DIMENSIONAL_LASER_FACTORY =
            new VETileFactory(VEBlocks.DIMENSIONAL_LASER_TILE, VEContainers.DIMENSIONAL_LASER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.DIMENSIONAL_LASER_MAX_POWER.get(),
                            Config.DIMENSIONAL_LASER_TRANSFER.get(),
                            Config.DIMENSIONAL_LASER_POWER_USAGE.get())
                    .withTanks(
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withDataFlag("fully_built")
                    .withDataFlag("build_tick")
                    .withDataFlag("first_stage_built")
                    .withRecipe(DIMENSIONAL_LASING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory DISTILLATION_UNIT_FACTORY =
            new VETileFactory(VEBlocks.DISTILLATION_UNIT_TILE, VEContainers.DISTILLATION_UNIT_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.DISTILLATION_UNIT_MAX_POWER.get(),
                            Config.DISTILLATION_UNIT_TRANSFER.get(),
                            Config.DIMENSIONAL_LASER_POWER_USAGE.get())
                    .withTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(1, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(DISTILLING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO needs a furnace processor
    public static final VETileFactory ELECTRIC_FURNACE_FACTORY =
            new VETileFactory(VEBlocks.ELECTRIC_FURNACE_TILE, VEContainers.ELECTRIC_FURNACE_FACTORY)
                    .addEnergyStorageWithConsumption(Config.ELECTROLYZER_MAX_POWER.get(),
                            Config.ELECTRIC_FURNACE_TRANSFER.get(),
                            Config.ELECTRIC_FURNACE_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory ELECTROLYZER_FACTORY =
            new VETileFactory(VEBlocks.ELECTROLYZER_TILE, VEContainers.ELECTROLYZER_FACTORY)
                    .addEnergyStorageWithConsumption(Config.ELECTROLYZER_MAX_POWER.get(),
                            Config.ELECTRIC_FURNACE_TRANSFER.get(),
                            Config.ELECTRIC_FURNACE_POWER_USAGE.get())
                    .countable()
                    .makesSound()
                    .withRecipe(ELECTROLYZING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory FLUID_ELECTROLYZER_FACTORY =
            new VETileFactory(VEBlocks.FLUID_ELECTROLYZER_TILE, VEContainers.FLUID_ELECTROLYZER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.FLUID_ELECTROLYZER_MAX_POWER.get(),
                            Config.FLUID_ELECTROLYZER_TRANSFER.get(),
                            Config.FLUID_ELECTROLYZER_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(ELECTROLYZING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory FLUID_MIXER_FACTORY =
            new VETileFactory(VEBlocks.FLUID_MIXER_TILE, VEContainers.FLUID_MIXER_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.FLUID_MIXER_MAX_POWER.get(),
                            Config.FLUID_MIXER_TRANSFER.get(),
                            Config.FLUID_MIXER_POWER_USAGE.get()
                    )
                    .withTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY),
                            new FluidInputTank(1, DEFAULT_TANK_CAPACITY),
                            new FluidOutputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(FLUID_MIXING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO needs processing
    public static final VETileFactory GAS_FIRED_FURNACE =
            new VETileFactory(VEBlocks.GAS_FIRED_FURNACE_TILE,VEContainers.GAS_FIRED_FURNACE_FACTORY)
                    .withTanks(
                            new FluidInputTank(0,DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory HYDROPONIC_INCUBATOR_FACTORY =
            new VETileFactory(VEBlocks.HYDROPONIC_INCUBATOR_TILE, VEContainers.HYDROPONIC_INCUBATOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.HYDROPONIC_INCUBATOR_MAX_POWER.get(),
                            Config.HYDROPONIC_INCUBATOR_TRANSFER.get(),
                            Config.HYDROPONIC_INCUBATOR_POWER_USAGE.get()
                    )
                    .withTanks(
                            new FluidInputTank(0, DEFAULT_TANK_CAPACITY)
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(HYDROPONIC_INCUBATING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory IMPLOSION_COMPRESSOR_FACTORY =
            new VETileFactory(VEBlocks.IMPLOSION_COMPRESSOR_TILE, VEContainers.IMPLOSION_COMPRESSOR_FACTORY)
                    .addEnergyStorageWithConsumption(
                            Config.IMPLOSION_COMPRESSOR_MAX_POWER.get(),
                            Config.IMPLOSION_COMPRESSOR_TRANSFER.get(),
                            Config.IMPLOSION_COMPRESSOR_POWER_USAGE.get()
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(IMPLOSION_COMPRESSING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory PRIMITIVE_BLAST_FURNACE_FACTORY =
            new VETileFactory(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE, VEContainers.PRIMITIVE_BLAST_FURNACE_FACTORY)
                    .countable()
                    .makesSound()
                    .withRecipe(INDUSTRIAL_BLASTING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO needs custom processor
    public static final VETileFactory PRIMITIVE_SOLAR_PANEL_FACTORY =
            new VETileFactory(VEBlocks.PRIMITIVE_SOLAR_PANEL_TILE,VEContainers.PRIMITIVE_SOLAR_PANEL_FACTORY)
                    .addEnergyStorage(
                            Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get(),
                            Config.PRIMITIVE_SOLAR_PANEL_SEND.get()
                    )
                    .sendsOutPower()
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory PRIMITIVE_STIRLING_GENERATOR_TILE_FACTORY =
            new VETileFactory(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE, VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),
                            Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get())
                    .countable()
                    .makesSound()
                    .sendsOutPower()
                    .withRecipe(STIRLING)
                    .withCustomRecipeProcessing(new GeneratorProcessor(true, 4));

    // TODO make a custom processor
    public static final VETileFactory PUMP_FACTORY =
            new VETileFactory(VEBlocks.PUMP_TILE,VEContainers.PUMP_FACTORY)
                    .addEnergyStorage(
                            Config.PUMP_MAX_POWER.get(),
                            Config.PUMP_TRANSFER.get()
                    )
                    .withTanks(new FluidOutputTank(0,DEFAULT_TANK_CAPACITY))
                    .countable()
                    .makesSound()
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO make a custom processor
    public static final VETileFactory SAWMILL_FACTORY =
            new VETileFactory(VEBlocks.SAWMILL_TILE,VEContainers.SAWMILL_FACTORY)
                    .addEnergyStorage(
                            Config.SAWMILL_MAX_POWER.get(),
                            Config.SAWMILL_TRANSFER.get()
                    )
                    .countable()
                    .makesSound()
                    .withRecipe(SAWMILLING)
                    .withCustomRecipeProcessing(new DefaultProcessor());

    // TODO needs a custom processor
    public static final VETileFactory SOLAR_PANEL_FACTORY =
            new VETileFactory(VEBlocks.SOLAR_PANEL_TILE,VEContainers.SOLAR_PANEL_FACTORY)
                    .addEnergyStorage(
                            Config.SOLAR_PANEL_MAX_POWER.get(),
                            Config.SOLAR_PANEL_SEND.get()
                    )
                    .sendsOutPower()
                    .withCustomRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory STIRLING_GENERATOR_FACTORY =
            new VETileFactory(VEBlocks.STIRLING_GENERATOR_TILE,VEContainers.STIRLING_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.STIRLING_GENERATOR_MAX_POWER.get(),
                            Config.STIRLING_GENERATOR_SEND.get()
                    )
                    .sendsOutPower()
                    .withRecipe(STIRLING)
                    .withCustomRecipeProcessing(new GeneratorProcessor());

    // TODO needs a custom processor
    public static final VETileFactory TOOLING_STATION_FACTORY =
            new VETileFactory(VEBlocks.TOOLING_STATION_TILE,VEContainers.TOOLING_STATION_FACTORY)
                    .withRecipe(TOOLING)
                    .withCustomRecipeProcessing(new DefaultProcessor());
}
