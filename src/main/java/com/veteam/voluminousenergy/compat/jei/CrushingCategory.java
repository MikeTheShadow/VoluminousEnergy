package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.CrusherScreen;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CrushingCategory implements IRecipeCategory<CrusherRecipe> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public CrushingCategory(IGuiHelper guiHelper){
        // 60 -> 115 x, 10 -> 80 y
        background = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 68, 12, 40, 65).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.CRUSHER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
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
    public void draw(CrusherRecipe recipe, double mouseX, double mouseY) {
        arrow.draw(92 - 16, 35 - 30);
    }

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
        itemStacks.init(0, false, 11, 0);
        itemStacks.init(1, false, 2, 45);

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getMatchingStacks()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        // Calculate output
        ItemStack tempStack = recipe.getRecipeOutput(); // Get Item since amount will be wrong
        Item outputItem = tempStack.getItem();
        ItemStack jeiStack = new ItemStack(outputItem, recipe.getOutputAmount()); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);

        // Calculate RNG stack, only if RNG stack exists
        if (recipe.getRngItem() != null && recipe.getRngItem().getItem() != Items.AIR){ // Don't create the slot if the slot will be empty!
            itemStacks.init(2, true, 20, 45);
            tempStack = recipe.getRngItem();
            Item rngItem = tempStack.getItem();
            ItemStack rngStack = new ItemStack(rngItem, recipe.getOutputRngAmount());
            itemStacks.set(2, rngStack);
        }
    }

}
