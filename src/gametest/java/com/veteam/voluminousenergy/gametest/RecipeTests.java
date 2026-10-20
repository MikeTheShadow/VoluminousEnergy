package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.recipe.processor.CombustionGeneratorProcessor;
import com.veteam.voluminousenergy.recipe.processor.GeneratorProcessor;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Supplier;

/**
 * Runs one real recipe through every kind of recipe processor (test plan T2.17 and T2.18).
 */
public class RecipeTests {
    private static final BlockPos MACHINE = new BlockPos(4, 1, 1);

    private RecipeTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        recipe(tests, "crusher", VEBlocks.CRUSHER.block(), VERecipes.VERecipeTypes.CRUSHING, null);
        recipe(tests, "electrolyzer", VEBlocks.ELECTROLYZER.block(), VERecipes.VERecipeTypes.ELECTROLYZING, null);
        recipe(tests, "centrifugal_agitator", VEBlocks.CENTRIFUGAL_AGITATOR.block(), VERecipes.VERecipeTypes.CENTRIFUGAL_AGITATING, null);
        recipe(tests, "compressor", VEBlocks.COMPRESSOR.block(), VERecipes.VERecipeTypes.COMPRESSING, null);
        recipe(tests, "aqueoulizer", VEBlocks.AQUEOULIZER.block(), VERecipes.VERecipeTypes.AQUEOULIZING, null);
        recipe(tests, "distillation_unit", VEBlocks.DISTILLATION_UNIT.block(), VERecipes.VERecipeTypes.DISTILLING, VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK);
        recipe(tests, "centrifugal_separator", VEBlocks.CENTRIFUGAL_SEPARATOR.block(), VERecipes.VERecipeTypes.CENTRIFUGAL_SEPARATION, null);
        recipe(tests, "implosion_compressor", VEBlocks.IMPLOSION_COMPRESSOR.block(), VERecipes.VERecipeTypes.IMPLOSION_COMPRESSING, null);
        recipe(tests, "blast_furnace", VEBlocks.BLAST_FURNACE.block(), VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING, VEBlocks.TITANIUM_MACHINE_CASING_BLOCK);
        recipe(tests, "primitive_blast_furnace", VEBlocks.PRIMITIVE_BLAST_FURNACE.block(), VERecipes.VERecipeTypes.PRIMITIVE_BLAST_FURNACING, null);
        recipe(tests, "sawmill", VEBlocks.SAWMILL.block(), VERecipes.VERecipeTypes.SAWMILLING, null);
        recipe(tests, "fluid_electrolyzer", VEBlocks.FLUID_ELECTROLYZER.block(), VERecipes.VERecipeTypes.FLUID_ELECTROLYZING, null);
        recipe(tests, "fluid_mixer", VEBlocks.FLUID_MIXER.block(), VERecipes.VERecipeTypes.FLUID_MIXING, null);
        recipe(tests, "hydroponic_incubator", VEBlocks.HYDROPONIC_INCUBATOR.block(), VERecipes.VERecipeTypes.HYDROPONIC_INCUBATING, null);
        recipe(tests, "stirling_generator", VEBlocks.STIRLING_GENERATOR.block(), VERecipes.VERecipeTypes.STIRLING, null);
        recipe(tests, "primitive_stirling_generator", VEBlocks.PRIMITIVE_STIRLING_GENERATOR.block(), VERecipes.VERecipeTypes.STIRLING, null);
        recipe(tests, "combustion_generator", VEBlocks.COMBUSTION_GENERATOR.block(), VERecipes.VERecipeTypes.FUEL_COMBUSTION, null);
        tests.add(GameTestDefinition.of("recipe_electric_furnace", 400, RecipeTests::electricFurnace));
        tests.add(GameTestDefinition.of("recipe_gas_fired_furnace", 400, RecipeTests::gasFiredFurnace));
        tests.add(GameTestDefinition.large("recipe_air_compressor", 200, RecipeTests::airCompressor));
        tests.add(GameTestDefinition.large("recipe_pump", 1200, RecipeTests::pump));
        tests.add(GameTestDefinition.of("recipe_tooling_station", 100, RecipeTests::toolingStation));
        tests.add(GameTestDefinition.of("recipe_hydroponic_fluid_result", 20, RecipeTests::hydroponicFluidResult));
    }

    private static void recipe(List<GameTestDefinition> tests, String machine, Supplier<Block> block, Supplier<? extends RecipeType<?>> type,
                               Supplier<Block> casing) {
        String name = "recipe_" + machine;
        tests.add(GameTestDefinition.large(name, 2400, helper -> runRecipe(helper, name, block.get(), type.get(), casing == null ? null : casing.get())));
    }

    /**
     * Loads the inputs of a recipe that only those inputs match, powers the machine, and waits for
     * the recipe's first output or, for a generator, for energy.
     */
    private static void runRecipe(GameTestHelper helper, String name, Block block, RecipeType<?> type, Block casing) {
        // The oxidizer lookups read a cache that only a player's data sync builds; no player has joined here.
        OxidizerFluidsData.getAllOxidizerFluids();
        VETileEntity tile = MachineKit.place(helper, MACHINE, block);
        if (casing != null) {
            placeCasing(helper, tile, casing);
        }
        VERecipe recipe = chooseRecipe(tile, type);
        boolean generator = tile.getRecipeProcessor() instanceof GeneratorProcessor || tile.getRecipeProcessor() instanceof CombustionGeneratorProcessor;
        if (!generator && tile.getEnergy() != null) {
            helper.onEachTick(() -> tile.getEnergy().set(tile.getEnergy().getCapacity()));
        }
        int inputsBefore = inputTotal(tile);

        helper.succeedWhen(() -> {
            if (generator) {
                helper.assertTrue(tile.getEnergy().getAmountAsInt() > 0, "no energy generated from " + recipe.id() + ":\n" + MachineKit.describe(tile));
            } else {
                helper.assertTrue(hasOutput(tile, recipe), "no output yet from " + recipe.id() + ":\n" + MachineKit.describe(tile));
                helper.assertTrue(inputTotal(tile) < inputsBefore, "inputs not consumed by " + recipe.id());
            }
            GameTestLog.note(name, recipe.id().toString());
        });
    }

    private static VERecipe chooseRecipe(VETileEntity tile, RecipeType<?> type) {
        for (VERecipe recipe : VERecipe.getCachedRecipes(type)) {
            clear(tile);
            loadInputs(tile, recipe);
            if (VERecipe.getPotentialRecipes(tile).size() == 1 && VERecipe.getCompleteRecipe(tile) == recipe) {
                return recipe;
            }
        }
        throw new IllegalStateException("No recipe of " + type + " can be loaded on its own into " + MachineKit.name(tile.getBlockState().getBlock()));
    }

    private static void clear(VETileEntity tile) {
        VEItemStackHandler inventory = tile.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            tank.getTank().setFluid(FluidStack.EMPTY);
        }
    }

    private static void loadInputs(VETileEntity tile, VERecipe recipe) {
        VEItemStackHandler inventory = tile.getInventory();
        for (int ingredient = 0; recipe.hasIngredient(ingredient); ingredient++) {
            if (recipe.getIngredient(ingredient).isEmpty()) {
                continue;
            }
            ItemStack input = RecipeKit.firstInput(recipe, ingredient);
            for (VESlotManager manager : tile.getSlotManagers()) {
                int slot = manager.getSlotNum();
                if (manager.getSlotType() == SlotType.INPUT && inventory.getStack(slot).isEmpty() && recipe.getParser().canInsertItem(slot, input)) {
                    inventory.setStack(slot, input);
                    break;
                }
            }
        }
        for (VESlotManager manager : tile.getSlotManagers()) {
            int slot = manager.getSlotNum();
            if (manager.getSlotType() == SlotType.INPUT && inventory.getStack(slot).isEmpty() && recipe.getParser().canInsertItem(slot, new ItemStack(Items.GUNPOWDER))) {
                inventory.setStack(slot, new ItemStack(Items.GUNPOWDER));
            }
        }

        List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            if (tank.getTankType() == TankType.OUTPUT) {
                continue;
            }
            if (tank.getRecipePos() < fluidIngredients.size() && !fluidIngredients.get(tank.getRecipePos()).isEmpty()) {
                FluidStack fluid = sourceFluid(fluidIngredients.get(tank.getRecipePos()));
                tank.getTank().setFluid(fluid.copyWithAmount(tank.getTank().getCapacity()));
            } else if (tank.getTank().isEmpty()) {
                tank.getTank().setFluid(new FluidStack(Fluids.LAVA, tank.getTank().getCapacity()));
            }
        }
    }

    private static FluidStack sourceFluid(FluidIngredient ingredient) {
        for (FluidStack fluid : ingredient.getFluids()) {
            if (fluid.getFluid().isSource(fluid.getFluid().defaultFluidState())) {
                return fluid;
            }
        }
        return ingredient.getFluids()[0];
    }

    /**
     * Whether the recipe's first item result or first fluid result has appeared. Either can be
     * a chance roll, so one is enough.
     */
    private static boolean hasOutput(VETileEntity tile, VERecipe recipe) {
        ItemStack result = recipe.getResult(0);
        if (!result.isEmpty()) {
            for (VESlotManager manager : tile.getSlotManagers()) {
                if (manager.getSlotType() == SlotType.OUTPUT && tile.getInventory().getStack(manager.getSlotNum()).is(result.getItem())) {
                    return true;
                }
            }
        }
        if (recipe.getOutputFluids().isEmpty()) {
            return false;
        }
        FluidStack fluid = recipe.getOutputFluid(0);
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            if (tank.getTankType() == TankType.OUTPUT && tank.getTank().getFluid().is(fluid.getFluid())) {
                return true;
            }
        }
        return false;
    }

    private static int inputTotal(VETileEntity tile) {
        int total = 0;
        for (VESlotManager manager : tile.getSlotManagers()) {
            if (manager.getSlotType() == SlotType.INPUT) {
                total += tile.getInventory().getStack(manager.getSlotNum()).getCount();
            }
        }
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            if (tank.getTankType() != TankType.OUTPUT) {
                total += tank.getTank().getFluidAmount();
            }
        }
        return total;
    }

    /**
     * Fills the 3 by 3 by 3 box that {@code MultiBlockRecipeProcessor} checks beside a machine.
     */
    private static void placeCasing(GameTestHelper helper, VETileEntity tile, Block casing) {
        int rawDirection = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue();
        int startX = rawDirection == 1 ? 1 : -1;
        int startZ = rawDirection < 2 ? -1 : 1;
        int endX = startX + (rawDirection == 3 ? -1 : 1) * 2;
        int endZ = startZ + (rawDirection == 0 || rawDirection == 3 ? -1 : 1) * 2;
        for (BlockPos pos : BlockPos.betweenClosed(MACHINE.offset(startX, 0, startZ), MACHINE.offset(endX, 2, endZ))) {
            helper.setBlock(pos, casing);
        }
    }

    private static void electricFurnace(GameTestHelper helper) {
        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.ELECTRIC_FURNACE.block().get());
        furnace.getInventory().setStack(0, new ItemStack(Items.RAW_IRON, 2));
        helper.onEachTick(() -> furnace.getEnergy().set(furnace.getEnergy().getCapacity()));
        helper.succeedWhen(() -> {
            helper.assertTrue(furnace.getInventory().getStack(1).is(Items.IRON_INGOT), "no iron ingot");
            helper.assertValueEqual(furnace.getInventory().getStack(0).getCount(), 1, "raw iron left");
            GameTestLog.note("recipe_electric_furnace", "");
        });
    }

    private static void gasFiredFurnace(GameTestHelper helper) {
        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.GAS_FIRED_FURNACE.block().get());
        FluidStack fuel = new FluidStack(CombustibleFluidsData.getAllCombustibleFluids().getFirst(), 4000);
        furnace.getRelationalTank(0).getTank().setFluid(fuel);
        furnace.getInventory().setStack(2, new ItemStack(Items.RAW_IRON, 2));
        helper.succeedWhen(() -> {
            helper.assertTrue(furnace.getInventory().getStack(3).is(Items.IRON_INGOT), "no iron ingot");
            helper.assertTrue(furnace.getFluidStackFromTank(0).getAmount() < 4000, "no fuel burned");
            GameTestLog.note("recipe_gas_fired_furnace", fuel.getFluid().toString());
        });
    }

    private static void airCompressor(GameTestHelper helper) {
        VETileEntity compressor = MachineKit.place(helper, MACHINE, VEBlocks.AIR_COMPRESSOR.block().get());
        helper.onEachTick(() -> compressor.getEnergy().set(compressor.getEnergy().getCapacity()));
        helper.succeedWhen(() -> {
            helper.assertTrue(compressor.getFluidStackFromTank(0).is(VEFluids.COMPRESSED_AIR_REG.get()), "no compressed air");
            GameTestLog.note("recipe_air_compressor", compressor.getFluidStackFromTank(0).getAmount() + " mB");
        });
    }

    private static void pump(GameTestHelper helper) {
        BlockPos pumpPos = new BlockPos(4, 4, 4);
        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(1, 1, 1), new BlockPos(7, 3, 7))) {
            helper.setBlock(pos, Blocks.WATER);
        }
        VETileEntity pump = MachineKit.place(helper, pumpPos, VEBlocks.PUMP.block().get());
        helper.onEachTick(() -> pump.getEnergy().set(pump.getEnergy().getCapacity()));
        helper.succeedWhen(() -> {
            helper.assertTrue(pump.getFluidStackFromTank(0).is(Fluids.WATER), "no water pumped");
            GameTestLog.note("recipe_pump", pump.getFluidStackFromTank(0).getAmount() + " mB");
        });
    }

    private static void toolingStation(GameTestHelper helper) {
        VETileEntity station = MachineKit.place(helper, MACHINE, VEBlocks.TOOLING_STATION.block().get());
        FluidStack fuel = new FluidStack(VEFluids.GASOLINE_REG.get(), 3000);
        station.getRelationalTank(0).getTank().setFluid(fuel);
        station.getInventory().setStack(2, new ItemStack(VEMultitoolItems.MULTI_TOOL.get()));
        helper.succeedWhen(() -> {
            ItemStack multitool = station.getInventory().getStack(2);
            int stored = VEItemCapabilities.getFluidHandler(multitool).getAmountAsInt(0);
            helper.assertValueEqual(stored, 3000, "gasoline in the multitool");
            helper.assertTrue(station.getFluidStackFromTank(0).isEmpty(), "gasoline left in the station");
            GameTestLog.note("recipe_tooling_station", "");
        });
    }

    /**
     * T2.18: records whether any Hydroponic Incubator recipe makes a fluid, the case 26.2 lost
     * when the output tank was empty.
     */
    private static void hydroponicFluidResult(GameTestHelper helper) {
        long withFluid = VERecipe.getCachedRecipes(HydroponicIncubatorRecipe.RECIPE_TYPE).stream()
                .filter(recipe -> recipe.getOutputFluids().stream().anyMatch(fluid -> !fluid.isEmpty()))
                .count();
        GameTestLog.value("recipe_hydroponic_fluid_result", "recipes_with_fluid_results", withFluid);
        GameTestLog.pass(helper, "recipe_hydroponic_fluid_result", withFluid == 0 ? "no recipe has a fluid result" : withFluid + " recipes");
    }
}
