package com.veteam.voluminousenergy.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

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
    private static final ResourceLocation PLAINS_LASING = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "dimensional_lasing/crude_oil_plains");
    private static final ResourceLocation STEEL_BLASTING = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "industrial_blasting/steel");
    private static final BlockPos CANE_FURNACE = new BlockPos(1, 1, 2);
    private static final BlockPos CACTUS_FURNACE = new BlockPos(3, 1, 2);
    private static final ResourceLocation LOG_CHARRING = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "primitive_blast_furnacing/logs_to_charcoal");
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
        tests.add(GameTestDefinition.of("recipe_blasting_heat_source_codec", 20, RecipeTests::blastingHeatSourceCodec));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_counts", 3 * CHARRING_TICKS, RecipeTests::primitiveCharringCounts));
        tests.add(GameTestDefinition.of("recipe_primitive_charring_logs", 20, RecipeTests::primitiveCharringLogs));
        tests.add(GameTestDefinition.of("recipe_torch_yield_matches_burn_time", 20, RecipeTests::torchYieldMatchesBurnTime));
        tests.add(GameTestDefinition.of("recipe_ingredient_counts_enforced", 20, RecipeTests::ingredientCountsEnforced));
        tests.add(GameTestDefinition.of("recipe_shift_click_top_up_starts_recipe", 20, RecipeTests::shiftClickTopUpStartsRecipe));
    }

    /**
     * Every loaded dimensional lasing recipe encodes without throwing and decodes back to the same
     * region fluid, amounts, and climate ranges, with each climate range written under its own name.
     */
    private static void lasingCodecRoundTrip(GameTestHelper helper) {
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        Codec<DimensionalLaserRecipe> codec = DimensionalLaserRecipe.SERIALIZER.codec().codec();
        List<RecipeHolder<VERecipe>> recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(DimensionalLaserRecipe.RECIPE_TYPE);
        helper.assertFalse(recipes.isEmpty(), "no dimensional lasing recipes loaded");

        for (RecipeHolder<VERecipe> holder : recipes) {
            DimensionalLaserRecipe loaded = (DimensionalLaserRecipe) holder.value();
            JsonElement encoded = codec.encodeStart(ops, loaded).getOrThrow();
            DimensionalLaserRecipe decoded = codec.parse(ops, encoded).getOrThrow();
            assertSameLasing(helper, holder.id(), loaded, decoded);

            if (holder.id().equals(PLAINS_LASING)) {
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
     * The shipped steel recipe needs at least its 50 mB heat-source amount in the Blast Furnace's heat
     * tank and drains exactly that amount when it completes.
     */
    private static void blastingHeatSourceDrain(GameTestHelper helper) {
        Optional<RecipeHolder<?>> steelHolder = helper.getLevel().getRecipeManager().byKey(STEEL_BLASTING);
        helper.assertTrue(steelHolder.isPresent(), "steel blasting recipe not loaded");
        IndustrialBlastingRecipe steel = (IndustrialBlastingRecipe) steelHolder.get().value();
        int consumption = steel.getHeatSourceConsumption();
        helper.assertValueEqual(consumption, 50, "steel heat source consumption");
        BasicParser parser = steel.getParser();

        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.BLAST_FURNACE.block().get());
        helper.runAfterDelay(2, () -> {
            loadBlastingIngredients(furnace, steel);

            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, consumption - 10));
            helper.assertFalse(parser.isCompleteRecipe(furnace), "steel started with less lava than one operation consumes");

            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, 1000));
            helper.assertTrue(parser.isCompleteRecipe(furnace), "steel did not start with 1000 mB of lava");
            parser.completeRecipe(furnace);
            helper.assertValueEqual(furnace.getFluidStackFromTank(0).getAmount(), 1000 - consumption, "lava left after one operation");
            GameTestLog.pass(helper, "recipe_blasting_heat_source_drain", "consumption=" + consumption);
        });
    }

    /**
     * Blasting JSON without a heat-source consumption, or with 0, needs a hot-enough fluid in the tank
     * but drains none of it; 250 needs and drains 250 mB and survives the trip to a client; a negative
     * amount fails to decode.
     */
    private static void blastingHeatSourceCodec(GameTestHelper helper) {
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        IndustrialBlastingRecipe omitted = parseBlasting(ops, null);
        IndustrialBlastingRecipe zero = parseBlasting(ops, 0);
        IndustrialBlastingRecipe costly = parseBlasting(ops, 250);
        helper.assertValueEqual(omitted.getHeatSourceConsumption(), 0, "consumption with the field omitted");
        helper.assertValueEqual(zero.getHeatSourceConsumption(), 0, "consumption with the field at 0");
        helper.assertValueEqual(costly.getHeatSourceConsumption(), 250, "consumption with the field at 250");
        helper.assertTrue(Recipe.CODEC.parse(ops, blastingJson(-1)).error().isPresent(), "negative heat source consumption decoded");
        IndustrialBlastingRecipe received = (IndustrialBlastingRecipe) sendToClient(costly.getSerializer(), costly, helper.getLevel().registryAccess());
        helper.assertValueEqual(received.getHeatSourceConsumption(), 250, "consumption after sending to a client");
        helper.assertValueEqual(received.getMinimumHeat(), 900, "minimum heat after sending to a client");

        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.BLAST_FURNACE.block().get());
        helper.runAfterDelay(2, () -> {
            assertDrainsNoHeat(helper, furnace, omitted, "omitted");
            assertDrainsNoHeat(helper, furnace, zero, "zero");

            BasicParser parser = costly.getParser();
            loadBlastingIngredients(furnace, costly);
            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, 200));
            helper.assertFalse(parser.isCompleteRecipe(furnace), "250 mB recipe started with 200 mB of lava");
            MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, 1000));
            helper.assertTrue(parser.isCompleteRecipe(furnace), "250 mB recipe did not start with 1000 mB of lava");
            parser.completeRecipe(furnace);
            helper.assertValueEqual(furnace.getFluidStackFromTank(0).getAmount(), 750, "lava left after a 250 mB operation");
            GameTestLog.pass(helper, "recipe_blasting_heat_source_codec", "");
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
        Optional<RecipeHolder<?>> logHolder = helper.getLevel().getRecipeManager().byKey(LOG_CHARRING);
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
        int checked = 0;

        for (RecipeHolder<?> holder : helper.getLevel().getRecipeManager().getRecipes()) {
            ResourceLocation id = holder.id();
            if (!id.getNamespace().equals(VoluminousEnergy.MODID) || !id.getPath().startsWith(TORCH_RECIPE_PATH)) {
                continue;
            }
            Recipe<?> recipe = holder.value();
            ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
            Map<Item, Integer> yields = result.is(Items.SOUL_TORCH) ? soulTorchYields : torchYields;

            for (ItemStack fuel : recipe.getIngredients().getFirst().getItems()) {
                int expected = Math.max(1, fuel.getBurnTime(RecipeType.SMELTING) / TICKS_PER_TORCH);
                helper.assertValueEqual(result.getCount(), expected, id + " yield for " + fuel.getItem());
                yields.put(fuel.getItem(), result.getCount());
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
        Optional<RecipeHolder<?>> holder = helper.getLevel().getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, recipePath));
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

    @SuppressWarnings("unchecked")
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

    private static void assertDrainsNoHeat(GameTestHelper helper, VETileEntity furnace, IndustrialBlastingRecipe recipe, String label) {
        BasicParser parser = recipe.getParser();
        helper.assertTrue(recipe.getFluidIngredient(0).test(new FluidStack(Fluids.LAVA, 1)), label + ": heat fluid ingredient rejects lava");

        loadBlastingIngredients(furnace, recipe);
        MachineKit.setTank(furnace, 0, FluidStack.EMPTY);
        helper.assertFalse(parser.isCompleteRecipe(furnace), label + ": started with an empty heat tank");
        MachineKit.setTank(furnace, 0, new FluidStack(Fluids.LAVA, 10));
        helper.assertTrue(parser.isCompleteRecipe(furnace), label + ": did not start with 10 mB of lava");
        parser.completeRecipe(furnace);
        helper.assertValueEqual(furnace.getFluidStackFromTank(0).getAmount(), 10, label + ": lava left after an operation");
    }

    private static IndustrialBlastingRecipe parseBlasting(RegistryOps<JsonElement> ops, @Nullable Integer heatSourceConsumption) {
        return (IndustrialBlastingRecipe) Recipe.CODEC.parse(ops, blastingJson(heatSourceConsumption)).getOrThrow();
    }

    private static JsonObject blastingJson(@Nullable Integer heatSourceConsumption) {
        JsonObject json = JsonParser.parseString("""
                {
                  "type": "voluminousenergy:industrial_blasting",
                  "ingredients": [{"tag": "c:ingots/iron", "count": 1}, {"tag": "c:ingots/carbon", "count": 1}],
                  "process_time": 200,
                  "item_results": [{"item": "voluminousenergy:steel_ingot", "count": 1}],
                  "minimum_heat_kelvin": 900
                }
                """).getAsJsonObject();
        if (heatSourceConsumption != null) {
            json.addProperty("heat_source_consumption", heatSourceConsumption);
        }
        return json;
    }

    private static void loadBlastingIngredients(VETileEntity furnace, VERecipe recipe) {
        furnace.getInventory().setStackInSlot(2, ingredientStack(recipe, 0));
        furnace.getInventory().setStackInSlot(3, ingredientStack(recipe, 1));
    }

    private static ItemStack ingredientStack(VERecipe recipe, int index) {
        return recipe.getIngredient(index).getItems()[0].copyWithCount(recipe.getIngredientCount(index));
    }

    private static void assertSameLasing(GameTestHelper helper, ResourceLocation id, DimensionalLaserRecipe expected, DimensionalLaserRecipe actual) {
        helper.assertValueEqual(actual.getRegionFluid(), expected.getRegionFluid(), id + " region fluid");
        helper.assertValueEqual(actual.getMinimumAmount(), expected.getMinimumAmount(), id + " minimum amount");
        helper.assertValueEqual(actual.getMaximumAmount(), expected.getMaximumAmount(), id + " maximum amount");
        helper.assertValueEqual(actual.getContinentalnessMin(), expected.getContinentalnessMin(), id + " continentalness minimum");
        helper.assertValueEqual(actual.getContinentalnessMax(), expected.getContinentalnessMax(), id + " continentalness maximum");
        helper.assertValueEqual(actual.getErosionMin(), expected.getErosionMin(), id + " erosion minimum");
        helper.assertValueEqual(actual.getErosionMax(), expected.getErosionMax(), id + " erosion maximum");
        helper.assertValueEqual(actual.getHumidityMin(), expected.getHumidityMin(), id + " humidity minimum");
        helper.assertValueEqual(actual.getHumidityMax(), expected.getHumidityMax(), id + " humidity maximum");
        helper.assertValueEqual(actual.getTemperatureMin(), expected.getTemperatureMin(), id + " temperature minimum");
        helper.assertValueEqual(actual.getTemperatureMax(), expected.getTemperatureMax(), id + " temperature maximum");
    }

    private static void assertRange(GameTestHelper helper, JsonObject climate, String name, float minimum, float maximum) {
        JsonObject range = climate.getAsJsonObject(name);
        helper.assertValueEqual(range.get("minimum").getAsFloat(), minimum, PLAINS_LASING + " encoded " + name + " minimum");
        helper.assertValueEqual(range.get("maximum").getAsFloat(), maximum, PLAINS_LASING + " encoded " + name + " maximum");
    }
}
