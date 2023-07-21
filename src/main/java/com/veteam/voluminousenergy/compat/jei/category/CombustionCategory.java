package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.util.NumberUtil;
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
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
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
    public @NotNull Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.combustion");
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
    public void draw(CombustionGeneratorFuelRecipe recipe, IRecipeSlotsView slotsView, @NotNull GuiGraphics matrixStack, double mouseX, double mouseY){

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

            List<VEFluidRecipe> recipes = RecipeCache.getFluidRecipesWithoutLevelDangerous(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE);

            CombustionGeneratorOxidizerRecipe oxidizerRecipe = null;

            for(VEFluidRecipe veFluidRecipe : recipes) {
                if(veFluidRecipe instanceof CombustionGeneratorOxidizerRecipe cor) {
                    if(cor.getFluidIngredient(0).test(oxiStack.get())) {
                        oxidizerRecipe = cor;
                    }
                }
            }
            if(oxidizerRecipe == null) throw new IllegalStateException("No matching oxidizer for category: " + this.getClass().getName());

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
        for (VEFluidRecipe oxidizerRecipe : RecipeCache.getFluidRecipesWithoutLevelDangerous(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE)) {
            oxiStacks.addAll(Arrays.asList(oxidizerRecipe.getFluidIngredient(0).getFluids()));
        }

        oxidizerAcceptor.addIngredients(ForgeTypes.FLUID_STACK, oxiStacks);


    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, @NotNull CombustionGeneratorFuelRecipe recipe, @NotNull IFocusGroup focusGroup) {
        // Init
        IRecipeSlotBuilder fuel = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 18, 36);
        IRecipeSlotBuilder oxidizer = recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 86, 36);

        this.ingredientHandler(recipe, fuel, oxidizer);
    }
}