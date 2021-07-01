package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentrifugalAgitatorRecipe extends VEFluidRecipe {
    public static final IRecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_AGITATING;

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
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(IInventory inv){return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int width, int height){return true;}

    @Override
    @Deprecated
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

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

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugalAgitatorRecipe> {
        @Override
        public CentrifugalAgitatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            //recipe.inputAmount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "amount", 0);

            recipe.processTime = JSONUtils.getAsInt(json,"process_time",200);

            /*
            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }
             */

            int inputFluidAmount = JSONUtils.getAsInt(json.get("input_fluid").getAsJsonObject(),"amount",0);
            recipe.inputAmount = inputFluidAmount;

            // A tag is used instead of a manually defined fluid
            try{
                if(json.get("input_fluid").getAsJsonObject().has("tag") && !json.get("input_fluid").getAsJsonObject().has("fluid")){
                    ResourceLocation fluidTagLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("input_fluid").getAsJsonObject(),"tag","minecraft:empty"),':');
                    ITag<Fluid> tag = TagCollectionManager.getInstance().getFluids().getTag(fluidTagLocation);
                    if(tag != null){
                        for(Fluid fluid : tag.getValues()){
                            FluidStack tempStack = new FluidStack(fluid.getFluid(), recipe.inputAmount);
                            recipe.fluidInputList.add(tempStack);
                            recipe.rawFluidInputList.add(tempStack.getRawFluid());
                            recipe.inputArraySize = recipe.fluidInputList.size();
                        }
                    } else {
                        VoluminousEnergy.LOGGER.debug("Tag is null!");
                    }

                } else if (!json.get("input_fluid").getAsJsonObject().has("tag") && json.get("input_fluid").getAsJsonObject().has("fluid")){
                    // In here, a manually defined fluid is used instead of a tag
                    ResourceLocation fluidResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
                    recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation).getFluid()),recipe.inputAmount);
                    recipe.fluidInputList.add(recipe.inputFluid.copy());
                    recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                    recipe.inputArraySize = recipe.fluidInputList.size();
                } else {
                    throw new JsonSyntaxException("Invalid recipe input for the Centrifugal Agitator, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

            }

            ResourceLocation bucketResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("first_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int firstOutputFluidAmount = JSONUtils.getAsInt(json.get("first_result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstOutputFluidAmount);
            recipe.outputAmount = firstOutputFluidAmount;

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("second_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondOutputFluidAmount = JSONUtils.getAsInt(json.get("second_result").getAsJsonObject(),"amount",0);
            recipe.secondResult = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),secondOutputFluidAmount);
            recipe.secondAmount = secondOutputFluidAmount;

            return recipe;
        }

        @Nullable
        @Override
        public CentrifugalAgitatorRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
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
        public void toNetwork(PacketBuffer buffer, CentrifugalAgitatorRecipe recipe){
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
