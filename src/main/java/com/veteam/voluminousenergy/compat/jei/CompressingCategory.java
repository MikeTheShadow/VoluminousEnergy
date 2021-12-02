package com.veteam.voluminousenergy.compat.jei;
/*
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompressingCategory implements IRecipeCategory<CompressorRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public CompressingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 70, 40).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.COMPRESSOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.COMPRESSING_UID;
    }

    @Override
    public Class<? extends CompressorRecipe> getRecipeClass() {
        return CompressorRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.compressing");
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
    public void draw(CompressorRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,24, 12);
        emptyArrow.draw(matrixStack,24,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,10);
    }

    @Override
    public void setIngredients(CompressorRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getItems()))
                .collect(Collectors.toList()));

        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        for (ItemStack testStack : recipe.getIngredient().getItems()){
            testStack.setCount(64);
            ingredients.setInput(VanillaTypes.ITEM, testStack);
        }

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getResultItem()); // Normal output

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CompressorRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 2, 10);
        itemStacks.init(1, false, 48, 10);

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getItems()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        // Calculate output
        ItemStack tempStack = recipe.getResultItem(); // Get Item since amount will be wrong
        Item outputItem = tempStack.getItem();
        ItemStack jeiStack = new ItemStack(outputItem, recipe.getOutputAmount()); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);
    }
}*/