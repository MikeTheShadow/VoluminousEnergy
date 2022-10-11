package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.RecipeUtil;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FluidMixerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FLUID_MIXING;

    public static final FluidMixerRecipe.Serializer SERIALIZER = new FluidMixerRecipe.Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    // Second input (Fluid Mixer specific)
    public boolean secondFluidUsesTagKey;
    public String secondTagKeyString;
    public Lazy<ArrayList<FluidStack>> secondFluidInputList;
    public Lazy<ArrayList<Fluid>> secondRawFluidInputList;
    public Lazy<Integer> secondInputArraySize;

    private FluidStack result;
    private int firstInputAmount;
    private int secondInputAmount;
    private int outputAmount;

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    public FluidMixerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient.get(); }

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    @Deprecated
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

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
        return null;
    }

    public FluidStack getOutputFluid(){
        return this.result.copy();
    }

    @Override
    public List<Integer> getAmounts() {
        return null;
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
    public int getInputAmount() {return firstInputAmount;}

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.FLUID_MIXER_BLOCK);
    }

    public int getSecondInputAmount() { return secondInputAmount; }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FluidMixerRecipe> {
        @Override
        public FluidMixerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            FluidMixerRecipe recipe = new FluidMixerRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(ingredientJson));
            recipe.ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            // First Input fluid
            JsonObject inputFluid = json.get("first_input_fluid").getAsJsonObject();
            recipe.firstInputAmount = GsonHelper.getAsInt(inputFluid,"amount",0);

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');
                RecipeUtil.setupFluidLazyArrayInputsUsingTags(recipe, fluidTagLocation, recipe.firstInputAmount);
            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                RecipeUtil.setupFluidLazyArrayInputsWithFluid(recipe, fluidResourceLocation, recipe.firstInputAmount);
            } else {
                throw new JsonSyntaxException("Bad syntax for the Fluid Mixer recipe, input_fluid must be tag or fluid");
            }

            // Second input fluid
            inputFluid = json.get("second_input_fluid").getAsJsonObject();
            recipe.secondInputAmount = GsonHelper.getAsInt(inputFluid,"amount",0);

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');
                setupSecondFluidLazyArrayInputsUsingTags(recipe, fluidTagLocation, recipe.secondInputAmount);
            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                setupSecondFluidLazyArrayInputsWithFluid(recipe, fluidResourceLocation, recipe.secondInputAmount);
            } else {
                throw new JsonSyntaxException("Bad syntax for the Fluid Mixer recipe, input_fluid must be tag or fluid");
            }


            ResourceLocation bucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int firstOutputFluidAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstOutputFluidAmount);
            recipe.outputAmount = firstOutputFluidAmount;

            return recipe;
        }

        @Nullable
        @Override
        public FluidMixerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            FluidMixerRecipe recipe = new FluidMixerRecipe((recipeId));
            recipe.ingredientCount = buffer.readByte();
            recipe.firstInputAmount = buffer.readInt();
            recipe.secondInputAmount = buffer.readInt();

            // First Input Fluid
            // Start with usesTagKey check
            recipe.fluidUsesTagKey = buffer.readBoolean();

            if (recipe.fluidUsesTagKey){
                ResourceLocation fluidTagLocation = buffer.readResourceLocation();
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, recipe.firstInputAmount);
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

            // Second Input Fluid
            // Start with usesTagKey check
            recipe.secondFluidUsesTagKey = buffer.readBoolean();

            if (recipe.secondFluidUsesTagKey){
                ResourceLocation fluidTagLocation = buffer.readResourceLocation();
                recipe.secondRawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.secondFluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, recipe.secondInputAmount);
                recipe.secondInputArraySize = Lazy.of(() -> recipe.secondFluidInputList.get().size());
            } else {
                int inputArraySize = buffer.readInt();
                recipe.secondInputArraySize = Lazy.of(() -> inputArraySize);
                ArrayList<Fluid> fluids = new ArrayList<>();
                ArrayList<FluidStack> fluidStacks = new ArrayList<>();
                for (int i = 0; i < inputArraySize; i++){
                    FluidStack serverFluid = buffer.readFluidStack();
                    fluidStacks.add(serverFluid.copy());
                    fluids.add(serverFluid.getRawFluid());
                }

                recipe.secondFluidInputList = Lazy.of(() -> fluidStacks);
                recipe.secondRawFluidInputList = Lazy.of(() -> fluids);
            }

            recipe.result = buffer.readFluidStack();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidMixerRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.firstInputAmount);
            buffer.writeInt(recipe.secondInputAmount);

            // First Input Fluid
            buffer.writeBoolean(recipe.fluidUsesTagKey);

            if (recipe.fluidUsesTagKey){
                buffer.writeResourceLocation(new ResourceLocation(recipe.tagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            // Second input fluid
            buffer.writeBoolean(recipe.secondFluidUsesTagKey);

            if (recipe.secondFluidUsesTagKey){
                buffer.writeResourceLocation(new ResourceLocation(recipe.secondTagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.secondInputArraySize.get());
                for(int i = 0; i < recipe.secondInputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.secondFluidInputList.get().get(i).copy());
                }
            }

            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }

    protected static void setupSecondFluidLazyArrayInputsUsingTags(FluidMixerRecipe recipe, ResourceLocation fluidTagLocation, int fluidAmount){
        recipe.secondFluidUsesTagKey = true;
        recipe.secondTagKeyString = fluidTagLocation.toString();
        recipe.secondRawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
        recipe.secondFluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, fluidAmount);
        recipe.secondInputArraySize = Lazy.of(() -> recipe.secondFluidInputList.get().size());
    }

    protected static void setupSecondFluidLazyArrayInputsWithFluid(FluidMixerRecipe recipe, ResourceLocation fluidResourceLocation, int fluidAmount){
        recipe.secondFluidUsesTagKey = false;
        recipe.secondFluidInputList = Lazy.of(() -> {
            ArrayList<FluidStack> temp = new ArrayList<>();
            temp.add(new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),fluidAmount));
            return temp;
        });
        recipe.secondRawFluidInputList= Lazy.of(() -> {
            ArrayList<Fluid> temp = new ArrayList<>();
            temp.add(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation));
            return temp;
        });
        recipe.secondInputArraySize = Lazy.of(() -> 1);
    }
}