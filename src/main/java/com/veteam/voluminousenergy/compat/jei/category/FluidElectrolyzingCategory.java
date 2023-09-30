package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.FluidElectrolyzerRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FluidElectrolyzingCategory implements IRecipeCategory<FluidElectrolyzerRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    private final IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.FLUID_ELECTROLYZER_UID, FluidElectrolyzerRecipe.class);

    public FluidElectrolyzingCategory(IGuiHelper guiHelper) {
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.FLUID_ELECTROLYZER_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI, 199, 0, 23, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.fluid_electrolyzing");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(FluidElectrolyzerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 24, 12);
        emptyArrow.draw(matrixStack, 24, 12);
        slotDrawable.draw(matrixStack, 2, 10);
        slotDrawable.draw(matrixStack, 48, 10);
        slotDrawable.draw(matrixStack, 72, 10);

        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, "mB:",  -20, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getFluidIngredientAmount(0) + "",  2, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getOutputFluid(0).getAmount() + "",  48, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getOutputFluid(1).getAmount() + "",  72, 32, VEContainerScreen.GREY_TEXT_STYLE);
    }

    public void ingredientHandler(FluidElectrolyzerRecipe recipe,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor firstFluidOutputAcceptor,
                                  IIngredientAcceptor secondFluidOutputAcceptor) {

        fluidInputAcceptor.addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(0).getFluids()));

        firstFluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(0));
        secondFluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(1));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, FluidElectrolyzerRecipe recipe, IFocusGroup focusGroup) {
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