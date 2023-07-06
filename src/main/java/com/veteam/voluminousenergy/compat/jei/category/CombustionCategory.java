package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.COMBUSTING_UID, CombustionGeneratorFuelRecipe.class);

    public CombustionCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 64).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
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
    public void draw(CombustionGeneratorFuelRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics matrixStack, double mouseX, double mouseY){

        // Volumetric Energy label
        TextUtil.renderShadowedText(
                matrixStack,
                Minecraft.getInstance().font,
                TextUtil.translateString("jei.voluminousenergy.volumetric_energy").copy().append(": "),
                16,
                4,
                VEContainerScreen.WHITE_TEXT_STYLE
        );

        // Actual Volumetric Energy value + FE/B units added on the end
        TextUtil.renderUnshadowedText(
                matrixStack,
                Minecraft.getInstance().font,
                Component.nullToEmpty(recipe.getVolumetricEnergy() + " FE/B"),
                35,
                16,
                VEContainerScreen.GREY_TEXT_STYLE
        );

        slotDrawable.draw(matrixStack, 17, 35); // Fuel fluid
        slotDrawable.draw(matrixStack, 85, 35); // Oxidizer fluid

        Optional<FluidStack> oxiStack = slotsView.getSlotViews(RecipeIngredientRole.CATALYST).get(0).getDisplayedIngredient(ForgeTypes.FLUID_STACK);

        if (oxiStack.isPresent()){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipeWithoutLevel(oxiStack.get());

            int fePerTick = recipe.getVolumetricEnergy()/oxidizerRecipe.getProcessTime();
            Component fePerTickComponent = Component.nullToEmpty(fePerTick+"");
            int x = 50;
            if (fePerTick < 100){
                x = 54;
            } else if (fePerTick > 999 && fePerTick < 10_000){
                x = 46;
            } else if (fePerTick > 9999){
                NumberUtil.numberToTextComponent4FE(fePerTick);
                x = 46;
            }

            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, fePerTickComponent,  x, 45,VEContainerScreen.GREY_TEXT_STYLE);
        }

        TextUtil.renderShadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty("FE/t:"), 48,35,VEContainerScreen.WHITE_TEXT_STYLE);

        TextUtil.renderUnshadowedText(
                matrixStack,
                Minecraft.getInstance().font,
                TextUtil.translateString("jei.voluminousenergy.fluid.fuel").copy().append(":"),
                16,
                26,
                VEContainerScreen.GREY_TEXT_STYLE
        );

        // Oxidizer Label
        TextUtil.renderUnshadowedText(
                matrixStack,
                Minecraft.getInstance().font,
                TextUtil.translateString("jei.voluminousenergy.fluid.oxidizer").copy().append(":"),
                76,
                26,
                VEContainerScreen.GREY_TEXT_STYLE
        );

    }

    public void ingredientHandler(CombustionGeneratorFuelRecipe recipe,
                                  IIngredientAcceptor fuelAcceptor,
                                  IIngredientAcceptor oxidizerAcceptor) {

        List<FluidStack> inputList = new ArrayList<>(Arrays.asList(recipe.getFluidIngredient(0).getFluids()));
        fuelAcceptor.addIngredients(ForgeTypes.FLUID_STACK, inputList);

        ArrayList<FluidStack> oxiStacks = new ArrayList<>();
        for (CombustionGeneratorOxidizerRecipe oxidizerRecipe : CombustionGeneratorOxidizerRecipe.oxidizerRecipes) {
            for (Fluid fluid : oxidizerRecipe.rawFluidInputList.get()){
                oxiStacks.add(new FluidStack(fluid, 1000));
            }
        }

        oxidizerAcceptor.addIngredients(ForgeTypes.FLUID_STACK, oxiStacks);


    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CombustionGeneratorFuelRecipe recipe, IFocusGroup focusGroup) {
        // Init
        IRecipeSlotBuilder fuel = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 18, 36);
        IRecipeSlotBuilder oxidizer = recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 86, 36);

        this.ingredientHandler(recipe, fuel, oxidizer);
    }
}