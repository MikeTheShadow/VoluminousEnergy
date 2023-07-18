package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CentrifugalSeparatorRecipe;
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
import java.util.Collections;

public class CentrifugalSeparationCategory implements IRecipeCategory<CentrifugalSeparatorRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.CENTRIFUGAL_SEPARATION_UID, CentrifugalSeparatorRecipe.class);

    public CentrifugalSeparationCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get()));
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
        return TextUtil.translateString("jei.voluminousenergy.centrifugal_separation");
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
    public void draw(CentrifugalSeparatorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,25, 30);
        emptyArrow.draw(matrixStack,25,30);
        slotDrawable.draw(matrixStack,5,20); // Input
        slotDrawable.draw(matrixStack,5,38); // Bucket
        slotDrawable.draw(matrixStack,49,2); // First Output
        slotDrawable.draw(matrixStack,49,20); // First RNG
        slotDrawable.draw(matrixStack,49,38); // Second RNG
        slotDrawable.draw(matrixStack,49,56); // Third RNG

        if (recipe.getResult(1) != null && recipe.getResult(1).getItem() != Items.AIR){
            int chance = (int)(recipe.getRNGOutputs()[1] * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 26, VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getResult(2) != null && recipe.getResult(2).getItem() != Items.AIR){
            int chance = (int)(recipe.getRNGOutputs()[2] * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 44, VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getResult(3) != null && recipe.getResult(3).getItem() != Items.AIR){
            int chance = (int)(recipe.getRNGOutputs()[3] * 100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 62, VEContainerScreen.GREY_TEXT_STYLE);
        }

    }

    public void ingredientHandler(CentrifugalSeparatorRecipe recipe,
                               IIngredientAcceptor itemInputAcceptor,
                               IIngredientAcceptor bucketInputAcceptor,
                               IIngredientAcceptor primaryOutputAcceptor,
                               IIngredientAcceptor rng0OutputAcceptor,
                               IIngredientAcceptor rng1OutputAcceptor,
                               IIngredientAcceptor rng2OutputAcceptor) {

        // Input
        ArrayList<ItemStack> inputStacks = new ArrayList<>();
        for (ItemStack itemStack : recipe.getIngredient(0).getItems()){
            itemStack.setCount(recipe.getIngredientCount(0));
            inputStacks.add(itemStack);
        }

        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);



        if (!recipe.getIngredient(1).isEmpty()){
            ItemStack[] buckets = recipe.getIngredient(1).getItems();
            bucketInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(buckets).toList());
        } else {
            bucketInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(new ItemStack(Items.AIR,1)));
        }

        // Output --> ItemStacks here are not guaranteed to have correct amount; must do so manually
        ItemStack primaryOutputStack = recipe.getResult(0).copy();
        primaryOutputStack.setCount(recipe.getResultCount(0));
        primaryOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, primaryOutputStack);

        ItemStack rng0 = recipe.getResult(1).copy();
        rng0.setCount(rng0.getCount());
        rng0OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng0);

        ItemStack rng1 = recipe.getResult(2).copy();
        rng1.setCount(rng1.getCount());
        rng1OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng1);

        ItemStack rng2 = recipe.getResult(3).copy();
        rng2.setCount(rng2.getCount());
        rng2OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng2);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CentrifugalSeparatorRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT,6,21);
        IRecipeSlotBuilder bucketInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT,6, 39);

        // Outputs
        IRecipeSlotBuilder firstOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50,3);
        IRecipeSlotBuilder secondOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 21); // RNG 1
        IRecipeSlotBuilder thirdOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 39); // RNG 2
        IRecipeSlotBuilder fourthOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50, 57); // RNG 3

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        bucketInput.setSlotName(TextUtil.TRANSLATED_BUCKET_SLOT.getString());

        firstOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        secondOutput.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        thirdOutput.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        fourthOutput.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, bucketInput, firstOutput, secondOutput, thirdOutput, fourthOutput);
    }
}