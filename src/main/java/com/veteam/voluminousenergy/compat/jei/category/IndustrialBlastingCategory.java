package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class IndustrialBlastingCategory implements IRecipeCategory<IndustrialBlastingRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.INDUSTRIAL_BLASTING_UID, IndustrialBlastingRecipe.class);

    public IndustrialBlastingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 42, 5, 120, 60).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build(); // 176, 0, 23, 17
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true); // 199,0,23,17
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.industrial_blasting");
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
    public void draw(IndustrialBlastingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,54, 12); // 24, 12
        emptyArrow.draw(matrixStack,54,12); // 24, 12
        slotDrawable.draw(matrixStack,30,1); // 2, 1
        slotDrawable.draw(matrixStack,30,19); // 2, 19
        slotDrawable.draw(matrixStack,78,10); // 48, 10
        slotDrawable.draw(matrixStack,5,10);

        TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font,
                recipe.getMinimumHeat() + " K (" + (recipe.getMinimumHeat() - 273) + " \u00B0C; " +
                        ((int) ((recipe.getMinimumHeat()-273) * 1.8)+32) + " \u00B0F)",
                1,45, VEContainerScreen.GREY_TEXT_STYLE);
    }

    public void ingredientHandler(IndustrialBlastingRecipe recipe,
                                  IIngredientAcceptor firstInputAcceptor,
                                  IIngredientAcceptor secondInputAcceptor,
                                  IIngredientAcceptor heatFluidAcceptor,
                                  IIngredientAcceptor outputItemAcceptor) {
        // Inputs
        ArrayList<ItemStack> firstInputStacks = new ArrayList<>();
        for (Item inputItem : recipe.getFirstInputAsList()) {
            firstInputStacks.add(new ItemStack(inputItem, recipe.getIngredientCount()));
        }
        firstInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, firstInputStacks);

        AtomicReference<ArrayList<ItemStack>> atomicSecondInputStack = new AtomicReference(new ArrayList<>());
        recipe.onlySecondInput.get().parallelStream().forEach(item -> {
            ItemStack secondInputStack = new ItemStack(item, recipe.getSecondInputAmount());
            atomicSecondInputStack.get().add(secondInputStack);
        });
        secondInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, atomicSecondInputStack.get());

        heatFluidAcceptor.addIngredients(ForgeTypes.FLUID_STACK, RecipeUtil.getFluidsHotEnoughForIndustrialBlastingRecipe(recipe));

        // Output
        ItemStack resultStack = recipe.result.copy();
        resultStack.setCount(recipe.getOutputAmount());
        outputItemAcceptor.addIngredient(VanillaTypes.ITEM_STACK, resultStack);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, IndustrialBlastingRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder firstItemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 31, 2);
        IRecipeSlotBuilder secondItemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 31, 20);

        IRecipeSlotBuilder heatFluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 6, 11);

        // Output
        IRecipeSlotBuilder itemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 79, 11);

        firstItemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        secondItemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());

        heatFluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());

        itemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());

        this.ingredientHandler(recipe, firstItemInput, secondItemInput, heatFluidInput, itemOutput);
    }

}