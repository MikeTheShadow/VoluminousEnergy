package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
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

import java.util.Collections;
import java.util.List;

public class AqueoulizingCategory implements IRecipeCategory<AqueoulizerRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    private final IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType<>(VoluminousEnergyPlugin.AQUEOULIZING_UID, AqueoulizerRecipe.class);

    public AqueoulizingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.aqueoulizing");
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
    public void draw(AqueoulizerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,48, 12);
        emptyArrow.draw(matrixStack,48,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,24,10);
        slotDrawable.draw(matrixStack,72,10);

        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty("mB:"),  2, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(recipe.getFluidIngredientAmount(0) + ""),  24, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(recipe.getOutputFluids().get(0).getAmount() + ""),  72, 32, VEContainerScreen.GREY_TEXT_STYLE);
    }



    // NOTE: Needs to be recipe specific; refactoring of setIngredients, which is no longer used
    public void ingredientHandler(AqueoulizerRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor fluidOutputAcceptor) {

        // INPUT
        itemInputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, List.of(recipe.getIngredient(0).getItems()).get(0));

        fluidInputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(0).getFluids()).get(0));

        // OUTPUT
        fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, Collections.singletonList(recipe.getOutputFluid(0)).get(0));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, AqueoulizerRecipe recipe, IFocusGroup focusGroup) {

        // init
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11); // Coords reused from old init call
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 25, 11);

        // Outputs
        IRecipeSlotBuilder fluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);

        // Setup
        // Inputs
        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        this.ingredientHandler(recipe, itemInput, fluidInput, fluidOutput);

        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        fluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());
    }
}