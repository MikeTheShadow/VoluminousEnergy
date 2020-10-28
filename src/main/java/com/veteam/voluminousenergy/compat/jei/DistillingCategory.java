package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistillingCategory implements IRecipeCategory<DistillationRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public DistillingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 128, 40).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.ALUMINUM_SHELL));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.DISTILLING_UID;
    }

    @Override
    public Class<? extends DistillationRecipe> getRecipeClass() {
        return DistillationRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Distilling";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(DistillationRecipe recipe, double mouseX, double mouseY) {
        arrow.draw(24, 12);
        emptyArrow.draw(24,12);
        slotDrawable.draw(2,10);
        slotDrawable.draw(48,10);
        slotDrawable.draw(72,10);
        slotDrawable.draw(96,10);

        Minecraft.getInstance().fontRenderer.drawString("mB:", -20,32, 0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getInputAmount() + "", 2, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getOutputAmount() + "", 48, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getAmounts().get(2) + "", 72, 32,0x606060);
    }

    @Override
    public void setIngredients(DistillationRecipe recipe, IIngredients ingredients) {

        ArrayList<ItemStack> inputList = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getMatchingStacks()).forEach(itemStack -> {
            itemStack.setCount(1);
            inputList.add(itemStack);
        });
        ingredients.setInputs(VanillaTypes.ITEM, inputList);

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getRecipeOutput()); // Normal output
        outputStacks.add(recipe.getSecondResult());
        outputStacks.add(recipe.getThirdResult());

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DistillationRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 2,10);
        itemStacks.init(1, false, 48,10);
        itemStacks.init(2, false, 72,10);
        itemStacks.init(3,false, 96,10);

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
        ItemStack jeiStack = new ItemStack(outputItem, 1); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);

        tempStack = recipe.getSecondResult();
        outputItem = tempStack.getItem();
        jeiStack = new ItemStack(outputItem, 1);
        itemStacks.set(2, jeiStack);

        itemStacks.set(3,recipe.getThirdResult());

    }
}
