package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
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

public class CentrifugalAgitationCategory implements IRecipeCategory<CentrifugalAgitatorRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public CentrifugalAgitationCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.COMPRESSOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.CENTRIFUGAL_AGITATION_UID;
    }

    @Override
    public Class<? extends CentrifugalAgitatorRecipe> getRecipeClass() {
        return CentrifugalAgitatorRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Centrifugal Agitation";
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
    public void draw(CentrifugalAgitatorRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,24, 12);
        emptyArrow.draw(matrixStack,24,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,10);
        slotDrawable.draw(matrixStack,72,10);

        Minecraft.getInstance().fontRenderer.drawString(matrixStack,"mB:", -20,32, 0x606060);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.inputAmount + "", 2, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.outputAmount + "", 48, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.secondAmount + "", 72, 32,0x606060);
    }

    @Override
    public void setIngredients(CentrifugalAgitatorRecipe recipe, IIngredients ingredients) {

        ArrayList<ItemStack> inputList = new ArrayList<>();
        for (ItemStack testStack : recipe.getIngredient().getMatchingStacks()){
            testStack.setCount(1);
            inputList.add(testStack);
        }
        ingredients.setInputs(VanillaTypes.ITEM, inputList);

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getRecipeOutput()); // Normal output
        outputStacks.add(recipe.getSecondResult());

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CentrifugalAgitatorRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 2, 10);
        itemStacks.init(1, false, 48, 10);
        itemStacks.init(2, false, 72,10);

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
    }
}
