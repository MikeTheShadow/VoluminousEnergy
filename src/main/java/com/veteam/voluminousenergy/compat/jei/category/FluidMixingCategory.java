package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.FluidMixerRecipe;
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

public class FluidMixingCategory implements IRecipeCategory<FluidMixerRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.FLUID_MIXER_UID, FluidMixerRecipe.class);

    public FluidMixingCategory(IGuiHelper guiHelper) {
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.FLUID_MIXER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI, 199, 0, 23, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }

    @Deprecated
    @Override
    public ResourceLocation getUid() {
        return VoluminousEnergyPlugin.FLUID_MIXER_UID;
    }

    @Deprecated
    @Override
    public Class<? extends FluidMixerRecipe> getRecipeClass() {
        return FluidMixerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.fluid_mixing");
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
    public void draw(FluidMixerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 48, 12);
        emptyArrow.draw(matrixStack, 48, 12);
        slotDrawable.draw(matrixStack, 2, 10);
        slotDrawable.draw(matrixStack, 26, 10);
        slotDrawable.draw(matrixStack, 72, 10);

        Minecraft.getInstance().font.draw(matrixStack, "mB:", -20, 32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack, recipe.getInputAmount() + "", 2, 32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack, recipe.getSecondInputAmount() + "", 26, 32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack, recipe.getOutputAmount() + "", 72, 32, VEContainerScreen.GREY_TEXT_COLOUR);
    }

    public void ingredientHandler(FluidMixerRecipe recipe,
                                  IIngredientAcceptor firstFluidInputAcceptor,
                                  IIngredientAcceptor secondFluidInputAcceptor,
                                  IIngredientAcceptor outputFluidAccepetor) {

        firstFluidInputAcceptor.addIngredients(VanillaTypes.FLUID, recipe.fluidInputList.get());

        secondFluidInputAcceptor.addIngredients(VanillaTypes.FLUID, recipe.secondFluidInputList.get());
        outputFluidAccepetor.addIngredient(VanillaTypes.FLUID, recipe.getOutputFluid());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, FluidMixerRecipe recipe, IFocusGroup focusGroup) {
        // Input
        IRecipeSlotBuilder firstInputFluid = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);
        IRecipeSlotBuilder secondInputFluid = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 27, 11);


        // Output
        IRecipeSlotBuilder outputFluid = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);

        firstInputFluid.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        secondInputFluid.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        outputFluid.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());

        this.ingredientHandler(recipe, firstInputFluid, secondInputFluid, outputFluid);
    }
}
