package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.CrusherScreen;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class CrushingCategory implements IRecipeCategory<CrusherRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private IDrawable slotDrawable;
    private final IDrawable arrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType<>(VoluminousEnergyPlugin.CRUSHING_UID, CrusherRecipe.class);

    public CrushingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        background = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 68, 12, 40, 70).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.CRUSHER_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.crushing");
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
    public void draw(CrusherRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,10, 19);


        if (recipe.getResult(1) != null && recipe.getResult(1).getItem() != Items.AIR){ // Check RNG if it's not air
            int chance = (int)(recipe.getRNGOutputs()[1] * 100);
            int xPos = 20;
            if (chance < 100 && chance >= 10){
                xPos += 3;
            } else if (chance < 10){
                xPos += 5;
            }
            TextUtil.renderUnshadowedText(matrixStack,Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"), xPos,65, VEContainerScreen.GREY_TEXT_STYLE);
        }

    }

    public void ingredientHandler(CrusherRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor itemOutputAcceptor,
                                  IIngredientAcceptor itemRNGOutputAcceptor) {
        // Input
        ArrayList<ItemStack> inputStacks = new ArrayList<>();
        for (ItemStack itemStack : recipe.getIngredient(0).getItems()){
            itemStack.setCount(recipe.getIngredientCount(0));
            inputStacks.add(itemStack);
        }
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);

        // Output
        ItemStack resultStack = recipe.getResult(0).copy();
        resultStack.setCount(recipe.getResultCount(0));
        itemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, resultStack);

        ItemStack rngStack = recipe.getResult(1).copy();
        rngStack.setCount(recipe.getResultCount(1));
        itemRNGOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rngStack);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, @NotNull CrusherRecipe recipe, @NotNull IFocusGroup focusGroup) {
        // Input
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 12, 1);

        // Output
        IRecipeSlotBuilder primaryItemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 3, 46);
        IRecipeSlotBuilder rngItemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT,  21, 46);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        primaryItemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rngItemOutput.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, primaryItemOutput, rngItemOutput);
    }

}