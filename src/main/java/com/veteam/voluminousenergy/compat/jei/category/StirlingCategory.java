package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

import java.util.Arrays;

public class StirlingCategory implements IRecipeCategory<StirlingGeneratorRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.STIRLING_UID, StirlingGeneratorRecipe.class);

    public StirlingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 40, 44).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.stirling");
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
    public void draw(StirlingGeneratorRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,11,0);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, recipe.getEnergyPerTick() + " FE/t", -1,20, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, recipe.getProcessTime() + " t",-1,28, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, recipe.getProcessTime()/20 + " sec",-1,36, VEContainerScreen.GREY_TEXT_STYLE);
    }

    public void ingredientHandler(StirlingGeneratorRecipe recipe, IIngredientAcceptor itemInputAcceptor) {
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(recipe.getIngredient(0).getItems()).toList());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, @NotNull StirlingGeneratorRecipe recipe, @NotNull IFocusGroup  focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 12, 1);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        this.ingredientHandler(recipe, itemInput);
    }
}