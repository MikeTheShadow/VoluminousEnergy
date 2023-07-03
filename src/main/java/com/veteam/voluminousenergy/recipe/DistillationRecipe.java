package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DistillationRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    private FluidStack result;
    private FluidStack secondResult;
    private ItemStack thirdResult;
    private int inputAmount;
    private int outputAmount;
    private int secondAmount;
    private int thirdAmount;
    private float thirdChance;

    public DistillationRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public DistillationRecipe() {
        recipeId = null;
    }

    @Override
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

    public FluidStack getSecondResult(){return this.secondResult;}

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK.get());
    }

    public FluidStack getSecondFluid(){
        return this.secondResult.copy();
    }

    public FluidStack getOutputFluid(){
        return this.result.copy();
    }

    @Deprecated
    public FluidStack getInputFluid(){
        return this.fluidInputList.get().get(0);
    }


    public ItemStack getThirdResult(){
        return thirdResult;
    }

    public int getThirdAmount(){
        return thirdAmount;
    }

    @Override
    @Deprecated
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return ingredientList.get();
    }

    @Override
    public List<ItemStack> getOutputItems() {
        return null;
    }

    @Override
    public int getProcessTime() { return processTime; }

    public float getThirdChance(){return thirdChance;}


    public static class Serializer implements RecipeSerializer<DistillationRecipe> {
        @Override
        public @NotNull DistillationRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            DistillationRecipe recipe = new DistillationRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(ingredientJson));
            recipe.ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.inputAmount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "amount", 0);
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
                throw new JsonSyntaxException("Bad syntax for the Distillation recipe, input_fluid must be tag or fluid");
            }

            ResourceLocation bucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("first_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int firstAmount = GsonHelper.getAsInt(json.get("first_result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstAmount);
            recipe.outputAmount = firstAmount;

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("second_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondFluidAmount = GsonHelper.getAsInt(json.get("second_result").getAsJsonObject(),"amount",0);
            recipe.secondResult = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),secondFluidAmount);
            recipe.secondAmount = secondFluidAmount;

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("third_result").getAsJsonObject(),"item","minecraft:empty"),':');
            int thirdItemAmount = GsonHelper.getAsInt(json.get("third_result").getAsJsonObject(),"count",1);
            float thirdItemChance = GsonHelper.getAsFloat(json.get("third_result").getAsJsonObject(),"chance",0);
            recipe.thirdResult = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.thirdAmount = thirdItemAmount;
            recipe.thirdChance = thirdItemChance;

            recipe.fluidOutputList.add(recipe.result);
            recipe.fluidOutputList.add(recipe.secondResult);

            return recipe;
        }

        @Nullable
        @Override
        public DistillationRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            DistillationRecipe recipe = new DistillationRecipe((recipeId));
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readFluidStack();
            recipe.inputAmount = buffer.readInt();

            // This is probably not great, but eh, what else am I supposed to do in this situation?
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

            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();
            recipe.thirdResult = buffer.readItem();
            recipe.thirdAmount = buffer.readInt();
            recipe.thirdChance = buffer.readFloat();

            recipe.fluidOutputList.add(recipe.result);
            recipe.fluidOutputList.add(recipe.secondResult);

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DistillationRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.inputAmount);

            // Same as the comment in read, not optimal, but necessary
            buffer.writeBoolean(recipe.fluidUsesTagKey);

            if (recipe.fluidUsesTagKey){
                buffer.writeResourceLocation(new ResourceLocation(recipe.tagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
            buffer.writeItem(recipe.thirdResult);
            buffer.writeInt(recipe.thirdAmount);
            buffer.writeFloat(recipe.thirdChance);

            recipe.ingredient.get().toNetwork(buffer);

        }
    }
}
