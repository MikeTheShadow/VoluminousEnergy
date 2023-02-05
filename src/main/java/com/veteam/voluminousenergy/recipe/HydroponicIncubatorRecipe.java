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

public class HydroponicIncubatorRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.HYDROPONIC_INCUBATING;

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    private Lazy<FluidStack> inputFluid;
    private ItemStack result;
    private ItemStack rngResult0;
    private ItemStack rngResult1;
    private ItemStack rngResult2;
    private int inputAmount;
    private int outputAmount;
    private int outputRngAmount0;
    private int outputRngAmount1;
    private int outputRngAmount2;
    private float chance0;
    private float chance1;
    private float chance2;

    public HydroponicIncubatorRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient.get();}

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    public List<Integer> getAmounts() {
        return null;
    }

    public FluidStack getInputFluid(){
        return this.inputFluid.get();
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
    // Use getResult instead
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public ItemStack getResult() {
        return this.result;
    }

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
        return this.fluidInputList.get();
    }

    @Override
    public List<Fluid> getRawFluids(){
        return this.rawFluidInputList.get();
    }

    @Override
    public List<ItemStack> getResults() {
        return List.of(result.copy(), rngResult0.copy(), rngResult1.copy(), rngResult2.copy());
    }

    public List<Item> getResultItems() {
        return List.of(result.getItem(), rngResult0.getItem(), rngResult1.getItem(), rngResult2.getItem());
    }

    @Override
    public int getOutputAmount() {return this.outputAmount;}

    public int getOutputRngAmount0(){return outputRngAmount0;}

    public int getOutputRngAmount1(){return outputRngAmount1;}

    public int getOutputRngAmount2(){return outputRngAmount2;}

    @Override
    public FluidStack getOutputFluid() { // This recipe does not have an output fluid
        return null;
    }

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public int getInputAmount(){
        return this.inputAmount;
    }

    public ItemStack getRngItemSlot0(){return rngResult0;}

    public ItemStack getRngItemSlot1(){return rngResult1;}

    public ItemStack getRngItemSlot2(){return rngResult2;}

    public float getChance0(){return chance0;}

    public float getChance1(){return chance1;}

    public float getChance2(){return chance2;}

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<HydroponicIncubatorRecipe> {
        @Override
        public HydroponicIncubatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            HydroponicIncubatorRecipe recipe = new HydroponicIncubatorRecipe(recipeId);

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
                throw new JsonSyntaxException("Bad syntax for the Hydroponic Incubator recipe, input_fluid must be tag or fluid");
            }

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;

            // First RNG Slot, RNG 0
            ResourceLocation rngResourceLocation0 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_0").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount0 = GsonHelper.getAsInt(json.get("rng_slot_0").getAsJsonObject(),"count",0);
            float rngChance0 = GsonHelper.getAsFloat(json.get("rng_slot_0").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult0 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation0));
            recipe.outputRngAmount0 = rngAmount0;
            recipe.chance0 = rngChance0;

            //Second RNG Slot, RNG 1
            ResourceLocation rngResourceLocation1 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_1").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount1 = GsonHelper.getAsInt(json.get("rng_slot_1").getAsJsonObject(),"count",0);
            float rngChance1 = GsonHelper.getAsFloat(json.get("rng_slot_1").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult1 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation1));
            recipe.outputRngAmount1 = rngAmount1;
            recipe.chance1 = rngChance1;

            //Third RNG Slot, RNG 2
            ResourceLocation rngResourceLocation2 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_2").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount2 = GsonHelper.getAsInt(json.get("rng_slot_2").getAsJsonObject(),"count",0);
            float rngChance2 = GsonHelper.getAsFloat(json.get("rng_slot_2").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult2 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation2));
            recipe.outputRngAmount2 = rngAmount2;
            recipe.chance2 = rngChance2;

            return recipe;
        }

        @Nullable
        @Override
        public HydroponicIncubatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            HydroponicIncubatorRecipe recipe = new HydroponicIncubatorRecipe(recipeId);
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
            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            //RNG 0
            recipe.rngResult0 = buffer.readItem();
            recipe.outputRngAmount0 = buffer.readInt();
            recipe.chance0 = buffer.readFloat();
            //RNG 1
            recipe.rngResult1 = buffer.readItem();
            recipe.outputRngAmount1 = buffer.readInt();
            recipe.chance1 = buffer.readFloat();
            //RNG 2
            recipe.rngResult2 = buffer.readItem();
            recipe.outputRngAmount2 = buffer.readInt();
            recipe.chance2 = buffer.readFloat();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HydroponicIncubatorRecipe recipe){
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
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            //RNG 0
            buffer.writeItem(recipe.rngResult0);
            buffer.writeInt(recipe.outputRngAmount0);
            buffer.writeFloat(recipe.chance0);
            //RNG 1
            buffer.writeItem(recipe.rngResult1);
            buffer.writeInt(recipe.outputRngAmount1);
            buffer.writeFloat(recipe.chance1);
            //RNG 2
            buffer.writeItem(recipe.rngResult2);
            buffer.writeInt(recipe.outputRngAmount2);
            buffer.writeFloat(recipe.chance2);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }
}
