package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
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

public class AqueoulizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING;

    public static final Serializer SERIALIZER = new Serializer();

    public Lazy<ArrayList<Item>> ingredientList = RecipeUtil.getLazyItemsFromIngredient(this);
    public Lazy<ArrayList<FluidStack>> fluidInputList;
    public Lazy<ArrayList<Fluid>> rawFluidInputList;

    private final ResourceLocation recipeId;
    private int processTime;
    private Lazy<Integer> inputArraySize;

    private Lazy<FluidStack> inputFluid;
    private FluidStack result;
    private int inputAmount;
    private int outputAmount;
    private boolean fluidUsesTagKey;
    private String tagKeyString;

    public AqueoulizerRecipe() {
        recipeId = null;
    }

    public AqueoulizerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient.get();}

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

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            recipe.ingredientList = Lazy.of(() -> {
                ArrayList<Item> items = new ArrayList<>();
                for (ItemStack stack : recipe.ingredient.get().getItems()){
                    if(!items.contains(stack.getItem())){
                        items.add(stack.getItem());
                    }
                }
                return items;
            });

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.inputAmount = GsonHelper.getAsInt(inputFluid,"amount",0);

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                recipe.fluidUsesTagKey = true;
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');

                recipe.inputFluid = null;
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, recipe.inputAmount);
                recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());

            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                recipe.fluidUsesTagKey = false;
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                recipe.inputFluid = Lazy.of(() -> new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),recipe.inputAmount));
                recipe.fluidInputList = Lazy.of(() -> {
                    ArrayList<FluidStack> temp = new ArrayList<>();
                    temp.add(new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),recipe.inputAmount));
                    return temp;
                });
                recipe.rawFluidInputList= Lazy.of(() -> {
                    ArrayList<Fluid> temp = new ArrayList<>();
                    temp.add(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation));
                    return temp;
                });
                recipe.inputArraySize = Lazy.of(() -> 1);
            } else {
                throw new JsonSyntaxException("Bad syntax for the Aqueoulizer recipe, input_fluid must be tag or fluid");
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

            // Start with usesTagKey check
            recipe.fluidUsesTagKey = buffer.readBoolean();

            if (recipe.fluidUsesTagKey){
                recipe.tagKeyString = buffer.readComponent().getContents();
                ResourceLocation fluidTagLocation = new ResourceLocation(recipe.tagKeyString);
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, recipe.inputAmount);
                recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());
            } else {
                recipe.inputArraySize = Lazy.of(buffer::readInt);
                ArrayList<Fluid> fluids = new ArrayList<>();
                ArrayList<FluidStack> fluidStacks = new ArrayList<>();
                for (int i = 0; i < recipe.inputArraySize.get(); i++){
                    FluidStack serverFluid = buffer.readFluidStack();
                    fluidStacks.add(serverFluid.copy());
                    fluids.add(serverFluid.getRawFluid());
                }

                recipe.fluidInputList = Lazy.of(() -> fluidStacks);
                recipe.rawFluidInputList = Lazy.of(() -> fluids);
            }

            recipe.ingredientCount = buffer.readInt();
            recipe.result = buffer.readFluidStack();
            recipe.inputAmount = buffer.readInt();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.ingredient = Lazy.of(() -> Ingredient.fromNetwork(buffer));
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AqueoulizerRecipe recipe){
            buffer.writeBoolean(recipe.fluidUsesTagKey);

            if (recipe.fluidUsesTagKey){
                buffer.writeComponent(new TextComponent(recipe.tagKeyString));
            } else { // does not use tags for fluid input
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            buffer.writeInt(recipe.ingredientCount);
            buffer.writeFluidStack(recipe.result);
            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);

            recipe.ingredient.get().toNetwork(buffer);
        }
    }
}
