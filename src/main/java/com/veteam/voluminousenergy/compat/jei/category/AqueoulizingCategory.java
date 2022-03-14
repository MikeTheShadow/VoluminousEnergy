package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
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
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AqueoulizingCategory implements IRecipeCategory<AqueoulizerRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public AqueoulizingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.AQUEOULIZER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return new RecipeType(VoluminousEnergyPlugin.AQUEOULIZING_UID,AqueoulizerRecipe.class);
    }

    @Deprecated
    @Override
    public ResourceLocation getUid() {
        return VoluminousEnergyPlugin.AQUEOULIZING_UID;
    }

    @Deprecated
    @Override
    public Class<? extends AqueoulizerRecipe> getRecipeClass() {
        return AqueoulizerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.aqueoulizing");
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
    public void draw(AqueoulizerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,48, 12);
        emptyArrow.draw(matrixStack,48,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,24,10);
        slotDrawable.draw(matrixStack,72,10);

        Minecraft.getInstance().font.draw(matrixStack,"mB:", 2, 32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getInputAmount() + "", 24, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getOutputAmount() + "", 72, 32,VEContainerScreen.GREY_TEXT_COLOUR);
    }



    // NOTE: Needs to be recipe specific; refactoring of setIngredients, which is no longer used
    public void ingredientHandler(AqueoulizerRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor fluidOutputAcceptor) {

        // INPUT
        List<ItemStack> inputList = new ArrayList<>();
        for (ItemStack testStack : recipe.getIngredient().getItems()){
            testStack.setCount(recipe.getIngredientCount());
            inputList.add(testStack);
        }
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM, inputList);

        fluidInputAcceptor.addIngredients(VanillaTypes.FLUID, recipe.fluidInputList.get());

        // OUTPUT
        List<FluidStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getOutputFluid()); // Normal output
        fluidOutputAcceptor.addIngredients(VanillaTypes.FLUID, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, AqueoulizerRecipe recipe, IFocusGroup focusGroup) {

        // init
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11); // Coords reused from old init call
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 25, 11);

        // Outputs
        IRecipeSlotBuilder fluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);

        // Setup
        // Inputs
        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        this.ingredientHandler(recipe, itemInput, fluidInput, fluidOutput);

        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        fluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());
    }
}