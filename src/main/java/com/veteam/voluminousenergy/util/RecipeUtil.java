package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeUtil {
    public static boolean isAqueoulizerInputFluidEqual(Level world, Fluid fluid){
        for (Recipe<?> iRecipe : world.getRecipeManager().getRecipes()) {
            if(iRecipe instanceof AqueoulizerRecipe){
                for (FluidStack stack : ((AqueoulizerRecipe) iRecipe).fluidInputList.get()){
                    if(stack.getFluid().isSame(fluid)) return true;
                }
            }
        }
        return false;
    }

    public static boolean isAqueoulizerInputFluidEqual(AqueoulizerRecipe recipe, Fluid fluid){
        for (FluidStack stack : recipe.fluidInputList.get()){ if(stack.getFluid().isSame(fluid)) return true; }
        return false;
    }


    private static final HashMap<Integer,AqueoulizerRecipe> aqueoulizerRecipeHashMap = new HashMap<>();
    public static AqueoulizerRecipe getAqueoulizerRecipe(Level world, FluidStack inputFluid, ItemStack inputItem) {

        if(aqueoulizerRecipeHashMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof AqueoulizerRecipe aqueoulizerRecipe) {
                    for (FluidStack recipeFluid : aqueoulizerRecipe.fluidInputList.get()){
                        for(Item ingredient : aqueoulizerRecipe.ingredientList.get()){
                            String hash =  RegistryLookups.lookupItem(ingredient) + recipeFluid.getTranslationKey();
                            aqueoulizerRecipeHashMap.put(hash.hashCode(),aqueoulizerRecipe);
                        }
                    }
                }
            }
        }

        String hash = RegistryLookups.lookupItem(inputItem.getItem()) + inputFluid.getTranslationKey();
        return aqueoulizerRecipeHashMap.get(hash.hashCode());
    }

    public static ArrayList<AqueoulizerRecipe> getAqueoulizerRecipesFromItemInput(Level level, ItemStack inputStack){
        return getAqueoulizerRecipesFromItemInput(level, inputStack.getItem());
    }

    public static ArrayList<AqueoulizerRecipe> getAqueoulizerRecipesFromFluidInput(Level level, FluidStack fluidStack){
        return getAqueoulizerRecipesFromFluidInput(level, fluidStack.getRawFluid());
    }

    public static boolean isAqueoulizerOutput(Level level, FluidStack fluidStack){
        return isAqueoulizerOutput(level, fluidStack.getRawFluid());
    }

    private static final HashMap<Integer,ArrayList<AqueoulizerRecipe>> aqueoulizerItemMap = new HashMap<>();
    public static ArrayList<AqueoulizerRecipe> getAqueoulizerRecipesFromItemInput(Level level, Item inputItem){
        for(Recipe<?> recipe : level.getRecipeManager().getRecipes()){
            if (recipe instanceof AqueoulizerRecipe aqueoulizerRecipe) {
                for(Item item : aqueoulizerRecipe.ingredientList.get()) {
                    if(RegistryLookups.lookupItem(item) == null) continue;
                    int code = RegistryLookups.lookupItem(item).hashCode();
                    if(!aqueoulizerItemMap.containsKey(code)) {
                        aqueoulizerItemMap.put(code,new ArrayList<>());
                    }
                    aqueoulizerItemMap.get(code).add(aqueoulizerRecipe);
                }
            }
        }
        if(RegistryLookups.lookupItem(inputItem) == null) return new ArrayList<>();
        return aqueoulizerItemMap.getOrDefault(RegistryLookups.lookupItem(inputItem).hashCode(),new ArrayList<>());
    }

    public static ArrayList<AqueoulizerRecipe> getAqueoulizerRecipesFromFluidInput(Level level, Fluid fluid){
        ArrayList<AqueoulizerRecipe> validRecipes = new ArrayList<>();
        AtomicReference<ArrayList<AqueoulizerRecipe>> atomicValidRecipes = new AtomicReference<>(validRecipes);
        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof AqueoulizerRecipe aqueoulizerRecipe) {
                if (aqueoulizerRecipe.fluidInputList.get().contains(fluid)){
                    atomicValidRecipes.get().add(aqueoulizerRecipe);
                }
            }
        });
        return atomicValidRecipes.get();
    }

    public static boolean isAqueoulizerOutput(Level level, Fluid fluid){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof AqueoulizerRecipe aqueoulizerRecipe) {
                if (aqueoulizerRecipe.getOutputFluid().getRawFluid().isSame(fluid)){
                    atomicBoolean.set(true);
                }
            }
        });
        return atomicBoolean.get();
    }

    private static final HashMap<Integer,CentrifugalAgitatorRecipe> CentrifugalAgitatorMap = new HashMap<>();
    public static CentrifugalAgitatorRecipe getCentrifugalAgitatorRecipe(Level world, FluidStack inputFluid) {

        if (CentrifugalAgitatorMap.isEmpty()) {
            for (Recipe<?> recipe : world.getRecipeManager().getRecipes()) {
                if (recipe instanceof CentrifugalAgitatorRecipe centrifugalAgitatorRecipe) {
                    for (FluidStack recipeFluid : centrifugalAgitatorRecipe.fluidInputList.get()) {
                        String hash = recipeFluid.getTranslationKey();
                        CentrifugalAgitatorMap.put(hash.hashCode(), centrifugalAgitatorRecipe);
                    }
                }
            }
        }

        String hash = inputFluid.getTranslationKey();
        return CentrifugalAgitatorMap.get(hash.hashCode());
    }

    public static List<Fluid> getCentrifugalAgitatorInputFluids(Level world){
        List<Fluid> fluidList = new ArrayList<>();
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CentrifugalAgitatorRecipe centrifugalAgitatorRecipe){
                fluidList.addAll(centrifugalAgitatorRecipe.rawFluidInputList.get());
            }
        }
        return fluidList;
    }

    private static final HashMap<Integer,DistillationRecipe> distillationRecipeMap = new HashMap<>();
    public static DistillationRecipe getDistillationRecipe(Level world, FluidStack inputFluid){

        if(distillationRecipeMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof DistillationRecipe distillationRecipe) {
                    for (FluidStack recipeFluid : distillationRecipe.fluidInputList.get()){
                        String hash = recipeFluid.getTranslationKey();
                        distillationRecipeMap.put(hash.hashCode(),distillationRecipe);
                    }
                }
            }
        }

        String hash = inputFluid.getTranslationKey();
        return distillationRecipeMap.get(hash.hashCode());
    }

    public static List<Fluid> getDistillationInputFluids(Level world){
        List<Fluid> fluidList = new ArrayList<>();
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe distillationRecipe){
                fluidList.addAll(distillationRecipe.rawFluidInputList.get());
            }
        }
        return fluidList;
    }

    public static DistillationRecipe getDistillationRecipeFromResult(Level world, FluidStack resultFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getOutputFluid().isFluidEqual(resultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromSecondResult(Level world, FluidStack secondResultFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getSecondResult().isFluidEqual(secondResultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromThirdResult(Level world, ItemStack thirdResultItem){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if(((DistillationRecipe) recipe).getThirdResult().sameItem(thirdResultItem)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    private static final HashMap<Integer,CombustionGeneratorFuelRecipe> combustionGeneratorFuelRecipeMap = new HashMap<>();
    public static CombustionGeneratorFuelRecipe getFuelCombustionRecipe(Level world, FluidStack inputFluid){

        if(combustionGeneratorFuelRecipeMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof CombustionGeneratorFuelRecipe combustionGeneratorFuelRecipe) {
                    for (FluidStack fluid : combustionGeneratorFuelRecipe.fluidInputList.get()){
                        String hash = fluid.getTranslationKey();
                        combustionGeneratorFuelRecipeMap.put(hash.hashCode(),combustionGeneratorFuelRecipe);
                    }
                }
            }
        }

        String hash = inputFluid.getTranslationKey();
        return combustionGeneratorFuelRecipeMap.get(hash.hashCode());
    }

    public static List<Fluid> getFuelCombustionInputFluids(Level world){
        if (world == null) return new ArrayList<Fluid>();
        List<Fluid> fluidList = new ArrayList<>();
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CombustionGeneratorFuelRecipe combustionGeneratorFuelRecipe){
                fluidList.addAll(combustionGeneratorFuelRecipe.rawFluidInputList.get());
            }
        }
        return fluidList;
    }

    public static List<Fluid> getFuelCombustionInputFluidsParallel(Level world){
        if (world == null) return new ArrayList<Fluid>();
        AtomicReference<List<Fluid>> fluidList = new AtomicReference<>(new ArrayList<>());
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CombustionGeneratorFuelRecipe combustionGeneratorFuelRecipe){
                fluidList.get().addAll(combustionGeneratorFuelRecipe.rawFluidInputList.get());
            }
        });
        return fluidList.get();
    }

    public static boolean isCombustibleFuel(Fluid fluid, Level world){
        return getFuelCombustionInputFluidsParallel(world).contains(fluid);
    }

    public static boolean isCombustibleFuel(FluidStack fluidStack, Level world){
        return isCombustibleFuel(fluidStack.getFluid(), world);
    }


    public static boolean isCombustibleFuelWithoutLevel(FluidStack fluidStack){
        return isCombustibleFuelWithoutLevel(fluidStack.getRawFluid());
    }

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

    public static int getVolumetricEnergyFromFluid(Fluid fluid, Level level){
        if (level == null) return 0;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CombustionGeneratorFuelRecipe combustionGeneratorFuelRecipe){
                if (combustionGeneratorFuelRecipe.rawFluidInputList.get().contains(fluid)) {
                    if (atomicInteger.get() == 0) {
                        atomicInteger.set(combustionGeneratorFuelRecipe.getVolumetricEnergy());
                    }
                }
            }
        });
        return atomicInteger.get();
    }

    public static boolean isOxidizer(FluidStack fluidStack, Level level){
        return isOxidizer(fluidStack.getRawFluid(), level);
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
    private static final HashMap<Integer,IndustrialBlastingRecipe> industrialBlastingRecipeMap = new HashMap<>();
    public static IndustrialBlastingRecipe getIndustrialBlastingRecipe(Level world, ItemStack firstInput, ItemStack secondInput){
        if(industrialBlastingRecipeMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof IndustrialBlastingRecipe industrialBlastingRecipe) {
                    for(Item firstIn : industrialBlastingRecipe.getFirstInputAsList()) {
                        for(Item secondIn : industrialBlastingRecipe.onlySecondInput.get()) {
                            String hash = RegistryLookups.lookupItem(firstIn) + "" + RegistryLookups.lookupItem(secondIn);
                            industrialBlastingRecipeMap.put(hash.hashCode(),industrialBlastingRecipe);
                        }
                    }
                }
            }
        }

        String hash = RegistryLookups.lookupItem(firstInput.getItem()) + "" + RegistryLookups.lookupItem(secondInput.getItem());
        return industrialBlastingRecipeMap.get(hash.hashCode());
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
                if (((IndustrialBlastingRecipe) recipe).getResult().sameItem(outputStack)) return true;
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
                if(ForgeRegistries.ITEMS.getKey(recipe.getResultItem().getItem()).toString().contains("plank")){
                    AtomicBoolean foundInput = new AtomicBoolean(false);
                    recipe.getIngredients().parallelStream().forEach(ingredient -> { // Parallel stream
                        for(ItemStack itemStack : ingredient.getItems()) {
                            if(itemStack.getItem().equals(logStack.getItem())){
                                foundInput.set(true);
                            }
                        }
                    });

                    if (foundInput.get()){
                        return recipe.getResultItem().copy();
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
                if(RegistryLookups.lookupItem(recipe.getResultItem()).toString().contains("plank")){
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
                if (RegistryLookups.lookupItem(recipe.getResultItem()).toString().contains("plank")){
                    recipe.getIngredients().forEach(ingredient -> {
                        for (ItemStack itemStack : ingredient.getItems()){
                            if (itemStack.getItem().equals(logStack.getItem())){
                                atomicItemStack.set(recipe.getResultItem().copy());
                            }
                        }
                    });
                }
            }
        });
        return atomicItemStack.get();
    }

    // Parallel query of global recipes, will this improve performance?
    public static CraftingRecipe getRecipeFromLogParallel(Level world, ItemStack logStack){
        if (logStack.isEmpty()) return null;
        AtomicReference<CraftingRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CraftingRecipe){
                if (RegistryLookups.lookupItem(recipe.getResultItem()).toString().contains("plank")){
                    recipe.getIngredients().forEach(ingredient -> {
                        for (ItemStack itemStack : ingredient.getItems()){
                            if (itemStack.getItem().equals(logStack.getItem())){
                                atomicRecipe.set((CraftingRecipe) recipe);
                            }
                        }
                    });
                }
            }
        });
        return atomicRecipe.get(); // OR IF DOESN'T WORK: return atomicRecipe.get() == null ? null : atomicRecipe.get();
    }

    // Parallel query of global recipes, will this improve performance?
    public static ArrayList<ItemStack> getLogFromPlankParallel(Level world, ItemStack plankStack){
        if (plankStack.isEmpty()) return null;
        AtomicReference<ArrayList<ItemStack>> atomicItemStackArray = new AtomicReference<>(new ArrayList<ItemStack>());

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CraftingRecipe){
                if (RegistryLookups.lookupItem(recipe.getResultItem()).toString().contains("plank")){
                    if (recipe.getResultItem().is(plankStack.getItem())){
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

    public static Lazy<ArrayList<Item>> getLazyItemsFromIngredient(VEFluidRecipe recipe){
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

    public static void setupFluidLazyArrayInputsUsingTags(VEFluidRecipe recipe, ResourceLocation fluidTagLocation, int fluidAmount){
        recipe.fluidUsesTagKey = true;
        recipe.tagKeyString = fluidTagLocation.toString();
        recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
        recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, fluidAmount);
        recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());
    }

    public static void setupFluidLazyArrayInputsWithFluid(VEFluidRecipe recipe, ResourceLocation fluidResourceLocation, int fluidAmount){
        recipe.fluidUsesTagKey = false;
        recipe.fluidInputList = Lazy.of(() -> {
            ArrayList<FluidStack> temp = new ArrayList<>();
            temp.add(new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),fluidAmount));
            return temp;
        });
        recipe.rawFluidInputList= Lazy.of(() -> {
            ArrayList<Fluid> temp = new ArrayList<>();
            temp.add(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation));
            return temp;
        });
        recipe.inputArraySize = Lazy.of(() -> 1);
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

    public static Lazy<ArrayList<Fluid>> createLazyAnthologyFluids(Lazy<ArrayList<Fluid>>... toAnthologize){
        return Lazy.of(() -> {
            ArrayList<Fluid> anthology = new ArrayList<>();
            for (Lazy<ArrayList<Fluid>> fluids : toAnthologize){
                anthology.addAll(fluids.get());
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

        return Optional.ofNullable(atomicSublist.get().get(0));
    }


    private static final HashMap<Integer,FluidElectrolyzerRecipe> FluidElectrolyzerMap = new HashMap<>();
    public static FluidElectrolyzerRecipe getFluidElectrolyzerRecipe(Level world, FluidStack inputFluid) {

        if (FluidElectrolyzerMap.isEmpty()) {
            for (Recipe<?> recipe : world.getRecipeManager().getRecipes()) {
                if (recipe instanceof FluidElectrolyzerRecipe fluidElectrolyzerRecipe) {
                    for (FluidStack recipeFluid : fluidElectrolyzerRecipe.fluidInputList.get()) {
                        String hash = recipeFluid.getTranslationKey();
                        FluidElectrolyzerMap.put(hash.hashCode(), fluidElectrolyzerRecipe);
                    }
                }
            }
        }

        String hash = inputFluid.getTranslationKey();
        return FluidElectrolyzerMap.get(hash.hashCode());
    }

    private static final HashMap<Integer,FluidMixerRecipe> FluidMixerMap = new HashMap<>();
    public static FluidMixerRecipe getFluidMixerRecipe(Level world, FluidStack firstInput, FluidStack secondInput){
        if (FluidMixerMap.isEmpty()) {
            for (Recipe<?> recipe : world.getRecipeManager().getRecipes()) {
                if (recipe instanceof FluidMixerRecipe fluidMixerRecipe) {

                    for (FluidStack firstRecipeInput : fluidMixerRecipe.fluidInputList.get()) {
                        for (FluidStack secondRecipeInput : fluidMixerRecipe.secondFluidInputList.get()){
                            String firstHash = firstRecipeInput.getTranslationKey();
                            String secondHash = secondRecipeInput.getTranslationKey();
                            FluidMixerMap.put((firstHash + secondHash).hashCode(), fluidMixerRecipe);
                        }
                    }

                }
            }
        }

        String firstHash = firstInput.getTranslationKey();
        String secondHash = secondInput.getTranslationKey();

        return FluidMixerMap.get((firstHash + secondHash).hashCode());
    }

    private static final HashMap<Integer,HydroponicIncubatorRecipe> hydroponicIncubatorRecipeHashMap = new HashMap<>();
    public static HydroponicIncubatorRecipe getHydroponicIncubatorRecipe(Level world, FluidStack inputFluid, ItemStack inputItem) {

        if(hydroponicIncubatorRecipeHashMap.isEmpty()) {
            for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
                if (recipe instanceof HydroponicIncubatorRecipe hydroponicIncubatorRecipe) {
                    for (FluidStack recipeFluid : hydroponicIncubatorRecipe.fluidInputList.get()){
                        for(Item ingredient : hydroponicIncubatorRecipe.ingredientList.get()){
                            String hash = RegistryLookups.lookupItem(ingredient).toString() + recipeFluid.getTranslationKey();
                            hydroponicIncubatorRecipeHashMap.put(hash.hashCode(),hydroponicIncubatorRecipe);
                        }
                    }
                }
            }
        }

        String hash = RegistryLookups.lookupItem(inputItem.getItem()) + inputFluid.getTranslationKey();
        return hydroponicIncubatorRecipeHashMap.get(hash.hashCode());
    }

    private static final HashMap<Integer,ArrayList<HydroponicIncubatorRecipe>> hydroponicIncubatorItemMap = new HashMap<>();
    public static ArrayList<HydroponicIncubatorRecipe> getHydroponicIncubatorRecipesFromItemInput(Level level, Item inputItem){
        for(Recipe<?> recipe : level.getRecipeManager().getRecipes()){
            if (recipe instanceof HydroponicIncubatorRecipe hydroponicIncubatorRecipe) {
                for(Item item : hydroponicIncubatorRecipe.ingredientList.get()) {
                    if(RegistryLookups.lookupItem(item) == null) continue;
                    int code = item.hashCode();
                    if(!hydroponicIncubatorItemMap.containsKey(code)) {
                        hydroponicIncubatorItemMap.put(item.hashCode(),new ArrayList<>());
                    }
                    hydroponicIncubatorItemMap.get(code).add(hydroponicIncubatorRecipe);
                }
            }
        }
        if(RegistryLookups.lookupItem(inputItem) == null) return new ArrayList<>();
        return hydroponicIncubatorItemMap.getOrDefault(inputItem.hashCode(),new ArrayList<>());
    }

    private static final HashMap<Integer,ArrayList<HydroponicIncubatorRecipe>> hydroponicIncubatorOutputCache = new HashMap<>();
    public static final ArrayList<HydroponicIncubatorRecipe> getHydroponicIncubatorRecipesFromAnyItemOutput(Level level, Item outputItem) {
        if (hydroponicIncubatorOutputCache.isEmpty()) {
            for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
                if (recipe instanceof HydroponicIncubatorRecipe hydroponicIncubatorRecipe) {
                    for (Item resultItem : hydroponicIncubatorRecipe.getResultItems()) {
                        if (hydroponicIncubatorOutputCache.containsKey(resultItem.hashCode())) {
                            ArrayList<HydroponicIncubatorRecipe> arrayList = hydroponicIncubatorOutputCache.get(resultItem.hashCode());
                            if (arrayList.contains(hydroponicIncubatorRecipe)) continue;
                            arrayList.add(hydroponicIncubatorRecipe);
                            hydroponicIncubatorOutputCache.replace(resultItem.hashCode(), arrayList);
                        } else {
                            ArrayList<HydroponicIncubatorRecipe> arrayList = new ArrayList<>();
                            arrayList.add(hydroponicIncubatorRecipe);
                            hydroponicIncubatorOutputCache.put(resultItem.hashCode(), arrayList);
                        }

                    }
                }
            }
        }

        return hydroponicIncubatorOutputCache.getOrDefault(outputItem.hashCode(), new ArrayList<>());
    }
}
