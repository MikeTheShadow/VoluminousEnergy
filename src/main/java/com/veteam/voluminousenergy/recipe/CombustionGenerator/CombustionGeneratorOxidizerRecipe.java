package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class CombustionGeneratorOxidizerRecipe extends VERecipe {
    public static final RecipeType<CombustionGeneratorOxidizerRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.OXIDIZING;

    public static final Serializer SERIALIZER = new Serializer();

    public Lazy<ArrayList<Item>> ingredientList = RecipeUtil.getLazyItemsFromIngredient(this);
    public static ArrayList<CombustionGeneratorOxidizerRecipe> oxidizerRecipes = new ArrayList<>();
    public Lazy<ArrayList<FluidStack>> fluidInputList;
    public Lazy<ArrayList<Fluid>> rawFluidInputList;

    private final ResourceLocation recipeId;
    private int processTime;
    private Lazy<Integer> inputArraySize;

    private Lazy<FluidStack> inputFluid;
    public ItemStack result = new ItemStack(Items.BUCKET);

    // Tags stuff
    protected boolean usesTagKey;
    protected String tagKeyString;

    public CombustionGeneratorOxidizerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public Ingredient getIngredient(){ return ingredient.get();}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    public FluidStack getInputFluid(){
        return this.inputFluid.get().copy();
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
    public ItemStack getResultItem(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeType<?> getType(){return RECIPE_TYPE;}

    public int getProcessTime(){return processTime;}


    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CombustionGeneratorOxidizerRecipe> {
        @Override
        public CombustionGeneratorOxidizerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe(recipeId);

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",1600);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                recipe.usesTagKey = true;

                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');
                recipe.tagKeyString = fluidTagLocation.toString();

                recipe.inputFluid = null;
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, 1000);
                recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());

            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                recipe.usesTagKey = false;

                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                recipe.inputFluid = Lazy.of(() -> new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),1000));
                recipe.fluidInputList = Lazy.of(() -> {
                    ArrayList<FluidStack> temp = new ArrayList<>();
                    temp.add(new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),1));
                    return temp;
                });
                recipe.rawFluidInputList= Lazy.of(() -> {
                    ArrayList<Fluid> temp = new ArrayList<>();
                    temp.add(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation));
                    return temp;
                });
                recipe.inputArraySize = Lazy.of(() -> recipe.fluidInputList.get().size());
            } else {
                throw new JsonSyntaxException("Bad syntax for the Combustion Fuel recipe, input_fluid must be tag or fluid");
            }

            oxidizerRecipes.add(recipe);
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorOxidizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe((recipeId));
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            recipe.usesTagKey = buffer.readBoolean();

            if (recipe.usesTagKey){
                int sequenceLength = buffer.readInt();
                recipe.tagKeyString = buffer.readCharSequence(sequenceLength, StandardCharsets.UTF_8).toString();

                ResourceLocation fluidTagLocation = new ResourceLocation(recipe.tagKeyString);
                recipe.rawFluidInputList = TagUtil.getLazyFluids(fluidTagLocation);
                recipe.fluidInputList = TagUtil.getLazyFluidStacks(fluidTagLocation, 1000);
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


            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            //oxidizerRecipes.forEach(oxidizerRecipe -> {
            //    if (oxidizerRecipe.fluidInputList.get().contains(thisFluid)){
            //        atomicBoolean.set(true);
            //    }
            //});
            if (!atomicBoolean.get()) oxidizerRecipes.add(recipe);

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            oxidizerRecipes.add(recipe);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CombustionGeneratorOxidizerRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.processTime);
            buffer.writeBoolean(recipe.usesTagKey);

            if (recipe.usesTagKey){
                buffer.writeInt(recipe.tagKeyString.length());
                buffer.writeCharSequence(recipe.tagKeyString, StandardCharsets.UTF_8);
            } else {
                buffer.writeInt(recipe.inputArraySize.get());
                for(int i = 0; i < recipe.inputArraySize.get(); i++){
                    buffer.writeFluidStack(recipe.fluidInputList.get().get(i).copy());
                }
            }

            recipe.ingredient.get().toNetwork(buffer);
        }

    }

}