package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class HydroponicIncubatorCategory implements IRecipeCategory<HydroponicIncubatorRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    private final IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.HYDROPONIC_INCUBATOR_UID,HydroponicIncubatorRecipe.class);

    public HydroponicIncubatorCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 4, 4, 156, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get()));
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
        return TextUtil.translateString("jei.voluminousenergy.hydroponic_incubating");
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
    public void draw(HydroponicIncubatorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,48, 12);
        emptyArrow.draw(matrixStack,48,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,24,10);
        slotDrawable.draw(matrixStack,72,10); // Primary Output
        slotDrawable.draw(matrixStack,94,10); // RNG0 output
        slotDrawable.draw(matrixStack,116,10); // RNG1 output
        slotDrawable.draw(matrixStack,138,10); // RNG2 output

        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, "mB:",  2, 32, VEContainerScreen.GREY_TEXT_STYLE);
        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, recipe.getFluidIngredientAmount(0) + "",  24, 32,VEContainerScreen.GREY_TEXT_STYLE);

        if (recipe.getChance0() > 0) {
            int chance = (int) (recipe.getChance0()*100);
            int xPos = calculateXPos(94, chance);

            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, chance + "%",  xPos, 32,VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getChance1() > 0) {
            int chance = (int) (recipe.getChance1()*100);
            int xPos = calculateXPos(116, chance);

            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, chance + "%",  xPos, 32,VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getChance2() > 0) {
            int chance = (int) (recipe.getChance2()*100);
            int xPos = calculateXPos(138, chance);

            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, chance + "%",  xPos, 32,VEContainerScreen.GREY_TEXT_STYLE);
        }
    }

    private int calculateXPos(int xPos, int chance) {
        if (chance < 100 && chance > 9) {
            xPos += 3;
        } else if (chance < 10) {
            xPos += 5;
        }
        return xPos;
    }

    // NOTE: Needs to be recipe specific; refactoring of setIngredients, which is no longer used
    public void ingredientHandler(HydroponicIncubatorRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor primaryOutputAcceptor,
                                  IIngredientAcceptor rng0OutputAccepter,
                                  IIngredientAcceptor rng1OutputAccepter,
                                  IIngredientAcceptor rng2OutputAccepter) {

        // INPUT
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.getItemIngredient(0).getItems()));
        fluidInputAcceptor.addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredient(0).getFluids()));

        // OUTPUT
        primaryOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutputItem(0));
        rng0OutputAccepter.addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutputItem(1));
        rng1OutputAccepter.addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutputItem(2));
        rng2OutputAccepter.addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutputItem(3));

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, @NotNull HydroponicIncubatorRecipe recipe, IFocusGroup focusGroup) {

        // init
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11); // Coords reused from old init call
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 25, 11);

        // Outputs
        IRecipeSlotBuilder primaryOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);
        IRecipeSlotBuilder rng0Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 11);
        IRecipeSlotBuilder rng1Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 117, 11);
        IRecipeSlotBuilder rng2Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 139, 11);

        // Setup
        // Inputs
        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        primaryOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng0Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng1Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng2Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, fluidInput, primaryOutput, rng0Output, rng1Output, rng2Output);
    }
}