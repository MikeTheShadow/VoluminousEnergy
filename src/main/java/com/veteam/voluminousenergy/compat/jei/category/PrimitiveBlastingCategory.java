package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PrimitiveBlastingCategory implements IRecipeCategory<PrimitiveBlastFurnaceRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.PRIMITIVE_BLASTING_UID, PrimitiveBlastFurnaceRecipe.class);

    public PrimitiveBlastingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 70, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.PRIMITIVE_BLASTING_UID;
    }

    @Deprecated
    @Override
    public Class<? extends PrimitiveBlastFurnaceRecipe> getRecipeClass() {
        return PrimitiveBlastFurnaceRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.primitive_blasting");
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
    public void draw(PrimitiveBlastFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,24, 12);
        emptyArrow.draw(matrixStack,24,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,10);
    }

    public void ingredientHandler(PrimitiveBlastFurnaceRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor itemOutputAcceptor) {

        // Input
        ArrayList<ItemStack> inputStacks = new ArrayList<>();
        for (ItemStack itemStack : recipe.ingredient.get().getItems()){
            itemStack.setCount(recipe.ingredientCount);
            inputStacks.add(itemStack);
        }

        itemInputAcceptor.addIngredients(VanillaTypes.ITEM, inputStacks);

        // Output
        ItemStack outputStack = recipe.result.copy();
        outputStack.setCount(recipe.getOutputAmount());

        itemOutputAcceptor.addIngredient(VanillaTypes.ITEM, outputStack);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, PrimitiveBlastFurnaceRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);

        // Output
        IRecipeSlotBuilder itemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49, 11);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        itemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, itemOutput);
    }
}