package com.veteam.voluminousenergy.util.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.recipe.SawmillRecipe;
import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeUtil {

    private static final HashMap<Item, ItemStack> plankToRecipeMap = new HashMap<>();

    @Nullable
    public static ItemStack getPlankFromLogParallel(Level world, ItemStack logStack) {

        if (plankToRecipeMap.isEmpty()) {
            world.getRecipeManager().getRecipes().parallelStream().forEach(r -> {
                if (r.value() instanceof CraftingRecipe recipe) {
                    ItemStack result = recipe.getResultItem(world.registryAccess());
                    if (RegistryLookups.lookupItem(result).toString().contains("plank")) {
                        recipe.getIngredients().forEach(ingredient -> {
                            for (ItemStack ingredientItem : ingredient.getItems()) {
                                plankToRecipeMap.put(ingredientItem.getItem(), result);
                            }
                        });
                    }
                }
            });
        }
        if (!plankToRecipeMap.containsKey(logStack.getItem())) return null;

        return plankToRecipeMap.get(logStack.getItem()).copy();
    }

    // Parallel query of global recipes, will this improve performance?
    public static ArrayList<ItemStack> getLogFromPlankParallel(Level world, ItemStack plankStack) {
        if (plankStack.isEmpty()) return null;
        AtomicReference<ArrayList<ItemStack>> atomicItemStackArray = new AtomicReference<>(new ArrayList<ItemStack>());

        world.getRecipeManager().getRecipes().parallelStream().forEach(r -> {
            if (r.value() instanceof CraftingRecipe recipe) {
                if (RegistryLookups.lookupItem(recipe.getResultItem(world.registryAccess())).toString().contains("plank")) {
                    if (recipe.getResultItem(world.registryAccess()).is(plankStack.getItem())) {
                        recipe.getIngredients().forEach(ingredient -> {
                            for (int i = 0; i < ingredient.getItems().length; i++)
                                atomicItemStackArray.get().add(ingredient.getItems()[i]);
                        });
                    }
                }
            }
        });

        return atomicItemStackArray.get();
    }

    public static SawmillRecipe getSawmillingRecipeFromLog(Level world, ItemStack logStack) { // Parallel by default
        if (logStack.isEmpty()) return null;
        AtomicReference<SawmillRecipe> atomicRecipe = new AtomicReference<>(null);
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof SawmillRecipe SawmillRecipe) {
                if (!SawmillRecipe.isLogRecipe()) {
                    for (ItemStack ingredientStack : SawmillRecipe.getIngredient(0).getItems()) {
                        if (ingredientStack.getItem().equals(logStack.getItem())) {
                            atomicRecipe.set(SawmillRecipe);
                            break;
                        }
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static SawmillRecipe getSawmillingRecipeFromPlank(Level world, ItemStack plankStack) { // Parallel by default
        if (plankStack.isEmpty()) return null;
        AtomicReference<SawmillRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof SawmillRecipe SawmillRecipe) {
                if (!SawmillRecipe.isLogRecipe()) {
                    if (SawmillRecipe.getResult(0).getItem().equals(plankStack.getItem())) {
                        atomicRecipe.set(SawmillRecipe);
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static SawmillRecipe getSawmillingRecipeFromSecondOutput(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        AtomicReference<SawmillRecipe> atomicRecipe = new AtomicReference<>(null);

        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof SawmillRecipe SawmillRecipe) {
                if (!SawmillRecipe.isLogRecipe()) {
                    Item item = SawmillRecipe.getResult(0).getItem();
                    if (itemStack.getItem().equals(item)) {
                        atomicRecipe.set(SawmillRecipe);
                    }
                }
            }
        });
        return atomicRecipe.get();
    }

    private static final HashMap<Integer, StirlingGeneratorRecipe> stirlingGeneratorRecipeMap = new HashMap<>();

    public static StirlingGeneratorRecipe getStirlingGeneratorRecipe(Level world, ItemStack solidFuelStack) { // Parallel by default
        if (solidFuelStack.isEmpty()) return null;

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof StirlingGeneratorRecipe stirlingGeneratorRecipe) {
                for (ItemStack itemStack : stirlingGeneratorRecipe.getIngredient(0).getItems()) {
                    if (RegistryLookups.lookupItem(itemStack.getItem()) == null) continue;
                    stirlingGeneratorRecipeMap.put(RegistryLookups.lookupItem(itemStack.getItem()).hashCode(), stirlingGeneratorRecipe);
                }
            }
        });
        if (RegistryLookups.lookupItem(solidFuelStack.getItem()) == null) return null;
        return stirlingGeneratorRecipeMap.get(RegistryLookups.lookupItem(solidFuelStack.getItem()).hashCode());
    }

    public static Lazy<ArrayList<Item>> createLazyAnthology(Lazy<ArrayList<Item>>... toAnthologize) {
        return Lazy.of(() -> {
            ArrayList<Item> anthology = new ArrayList<>();
            for (Lazy<ArrayList<Item>> items : toAnthologize) {
                anthology.addAll(items.get());
            }
            return anthology;
        });
    }

    private static ArrayList<CrusherRecipe> cachedCrusherRecipes = new ArrayList<>();

    public static ArrayList<CrusherRecipe> getCrusherRecipes(Level level) {
        if (cachedCrusherRecipes.isEmpty()) {
            for (RecipeHolder<?> recipe : level.getRecipeManager().getRecipes()) {
                if (recipe.value() instanceof CrusherRecipe crusherRecipe) {
                    cachedCrusherRecipes.add(crusherRecipe);
                }
            }
        }

        return cachedCrusherRecipes;
    }

    private static final HashMap<Integer, CrusherRecipe> CrusherIORecipeCache = new HashMap<>();

    public static Optional<CrusherRecipe> getCrusherRecipeFromAnyOutputAndTryInput(Item output, Item potentiallyKnownInput, Level level) {
        int itemPairHash = new Pair<>(output, potentiallyKnownInput).hashCode();
        if (CrusherIORecipeCache.containsKey(itemPairHash)) {
            return Optional.of(CrusherIORecipeCache.get(itemPairHash));
        }

        AtomicReference<ArrayList<CrusherRecipe>> atomicSublist = new AtomicReference<>(new ArrayList<>());

        getCrusherRecipes(level).forEach(recipe -> {
            if (recipe.getResult(0).is(output) || recipe.getResult(1).is(output)) {
                atomicSublist.get().add(recipe);
            }
        });


        // This is for more accurate recipe finding; if multiple recipes have same output, but different inputs, knowing the input we can select the correct one
        if (potentiallyKnownInput != null && potentiallyKnownInput != Items.AIR && !atomicSublist.get().isEmpty()) {
            for (CrusherRecipe crusherRecipe : atomicSublist.get()) { // This feeds off of the sublist; Therefore this is ONLY Crusher Recipes with this output
                if (crusherRecipe == null) continue;
                for (ItemStack ingredientStack : crusherRecipe.getIngredient(0).getItems()) {
                    if (ingredientStack.getItem() == potentiallyKnownInput) {
                        CrusherIORecipeCache.put(itemPairHash, crusherRecipe);
                        return Optional.of(crusherRecipe);
                    }
                }

            }
        }

        try {
            return Optional.ofNullable(atomicSublist.get().get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Ingredient modifyIngredientAmounts(Ingredient ingredient, int amounts) {
        for (ItemStack stack : ingredient.getItems()) {
            stack.setCount(amounts);
        }
        return ingredient;
    }
}
