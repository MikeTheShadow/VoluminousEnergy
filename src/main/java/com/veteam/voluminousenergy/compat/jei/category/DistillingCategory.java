package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
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

public class DistillingCategory implements IRecipeCategory<DistillationRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.DISTILLING_UID, DistillationRecipe.class);

    public DistillingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 42, 5, 128, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.distilling");
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
    public void draw(DistillationRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,24, 12);
        emptyArrow.draw(matrixStack,24,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,10);
        slotDrawable.draw(matrixStack,72,10);
        slotDrawable.draw(matrixStack,96,10);

        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, "mB:", -20,32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font,recipe.getFluidIngredientAmount(0)  + "", 2, 32,VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font,recipe.getOutputFluid(0).getAmount() + "", 48, 32,VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font,recipe.getOutputFluid(1).getAmount() + "", 72, 32,VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, (int)(recipe.getThirdChance()*100) + "%", 96, 32,VEContainerScreen.GREY_TEXT_STYLE);
    }

    public void ingredientHandler(DistillationRecipe recipe,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor firstFluidOutputAcceptor,
                                  IIngredientAcceptor secondFluidOutputAcceptor,
                                  IIngredientAcceptor itemOutputAcceptor) {

        // Input
        fluidInputAcceptor.addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(0).getFluids()));

        // Output
        firstFluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(0));
        secondFluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(1));

        ItemStack itemStackResult = recipe.getOutputItem(0).copy();
        itemStackResult.setCount(recipe.getOutputItem(0).getCount());
        itemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK,itemStackResult);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, DistillationRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);

        // Outputs
        IRecipeSlotBuilder firstFluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49, 11);
        IRecipeSlotBuilder secondFluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);
        IRecipeSlotBuilder itemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 97, 11);

        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        firstFluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());
        secondFluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());
        itemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());

        this.ingredientHandler(recipe, fluidInput, firstFluidOutput, secondFluidOutput, itemOutput);
    }
}