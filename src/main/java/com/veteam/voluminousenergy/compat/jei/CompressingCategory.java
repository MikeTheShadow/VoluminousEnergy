package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
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
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompressingCategory implements IRecipeCategory<CompressorRecipe> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public CompressingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        background = guiHelper.drawableBuilder(new ResourceLocation(VoluminousEnergy.MODID + "textures/gui/compressor_gui.png"), 68, 12, 40, 70).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.COMPRESSOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(new ResourceLocation(VoluminousEnergy.MODID + "textures/gui/compressor_gui.png"), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
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
    public String getTitle() {
        return "Compressing";
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
    public void draw(CompressorRecipe recipe, double mouseX, double mouseY) {
        arrow.draw(10, 19);
    }

    @Override
    public void setIngredients(CompressorRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getMatchingStacks()))
                .collect(Collectors.toList()));

        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        for (ItemStack testStack : recipe.getIngredient().getMatchingStacks()){
            testStack.setCount(64);
            ingredients.setInput(VanillaTypes.ITEM, testStack);
        }

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getRecipeOutput()); // Normal output

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CompressorRecipe recipe, IIngredients ingredients) {
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
    }
}
