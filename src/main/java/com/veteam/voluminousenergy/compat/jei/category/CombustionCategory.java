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
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public CombustionCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 64).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return new RecipeType(VoluminousEnergyPlugin.COMBUSTING_UID, CombustionGeneratorFuelRecipe.class);
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.COMBUSTING_UID;
    }

    @Deprecated
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

        // Volumetric Energy label
        Minecraft.getInstance().font.drawShadow(
                matrixStack,
                TextUtil.translateString("jei.voluminousenergy.volumetric_energy").copy().append(": "),
                16,
                4,
                VEContainerScreen.WHITE_TEXT_COLOUR
        );

        // Actual Volumetric Energy value + FE/B units added on the end
        Minecraft.getInstance().font.draw(
                matrixStack,
                recipe.getVolumetricEnergy() + " FE/B",
                35,
                16,
                VEContainerScreen.GREY_TEXT_COLOUR
        );

        slotDrawable.draw(matrixStack, 17, 35); // Fuel fluid
        slotDrawable.draw(matrixStack, 85, 35); // Oxidizer fluid

        Optional<FluidStack> oxiStack = slotsView.getSlotViews(RecipeIngredientRole.CATALYST).get(0).getDisplayedIngredient(VanillaTypes.FLUID);

        if (oxiStack.isPresent()){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipeWithoutLevel(oxiStack.get());

            int fePerTick = recipe.getVolumetricEnergy()/oxidizerRecipe.getProcessTime();
            TextComponent fePerTickComponent = new TextComponent(fePerTick+"");
            int x = 50;
            if (fePerTick < 100){
                x = 54;
            } else if (fePerTick > 999 && fePerTick < 10_000){
                x = 46;
            } else if (fePerTick > 9999){
                NumberUtil.numberToTextComponent4FE(fePerTick);
                x = 46;
            }

            Minecraft.getInstance().font.draw(matrixStack, fePerTickComponent, x, 45,VEContainerScreen.GREY_TEXT_COLOUR);
        }

        Minecraft.getInstance().font.drawShadow(matrixStack,"FE/t:",48,35,VEContainerScreen.WHITE_TEXT_COLOUR);

        Minecraft.getInstance().font.draw(
                matrixStack,
                TextUtil.translateString("jei.voluminousenergy.fluid.fuel").copy().append(":"),
                16,
                26,
                VEContainerScreen.GREY_TEXT_COLOUR
        );

        // Oxidizer Label
        Minecraft.getInstance().font.draw(
                matrixStack,
                TextUtil.translateString("jei.voluminousenergy.fluid.oxidizer").copy().append(":"),
                76,
                26,
                VEContainerScreen.GREY_TEXT_COLOUR
        );

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
        IRecipeSlotBuilder fuel = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 18, 36);
        IRecipeSlotBuilder oxidizer = recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 86, 36);

        this.ingredientHandler(recipe, fuel, oxidizer);
    }
}