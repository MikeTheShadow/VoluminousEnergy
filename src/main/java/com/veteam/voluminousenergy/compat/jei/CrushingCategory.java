package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.CrusherScreen;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CrushingCategory implements IRecipeCategory<CrusherRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public CrushingCategory(IGuiHelper guiHelper){
        background = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 37, 17, 102, 50).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.CRUSHER_BLOCK.getBlock()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.CRUSHING_UID;
    }

    @Override
    public Class<? extends CrusherRecipe> getRecipeClass() {
        return CrusherRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Crushing";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    // Override draw here if needed

    @Override
    public void setIngredients(CrusherRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getMatchingStacks()))
                .collect(Collectors.toList()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrusherRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 24, 5);
        itemStacks.init(1, true, 24, 28);

        int i = 0;
        for (Map.Entry<Ingredient, Integer> entry : recipe.getIngredientMap().entrySet()){
            Ingredient ingredient = entry.getKey();
            Integer count = entry.getValue();
            itemStacks.set(i++, Arrays.stream(ingredient.getMatchingStacks())
                .map(stack -> {
                    ItemStack itemStack = stack.copy();
                    itemStack.setCount(count);
                    return itemStack;
                }).collect(Collectors.toList())
            );
        }
    }

}
