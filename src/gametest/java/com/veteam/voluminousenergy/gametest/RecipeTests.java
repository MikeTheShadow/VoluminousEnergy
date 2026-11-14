package com.veteam.voluminousenergy.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Recipe encoding, Blast Furnace heat-source rules, Primitive Blast Furnace charring, and torch yields
 * (specs machines/dimensional-laser, machines/blast-furnace, machines/primitive-blast-furnace, and
 * items/solid-fuels).
 */
public class RecipeTests {
    private static final BlockPos MACHINE = new BlockPos(2, 1, 2);
    private static final Identifier PLAINS_LASING = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "dimensional_lasing/crude_oil_plains");
    private static final ResourceKey<Recipe<?>> STEEL_BLASTING = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "industrial_blasting/steel"));
    private static final BlockPos CANE_FURNACE = new BlockPos(1, 1, 2);
    private static final BlockPos CACTUS_FURNACE = new BlockPos(3, 1, 2);
    private static final ResourceKey<Recipe<?>> LOG_CHARRING = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "primitive_blast_furnacing/logs_to_charcoal"));
    private static final String TORCH_RECIPE_PATH = "vanilla_crafting/torch/";
    private static final int TICKS_PER_TORCH = 400;
    private static final int CHARRING_TICKS = 200;
    private static final BlockPos ELECTROLYZER = new BlockPos(1, 1, 2);
    private static final BlockPos SEPARATOR = new BlockPos(3, 1, 2);

    private RecipeTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("recipe_lasing_codec_round_trip", 20, RecipeTests::lasingCodecRoundTrip));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_fluids_per_recipe", 20, RecipeTests::blastingHeatFluidsPerRecipe));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_source_drain", 20, RecipeTests::blastingHeatSourceDrain));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_counts", 3 * CHARRING_TICKS, RecipeTests::primitiveCharringCounts));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_logs", 20, RecipeTests::primitiveCharringLogs));
        tests.add(GameTestDefinition.of("recipe_torch_yield_matches_burn_time", 20, RecipeTests::torchYieldMatchesBurnTime));
        tests.add(GameTestDefinition.of("recipe_ingredient_counts_survive_network", 20, RecipeTests::ingredientCountsSurviveNetwork));
        tests.add(GameTestDefinition.of("recipe_ingredient_counts_enforced", 20, RecipeTests::ingredientCountsEnforced));
        tests.add(GameTestDefinition.of("recipe_shift_click_top_up_starts_recipe", 20, RecipeTests::shiftClickTopUpStartsRecipe));
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
            furnace.getInventory().setStackInSlot(2, ingredientStack(steel, 0));
            furnace.getInventory().setStackInSlot(3, ingredientStack(steel, 1));

            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, consumption - 10));
            helper.assertFalse(parser.isCompleteRecipe(furnace), "steel started with less lava than one operation consumes");

            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, 1000));
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
            caneFurnace.getInventory().setStackInSlot(0, new ItemStack(Items.SUGAR_CANE, 15));
            cactusFurnace.getInventory().setStackInSlot(0, new ItemStack(Items.CACTUS, 2));
        });
        helper.runAfterDelay(CHARRING_TICKS + 12, () -> {
            assertIdle(helper, caneFurnace, 15, "sugar cane");
            assertIdle(helper, cactusFurnace, 2, "cactus");
            caneFurnace.getInventory().setStackInSlot(0, new ItemStack(Items.SUGAR_CANE, 16));
            cactusFurnace.getInventory().setStackInSlot(0, new ItemStack(Items.CACTUS, 3));
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
        FuelValues fuelValues = helper.getLevel().fuelValues();
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
                int expected = Math.max(1, new ItemStack(fuel).getBurnTime(RecipeType.SMELTING, fuelValues) / TICKS_PER_TORCH);
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

    /**
     * The Electrolyzer and Centrifugal Separator refuse a recipe one item short of any ingredient's
     * count and consume exactly that count, both with the server's recipe and with the copy a client
     * receives, which a single-player world runs its machines on.
     */
    private static void ingredientCountsEnforced(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, ELECTROLYZER, VEBlocks.ELECTROLYZER.block().get());
        VETileEntity separator = MachineKit.place(helper, SEPARATOR, VEBlocks.CENTRIFUGAL_SEPARATOR.block().get());
        helper.runAfterDelay(2, () -> {
            assertCountEnforced(helper, electrolyzer, "electrolyzing/bauxite", 0,
                    new ItemStack(VEItems.BAUXITE_DUST.get(), 12));
            assertCountEnforced(helper, electrolyzer, "electrolyzing/copper_carbonate", 1,
                    new ItemStack(VEItems.COPPER_CARBONATE_DUST.get()), new ItemStack(Items.BUCKET, 2));
            assertCountEnforced(helper, separator, "centrifugal_separation/basalt_dust", 0,
                    new ItemStack(VEItems.BASALT_DUST.get(), 16));
            GameTestLog.pass(helper, "recipe_ingredient_counts_enforced", "");
        });
    }

    /**
     * Shift-clicking the missing items onto a partial input stack in an open Electrolyzer menu makes
     * the machine pick up the now complete recipe, as placing them by hand does.
     */
    private static void shiftClickTopUpStartsRecipe(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, ELECTROLYZER, VEBlocks.ELECTROLYZER.block().get());
        BasicProcessor processor = (BasicProcessor) electrolyzer.getRecipeProcessor();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        AbstractContainerMenu[] menu = new AbstractContainerMenu[1];
        helper.runAfterDelay(2, () -> {
            electrolyzer.getInventory().setStackInSlot(0, new ItemStack(VEItems.BAUXITE_DUST.get(), 10));
            menu[0] = electrolyzer.createMenu(1, player.getInventory(), player);
        });
        helper.runAfterDelay(4, () -> {
            helper.assertFalse(processor.isRecipeReady(), "electrolyzer started with 10 bauxite dust");
            ItemStack topUp = new ItemStack(VEItems.BAUXITE_DUST.get(), 2);
            player.getInventory().setItem(9, topUp);
            int menuSlot = menuSlotHolding(menu[0], topUp);
            helper.assertTrue(menuSlot > electrolyzer.getSlotManagers().size(), "player inventory slot not found in the menu");
            menu[0].quickMoveStack(player, menuSlot);
            helper.assertValueEqual(electrolyzer.getInventory().getStackInSlot(0).getCount(), 12, "bauxite dust after the shift-click");
        });
        helper.runAfterDelay(6, () -> {
            helper.assertTrue(processor.isRecipeReady(), "electrolyzer did not start after 2 bauxite dust were shift-clicked onto 10");
            GameTestLog.pass(helper, "recipe_shift_click_top_up_starts_recipe", "");
        });
    }

    private static int menuSlotHolding(AbstractContainerMenu menu, ItemStack stack) {
        for (int slot = 0; slot < menu.slots.size(); slot++) {
            if (menu.slots.get(slot).getItem() == stack) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Loads {@code inputs} into the machine's first slots, one short in {@code shortSlot}, and checks
     * the recipe only completes with the full count and then leaves every input slot empty.
     */
    private static void assertCountEnforced(GameTestHelper helper, VETileEntity machine, String recipePath, int shortSlot, ItemStack... inputs) {
        Optional<RecipeHolder<?>> holder = helper.getLevel().recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, recipePath)));
        helper.assertTrue(holder.isPresent(), recipePath + " not loaded");
        VERecipe loaded = (VERecipe) holder.get().value();
        VERecipe received = (VERecipe) sendToClient(loaded.getSerializer(), loaded, helper.getLevel().registryAccess());

        for (VERecipe recipe : List.of(loaded, received)) {
            String label = recipePath + (recipe == loaded ? " on the server" : " as a client receives it");
            BasicParser parser = recipe.getParser();
            loadInputs(machine, inputs);
            ItemStack shortStack = inputs[shortSlot].copyWithCount(inputs[shortSlot].getCount() - 1);
            machine.getInventory().setStackInSlot(shortSlot, shortStack);
            helper.assertFalse(parser.isCompleteRecipe(machine), label + " started with " + shortStack);

            loadInputs(machine, inputs);
            helper.assertTrue(parser.isCompleteRecipe(machine), label + " did not start with its full inputs");
            parser.completeRecipe(machine);
            for (int slot = 0; slot < inputs.length; slot++) {
                ItemStack left = machine.getInventory().getStackInSlot(slot);
                helper.assertTrue(left.isEmpty(), label + " left " + left + " in slot " + slot);
            }
        }
    }

    private static void loadInputs(VETileEntity machine, ItemStack... inputs) {
        VEItemStackHandler inventory = machine.getInventory();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            inventory.setStackInSlot(slot, slot < inputs.length ? inputs[slot].copy() : ItemStack.EMPTY);
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private static <T extends Recipe<?>> T sendToClient(RecipeSerializer<T> serializer, Recipe<?> recipe, RegistryAccess registryAccess) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        serializer.streamCodec().encode(buffer, (T) recipe);
        return serializer.streamCodec().decode(buffer);
    }

    private static void assertIdle(GameTestHelper helper, VETileEntity furnace, int inputCount, String input) {
        helper.assertValueEqual(furnace.getInventory().getStackInSlot(0).getCount(), inputCount, input + " left with too few to char");
        helper.assertTrue(furnace.getInventory().getStackInSlot(1).isEmpty(), "charred with only " + inputCount + " " + input);
    }

    private static void assertCharred(GameTestHelper helper, VETileEntity furnace, Item fuel, String input) {
        ItemStack output = furnace.getInventory().getStackInSlot(1);
        helper.assertTrue(furnace.getInventory().getStackInSlot(0).isEmpty(), input + " not fully consumed");
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
