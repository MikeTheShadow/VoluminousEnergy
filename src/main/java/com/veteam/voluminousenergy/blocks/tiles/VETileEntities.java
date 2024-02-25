package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.FluidInputTank;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.FluidOutputTank;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.recipe.processor.DefaultProcessor;
import com.veteam.voluminousenergy.recipe.processor.GeneratorProcessor;
import com.veteam.voluminousenergy.tools.Config;

public class VETileEntities {

    static final int DEFAULT_TANK_CAPACITY = 4000;

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
                    .withRecipe(VERecipes.VERecipeTypes.AQUEOULIZING)
                    .withRecipeProcessing(new DefaultProcessor());

    public static final VETileFactory PRIMITIVE_STIRLING_GENERATOR_TILE_FACTORY =
            new VETileFactory(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE,VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY)
                    .addEnergyStorage(
                            Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),
                            Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get())
                    .countable()
                    .makesSound()
                    .sendsOutPower()
                    .withRecipe(VERecipes.VERecipeTypes.STIRLING)
                    .withRecipeProcessing(new GeneratorProcessor(true,4));
}
