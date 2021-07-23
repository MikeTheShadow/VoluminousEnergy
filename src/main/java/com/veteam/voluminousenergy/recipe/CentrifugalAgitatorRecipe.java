package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.tags.SerializationTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentrifugalAgitatorRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_AGITATING;

    public static final Serializer SERIALIZER = new Serializer();

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public ArrayList<FluidStack> fluidInputList = new ArrayList<>();
    public ArrayList<Fluid> rawFluidInputList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;
    private int inputArraySize;

    private FluidStack inputFluid;
    private FluidStack result;
    private FluidStack secondResult;
    private int inputAmount;
    private int outputAmount;
    private int secondAmount;

    public CentrifugalAgitatorRecipe() {
        recipeId = null;
    }

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    public CentrifugalAgitatorRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient; }

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

    public FluidStack getInputFluid(){
        return this.inputFluid.copy();
    }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
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
        return new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CentrifugalAgitatorRecipe> {
        @Override
        public CentrifugalAgitatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            //recipe.inputAmount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "amount", 0);

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            /*
            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }
             */

            int inputFluidAmount = GsonHelper.getAsInt(json.get("input_fluid").getAsJsonObject(),"amount",0);
            recipe.inputAmount = inputFluidAmount;

            // A tag is used instead of a manually defined fluid
            try{
                if(json.get("input_fluid").getAsJsonObject().has("tag") && !json.get("input_fluid").getAsJsonObject().has("fluid")){
                    ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("input_fluid").getAsJsonObject(),"tag","minecraft:empty"),':');
                    Tag<Fluid> tag = SerializationTags.getInstance().getCustomTypeCollection(ForgeRegistries.FLUIDS).getTag(fluidTagLocation);
                    if(tag != null){
                        for(Fluid fluid : tag.getValues()){
                            FluidStack tempStack = new FluidStack(fluid, recipe.inputAmount);
                            recipe.fluidInputList.add(tempStack);
                            recipe.rawFluidInputList.add(tempStack.getRawFluid());
                            recipe.inputArraySize = recipe.fluidInputList.size();
                        }
                    } else {
                        VoluminousEnergy.LOGGER.debug("Tag is null!");
                    }

                } else if (!json.get("input_fluid").getAsJsonObject().has("tag") && json.get("input_fluid").getAsJsonObject().has("fluid")){
                    // In here, a manually defined fluid is used instead of a tag
                    ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
                    recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),recipe.inputAmount);
                    recipe.fluidInputList.add(recipe.inputFluid.copy());
                    recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                    recipe.inputArraySize = recipe.fluidInputList.size();
                } else {
                    throw new JsonSyntaxException("Invalid recipe input for the Centrifugal Agitator, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

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
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            recipe.ingredientCount = buffer.readByte();

            // This is probably not great, but eh, what else am I supposed to do in this situation?
            recipe.inputArraySize = buffer.readInt();
            for (int i = 0; i < recipe.inputArraySize; i++){
                FluidStack serverFluid = buffer.readFluidStack();
                recipe.fluidInputList.add(serverFluid.copy());
                recipe.rawFluidInputList.add(serverFluid.getRawFluid());
            }

            recipe.result = buffer.readFluidStack();
            recipe.inputAmount = buffer.readInt();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CentrifugalAgitatorRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());

            buffer.writeInt(recipe.inputArraySize);
            for(int i = 0; i < recipe.inputArraySize; i++){
                buffer.writeFluidStack(recipe.fluidInputList.get(i).copy());
            }

            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
        }
    }
}
