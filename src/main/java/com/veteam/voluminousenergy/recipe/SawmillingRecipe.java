package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SawmillingRecipe extends VERecipe {

    public static final RecipeType<SawmillingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.SAWMILLING;

    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;
    public ItemStack secondResult;
    private int processTime;
    private int outputAmount;
    private int secondAmount;
    private FluidStack outputFluid;
    private boolean isLogRecipe;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public SawmillingRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient(){ return ingredient.get();}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    public ItemStack getRngItem(){return secondResult;}

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

    public int getOutputAmount() {return outputAmount;}

    public int getSecondAmount(){return secondAmount;}

    public FluidStack getOutputFluid(){return outputFluid.copy();}

    public int getProcessTime() { return processTime; }

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.SAWMILL_BLOCK);
    }

    public boolean isLogRecipe() {
        return isLogRecipe;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SawmillingRecipe>{

        @Override
        public SawmillingRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            SawmillingRecipe recipe = new SawmillingRecipe(recipeId);

            recipe.isLogRecipe = GsonHelper.getAsBoolean(json, "auto_log_to_plank", false);

            if (recipe.isLogRecipe) return recipe;

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            JsonObject resultData = json.get("result").getAsJsonObject();
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(resultData,"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(resultData,"count",1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;

            JsonObject secondResultData = json.get("second_result").getAsJsonObject();
            ResourceLocation secondResultResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondResultData,"item","minecraft:air"),':');
            int secondResultAmount = GsonHelper.getAsInt(secondResultData,"count",0);

            recipe.secondResult = new ItemStack(ForgeRegistries.ITEMS.getValue(secondResultResourceLocation));
            recipe.secondAmount = secondResultAmount;

            JsonObject outputFluid = json.get("output_fluid").getAsJsonObject();
            ResourceLocation outputFluidRL = ResourceLocation.of(GsonHelper.getAsString(outputFluid, "fluid", "minecraft:air"),':');
            int fluidAmount = GsonHelper.getAsInt(outputFluid, "amount", 0);

            recipe.outputFluid = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(outputFluidRL)), fluidAmount);

            return recipe;
        }

        @Nullable
        @Override
        public SawmillingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            SawmillingRecipe recipe = new SawmillingRecipe((recipeId));

            recipe.isLogRecipe = buffer.readBoolean();
            if (!recipe.isLogRecipe){
                recipe.ingredientCount = buffer.readByte();
                recipe.result = buffer.readItem();
                recipe.processTime = buffer.readInt();
                recipe.outputAmount = buffer.readInt();
                recipe.secondResult = buffer.readItem();
                recipe.secondAmount = buffer.readInt();
                recipe.outputFluid = buffer.readFluidStack();

                Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
                recipe.ingredient = Lazy.of(() -> tempIngredient);
            }

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SawmillingRecipe recipe){
            buffer.writeBoolean(recipe.isLogRecipe);
            if (!recipe.isLogRecipe){
                buffer.writeByte(recipe.getIngredientCount());
                buffer.writeItem(recipe.getResult());
                buffer.writeInt(recipe.processTime);
                buffer.writeInt(recipe.outputAmount);
                buffer.writeItem(recipe.secondResult);
                buffer.writeInt(recipe.secondAmount);
                buffer.writeFluidStack(recipe.outputFluid);

                recipe.ingredient.get().toNetwork(buffer);
            }
        }
    }
}
