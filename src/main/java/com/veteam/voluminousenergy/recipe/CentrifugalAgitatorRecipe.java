package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentrifugalAgitatorRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_AGITATING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int processTime;
    
    private FluidStack result;
    private FluidStack secondResult;
    private int inputAmount;
    private int outputAmount;
    private int secondAmount;
    
    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    public CentrifugalAgitatorRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient.get(); }

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    @Deprecated
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

    public FluidStack getSecondResult(){return secondResult;}

    public List<FluidStack> getFluids() {
        List<FluidStack> f = new ArrayList<>();
        f.add(getOutputFluid());
        f.add(getSecondFluid());
        return f;
    }

    @Override
    public List<Fluid> getRawFluids() {
        List<Fluid> f = new ArrayList<>();
        f.add(getOutputFluid().getRawFluid());
        f.add(getSecondFluid().getRawFluid());
        return f;
    }

    @Override
    public List<ItemStack> getResults() {
        return null;
    }

    public FluidStack getSecondFluid(){
        return this.secondResult.copy();
    }

    public FluidStack getOutputFluid(){
        return this.result.copy();
    }

    @Override
    public List<Integer> getAmounts() {
        return null;
    }

    @Deprecated
    public FluidStack getInputFluid(){
        return this.fluidInputList.get().get(0).copy();
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
    @Deprecated
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public int getOutputAmount() {return outputAmount;}

    @Override
    public int getInputAmount() {return inputAmount;}

    public int getSecondAmount(){return secondAmount;}

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<CentrifugalAgitatorRecipe> {
        @Override
        public CentrifugalAgitatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(ingredientJson));
            recipe.ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.inputAmount = GsonHelper.getAsInt(inputFluid,"amount",0);

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');
                RecipeUtil.setupFluidLazyArrayInputsUsingTags(recipe, fluidTagLocation, recipe.inputAmount);
            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                RecipeUtil.setupFluidLazyArrayInputsWithFluid(recipe, fluidResourceLocation, recipe.inputAmount);
            } else {
                throw new JsonSyntaxException("Bad syntax for the Centrifugal Agitator recipe, input_fluid must be tag or fluid");
            }

            ResourceLocation bucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("first_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int firstOutputFluidAmount = GsonHelper.getAsInt(json.get("first_result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstOutputFluidAmount);
            recipe.outputAmount = firstOutputFluidAmount;

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("second_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondOutputFluidAmount = GsonHelper.getAsInt(json.get("second_result").getAsJsonObject(),"amount",0);
            recipe.secondResult = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),secondOutputFluidAmount);
            recipe.secondAmount = secondOutputFluidAmount;

            return recipe;
        }

        @Nullable
        @Override
        public CentrifugalAgitatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe((recipeId));
            recipe.ingredientCount = buffer.readByte();
            recipe.inputAmount = buffer.readInt();

            // Start with usesTagKey check
            recipe.fluidUsesTagKey = buffer.readBoolean();

            if (recipe.fluidUsesTagKey){
                ResourceLocation fluidTagLocation = buffer.readResourceLocation();
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, recipe.inputAmount);
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

            recipe.result = buffer.readFluidStack();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CentrifugalAgitatorRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.inputAmount);

            buffer.writeBoolean(recipe.fluidUsesTagKey);

            if (recipe.fluidUsesTagKey){
                buffer.writeResourceLocation(new ResourceLocation(recipe.tagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }
}
