package com.veteam.voluminousenergy.util.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.util.RegistryLookups;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeUtil {

    public static ToolingRecipe getToolingRecipeFromBitAndBase(Level world, ItemStack bitStack, ItemStack baseStack){
        if(baseStack.isEmpty() || bitStack.isEmpty()) return null;
        for (RecipeHolder<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe.value() instanceof ToolingRecipe toolingRecipe){
                if (toolingRecipe.getBits().contains(bitStack.getItem())
                        && toolingRecipe.getBases().contains(baseStack.getItem())) {
                    return toolingRecipe;
                }
            }
        }
        return null;
    }

    public static ToolingRecipe getToolingRecipeFromResult(Level world, ItemStack resultStack){
        if(resultStack.isEmpty()) return null;
        for (RecipeHolder<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe.value() instanceof ToolingRecipe toolingRecipe){
                if(toolingRecipe.getResult(0).is(resultStack.getItem())){
                    return toolingRecipe;
                }
            }
        }
        return null;
    }

    private static final HashMap<Item,ItemStack> plankToRecipeMap = new HashMap<>();
    @Nullable
    public static ItemStack getPlankFromLogParallel(Level world, ItemStack logStack){

        if(plankToRecipeMap.isEmpty()) {
            world.getRecipeManager().getRecipes().parallelStream().forEach(r -> {
                if (r.value() instanceof CraftingRecipe recipe) {
                    ItemStack result = recipe.getResultItem(world.registryAccess());
                    if (RegistryLookups.lookupItem(result).toString().contains("plank")){
                        recipe.getIngredients().forEach(ingredient -> {
                            for (ItemStack ingredientItem : ingredient.getItems()){
                                plankToRecipeMap.put(ingredientItem.getItem(),result);
                            }
                        });
                    }
                }
            });
        }
        if(!plankToRecipeMap.containsKey(logStack.getItem())) return null;

        return plankToRecipeMap.get(logStack.getItem()).copy();
    }

    // Parallel query of global recipes, will this improve performance?
    public static ArrayList<ItemStack> getLogFromPlankParallel(Level world, ItemStack plankStack){
        if (plankStack.isEmpty()) return null;
        AtomicReference<ArrayList<ItemStack>> atomicItemStackArray = new AtomicReference<>(new ArrayList<ItemStack>());

        world.getRecipeManager().getRecipes().parallelStream().forEach(r -> {
            if (r.value() instanceof CraftingRecipe recipe){
                if (RegistryLookups.lookupItem(recipe.getResultItem(world.registryAccess())).toString().contains("plank")){
                    if (recipe.getResultItem(world.registryAccess()).is(plankStack.getItem())){
                        recipe.getIngredients().forEach(ingredient -> {
                            for(int i = 0; i < ingredient.getItems().length; i++) atomicItemStackArray.get().add(ingredient.getItems()[i]);
                        });
                    }
                }
            }
        });

        return atomicItemStackArray.get();
    }

    public static VEFluidSawmillRecipe getSawmillingRecipeFromLog(Level world, ItemStack logStack){ // Parallel by default
        if (logStack.isEmpty()) return null;
        AtomicReference<VEFluidSawmillRecipe> atomicRecipe = new AtomicReference<>(null);
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if(recipe.value() instanceof VEFluidSawmillRecipe VEFluidSawmillRecipe){
                if (!VEFluidSawmillRecipe.isLogRecipe()){
                    for (ItemStack ingredientStack : VEFluidSawmillRecipe.getIngredient(0).getItems()){
                        if (ingredientStack.getItem().equals(logStack.getItem())){
                            atomicRecipe.set(VEFluidSawmillRecipe);
                            break;
                        }
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static VEFluidSawmillRecipe getSawmillingRecipeFromPlank(Level world, ItemStack plankStack){ // Parallel by default
        if (plankStack.isEmpty()) return null;
        AtomicReference<VEFluidSawmillRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof VEFluidSawmillRecipe VEFluidSawmillRecipe){
                if (!VEFluidSawmillRecipe.isLogRecipe()){
                    if (VEFluidSawmillRecipe.getResult(0).getItem().equals(plankStack.getItem())){
                        atomicRecipe.set(VEFluidSawmillRecipe);
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static VEFluidSawmillRecipe getSawmillingRecipeFromSecondOutput(Level level, ItemStack itemStack){
        if (itemStack.isEmpty()) return null;
        AtomicReference<VEFluidSawmillRecipe> atomicRecipe = new AtomicReference<>(null);

        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof VEFluidSawmillRecipe VEFluidSawmillRecipe){
                if (!VEFluidSawmillRecipe.isLogRecipe()){
                    Item item = VEFluidSawmillRecipe.getResult(0).getItem();
                    if (itemStack.getItem().equals(item)){
                        atomicRecipe.set(VEFluidSawmillRecipe);
                    }
                }
            }
        });
        return atomicRecipe.get();
    }
    private static final HashMap<Integer,StirlingGeneratorRecipe> stirlingGeneratorRecipeMap = new HashMap<>();
    public static StirlingGeneratorRecipe getStirlingGeneratorRecipe(Level world, ItemStack solidFuelStack){ // Parallel by default
        if (solidFuelStack.isEmpty()) return null;

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe.value() instanceof StirlingGeneratorRecipe stirlingGeneratorRecipe){
                for (ItemStack itemStack : stirlingGeneratorRecipe.getIngredient(0).getItems()) {
                    if(RegistryLookups.lookupItem(itemStack.getItem()) == null) continue;
                    stirlingGeneratorRecipeMap.put(RegistryLookups.lookupItem(itemStack.getItem()).hashCode(),stirlingGeneratorRecipe);
                }
            }
        });
        if(RegistryLookups.lookupItem(solidFuelStack.getItem()) == null) return null;
        return stirlingGeneratorRecipeMap.get(RegistryLookups.lookupItem(solidFuelStack.getItem()).hashCode());
    }

    public static Lazy<ArrayList<Item>> createLazyAnthology(Lazy<ArrayList<Item>>... toAnthologize){
        return Lazy.of(() -> {
            ArrayList<Item> anthology = new ArrayList<>();
            for (Lazy<ArrayList<Item>> items : toAnthologize){
                anthology.addAll(items.get());
            }
            return anthology;
        });
    }

    private static ArrayList<CrusherRecipe> cachedCrusherRecipes = new ArrayList<>();
    public static ArrayList<CrusherRecipe> getCrusherRecipes(Level level){
        if (cachedCrusherRecipes.isEmpty()){
            for (RecipeHolder<?> recipe : level.getRecipeManager().getRecipes()){
                if (recipe.value() instanceof CrusherRecipe crusherRecipe){
                    cachedCrusherRecipes.add(crusherRecipe);
                }
            }
        }

        return cachedCrusherRecipes;
    }

    private static final HashMap<Integer,CrusherRecipe> CrusherIORecipeCache = new HashMap<>();
    public static Optional<CrusherRecipe> getCrusherRecipeFromAnyOutputAndTryInput(Item output, Item potentiallyKnownInput, Level level){
        int itemPairHash = new Pair<>(output, potentiallyKnownInput).hashCode();
        if (CrusherIORecipeCache.containsKey(itemPairHash)){
            return Optional.of(CrusherIORecipeCache.get(itemPairHash));
        }

        AtomicReference<ArrayList<CrusherRecipe>> atomicSublist = new AtomicReference<>(new ArrayList<>());

        getCrusherRecipes(level).forEach(recipe -> {
            if (recipe.getResult(0).is(output) || recipe.getResult(1).is(output)){
                atomicSublist.get().add(recipe);
            }
        });


        // This is for more accurate recipe finding; if multiple recipes have same output, but different inputs, knowing the input we can select the correct one
        if (potentiallyKnownInput != null && potentiallyKnownInput != Items.AIR && !atomicSublist.get().isEmpty()){
            for (CrusherRecipe crusherRecipe : atomicSublist.get()){ // This feeds off of the sublist; Therefore this is ONLY Crusher Recipes with this output
                if (crusherRecipe == null) continue;
                for (ItemStack ingredientStack : crusherRecipe.getIngredient(0).getItems()){
                    if (ingredientStack.getItem() == potentiallyKnownInput) {
                        CrusherIORecipeCache.put(itemPairHash, crusherRecipe);
                        return  Optional.of(crusherRecipe);
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

    public static FluidStack pullFluidFromJSON(String id, JsonObject json) {
        ResourceLocation bucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get(id).getAsJsonObject(),"fluid","minecraft:empty"),':');
        int firstAmount = GsonHelper.getAsInt(json.get(id).getAsJsonObject(),"amount",0);
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstAmount);
    }

    public static ItemStack pullItemFromJSON(String id, JsonObject json) {
        ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get(id).getAsJsonObject(),"item","minecraft:empty"),':');
        int count = GsonHelper.getAsInt(json.get(id).getAsJsonObject(),"amount",1);

        return new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation),count);
    }

    public static Ingredient modifyIngredientAmounts(Ingredient ingredient, int amounts) {
        for(ItemStack stack : ingredient.getItems()) {
            stack.setCount(amounts);
        }
        return ingredient;
    }

    public static Ingredient pullUnknownItemFromJSON(JsonObject object,int count) {
        ResourceLocation location;
        boolean isTag = false;
        if(object.has("tag")){
            location = ResourceLocation.of(GsonHelper.getAsString(object,"tag","minecraft:air"),':');
            isTag = true;
        } else if(object.has("item")){
            location = ResourceLocation.of(GsonHelper.getAsString(object,"item","minecraft:air"),':');
        } else {
            throw new JsonSyntaxException("Bad recipe syntax!");
        }

        if(!isTag) {
            Item item = ForgeRegistries.ITEMS.getValue(location);
            if(item == null) {
                throw new IllegalStateException("Fluid does not exist for a recipe!");
            }
            return Ingredient.of(new ItemStack(item,count));
        }

        TagKey<Item> tag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), location);

        return RecipeUtil.modifyIngredientAmounts(Ingredient.of(tag),count);
    }
}
