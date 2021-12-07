package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.*;

public class CombustionGeneratorFuelRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION;

    public static final Serializer SERIALIZER = new Serializer();

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public ArrayList<FluidStack> fluidInputList = new ArrayList<>();
    public ArrayList<Fluid> rawFluidInputList = new ArrayList<>();
    public static Map<Fluid, Integer> rawFluidWithVolumetricEnergy = new HashMap<>();
    public static ArrayList<Fluid> rawFluidInputListStatic = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int volumetricEnergy;
    private int inputArraySize;

    private FluidStack inputFluid;
    private ItemStack result;

    public CombustionGeneratorFuelRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public CombustionGeneratorFuelRecipe() {
        recipeId = null;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public Ingredient getIngredient(){ return ingredient;}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

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
    public ItemStack getResultItem(){return result;}

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
        List<ItemStack> s = new ArrayList<>();
        s.add(getResult());
        return s;
    }

    @Override
    public int getInputAmount() {
        return 0;
    }

    @Override
    public int getOutputAmount() {
        return 0;
    }

    @Override
    public FluidStack getOutputFluid() {
        return new FluidStack(((BucketItem) result.getItem()).getFluid(),250);
    }

    @Override
    public List<Integer> getAmounts() {
        return null;
    }

    @Override
    public int getProcessTime() { // Just get the volumetric energy for now
        return getVolumetricEnergy();
    }

    public int getVolumetricEnergy() {return volumetricEnergy;}


    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CombustionGeneratorFuelRecipe> {
        @Override
        public CombustionGeneratorFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.volumetricEnergy = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "volumetric_energy", 102400);

            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');

                Tag<Fluid> tag = RecipeUtil.getTagFromResourceLocationForFluids(fluidTagLocation, "Fuel Combustion");
                if(tag != null){
                    for(Fluid fluid : tag.getValues()){
                        FluidStack tempStack = new FluidStack(fluid, 1000);
                        recipe.fluidInputList.add(tempStack);
                        recipe.rawFluidInputList.add(tempStack.getRawFluid());
                        if (!rawFluidInputListStatic.contains(tempStack.getRawFluid())){
                            rawFluidWithVolumetricEnergy.put(tempStack.getRawFluid(), recipe.volumetricEnergy);
                            rawFluidInputListStatic.add(tempStack.getRawFluid());
                        }

                        recipe.inputArraySize = recipe.fluidInputList.size();
                    }
                } else {
                    VoluminousEnergy.LOGGER.debug("Tag is null!");
                }
            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),1000);
                recipe.fluidInputList.add(recipe.inputFluid);
                recipe.rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                recipe.inputArraySize = recipe.fluidInputList.size();
            } else {
                throw new JsonSyntaxException("Bad syntax for the Combustion Fuel recipe, input_fluid must be tag or fluid");
            }

            recipe.result = new ItemStack(Items.BUCKET); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorFuelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe((recipeId));
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.volumetricEnergy = buffer.readInt();

            // This is probably not great, but eh, what else am I supposed to do in this situation?
            recipe.inputArraySize = buffer.readInt();
            for (int i = 0; i < recipe.inputArraySize; i++){
                FluidStack serverFluid = buffer.readFluidStack();
                recipe.fluidInputList.add(serverFluid.copy());
                recipe.rawFluidInputList.add(serverFluid.getRawFluid());
                if (!rawFluidInputListStatic.contains(serverFluid.getRawFluid())){ // TODO: Test changes on server environment
                    rawFluidWithVolumetricEnergy.put(serverFluid.getRawFluid(), recipe.volumetricEnergy);
                    rawFluidInputListStatic.add(serverFluid.getRawFluid());
                }
            }

            recipe.result = buffer.readItem();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CombustionGeneratorFuelRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.volumetricEnergy);

            // Same as the comment in read, not optimal, but necessary
            buffer.writeInt(recipe.inputArraySize);
            for(int i = 0; i < recipe.inputArraySize; i++){
                buffer.writeFluidStack(recipe.fluidInputList.get(i).copy());
            }

            buffer.writeItem(recipe.getResult());
        }
    }
}
