package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.recipe.ToolingRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class ToolingCategory implements IRecipeCategory<ToolingRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public ToolingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 70, 40).build();
        //ItemStack drawStack = new ItemStack(VEMultitools.IRON_DRILL_MULTITOOL);
        //drawStack.getOrCreateTag();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEMultitools.IRON_DRILL_MULTITOOL));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.TOOLING_UID;
    }

    @Override
    public Class<? extends ToolingRecipe> getRecipeClass() {
        return ToolingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.tooling");
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
    public void draw(ToolingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,1);
        slotDrawable.draw(matrixStack,48,19);

    }

    @Override
    public void setIngredients(ToolingRecipe recipe, IIngredients ingredients) {
        /*ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getItems()))
                .collect(Collectors.toList()));*/

        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        ArrayList<ItemStack> inputList = new ArrayList<>();
        inputList.add(recipe.getResult().copy());

        for (Item bitItem : recipe.getBits()){
            inputList.add(new ItemStack(bitItem, 64));
        }

        for (Item baseItem : recipe.getBases()){
            inputList.add(new ItemStack(baseItem, 64));
        }

        ingredients.setInputs(VanillaTypes.ITEM, inputList);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ToolingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 2, 10); // This is the result / complete multitool
        itemStacks.init(1, false, 48, 1); // This is the top which should be the bit
        itemStacks.init(2, false, 48, 19); // This is the bottom which should be the Base

        ArrayList<ItemStack> bitList = new ArrayList<>();
        ArrayList<ItemStack> baseList = new ArrayList<>();

        recipe.getBits().forEach(bit -> bitList.add(new ItemStack(bit)));
        recipe.getBases().forEach(base -> baseList.add(new ItemStack(base)));

        itemStacks.set(0, recipe.getResultItem().copy());
        itemStacks.set(1, bitList);
        itemStacks.set(2, baseList);
    }
}
