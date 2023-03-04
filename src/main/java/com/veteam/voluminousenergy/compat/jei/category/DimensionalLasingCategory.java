package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class DimensionalLasingCategory implements IRecipeCategory<DimensionalLaserRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.DIMENSIONAL_LASER_UID, DimensionalLaserRecipe.class);

    public DimensionalLasingCategory(IGuiHelper guiHelper) {
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        ResourceLocation dimensionalLaserGUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/dimensional_laser_gui.png");
        background = guiHelper.drawableBuilder(GUI, 42, 5, 128, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.DIMENSIONAL_LASER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(dimensionalLaserGUI, 97, 34, 15, 16).build();
        emptyArrow = guiHelper.drawableBuilder(dimensionalLaserGUI,176,0,15,17).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Deprecated
    @Override
    public ResourceLocation getUid() {
        return VoluminousEnergyPlugin.DIMENSIONAL_LASER_UID;
    }

    @Deprecated
    @Override
    public Class<? extends DimensionalLaserRecipe> getRecipeClass() {
        return DimensionalLaserRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.dimensional_lasing");
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
    public void draw(DimensionalLaserRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,9, 4);
        emptyArrow.draw(matrixStack,9,4);
        slotDrawable.draw(matrixStack,8,22);

        int xPos = 36;
        Minecraft.getInstance().font.draw(matrixStack, "C: "
                + recipe.getFluidClimateSpawn().getContinentalnessClimateParameter().getA()
                + " ~ "
                + recipe.getFluidClimateSpawn().getContinentalnessClimateParameter().getB()
                , xPos, -1, VEContainerScreen.GREY_TEXT_COLOUR
        );

        Minecraft.getInstance().font.draw(matrixStack, "E: "
                        + recipe.getFluidClimateSpawn().getErosionClimateParameter().getA()
                        + " ~ "
                        + recipe.getFluidClimateSpawn().getErosionClimateParameter().getB()
                , xPos, 9, VEContainerScreen.GREY_TEXT_COLOUR
        );

        Minecraft.getInstance().font.draw(matrixStack, "H: "
                        + recipe.getFluidClimateSpawn().getHumidityClimateParameter().getA()
                        + " ~ "
                        + recipe.getFluidClimateSpawn().getHumidityClimateParameter().getB()
                , xPos, 19, VEContainerScreen.GREY_TEXT_COLOUR
        );


        Minecraft.getInstance().font.draw(matrixStack, "T: "
                        + recipe.getFluidClimateSpawn().getTemperatureClimateParameter().getA()
                        + " ~ "
                        + recipe.getFluidClimateSpawn().getTemperatureClimateParameter().getB()
                , xPos, 29, VEContainerScreen.GREY_TEXT_COLOUR
        );

    }

    public void ingredientHandler(DimensionalLaserRecipe recipe, IIngredientAcceptor fluidOutputAcceptor) {
        fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.getFluidClimateSpawn().getFluid(), 1000));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, DimensionalLaserRecipe recipe, IFocusGroup focusGroup) {

        IRecipeSlotBuilder fluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 9, 23);


        // Setup
        // Inputs
        fluidOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_TANK.getString());

        this.ingredientHandler(recipe, fluidOutput);
    }
}
