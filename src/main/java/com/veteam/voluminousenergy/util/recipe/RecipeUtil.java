package com.veteam.voluminousenergy.util.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeUtil {

    public static boolean isAqueoulizerOutput(Level level, FluidStack fluidStack){
        return isAqueoulizerOutput(level, fluidStack.getRawFluid(),0);
    }


    public static boolean isAqueoulizerOutput(Level level, Fluid fluid,int id){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof AqueoulizerRecipe aqueoulizerRecipe) {
                if (aqueoulizerRecipe.getOutputFluids().get(id).getRawFluid().isSame(fluid)){
                    atomicBoolean.set(true);
                }
            }
        });
        return atomicBoolean.get();
    }

    private static final HashMap<Integer,CombustionGeneratorFuelRecipe> combustionGeneratorFuelRecipeMap = new HashMap<>();

    public static boolean isCombustibleFuelWithoutLevel(Fluid fluid){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        CombustionGeneratorFuelRecipe.lazyFluidsWithVolumetricEnergy.parallelStream().forEach(lazyPair -> {
            if (lazyPair.get().getA().contains(fluid)) atomicBoolean.set(true);
        });
        return atomicBoolean.get();
    }

    public static ArrayList<Fluid> getCombustibleFuelsWithoutLevel(){
        AtomicReference<ArrayList<Fluid>> fuels = new AtomicReference<>(new ArrayList<>());
        CombustionGeneratorFuelRecipe.lazyFluidsWithVolumetricEnergy.forEach(lazyPair -> { // Parallelization wouldn't make sense here since all we do is writes, every thread would be blocked until they could write to the array
            fuels.get().addAll(lazyPair.get().getA());
        });
        return fuels.get();
    }

    public static boolean isOxidizer(Fluid fluid, Level level){
        if (level == null) return false;
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CombustionGeneratorOxidizerRecipe oxidizerRecipe){
                if (oxidizerRecipe.rawFluidInputList.get().contains(fluid)) {
                    atomicBoolean.set(true);
                }
            }
        });
        return atomicBoolean.get();
    }
    private static final HashMap<Integer,CombustionGeneratorOxidizerRecipe> combustionOxidizerMap = new HashMap<>();
    public static CombustionGeneratorOxidizerRecipe getOxidizerCombustionRecipe(Level world, FluidStack inputFluid){

        if(combustionOxidizerMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof CombustionGeneratorOxidizerRecipe combustionGeneratorOxidizerRecipe) {
                    for (FluidStack recipeFluid : combustionGeneratorOxidizerRecipe.fluidInputList.get()){
                        String hash = recipeFluid.getTranslationKey();
                        combustionOxidizerMap.put(hash.hashCode(),combustionGeneratorOxidizerRecipe);
                    }
                }
            }
        }

        String hash = inputFluid.getTranslationKey();
        return combustionOxidizerMap.get(hash.hashCode());

    }

    public static List<Fluid> getOxidizerFluids(Level world){
        if (world == null) return new ArrayList<>();
        AtomicReference<List<Fluid>> fluidList = new AtomicReference<>(new ArrayList<>());
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CombustionGeneratorOxidizerRecipe oxidizerRecipe){
                fluidList.get().addAll(oxidizerRecipe.rawFluidInputList.get());
            }
        });
        return fluidList.get();
    }

    public static CombustionGeneratorOxidizerRecipe getOxidizerCombustionRecipeWithoutLevel(FluidStack fluidStack){
        return getOxidizerCombustionRecipeWithoutLevel(fluidStack.getRawFluid());
    }

    public static CombustionGeneratorOxidizerRecipe getOxidizerCombustionRecipeWithoutLevel(Fluid fluid){
        AtomicReference<CombustionGeneratorOxidizerRecipe> recipeToReturn = new AtomicReference<>(null);
        CombustionGeneratorOxidizerRecipe.oxidizerRecipes.parallelStream().forEach(oxidizerRecipe -> {
            if (oxidizerRecipe.rawFluidInputList.get().contains(fluid)){
                recipeToReturn.set(oxidizerRecipe);
            }
        });
        return recipeToReturn.get();
    }

    public static boolean isFirstIngredientForIndustrialBlastingRecipe(Level world, ItemStack firstInput){
        if (firstInput.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).getFirstInputAsList().contains(firstInput.getItem())) return true;
            }
        }
        return false;
    }

    public static boolean isSecondIngredientForIndustrialBlastingRecipe(Level world, ItemStack secondInput){
        if(secondInput.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).onlySecondInput.get().contains(secondInput.getItem())) return true;
            }
        }
        return false;
    }

    public static boolean isAnOutputForIndustrialBlastingRecipe(Level world, ItemStack outputStack){
        if (outputStack.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).getResult().is(outputStack.getItem())) return true;
            }
        }
        return false;
    }

    public static ToolingRecipe getToolingRecipeFromBitAndBase(Level world, ItemStack bitStack, ItemStack baseStack){
        if(baseStack.isEmpty() || bitStack.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof ToolingRecipe){
                if (((ToolingRecipe) recipe).getBits().contains(bitStack.getItem())
                        && ((ToolingRecipe) recipe).getBases().contains(baseStack.getItem())) {
                    return (ToolingRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static ToolingRecipe getToolingRecipeFromResult(Level world, ItemStack resultStack){
        if(resultStack.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof ToolingRecipe){
                if(((ToolingRecipe) recipe).getResult().is(resultStack.getItem())){
                    return (ToolingRecipe) recipe;
                }
            }
        }
        return null;
    }

    //
    public static ItemStack getPlankFromLog(Level world, ItemStack logStack){
        if (logStack.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CraftingRecipe){
                if(ForgeRegistries.ITEMS.getKey(recipe.getResultItem(world.registryAccess()).getItem()).toString().contains("plank")){
                    AtomicBoolean foundInput = new AtomicBoolean(false);
                    recipe.getIngredients().parallelStream().forEach(ingredient -> { // Parallel stream
                        for(ItemStack itemStack : ingredient.getItems()) {
                            if(itemStack.getItem().equals(logStack.getItem())){
                                foundInput.set(true);
                            }
                        }
                    });

                    if (foundInput.get()){
                        return recipe.getResultItem(world.registryAccess()).copy();
                    }
                }
            }
        }
        return null;
    }

    public static CraftingRecipe getRecipeFromLog(Level world, ItemStack logStack){
        if (logStack.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CraftingRecipe){
                if(RegistryLookups.lookupItem(recipe.getResultItem(world.registryAccess())).toString().contains("plank")){
                    AtomicBoolean foundInput = new AtomicBoolean(false);
                    recipe.getIngredients().parallelStream().forEach(ingredient -> { // Parallel stream
                        for(ItemStack itemStack : ingredient.getItems()) {
                            if(itemStack.getItem().equals(logStack.getItem())){
                                foundInput.set(true);
                            }
                        }
                    });

                    if (foundInput.get()){
                        return (CraftingRecipe) recipe;
                    }
                }
            }
        }
        return null;
    }

    // Parallel query of global recipes, will this improve performance?
    public static ItemStack getPlankFromLogParallel(Level world, ItemStack logStack){
        if (logStack.isEmpty()) return null;
        AtomicReference<ItemStack> atomicItemStack = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CraftingRecipe){
                if (RegistryLookups.lookupItem(recipe.getResultItem(world.registryAccess())).toString().contains("plank")){
                    recipe.getIngredients().forEach(ingredient -> {
                        for (ItemStack itemStack : ingredient.getItems()){
                            if (itemStack.getItem().equals(logStack.getItem())){
                                atomicItemStack.set(recipe.getResultItem(world.registryAccess()).copy());
                            }
                        }
                    });
                }
            }
        });
        return atomicItemStack.get();
    }

    // Parallel query of global recipes, will this improve performance?
    public static ArrayList<ItemStack> getLogFromPlankParallel(Level world, ItemStack plankStack){
        if (plankStack.isEmpty()) return null;
        AtomicReference<ArrayList<ItemStack>> atomicItemStackArray = new AtomicReference<>(new ArrayList<ItemStack>());

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CraftingRecipe){
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

    public static SawmillingRecipe getSawmillingRecipeFromLog(Level world, ItemStack logStack){ // Parallel by default
        if (logStack.isEmpty()) return null;
        AtomicReference<SawmillingRecipe> atomicRecipe = new AtomicReference<>(null);
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if(recipe instanceof SawmillingRecipe sawmillingRecipe){
                if (!sawmillingRecipe.isLogRecipe()){
                    for (ItemStack ingredientStack : sawmillingRecipe.ingredient.get().getItems()){
                        if (ingredientStack.getItem().equals(logStack.getItem())){
                            atomicRecipe.set(sawmillingRecipe);
                            break;
                        }
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static SawmillingRecipe getSawmillingRecipeFromPlank(Level world, ItemStack plankStack){ // Parallel by default
        if (plankStack.isEmpty()) return null;
        AtomicReference<SawmillingRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof SawmillingRecipe sawmillingRecipe){
                if (!sawmillingRecipe.isLogRecipe()){
                    if (sawmillingRecipe.result.getItem().equals(plankStack.getItem())){
                        atomicRecipe.set((SawmillingRecipe) recipe);
                    }
                }
            }
        });

        return atomicRecipe.get();
    }

    public static SawmillingRecipe getSawmillingRecipeFromSecondOutput(Level level, ItemStack itemStack){
        if (itemStack.isEmpty()) return null;
        AtomicReference<SawmillingRecipe> atomicRecipe = new AtomicReference<>(null);

        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof SawmillingRecipe sawmillingRecipe){
                if (!sawmillingRecipe.isLogRecipe()){
                    Item item = sawmillingRecipe.secondResult.getItem();
                    if (itemStack.getItem().equals(item)){
                        atomicRecipe.set(sawmillingRecipe);
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
            if (recipe instanceof StirlingGeneratorRecipe stirlingGeneratorRecipe){
                for (ItemStack itemStack : stirlingGeneratorRecipe.getIngredient().getItems()) {
                    if(RegistryLookups.lookupItem(itemStack.getItem()) == null) continue;
                    stirlingGeneratorRecipeMap.put(RegistryLookups.lookupItem(itemStack.getItem()).hashCode(),stirlingGeneratorRecipe);
                }
            }
        });
        if(RegistryLookups.lookupItem(solidFuelStack.getItem()) == null) return null;
        return stirlingGeneratorRecipeMap.get(RegistryLookups.lookupItem(solidFuelStack.getItem()).hashCode());
    }

    public static Lazy<ArrayList<Item>> getLazyItemsFromIngredient(VERecipe recipe){
        return Lazy.of(() -> {
            ArrayList<Item> items = new ArrayList<>();
            for (ItemStack stack : recipe.ingredient.get().getItems()){
                if(!items.contains(stack.getItem())){
                    items.add(stack.getItem());
                }
            }
            return items;
        });
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

    public static int getVolumetricEnergyWithoutLevel(Fluid fluid){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CombustionGeneratorFuelRecipe.lazyFluidsWithVolumetricEnergy.parallelStream().forEach(lazyPair -> {
            if (lazyPair.get().getA().contains(fluid)) atomicInteger.set(lazyPair.get().getB());
        });
        return atomicInteger.get();
    }

    public static ArrayList<FluidStack> getFluidsAsHotOrHotterThanIntAsFluidStacks(int minimumTemperatureKelvin, int stackAmount){
        AtomicReference<ArrayList<FluidStack>> fluidStacks = new AtomicReference<>(new ArrayList<>());
        ForgeRegistries.FLUIDS.getValues().parallelStream().forEach(fluid -> {
            if (fluid.getFluidType().getTemperature() > minimumTemperatureKelvin) fluidStacks.get().add(new FluidStack(fluid, stackAmount));
        });
        return fluidStacks.get();
    }

    public static ArrayList<FluidStack> getFluidsHotEnoughForIndustrialBlastingRecipe(IndustrialBlastingRecipe recipe){
        return getFluidsAsHotOrHotterThanIntAsFluidStacks(recipe.getMinimumHeat(), Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get());
    }

    private static ArrayList<CrusherRecipe> cachedCrusherRecipes = new ArrayList<>();
    public static ArrayList<CrusherRecipe> getCrusherRecipes(Level level){
        if (cachedCrusherRecipes.isEmpty()){
            for (Recipe recipe : level.getRecipeManager().getRecipes()){
                if (recipe instanceof CrusherRecipe crusherRecipe){
                    cachedCrusherRecipes.add(crusherRecipe);
                }
            }
        }

        return cachedCrusherRecipes;
    }

    private static HashMap<Integer,CrusherRecipe> CrusherIORecipeCache = new HashMap<>();
    public static Optional<CrusherRecipe> getCrusherRecipeFromAnyOutputAndTryInput(Item output, Item potentiallyKnownInput, Level level){
        int itemPairHash = new Pair<>(output, potentiallyKnownInput).hashCode();
        if (CrusherIORecipeCache.containsKey(itemPairHash)){
            return Optional.of(CrusherIORecipeCache.get(itemPairHash));
        }

        AtomicReference<ArrayList<CrusherRecipe>> atomicSublist = new AtomicReference<>(new ArrayList<>());

        getCrusherRecipes(level).forEach(recipe -> {
            if (recipe.result.is(output) || recipe.rngResult.is(output)){
                atomicSublist.get().add(recipe);
            }
        });


        // This is for more accurate recipe finding; if multiple recipes have same output, but different inputs, knowing the input we can select the correct one
        if (potentiallyKnownInput != null && potentiallyKnownInput != Items.AIR && !atomicSublist.get().isEmpty()){
            for (CrusherRecipe crusherRecipe : atomicSublist.get()){ // This feeds off of the sublist; Therefore this is ONLY Crusher Recipes with this output
                if (crusherRecipe == null) continue;
                for (ItemStack ingredientStack : crusherRecipe.getIngredient().getItems()){
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
        int count = GsonHelper.getAsInt(json.get(id).getAsJsonObject(),"count",1);

        return new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation),count);
    }

    public static Ingredient modifyIngredientAmounts(Ingredient ingredient, int amounts) {
        for(ItemStack stack : ingredient.getItems()) {
            stack.setCount(amounts);
        }
        return ingredient;
    }
}
