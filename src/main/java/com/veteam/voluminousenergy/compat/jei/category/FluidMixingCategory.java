package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.FluidMixerRecipe;
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

public class FluidMixingCategory implements IRecipeCategory<FluidMixerRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    private final IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.FLUID_MIXER_UID, FluidMixerRecipe.class);

    public FluidMixingCategory(IGuiHelper guiHelper) {
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.FLUID_MIXER_BLOCK.get()));
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
        return TextUtil.translateString("jei.voluminousenergy.fluid_mixing");
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
    public void draw(FluidMixerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 48, 12);
        emptyArrow.draw(matrixStack, 48, 12);
        slotDrawable.draw(matrixStack, 2, 10);
        slotDrawable.draw(matrixStack, 24, 10);
        slotDrawable.draw(matrixStack, 72, 10);

        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, "mB:",  -20, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getFluidIngredientAmount(0) + "",  2, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getFluidIngredientAmount(1) + "",  24, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getOutputFluid(0).getAmount() + "",  72, 32, VEContainerScreen.GREY_TEXT_STYLE);
    }

    public void ingredientHandler(FluidMixerRecipe recipe,
                                  IIngredientAcceptor firstFluidInputAcceptor,
                                  IIngredientAcceptor secondFluidInputAcceptor,
                                  IIngredientAcceptor outputFluidAccepetor) {

        firstFluidInputAcceptor.addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(0).getFluids()));

        secondFluidInputAcceptor.addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(1).getFluids()));
        outputFluidAccepetor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(0));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, FluidMixerRecipe recipe, IFocusGroup focusGroup) {
        // Input
        IRecipeSlotBuilder firstInputFluid = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);
        IRecipeSlotBuilder secondInputFluid = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 25, 11);


        // Output
        IRecipeSlotBuilder outputFluid = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);

        firstInputFluid.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        secondInputFluid.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        outputFluid.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());

        this.ingredientHandler(recipe, firstInputFluid, secondInputFluid, outputFluid);
    }
}
