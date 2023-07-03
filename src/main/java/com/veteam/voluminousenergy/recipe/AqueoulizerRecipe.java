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

public class AqueoulizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    private Lazy<FluidStack> inputFluid;
    private FluidStack result;
    private int inputAmount;
    private int outputAmount;

    public AqueoulizerRecipe() {
        recipeId = null;
    }

    public AqueoulizerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    @Deprecated
    // use getOutputFluid instead
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

    public FluidStack getInputFluid(){
        return this.inputFluid.get();
    }

    @Override
    @Deprecated
    // Use getResult instead
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    @Override
    public List<FluidStack> getInputFluids() {
        return this.fluidInputList.get();
    }

    @Override
    public List<ItemStack> getOutputItems() {
        return null;
    }

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<AqueoulizerRecipe> {
        @Override
        public @NotNull AqueoulizerRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            AqueoulizerRecipe recipe = new AqueoulizerRecipe(recipeId);

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
                throw new JsonSyntaxException("Bad syntax for the Aqueoulizer recipe, input_fluid must be tag or fluid");
            }

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            recipe.outputAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),recipe.outputAmount);

            recipe.fluidOutputList.add(recipe.result);

            return recipe;
        }

        @Nullable
        @Override
        public AqueoulizerRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            AqueoulizerRecipe recipe = new AqueoulizerRecipe((recipeId));
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

            recipe.ingredientCount = buffer.readInt();
            recipe.result = buffer.readFluidStack();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);
            recipe.fluidOutputList.add(recipe.result);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AqueoulizerRecipe recipe){
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

            buffer.writeInt(recipe.ingredientCount);
            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }
}
