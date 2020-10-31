package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StirlingCategory implements IRecipeCategory<StirlingGeneratorRecipe> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public StirlingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 40, 44).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.STIRLING_UID;
    }

    @Override
    public Class<? extends StirlingGeneratorRecipe> getRecipeClass() {
        return StirlingGeneratorRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Stirling";
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
    public void draw(StirlingGeneratorRecipe recipe, double mouseX, double mouseY) {
        slotDrawable.draw(11,0);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getEnergyPerTick() + " FE/t",-1,20, 0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getProcessTime() + " t",-1,28, 0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getProcessTime()/20 + " sec",-1,36, 0x606060);
    }

    @Override
    public void setIngredients(StirlingGeneratorRecipe recipe, IIngredients ingredients) {
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
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, StirlingGeneratorRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 11, 0);

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getMatchingStacks()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);
    }
}
