package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HydroponicIncubatorCategory implements IRecipeCategory<HydroponicIncubatorRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.HYDROPONIC_INCUBATOR_UID,HydroponicIncubatorRecipe.class);

    public HydroponicIncubatorCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 4, 4, 156, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR_BLOCK));
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
    public ResourceLocation getUid() {
        return VoluminousEnergyPlugin.HYDROPONIC_INCUBATOR_UID;
    }

    @Deprecated
    @Override
    public Class<? extends HydroponicIncubatorRecipe> getRecipeClass() {
        return HydroponicIncubatorRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.hydroponic_incubating");
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
    public void draw(HydroponicIncubatorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,48, 12);
        emptyArrow.draw(matrixStack,48,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,24,10);
        slotDrawable.draw(matrixStack,72,10); // Primary Output
        slotDrawable.draw(matrixStack,94,10); // RNG0 output
        slotDrawable.draw(matrixStack,116,10); // RNG1 output
        slotDrawable.draw(matrixStack,138,10); // RNG2 output

        Minecraft.getInstance().font.draw(matrixStack,"mB:", 2, 32, VEContainerScreen.GREY_TEXT_COLOUR);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getInputAmount() + "", 24, 32,VEContainerScreen.GREY_TEXT_COLOUR);

        if (recipe.getChance0() > 0) {
            int chance = (int) (recipe.getChance0()*100);
            int xPos = calculateXPos(94, chance);

            Minecraft.getInstance().font.draw(matrixStack,chance + "%", xPos, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        }

        if (recipe.getChance1() > 0) {
            int chance = (int) (recipe.getChance1()*100);
            int xPos = calculateXPos(116, chance);

            Minecraft.getInstance().font.draw(matrixStack,chance + "%", xPos, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        }

        if (recipe.getChance2() > 0) {
            int chance = (int) (recipe.getChance2()*100);
            int xPos = calculateXPos(138, chance);

            Minecraft.getInstance().font.draw(matrixStack,chance + "%", xPos, 32,VEContainerScreen.GREY_TEXT_COLOUR);
        }
    }

    private int calculateXPos(int xPos, int chance) {
        if (chance < 100 && chance > 9) {
            xPos += 3;
        } else if (chance < 10) {
            xPos += 5;
        }
        return xPos;
    }

    // NOTE: Needs to be recipe specific; refactoring of setIngredients, which is no longer used
    public void ingredientHandler(HydroponicIncubatorRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor fluidInputAcceptor,
                                  IIngredientAcceptor primaryOutputAcceptor,
                                  IIngredientAcceptor rng0OutputAccepter,
                                  IIngredientAcceptor rng1OutputAccepter,
                                  IIngredientAcceptor rng2OutputAccepter) {

        // INPUT
        List<ItemStack> inputList = new ArrayList<>();
        for (ItemStack testStack : recipe.getIngredient().getItems()){
            testStack.setCount(recipe.getIngredientCount());
            inputList.add(testStack);
        }
        itemInputAcceptor.addIngredients(VanillaTypes.ITEM, inputList);

        fluidInputAcceptor.addIngredients(VanillaTypes.FLUID, recipe.fluidInputList.get());

        // OUTPUT
        ItemStack primaryOutputStack = recipe.getResult().copy();
        primaryOutputStack.setCount(recipe.getOutputAmount());
        primaryOutputAcceptor.addIngredient(VanillaTypes.ITEM, primaryOutputStack);

        ItemStack rng0Stack = recipe.getRngItemSlot0().copy();
        rng0Stack.setCount(recipe.getOutputRngAmount0());
        rng0OutputAccepter.addIngredient(VanillaTypes.ITEM, rng0Stack);

        ItemStack rng1Stack = recipe.getRngItemSlot1().copy();
        rng1Stack.setCount(recipe.getOutputRngAmount1());
        rng1OutputAccepter.addIngredient(VanillaTypes.ITEM, rng1Stack);

        ItemStack rng2Stack = recipe.getRngItemSlot2().copy();
        rng2Stack.setCount(recipe.getOutputRngAmount2());
        rng2OutputAccepter.addIngredient(VanillaTypes.ITEM, rng2Stack);

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, HydroponicIncubatorRecipe recipe, IFocusGroup focusGroup) {

        // init
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11); // Coords reused from old init call
        IRecipeSlotBuilder fluidInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 25, 11);

        // Outputs
        IRecipeSlotBuilder primaryOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 11);
        IRecipeSlotBuilder rng0Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 11);
        IRecipeSlotBuilder rng1Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 117, 11);
        IRecipeSlotBuilder rng2Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 139, 11);

        // Setup
        // Inputs
        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        fluidInput.setSlotName(TextUtil.TRANSLATED_INPUT_TANK.getString());
        primaryOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng0Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng1Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng2Output.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, fluidInput, primaryOutput, rng0Output, rng1Output, rng2Output);
    }
}