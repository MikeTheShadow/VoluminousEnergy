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

public class AqueoulizerRecipe extends VEFluidRecipe {
    public static final IRecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING;

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
    public ItemStack getResult() {return new ItemStack(this.result.getFluid().getFilledBucket());}

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
    // Use getResult instead
    public ItemStack getRecipeOutput(){return this.getResult();}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

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
    public ItemStack getIcon(){
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AqueoulizerRecipe> {
        @Override
        public AqueoulizerRecipe read(ResourceLocation recipeId, JsonObject json) {
            AqueoulizerRecipe recipe = new AqueoulizerRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = JSONUtils.getInt(json,"process_time",200);


            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }


            //ResourceLocation fluidResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int inputFluidAmount = JSONUtils.getInt(json.get("input_fluid").getAsJsonObject(),"amount",0);
            //recipe.inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation),inputFluidAmount);
            recipe.inputAmount = inputFluidAmount;

            // A tag is used instead of a manually defined fluid
            try{
                if(json.get("input_fluid").getAsJsonObject().has("tag") && !json.get("input_fluid").getAsJsonObject().has("fluid")){
                    ResourceLocation fluidTagLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"tag","minecraft:empty"),':');
                    //VoluminousEnergy.LOGGER.debug("FLUID TAG: " + fluidTagLocation);
                    ITag<Fluid> tag = TagCollectionManager.getManager().getFluidTags().get(fluidTagLocation);
                    if(tag != null){
                        for(Fluid fluid : tag.getAllElements()){
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
                    ResourceLocation fluidResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("input_fluid").getAsJsonObject(),"fluid","minecraft:empty"),':');
                    recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),recipe.inputAmount);
                    recipe.fluidInputList.add(recipe.inputFluid);
                    recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                    recipe.inputArraySize = recipe.fluidInputList.size();
                } else {
                    throw new JsonSyntaxException("Invalid recipe input for the Aqueoulizer, please check usage of tag and fluid in the json file.");
                }
            } catch (Exception e){

            }

            ResourceLocation secondBucketResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("result").getAsJsonObject(),"fluid","minecraft:empty"),':');
            int secondFluidAmount = JSONUtils.getInt(json.get("result").getAsJsonObject(),"amount",0);
            recipe.outputAmount = secondFluidAmount;
            recipe.result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)),recipe.outputAmount);

            return recipe;
        }

        @Nullable
        @Override
        public AqueoulizerRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            AqueoulizerRecipe recipe = new AqueoulizerRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
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
        public void write(PacketBuffer buffer, AqueoulizerRecipe recipe){
            recipe.ingredient.write(buffer);
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
