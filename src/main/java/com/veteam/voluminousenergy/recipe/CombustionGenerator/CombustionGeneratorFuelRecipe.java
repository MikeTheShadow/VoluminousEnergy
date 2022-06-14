package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class CombustionGeneratorFuelRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION;

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int volumetricEnergy;
    private Lazy<Integer> inputArraySize;

    @Deprecated
    private FluidStack inputFluid;
    private ItemStack result;

    public static ArrayList<Lazy<Pair<ArrayList<Fluid>,Integer>>> lazyFluidsWithVolumetricEnergy = new ArrayList<>();

    public CombustionGeneratorFuelRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public CombustionGeneratorFuelRecipe() {
        recipeId = null;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public Ingredient getIngredient(){ return ingredient.get();}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    @Deprecated
    public FluidStack getInputFluid(){
        return this.inputFluid.copy();
    }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(Container inv){return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int width, int height){return true;}

    @Override
    public ItemStack getResultItem(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return ingredientList.get();
    }

    @Override
    public List<FluidStack> getFluids() {
        List<FluidStack> f = new ArrayList<>();
        f.add(getOutputFluid());
        return f;
    }

    @Override
    public List<Fluid> getRawFluids() {
        List<Fluid> f = new ArrayList<>();
        f.add(getOutputFluid().getRawFluid());
        return f;
    }

    @Override
    public List<ItemStack> getResults() {
        List<ItemStack> s = new ArrayList<>();
        s.add(getResult());
        return s;
    }

    @Override
    public int getInputAmount() {
        return 0;
    }

    @Override
    public int getOutputAmount() {
        return 0;
    }

    @Override
    public FluidStack getOutputFluid() {
        return new FluidStack(((BucketItem) result.getItem()).getFluid(),250);
    }

    @Override
    public List<Integer> getAmounts() {
        return null;
    }

    @Override
    public int getProcessTime() { // Just get the volumetric energy for now
        return getVolumetricEnergy();
    }

    public int getVolumetricEnergy() {return volumetricEnergy;}


    public static class Serializer implements RecipeSerializer<CombustionGeneratorFuelRecipe> {
        @Override
        public CombustionGeneratorFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe(recipeId);

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.volumetricEnergy = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "volumetric_energy", 102400);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');

                recipe.inputFluid = null;
                RecipeUtil.setupFluidLazyArrayInputsUsingTags(recipe, fluidTagLocation, 1000);

            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),1000);
                RecipeUtil.setupFluidLazyArrayInputsWithFluid(recipe, fluidResourceLocation, 1000);

            } else {
                throw new JsonSyntaxException("Bad syntax for the Combustion Fuel recipe, input_fluid must be tag or fluid");
            }

            lazyFluidsWithVolumetricEnergy.add(Lazy.of(() -> new Pair<>(recipe.rawFluidInputList.get(), recipe.volumetricEnergy)));

            recipe.result = new ItemStack(Items.BUCKET); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorFuelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe((recipeId));

            // Start with usesTagKey check
            recipe.fluidUsesTagKey = buffer.readBoolean();

            if (recipe.fluidUsesTagKey){
                ResourceLocation fluidTagLocation = buffer.readResourceLocation();
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, 1000);
                recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());
            } else {
                int inputArraySize = buffer.readInt();
                recipe.inputArraySize = Lazy.of(() -> inputArraySize);
                ArrayList<Fluid> fluids = new ArrayList<>();
                ArrayList<FluidStack> fluidStacks = new ArrayList<>();
                for (int i = 0; i < inputArraySize; i++){
                    FluidStack serverFluid = buffer.readFluidStack();
                    fluidStacks.add(serverFluid.copy());
                    fluids.add(serverFluid.getRawFluid());
                }

                recipe.fluidInputList = Lazy.of(() -> fluidStacks);
                recipe.rawFluidInputList = Lazy.of(() -> fluids);
            }

            recipe.ingredientCount = buffer.readInt();
            recipe.volumetricEnergy = buffer.readInt();
            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            lazyFluidsWithVolumetricEnergy.add(Lazy.of(() -> new Pair<>(recipe.rawFluidInputList.get(), recipe.volumetricEnergy)));

            recipe.result = new ItemStack(Items.BUCKET);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CombustionGeneratorFuelRecipe recipe){
            buffer.writeBoolean(recipe.fluidUsesTagKey);

            if (recipe.fluidUsesTagKey){
                buffer.writeResourceLocation(new ResourceLocation(recipe.tagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            buffer.writeInt(recipe.ingredientCount);
            buffer.writeInt(recipe.volumetricEnergy);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }
}
