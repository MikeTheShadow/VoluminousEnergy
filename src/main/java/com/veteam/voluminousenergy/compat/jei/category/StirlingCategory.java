package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class StirlingCategory implements IRecipeCategory<StirlingGeneratorRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public StirlingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 40, 44).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return new RecipeType(VoluminousEnergyPlugin.STIRLING_UID, StirlingGeneratorRecipe.class);
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.STIRLING_UID;
    }

    @Deprecated
    @Override
    public Class<? extends StirlingGeneratorRecipe> getRecipeClass() {
        return StirlingGeneratorRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.stirling");
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
    public void draw(StirlingGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,11,0);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getEnergyPerTick() + " FE/t",-1,20, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getProcessTime() + " t",-1,28, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getProcessTime()/20 + " sec",-1,36, VEContainerScreen.GREY_TEXT_COLOUR);
    }

    public void ingredientHandler(StirlingGeneratorRecipe recipe, IIngredientAcceptor itemInputAcceptor) {
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM, Arrays.stream(recipe.ingredient.get().getItems()).toList());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, StirlingGeneratorRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 12, 1);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        this.ingredientHandler(recipe, itemInput);
    }
}