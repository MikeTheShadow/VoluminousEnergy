package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static AqueoulizerRecipe getAqueoulizerRecipe(Level world, FluidStack inputFluid, ItemStack inputItem){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof AqueoulizerRecipe){
                for (FluidStack recipeFluid : ((AqueoulizerRecipe) recipe).fluidInputList.get()){
                    if(recipeFluid.isFluidEqual(inputFluid)) {
                        for(Item ingredient : ((AqueoulizerRecipe) recipe).ingredientList.get()){
                            if(ingredient.equals(inputItem.getItem())) return (AqueoulizerRecipe) recipe;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static CentrifugalAgitatorRecipe getCentrifugalAgitatorRecipe(Level world, FluidStack inputFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CentrifugalAgitatorRecipe){
                for (FluidStack recipeFluid : ((CentrifugalAgitatorRecipe) recipe).fluidInputList.get()){
                    if(recipeFluid.isFluidEqual(inputFluid)){
                        return (CentrifugalAgitatorRecipe) recipe;
                    }
                }
            }
        }
        return null;
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

    public static DistillationRecipe getDistillationRecipe(Level world, FluidStack inputFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                for (FluidStack recipeFluid : ((DistillationRecipe) recipe).fluidInputList.get()){
                    if(recipeFluid.isFluidEqual(inputFluid)){
                        return (DistillationRecipe) recipe;
                    }
                }
            }
        }
        return null;
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

    public static CombustionGeneratorFuelRecipe getFuelCombustionRecipe(Level world, FluidStack inputFluid){
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CombustionGeneratorFuelRecipe){
                if (((CombustionGeneratorFuelRecipe) recipe).rawFluidInputList.get().contains(inputFluid.getRawFluid())){
                    return (CombustionGeneratorFuelRecipe) recipe;
                }
            }
        }
        return null;
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

    public static CombustionGeneratorOxidizerRecipe getOxidizerCombustionRecipe(Level world, FluidStack inputFluid){
        AtomicReference<CombustionGeneratorOxidizerRecipe> recipeToReturn = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if(recipe instanceof CombustionGeneratorOxidizerRecipe combustionGeneratorOxidizerRecipe){
                if(combustionGeneratorOxidizerRecipe.rawFluidInputList.get().contains(inputFluid.getRawFluid())){
                    recipeToReturn.set(combustionGeneratorOxidizerRecipe);
                }
            }
        });

        return recipeToReturn.get();
    }

    public static List<Fluid> getOxidizerFluids(Level world){
        if (world == null) return new ArrayList<Fluid>();
        AtomicReference<List<Fluid>> fluidList = new AtomicReference<>(new ArrayList<>());
        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof CombustionGeneratorOxidizerRecipe oxidizerRecipe){
                fluidList.get().addAll(oxidizerRecipe.rawFluidInputList.get());
            }
        });
        return fluidList.get();
    }

    public static IndustrialBlastingRecipe getIndustrialBlastingRecipe(Level world, ItemStack firstInput, ItemStack secondInput){
        if(firstInput.isEmpty() || secondInput.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if( ((IndustrialBlastingRecipe) recipe).getFirstInputAsList().contains(firstInput.getItem()) &&
                        ((IndustrialBlastingRecipe) recipe).onlySecondInput.get().contains(secondInput.getItem())){
                    return (IndustrialBlastingRecipe) recipe;
                }
            }
        }
        return null;
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
                if(recipe.getResultItem().getItem().getRegistryName().toString().contains("plank")){
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
                if(recipe.getResultItem().getItem().getRegistryName().toString().contains("plank")){
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
                if (recipe.getResultItem().getItem().getRegistryName().toString().contains("plank")){
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
                if (recipe.getResultItem().getItem().getRegistryName().toString().contains("plank")){
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
                if (recipe.getResultItem().getItem().getRegistryName().toString().contains("plank")){
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
            if(recipe instanceof SawmillingRecipe){
                recipe.getIngredients().forEach(ingredient -> {
                    for (ItemStack ingredientStack : ingredient.getItems()){
                        if (ingredientStack.is(logStack.getItem())){
                            atomicRecipe.set((SawmillingRecipe) recipe);
                            break;
                        }
                    }
                });
            }
        });

        return atomicRecipe.get();
    }

    public static SawmillingRecipe getSawmillingRecipeFromPlank(Level world, ItemStack plankStack){ // Parallel by default
        if (plankStack.isEmpty()) return null;
        AtomicReference<SawmillingRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof SawmillingRecipe){
                if (((SawmillingRecipe) recipe).result.is(plankStack.getItem())){
                    atomicRecipe.set((SawmillingRecipe) recipe);
                }
            }
        });

        return atomicRecipe.get();
    }

    public static StirlingGeneratorRecipe getStirlingGeneratorRecipe(Level world, ItemStack solidFuelStack){ // Parallel by default
        if (solidFuelStack.isEmpty()) return null;
        AtomicReference<StirlingGeneratorRecipe> atomicRecipe = new AtomicReference<>(null);

        world.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof StirlingGeneratorRecipe){
                for (ItemStack itemStack : ((StirlingGeneratorRecipe) recipe).getIngredient().getItems()) {
                    if (itemStack.getItem() == solidFuelStack.getItem()){
                        atomicRecipe.set((StirlingGeneratorRecipe) recipe);
                        break;
                    }
                }
            }
        });

        return atomicRecipe.get();
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
}
