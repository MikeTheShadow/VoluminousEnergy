package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.RecipeUtil;
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
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Optional;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public CombustionCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.COMBUSTING_UID;
    }

    @Override
    public Class<? extends CombustionGeneratorFuelRecipe> getRecipeClass() {
        return CombustionGeneratorFuelRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.combustion");
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
    public void draw(CombustionGeneratorFuelRecipe recipe, IRecipeSlotsView slotsView, PoseStack matrixStack, double mouseX, double mouseY){

        Minecraft.getInstance().font.draw(matrixStack,"Volumetric Energy: ",31,4, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getVolumetricEnergy() + " FE",42,16, VEContainerScreen.GREY_TEXT_COLOUR);
        slotDrawable.draw(matrixStack,11,0); // Volumetric Energy
        slotDrawable.draw(matrixStack, 11, 35); // Fuel fluid
        slotDrawable.draw(matrixStack, 95, 35); // Oxidizer fluid

        Optional<FluidStack> oxiStack = slotsView.getSlotViews(RecipeIngredientRole.CATALYST).get(0).getDisplayedIngredient(VanillaTypes.FLUID);

        if (oxiStack.isPresent()){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipeWithoutLevel(oxiStack.get());

            int fePerTick = recipe.getVolumetricEnergy()/oxidizerRecipe.getProcessTime();
            TextComponent fePerTickComponent = new TextComponent(fePerTick+"");
            int x = 50;
            if (fePerTick < 100){
                x = 54;
            } else if (fePerTick > 999){
                x = 46;
            } else if (fePerTick > 9999){
                NumberUtil.numberToTextComponent4FE(fePerTick);
            }

            Minecraft.getInstance().font.drawShadow(matrixStack, fePerTickComponent, x, 44,VEContainerScreen.WHITE_TEXT_COLOUR);
        }

        Minecraft.getInstance().font.drawShadow(matrixStack,"FE/t:",48,36,VEContainerScreen.WHITE_TEXT_COLOUR);

        Minecraft.getInstance().font.draw(matrixStack, "Fuel:", 10, 26, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack, "Oxidizer:", 88,26, VEContainerScreen.GREY_TEXT_COLOUR);

    }

    public void ingredientHandler(CombustionGeneratorFuelRecipe recipe,
                                  IIngredientAcceptor fuelAcceptor,
                                  IIngredientAcceptor oxidizerAcceptor) {

        ArrayList<FluidStack> fuelStacks = new ArrayList<>();
        for (Fluid fluid : recipe.rawFluidInputList.get()){
            fuelStacks.add(new FluidStack(fluid, 1000));
        }

        fuelAcceptor.addIngredients(VanillaTypes.FLUID, fuelStacks);

        ArrayList<FluidStack> oxiStacks = new ArrayList<>();
        for (CombustionGeneratorOxidizerRecipe oxidizerRecipe : CombustionGeneratorOxidizerRecipe.oxidizerRecipes) {
            for (Fluid fluid : oxidizerRecipe.rawFluidInputList.get()){
                oxiStacks.add(new FluidStack(fluid, 1000));
            }
        }

        oxidizerAcceptor.addIngredients(VanillaTypes.FLUID, oxiStacks);


    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CombustionGeneratorFuelRecipe recipe, IFocusGroup focusGroup) {
        // Init
        IRecipeSlotBuilder fuel = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 12, 36);
        IRecipeSlotBuilder oxidizer = recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 96, 36);

        this.ingredientHandler(recipe, fuel, oxidizer);
    }
}