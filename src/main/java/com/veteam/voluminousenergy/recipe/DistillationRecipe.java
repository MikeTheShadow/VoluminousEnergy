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

public class DistillationRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING;

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
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

    public FluidStack getSecondResult(){return this.secondResult;}

    @Override
    public int getInputAmount(){ return inputAmount; }

    @Override
    public ItemStack getToastSymbol(){
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


    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DistillationRecipe> {
        @Override
        public DistillationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            DistillationRecipe recipe = new DistillationRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.inputAmount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "amount", 0);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }

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
                    throw new JsonSyntaxException("Invalid recipe input for the Distillation Unit, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

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
            recipe.thirdResult = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.thirdAmount = thirdItemAmount;

            return recipe;
        }

        @Nullable
        @Override
        public DistillationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            DistillationRecipe recipe = new DistillationRecipe((recipeId));
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readFluidStack();

            // This is probably not great, but eh, what else am I supposed to do in this situation?
            recipe.inputArraySize = buffer.readInt();
            for (int i = 0; i < recipe.inputArraySize; i++){
                FluidStack serverFluid = buffer.readFluidStack();
                recipe.fluidInputList.add(serverFluid.copy());
                recipe.rawFluidInputList.add(serverFluid.getRawFluid());
            }

            recipe.inputAmount = buffer.readInt();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();
            recipe.thirdResult = buffer.readItem();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DistillationRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeFluidStack(recipe.result);

            // Same as the comment in read, not optimal, but necessary
            buffer.writeInt(recipe.inputArraySize);
            recipe.fluidInputList.forEach(fluid -> {
                buffer.writeFluidStack(fluid.copy());
            });

            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
            buffer.writeItem(recipe.thirdResult);
        }
    }
}
