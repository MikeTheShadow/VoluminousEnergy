package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return new RecipeType(VoluminousEnergyPlugin.CENTRIFUGAL_AGITATION_UID, CentrifugalAgitatorRecipe.class);
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.CENTRIFUGAL_AGITATION_UID;
    }

    @Deprecated
    @Override
    public Class<? extends CentrifugalAgitatorRecipe> getRecipeClass() {
        return CentrifugalAgitatorRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.centrifugal_agitation");
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
    public void draw(CentrifugalAgitatorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,24, 12);
        emptyArrow.draw(matrixStack,24,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,10);
        slotDrawable.draw(matrixStack,72,10);

        Minecraft.getInstance().font.draw(matrixStack,"mB:", -20,32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getInputAmount() + "", 2, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getOutputAmount() + "", 48, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getSecondAmount() + "", 72, 32,VEContainerScreen.GREY_TEXT_COLOUR);
    }

    public void ingredientHandler(CentrifugalAgitatorRecipe recipe,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor firstFluidOutputAcceptor,
                                  IIngredientAcceptor secondFluidOutputAcceptor) {

        fluidInputAcceptor.addIngredients(VanillaTypes.FLUID, recipe.fluidInputList.get());

        firstFluidOutputAcceptor.addIngredient(VanillaTypes.FLUID, recipe.getOutputFluid());
        secondFluidOutputAcceptor.addIngredient(VanillaTypes.FLUID, recipe.getSecondFluid());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CentrifugalAgitatorRecipe recipe, IFocusGroup focusGroup) {
        // Input
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);

        // Output
        IRecipeSlotBuilder firstFluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49, 11);
        IRecipeSlotBuilder secondFluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);

        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        firstFluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());
        secondFluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());

        this.ingredientHandler(recipe, fluidInput, firstFluidOutput, secondFluidOutput);
    }
}