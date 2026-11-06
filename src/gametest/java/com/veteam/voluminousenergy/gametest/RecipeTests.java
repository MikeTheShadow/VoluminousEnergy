package com.veteam.voluminousenergy.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

/**
 * Recipe encoding and Blast Furnace heat-source rules (specs machines/dimensional-laser and
 * machines/blast-furnace).
 */
public class RecipeTests {
    private static final BlockPos MACHINE = new BlockPos(2, 1, 2);
    private static final Identifier PLAINS_LASING = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "dimensional_lasing/crude_oil_plains");
    private static final ResourceKey<Recipe<?>> STEEL_BLASTING = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "industrial_blasting/steel"));

    private RecipeTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("recipe_lasing_codec_round_trip", 20, RecipeTests::lasingCodecRoundTrip));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_fluids_per_recipe", 20, RecipeTests::blastingHeatFluidsPerRecipe));
        tests.add(GameTestDefinition.of("recipe_blasting_heat_source_drain", 20, RecipeTests::blastingHeatSourceDrain));
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
