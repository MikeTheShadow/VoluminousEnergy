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

public class AqueoulizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING;

    public static final Serializer SERIALIZER = new Serializer();

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public ArrayList<FluidStack> fluidInputList = new ArrayList<>();
    public ArrayList<Fluid> rawFluidInputList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;
    private int inputArraySize;

    private FluidStack inputFluid;
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
    public Ingredient getIngredient(){ return ingredient;}

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    @Deprecated
    // use getOutputFluid instead
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getBucket());}

    @Override
    public FluidStack getOutputFluid(){
        return this.result;
    }

    @Override
    public List<Integer> getAmounts() {
        return null;
    }

    public FluidStack getInputFluid(){
        return this.inputFluid;
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
    // Use getResult instead
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    @Override
    public List<FluidStack> getFluids() {
        return this.fluidInputList;
    }

    @Override
    public List<Fluid> getRawFluids(){
        return this.rawFluidInputList;
    }

    @Override
    public List<ItemStack> getResults() {
        return null;
    }

    @Override
    public int getOutputAmount() {return this.outputAmount;}

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public int getInputAmount(){
        return this.inputAmount;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AqueoulizerRecipe> {
        @Override
        public AqueoulizerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            AqueoulizerRecipe recipe = new AqueoulizerRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);


            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }


            //ResourceLocation fluidResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int inputFluidAmount = GsonHelper.getAsInt(json.get("input_fluid").getAsJsonObject(),"amount",0);
            //recipe.inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation),inputFluidAmount);
            recipe.inputAmount = inputFluidAmount;

            // A tag is used instead of a manually defined fluid
            try{
                if(json.get("input_fluid").getAsJsonObject().has("tag") && !json.get("input_fluid").getAsJsonObject().has("fluid")){
                    ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("input_fluid").getAsJsonObject(),"tag","minecraft:empty"),':');
                    //VoluminousEnergy.LOGGER.debug("FLUID TAG: " + fluidTagLocation);
                    Tag<Fluid> tag = SerializationTags.getInstance().getFluids().getTag(fluidTagLocation);
                    if(tag != null){
                        for(Fluid fluid : tag.getValues()){
                            FluidStack tempStack = new FluidStack(fluid.getFluid(), inputFluidAmount);
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
                    recipe.fluidInputList.add(recipe.inputFluid);
                    recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                    recipe.inputArraySize = recipe.fluidInputList.size();
                } else {
                    throw new JsonSyntaxException("Invalid recipe input for the Aqueoulizer, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

            }

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondFluidAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"amount",0);
            recipe.outputAmount = secondFluidAmount;
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),recipe.outputAmount);

            return recipe;
        }

        @Nullable
        @Override
        public AqueoulizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            AqueoulizerRecipe recipe = new AqueoulizerRecipe((recipeId));
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
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AqueoulizerRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeFluidStack(recipe.result);


            buffer.writeInt(recipe.inputArraySize);
            for(int i = 0; i < recipe.inputArraySize; i++){
                buffer.writeFluidStack(recipe.fluidInputList.get(i).copy());
            }

            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
        }
    }
}
