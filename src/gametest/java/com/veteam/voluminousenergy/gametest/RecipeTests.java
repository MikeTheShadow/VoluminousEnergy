package com.veteam.voluminousenergy.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.processor.CombustionGeneratorProcessor;
import com.veteam.voluminousenergy.recipe.processor.GeneratorProcessor;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Runs one real recipe through every kind of recipe processor (test plan T2.17 and T2.18), and checks
 * recipe encoding, Blast Furnace heat-source rules, Primitive Blast Furnace charring, and torch yields
 * (specs machines/dimensional-laser, machines/blast-furnace, machines/primitive-blast-furnace, and
 * items/solid-fuels).
 */
public class RecipeTests {
    private static final BlockPos MACHINE = new BlockPos(4, 1, 1);
    private static final Identifier PLAINS_LASING = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "dimensional_lasing/crude_oil_plains");
    private static final ResourceKey<Recipe<?>> STEEL_BLASTING = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "industrial_blasting/steel"));
    private static final BlockPos CANE_FURNACE = new BlockPos(1, 1, 2);
    private static final BlockPos CACTUS_FURNACE = new BlockPos(3, 1, 2);
    private static final ResourceKey<Recipe<?>> LOG_CHARRING = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "primitive_blast_furnacing/logs_to_charcoal"));
    private static final BlockPos FUEL_FURNACE = new BlockPos(2, 1, 2);
    private static final String TORCH_RECIPE_PATH = "vanilla_crafting/torch/";
    private static final int TICKS_PER_TORCH = 400;
    private static final int CHARRING_TICKS = 200;

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
        tests.add(GameTestDefinition.of("recipe_lasing_codec_round_trip", 20, RecipeTests::lasingCodecRoundTrip));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_fluids_per_recipe", 20, RecipeTests::blastingHeatFluidsPerRecipe));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_source_drain", 20, RecipeTests::blastingHeatSourceDrain));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_counts", 3 * CHARRING_TICKS, RecipeTests::primitiveCharringCounts));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_logs", 20, RecipeTests::primitiveCharringLogs));
        tests.add(GameTestDefinition.of("recipe_torch_yield_matches_burn_time", 20, RecipeTests::torchYieldMatchesBurnTime));
        tests.add(GameTestDefinition.of("recipe_ingredient_counts_survive_network", 20, RecipeTests::ingredientCountsSurviveNetwork));
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

    /**
     * Every loaded dimensional lasing recipe encodes without throwing and decodes back to the same
     * region fluid, amounts, and climate ranges, with each climate range written under its own name.
     */
    private static void lasingCodecRoundTrip(GameTestHelper helper) {
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        Codec<DimensionalLaserRecipe> codec = DimensionalLaserRecipe.VE_RECIPE_CODEC.codec();
        List<RecipeHolder<?>> recipes = helper.getLevel().getServer().getRecipeManager().getRecipes().stream()
                .filter(holder -> holder.value() instanceof DimensionalLaserRecipe)
                .toList();
        helper.assertFalse(recipes.isEmpty(), "no dimensional lasing recipes loaded");

        for (RecipeHolder<?> holder : recipes) {
            DimensionalLaserRecipe loaded = (DimensionalLaserRecipe) holder.value();
            JsonElement encoded = codec.encodeStart(ops, loaded).getOrThrow();
            DimensionalLaserRecipe decoded = codec.parse(ops, encoded).getOrThrow();
            Identifier id = holder.id().identifier();
            assertSameLasing(helper, id, loaded, decoded);

            if (id.equals(PLAINS_LASING)) {
                JsonObject climate = encoded.getAsJsonObject().getAsJsonObject("climate");
                assertRange(helper, climate, "erosion", -0.5F, 0.25F);
                assertRange(helper, climate, "humidity", -0.3F, -0.15F);
                assertRange(helper, climate, "temperature", -0.15F, 0.05F);
            }
        }
        GameTestLog.pass(helper, "recipe_lasing_codec_round_trip", "recipes=" + recipes.size());
    }

    /**
     * Each blasting recipe lists the fluids hot enough for its own minimum heat, whichever recipe is
     * asked first, and a fluid exactly at the minimum heat counts.
     */
    private static void blastingHeatFluidsPerRecipe(GameTestHelper helper) {
        int lavaTemperature = Fluids.LAVA.getFluidType().getTemperature();
        GameTestLog.value("recipe_blasting_heat_fluids_per_recipe", "lava_temperature", lavaTemperature);

        List<Fluid> steelHeat = blastingAt(900).getHotEnoughFluids();
        List<Fluid> hotterHeat = blastingAt(lavaTemperature + 200).getHotEnoughFluids();
        List<Fluid> lavaHeat = blastingAt(lavaTemperature).getHotEnoughFluids();

        helper.assertTrue(steelHeat.contains(Fluids.LAVA), "lava missing from the 900 K recipe");
        helper.assertFalse(hotterHeat.contains(Fluids.LAVA), "lava listed for a recipe hotter than lava");
        helper.assertTrue(lavaHeat.contains(Fluids.LAVA), "lava missing from a recipe at lava's own temperature");
        GameTestLog.pass(helper, "recipe_blasting_heat_fluids_per_recipe", "");
    }

    /**
     * The steel recipe needs at least the configured heat-source amount in the Blast Furnace's heat
     * tank and drains exactly that amount when it completes.
     */
    private static void blastingHeatSourceDrain(GameTestHelper helper) {
        int consumption = Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get();
        Optional<RecipeHolder<?>> steelHolder = helper.getLevel().recipeAccess().byKey(STEEL_BLASTING);
        helper.assertTrue(steelHolder.isPresent(), "steel blasting recipe not loaded");
        IndustrialBlastingRecipe steel = (IndustrialBlastingRecipe) steelHolder.get().value();
        BasicParser parser = steel.getParser();

        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.BLAST_FURNACE.block().get());
        helper.runAfterDelay(2, () -> {
            furnace.getInventory().setStack(2, ingredientStack(steel, 0));
            furnace.getInventory().setStack(3, ingredientStack(steel, 1));

            furnace.getRelationalTank(0).getTank().setFluid(new FluidStack(Fluids.LAVA, consumption - 10));
            helper.assertFalse(parser.isCompleteRecipe(furnace), "steel started with less lava than one operation consumes");

            furnace.getRelationalTank(0).getTank().setFluid(new FluidStack(Fluids.LAVA, 1000));
            helper.assertTrue(parser.isCompleteRecipe(furnace), "steel did not start with 1000 mB of lava");
            parser.completeRecipe(furnace);
            GameTestLog.assertEqual(helper, furnace.getFluidStackFromTank(0).getAmount(), 1000 - consumption, "lava left after one operation");
            GameTestLog.pass(helper, "recipe_blasting_heat_source_drain", "consumption=" + consumption);
        });
    }

    /**
     * A Primitive Blast Furnace holding one item fewer than a charring recipe needs stays idle past
     * the recipe's process time, and one more item lets it consume the whole count for a single fuel.
     */
    private static void primitiveCharringCounts(GameTestHelper helper) {
        VETileEntity caneFurnace = MachineKit.place(helper, CANE_FURNACE, VEBlocks.PRIMITIVE_BLAST_FURNACE.block().get());
        VETileEntity cactusFurnace = MachineKit.place(helper, CACTUS_FURNACE, VEBlocks.PRIMITIVE_BLAST_FURNACE.block().get());
        helper.runAfterDelay(2, () -> {
            caneFurnace.getInventory().setStack(0, new ItemStack(Items.SUGAR_CANE, 15));
            cactusFurnace.getInventory().setStack(0, new ItemStack(Items.CACTUS, 2));
        });
        helper.runAfterDelay(CHARRING_TICKS + 12, () -> {
            assertIdle(helper, caneFurnace, 15, "sugar cane");
            assertIdle(helper, cactusFurnace, 2, "cactus");
            caneFurnace.getInventory().setStack(0, new ItemStack(Items.SUGAR_CANE, 16));
            cactusFurnace.getInventory().setStack(0, new ItemStack(Items.CACTUS, 3));
            helper.succeedWhen(() -> {
                assertCharred(helper, caneFurnace, VEItems.CANE_CHARCOAL.get(), "16 sugar cane");
                assertCharred(helper, cactusFurnace, VEItems.CACTCHAR.get(), "3 cactus");
                GameTestLog.note("recipe_primitive_charring_counts", "");
            });
        });
    }

    /**
     * The log charring recipe takes burnable logs and wood, not nether stems, and runs for 800 ticks.
     */
    private static void primitiveCharringLogs(GameTestHelper helper) {
        Optional<RecipeHolder<?>> logHolder = helper.getLevel().recipeAccess().byKey(LOG_CHARRING);
        helper.assertTrue(logHolder.isPresent(), "log charring recipe not loaded");
        VERecipe logCharring = (VERecipe) logHolder.get().value();
        Ingredient logs = logCharring.getIngredient(0);

        helper.assertTrue(logs.test(new ItemStack(Items.OAK_LOG)), "oak log not accepted");
        helper.assertTrue(logs.test(new ItemStack(Items.STRIPPED_BIRCH_WOOD)), "stripped birch wood not accepted");
        helper.assertFalse(logs.test(new ItemStack(Items.CRIMSON_STEM)), "crimson stem accepted");
        helper.assertValueEqual(logCharring.getProcessTime(), 800, "log charring process time");
        helper.assertTrue(logCharring.getResult(0).is(Items.CHARCOAL), "log charring does not give charcoal");
        GameTestLog.pass(helper, "recipe_primitive_charring_logs", "");
    }

    /**
     * Every Voluminous Energy torch and soul torch recipe gives one torch per 400 ticks of its fuel's
     * burn time, and at least one.
     */
    private static void torchYieldMatchesBurnTime(GameTestHelper helper) {
        Map<Item, Integer> torchYields = new HashMap<>();
        Map<Item, Integer> soulTorchYields = new HashMap<>();
        helper.setBlock(FUEL_FURNACE, Blocks.FURNACE);
        AbstractFurnaceBlockEntity furnace = helper.getBlockEntity(FUEL_FURNACE, AbstractFurnaceBlockEntity.class);
        int checked = 0;

        for (RecipeHolder<?> holder : helper.getLevel().getServer().getRecipeManager().getRecipes()) {
            Identifier id = holder.id().identifier();
            if (!id.getNamespace().equals(VoluminousEnergy.MODID) || !id.getPath().startsWith(TORCH_RECIPE_PATH)) {
                continue;
            }
            ShapedRecipe recipe = (ShapedRecipe) holder.value();
            ItemStack result = recipe.assemble(CraftingInput.EMPTY);
            Map<Item, Integer> yields = result.is(Items.SOUL_TORCH) ? soulTorchYields : torchYields;

            for (Item fuel : recipe.getIngredients().getFirst().orElseThrow().items().map(Holder::value).toList()) {
                int expected = Math.max(1, furnaceBurnTicks(helper, furnace, fuel) / TICKS_PER_TORCH);
                helper.assertValueEqual(result.getCount(), expected, id + " yield for " + fuel);
                yields.put(fuel, result.getCount());
                checked++;
            }
        }

        for (Map<Item, Integer> yields : List.of(torchYields, soulTorchYields)) {
            assertTorchYield(helper, yields, VEItems.CACTCHAR.get(), 4);
            assertTorchYield(helper, yields, VEItems.CANE_CHARCOAL.get(), 4);
            assertTorchYield(helper, yields, VEItems.COALCOKE.get(), 8);
            assertTorchYield(helper, yields, VEItems.PETCOKE.get(), 10);
        }
        GameTestLog.pass(helper, "recipe_torch_yield_matches_burn_time", "fuels=" + checked);
    }

    private static int furnaceBurnTicks(GameTestHelper helper, AbstractFurnaceBlockEntity furnace, Item fuel) {
        try {
            Method burnDuration = AbstractFurnaceBlockEntity.class.getDeclaredMethod("getBurnDuration", ServerLevel.class, ItemStack.class);
            burnDuration.setAccessible(true);
            return (int) burnDuration.invoke(furnace, helper.getLevel(), new ItemStack(fuel));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Every Voluminous Energy machine recipe keeps each ingredient's count when sent to a client, so a
     * single-player world, whose client copies replace the server's, still needs the full count.
     */
    private static void ingredientCountsSurviveNetwork(GameTestHelper helper) {
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        List<String> mismatches = new ArrayList<>();
        int checked = 0;

        for (RecipeHolder<?> holder : helper.getLevel().getServer().getRecipeManager().getRecipes()) {
            if (!(holder.value() instanceof VERecipe loaded)) {
                continue;
            }
            VERecipe received = (VERecipe) sendToClient(loaded.getSerializer(), loaded, registryAccess);
            for (int ingredient = 0; loaded.hasIngredient(ingredient); ingredient++) {
                int expected = loaded.getIngredientCount(ingredient);
                int actual = received.getIngredientCount(ingredient);
                if (actual != expected) {
                    mismatches.add(holder.id().identifier() + " ingredient " + ingredient + ": " + actual + " instead of " + expected);
                }
                checked++;
            }
        }
        helper.assertTrue(mismatches.isEmpty(), mismatches.size() + " ingredient counts changed over the network, first: " + mismatches.stream().findFirst().orElse(""));
        GameTestLog.pass(helper, "recipe_ingredient_counts_survive_network", "ingredients=" + checked);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private static <T extends Recipe<?>> T sendToClient(RecipeSerializer<T> serializer, Recipe<?> recipe, RegistryAccess registryAccess) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        serializer.streamCodec().encode(buffer, (T) recipe);
        return serializer.streamCodec().decode(buffer);
    }

    private static void assertIdle(GameTestHelper helper, VETileEntity furnace, int inputCount, String input) {
        helper.assertValueEqual(furnace.getInventory().getStack(0).getCount(), inputCount, input + " left with too few to char");
        helper.assertTrue(furnace.getInventory().getStack(1).isEmpty(), "charred with only " + inputCount + " " + input);
    }

    private static void assertCharred(GameTestHelper helper, VETileEntity furnace, Item fuel, String input) {
        ItemStack output = furnace.getInventory().getStack(1);
        helper.assertTrue(furnace.getInventory().getStack(0).isEmpty(), input + " not fully consumed");
        helper.assertTrue(output.is(fuel) && output.getCount() == 1, input + " gave " + output + " instead of 1 " + fuel);
    }

    private static void assertTorchYield(GameTestHelper helper, Map<Item, Integer> yields, Item fuel, int expected) {
        helper.assertValueEqual(yields.get(fuel), expected, "torch recipe yield for " + fuel);
    }

    private static IndustrialBlastingRecipe blastingAt(int minimumHeat) {
        IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe();
        recipe.setMinimumHeat(minimumHeat);
        return recipe;
    }

    private static ItemStack ingredientStack(VERecipe recipe, int index) {
        return new ItemStack(recipe.getIngredient(index).items().findFirst().orElseThrow(), recipe.getIngredientCount(index));
    }

    private static void assertSameLasing(GameTestHelper helper, Identifier id, DimensionalLaserRecipe expected, DimensionalLaserRecipe actual) {
        GameTestLog.assertEqual(helper, actual.getRegionFluid(), expected.getRegionFluid(), id + " region fluid");
        GameTestLog.assertEqual(helper, actual.getMinimumAmount(), expected.getMinimumAmount(), id + " minimum amount");
        GameTestLog.assertEqual(helper, actual.getMaximumAmount(), expected.getMaximumAmount(), id + " maximum amount");
        GameTestLog.assertEqual(helper, actual.getContinentalnessMin(), expected.getContinentalnessMin(), id + " continentalness minimum");
        GameTestLog.assertEqual(helper, actual.getContinentalnessMax(), expected.getContinentalnessMax(), id + " continentalness maximum");
        GameTestLog.assertEqual(helper, actual.getErosionMin(), expected.getErosionMin(), id + " erosion minimum");
        GameTestLog.assertEqual(helper, actual.getErosionMax(), expected.getErosionMax(), id + " erosion maximum");
        GameTestLog.assertEqual(helper, actual.getHumidityMin(), expected.getHumidityMin(), id + " humidity minimum");
        GameTestLog.assertEqual(helper, actual.getHumidityMax(), expected.getHumidityMax(), id + " humidity maximum");
        GameTestLog.assertEqual(helper, actual.getTemperatureMin(), expected.getTemperatureMin(), id + " temperature minimum");
        GameTestLog.assertEqual(helper, actual.getTemperatureMax(), expected.getTemperatureMax(), id + " temperature maximum");
    }

    private static void assertRange(GameTestHelper helper, JsonObject climate, String name, float minimum, float maximum) {
        JsonObject range = climate.getAsJsonObject(name);
        GameTestLog.assertEqual(helper, range.get("minimum").getAsFloat(), minimum, PLAINS_LASING + " encoded " + name + " minimum");
        GameTestLog.assertEqual(helper, range.get("maximum").getAsFloat(), maximum, PLAINS_LASING + " encoded " + name + " maximum");
    }
}
