package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
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
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK));
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
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.getInputAmount() + "", 2, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.getOutputAmount() + "", 48, 32,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,recipe.getSecondAmount() + "", 72, 32,0x606060);
    }

    @Override
    public void setIngredients(CentrifugalAgitatorRecipe recipe, IIngredients ingredients) {

        // INPUT
        ingredients.setInputs(VanillaTypes.FLUID, recipe.fluidInputList);

        // OUTPUT
        List<FluidStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getOutputFluid()); // Normal output
        outputStacks.add(recipe.getSecondFluid()); // Second output
        ingredients.setOutputs(VanillaTypes.FLUID, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CentrifugalAgitatorRecipe recipe, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
        fluidStacks.init(0, false, 3, 11);
        fluidStacks.init(1, false, 49, 11);
        fluidStacks.init(2, false, 73,11);

        // Input
        fluidStacks.set(0, recipe.fluidInputList);

        // Calculate output
        fluidStacks.set(1, recipe.getOutputFluid());
        fluidStacks.set(2, recipe.getSecondFluid());
    }
}
