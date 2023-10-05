package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElectrolyzingCategory implements IRecipeCategory<ElectrolyzerRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.ELECTROLYZING_UID, ElectrolyzerRecipe.class);

    public ElectrolyzingCategory(IGuiHelper guiHelper) {
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.ELECTROLYZER_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI, 199, 0, 23, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);

    }

    @Override
    public @NotNull RecipeType getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.electrolyzing");
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
    public void draw(ElectrolyzerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 25, 30);
        emptyArrow.draw(matrixStack, 25, 30);
        slotDrawable.draw(matrixStack, 5, 20); // Input
        slotDrawable.draw(matrixStack, 5, 38); // Bucket
        slotDrawable.draw(matrixStack, 49, 2); // First Output
        slotDrawable.draw(matrixStack, 49, 20); // First RNG
        slotDrawable.draw(matrixStack, 49, 38); // Second RNG
        slotDrawable.draw(matrixStack, 49, 56); // Third RNG

        List<Float> chances = recipe.getRNGOutputs();

        if (recipe.getResult(1) != null && recipe.getResult(1).getItem() != Items.AIR) {
            int chance = (int) (chances.get(1) * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"), 74, 26, VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getResult(2) != null && recipe.getResult(2).getItem() != Items.AIR) {
            int chance = (int) (chances.get(2) * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"), 74, 44, VEContainerScreen.GREY_TEXT_STYLE);

        }

        if (recipe.getResult(3) != null && recipe.getResult(3).getItem() != Items.AIR) {
            int chance = (int) (chances.get(3) * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"), 74, 62, VEContainerScreen.GREY_TEXT_STYLE);
        }

    }

    public void ingredientHandler(ElectrolyzerRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor bucketInputAcceptor,
                                  IIngredientAcceptor primaryOutputAcceptor,
                                  IIngredientAcceptor rng0OutputAcceptor,
                                  IIngredientAcceptor rng1OutputAcceptor,
                                  IIngredientAcceptor rng2OutputAcceptor) {
        ArrayList<ItemStack> inputStacks = new ArrayList<>(Arrays.asList(recipe.getIngredient(0).getItems()));

        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);

        if (!recipe.getIngredient(1).isEmpty()) {
            ItemStack bucketStack = new ItemStack(Items.BUCKET, recipe.getIngredient(1).getItems()[0].getCount());
            bucketInputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, bucketStack);
        }

        // Output --> ItemStacks here are not guaranteed to have correct amount; must do so manually
        ItemStack primaryOutputStack = recipe.getResult(0).copy();
        primaryOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, primaryOutputStack);

        ItemStack rng0 = recipe.getResult(1).copy();
        rng0OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng0);

        ItemStack rng1 = recipe.getResult(2).copy();
        rng1OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng1);

        ItemStack rng2 = recipe.getResult(3).copy();
        rng2OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng2);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, ElectrolyzerRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 6, 21);
        IRecipeSlotBuilder bucketInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 6, 39);

        // Output
        IRecipeSlotBuilder itemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 3);
        IRecipeSlotBuilder rng0Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 21);
        IRecipeSlotBuilder rng1Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 39);
        IRecipeSlotBuilder rng2Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 57);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        bucketInput.setSlotName(TextUtil.TRANSLATED_BUCKET_SLOT.getString());
        itemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng0Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        rng1Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        rng2Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, bucketInput, itemOutput, rng0Output, rng1Output, rng2Output);
    }
}