package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class CombustionGeneratorOxidizerRecipe extends VERecipe {
    public static final RecipeType<CombustionGeneratorOxidizerRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.OXIDIZING;

    public static final Serializer SERIALIZER = new Serializer();

    public static ArrayList<Item> ingredientList = new ArrayList<>();
    public static ArrayList<OxidizerProperties> oxidizerList = new ArrayList<>();
    public static ArrayList<CombustionGeneratorOxidizerRecipe> oxidizerRecipes = new ArrayList<>();
    public static ArrayList<FluidStack> fluidInputList = new ArrayList<>();
    public static ArrayList<Fluid> rawFluidInputList = new ArrayList<>();

    public ArrayList<FluidStack> nsFluidInputList = new ArrayList<>();
    public ArrayList<Fluid> nsRawFluidInputList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;
    private int inputArraySize;

    private FluidStack inputFluid;
    public ItemStack result;

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
        return this.inputFluid.copy();
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

            for (ItemStack stack : recipe.ingredient.get().getItems()){
                if(!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            for (ItemStack stack : recipe.ingredient.get().getItems()){
                boolean hit = false;
                for (OxidizerProperties oxidizerProperties : oxidizerList) {
                    ItemStack bucketStack = oxidizerProperties.getBucketItem();
                    if (bucketStack.getItem() == stack.getItem()) {
                        hit = true;
                        break;
                    }
                }
                if (!hit){
                    OxidizerProperties temp = new OxidizerProperties(stack,recipe.processTime);
                    oxidizerList.add(temp);
                }
            }

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();

            if(inputFluid.has("tag") && !inputFluid.has("fluid")){
                // A tag is used instead of a manually defined fluid
                ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"tag","minecraft:air"),':');

                TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, fluidTagLocation);
                if (tag != null){
                    for (Holder<Fluid> fluidHolder : Registry.FLUID.getTagOrEmpty(tag)){ // TODO: Forge use their own registry but this was not the case for tags in 18.1
                        FluidStack tempStack = new FluidStack(fluidHolder.value(), 1000);
                        fluidInputList.add(tempStack);
                        rawFluidInputList.add(tempStack.getRawFluid());
                        recipe.nsFluidInputList.add(tempStack.copy());
                        recipe.nsRawFluidInputList.add(tempStack.getRawFluid());
                        recipe.inputArraySize = recipe.nsFluidInputList.size();
                    }
                    oxidizerRecipes.add(recipe);
                    // Sane add
                    //saneAdd(recipe);
                } else {
                    VoluminousEnergy.LOGGER.debug("Tag is null!");
                }
            } else if (inputFluid.has("fluid") && !inputFluid.has("tag")){
                // In here, a manually defined fluid is used instead of a tag
                ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(inputFluid,"fluid","minecraft:empty"),':');
                recipe.inputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)),1000);
                fluidInputList.add(recipe.inputFluid.copy());
                rawFluidInputList.add(recipe.inputFluid.getRawFluid());
                recipe.nsFluidInputList.add(recipe.inputFluid.copy());
                recipe.nsRawFluidInputList.add(recipe.inputFluid.getRawFluid());
                recipe.inputArraySize = recipe.nsFluidInputList.size();
                oxidizerRecipes.add(recipe);
                //saneAdd(recipe);
            } else {
                throw new JsonSyntaxException("Bad syntax for the Combustion Fuel recipe, input_fluid must be tag or fluid");
            }

            recipe.result = new ItemStack(Items.BUCKET); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorOxidizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe((recipeId));
            recipe.ingredient = Lazy.of(() -> Ingredient.fromNetwork(buffer));
            recipe.ingredientCount = buffer.readByte();

            recipe.inputArraySize = buffer.readInt();
            for (int i = 0; i < recipe.inputArraySize; i++){
                FluidStack serverFluid = buffer.readFluidStack();
                recipe.nsFluidInputList.add(serverFluid.copy());
                recipe.nsRawFluidInputList.add(serverFluid.getRawFluid());
            }

            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            //saneAdd(recipe);
            oxidizerRecipes.add(recipe);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CombustionGeneratorOxidizerRecipe recipe){
            recipe.ingredient.get().toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());

            buffer.writeInt(recipe.inputArraySize);
            for(int i = 0; i < recipe.inputArraySize; i++){
                buffer.writeFluidStack(recipe.nsFluidInputList.get(i).copy());
            }

            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            oxidizerRecipes.add(recipe);
            //saneAdd(recipe);
        }

        // TODO: Rewrite after forge fix
        public void saneAdd(CombustionGeneratorOxidizerRecipe recipe){
            if(CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size() >= (Short.MAX_VALUE * 32)) return; // If greater than 1,048,544 don't bother to add any more
            // Sanity check to prevent multiple of the same recipes being stored in the array
            ArrayList<FluidStack> sanityList = new ArrayList<>();
            for(int i = 0; (i < CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size() || CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size() == 0); i++){
                if(CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size() == 0){
                    sanityList.addAll(recipe.nsFluidInputList);

                    oxidizerRecipes.add(recipe);
                    continue;
                }
                CombustionGeneratorOxidizerRecipe referenceRecipe = CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i);
                for(int j = 0; j < referenceRecipe.nsFluidInputList.size(); j++){
                    if(!sanityList.isEmpty()){
                        AtomicBoolean isInsane = new AtomicBoolean(false);

                        referenceRecipe.nsFluidInputList.forEach(fluidStack -> {
                            if(sanityList.contains(fluidStack)){
                                isInsane.set(true);
                            }
                        });

                        if(!isInsane.get()){
                            sanityList.addAll(referenceRecipe.nsFluidInputList);

                            // Original logic
                            oxidizerRecipes.add(recipe);
                        }
                    } else { // assume sane
                        sanityList.addAll(referenceRecipe.nsFluidInputList);

                        oxidizerRecipes.add(recipe);
                    }
                }
            }
        }
    }


}