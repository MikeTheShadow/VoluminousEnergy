package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BucketItem;
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

public class DistillationRecipe extends VEFluidRecipe {
    public static final IRecipeType<VEFluidRecipe> RECIPE_TYPE = new IRecipeType<VEFluidRecipe>() {
        @Override
        public String toString() {
            return RecipeConstants.DISTILLING.toString();
        }
    };

    public static final Serializer SERIALIZER = new Serializer();

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public ArrayList<FluidStack> fluidInputList = new ArrayList<>();
    public ArrayList<Fluid> rawFluidInputList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;

    private FluidStack inputFluid;
    private FluidStack result;
    private FluidStack secondResult;
    private ItemStack thirdResult;
    private int inputAmount;
    private int outputAmount;
    private int secondAmount;
    private int thirdAmount;

    public DistillationRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public DistillationRecipe() {
        recipeId = null;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient;}

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getFilledBucket());}

    public FluidStack getSecondResult(){return this.secondResult;}

    @Override
    public int getInputAmount(){ return inputAmount; }

    @Override
    public ItemStack getIcon(){
        return new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK);
    }

    public FluidStack getSecondFluid(){
        return this.secondResult.copy();
    }

    public FluidStack getOutputFluid(){
        return this.result.copy();
    }

    public FluidStack getInputFluid(){
        return this.inputFluid.copy();
    }


    public ItemStack getThirdResult(){
        return thirdResult;
    }

    public int getThirdAmount(){
        return thirdAmount;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getStackInSlot(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv){return ItemStack.EMPTY;}

    @Override
    public boolean canFit(int width, int height){return true;}

    @Override
    @Deprecated
    public ItemStack getRecipeOutput(){return this.getResult();}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return ingredientList;
    }

    @Override
    public List<FluidStack> getFluids() {
        List<FluidStack> f = new ArrayList<>();
        f.add(null);
        f.add(getOutputFluid());
        f.add(getSecondFluid());
        return f;
    }

    @Override
    public List<Fluid> getRawFluids() {
        List<Fluid> f = new ArrayList<>();
        f.add(null);
        f.add(getOutputFluid().getRawFluid());
        f.add(getSecondFluid().getRawFluid());
        return f;
    }

    @Override
    public List<ItemStack> getResults() {
        return null;
    }

    @Override
    public List<Integer> getAmounts() {
        List<Integer> l = new ArrayList<>();
        l.add(getInputAmount());
        l.add(getOutputAmount());
        l.add(secondAmount);
        l.add(getThirdAmount());
        return l;
    }

    @Override
    public int getOutputAmount() {return outputAmount;}

    @Override
    public int getProcessTime() { return processTime; }


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DistillationRecipe> {
        @Override
        public DistillationRecipe read(ResourceLocation recipeId, JsonObject json) {
            DistillationRecipe recipe = new DistillationRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.inputAmount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "amount", 0);
            recipe.processTime = JSONUtils.getInt(json,"process_time",200);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }

            int inputFluidAmount = JSONUtils.getInt(json.get("input_fluid").getAsJsonObject(),"amount",0);
            recipe.inputAmount = inputFluidAmount;

            // A tag is used instead of a manually defined fluid
            try{
                if(json.get("input_fluid").getAsJsonObject().has("tag") && !json.get("input_fluid").getAsJsonObject().has("fluid")){
                    ResourceLocation fluidTagLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"tag","minecraft:empty"),':');
                    ITag<Fluid> tag = TagCollectionManager.getManager().getFluidTags().get(fluidTagLocation);
                    if(tag != null){
                        for(Fluid fluid : tag.getAllElements()){
                            FluidStack tempStack = new FluidStack(fluid.getFluid(), recipe.inputAmount);
                            recipe.fluidInputList.add(tempStack);
                            recipe.rawFluidInputList.add(tempStack.getRawFluid());
                        }
                    } else {
                        VoluminousEnergy.LOGGER.debug("Tag is null!");
                    }

                } else if (!json.get("input_fluid").getAsJsonObject().has("tag") && json.get("input_fluid").getAsJsonObject().has("fluid")){
                    // In here, a manually defined fluid is used instead of a tag
                    ResourceLocation fluidResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
                    recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation).getFluid()),recipe.inputAmount);
                    recipe.fluidInputList.add(recipe.inputFluid.copy());
                    recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                } else {
                    throw new JsonSyntaxException("Invalid recipe input for the Centrifugal Agitator, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

            }

            ResourceLocation bucketResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("first_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int firstAmount = JSONUtils.getInt(json.get("first_result").getAsJsonObject(),"amount",0);
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)),firstAmount);
            recipe.outputAmount = firstAmount;

            ResourceLocation secondBucketResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("second_result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondFluidAmount = JSONUtils.getInt(json.get("second_result").getAsJsonObject(),"amount",0);
            recipe.secondResult = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),secondFluidAmount);
            recipe.secondAmount = secondFluidAmount;

            ResourceLocation itemResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("third_result").getAsJsonObject(),"item","minecraft:empty"),':');
            int thirdItemAmount = JSONUtils.getInt(json.get("third_result").getAsJsonObject(),"count",1);
            recipe.thirdResult = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.thirdAmount = thirdItemAmount;

            return recipe;
        }

        @Nullable
        @Override
        public DistillationRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            DistillationRecipe recipe = new DistillationRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readFluidStack();
            recipe.inputFluid = buffer.readFluidStack();
            recipe.inputAmount = buffer.readInt();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, DistillationRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeFluidStack(recipe.result);
            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
        }
    }
}
