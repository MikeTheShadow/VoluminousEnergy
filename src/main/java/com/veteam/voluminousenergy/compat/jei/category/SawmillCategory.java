package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.VEFluidSawmillRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RegistryLookups;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class SawmillCategory implements IRecipeCategory<VEFluidSawmillRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.SAWMILL_UID, VEFluidSawmillRecipe.class);
    //protected static ArrayList<Pair<ItemStack, ItemStack>> logPlankPairList = new ArrayList<>();


    public SawmillCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 86, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.SAWMILL_BLOCK.get()));
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.sawmilling");
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
    public void draw(VEFluidSawmillRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,1);
        slotDrawable.draw(matrixStack,48,19);
        slotDrawable.draw(matrixStack,66,9);
        arrow.draw(matrixStack,24, 11);
        emptyArrow.draw(matrixStack,24,11);
    }

    public void ingredientHandler(VEFluidSawmillRecipe recipe,
                                  IIngredientAcceptor inputItemAcceptor,
                                  IIngredientAcceptor primaryItemOutputAcceptor,
                                  IIngredientAcceptor secondaryItemOutputAcceptor,
                                  IIngredientAcceptor fluidOutputAcceptor) {
        if (recipe.isLogRecipe() && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){

            AtomicReference<ArrayList<ItemStack>> atomicLogStacks = new AtomicReference<>(new ArrayList<>());
            AtomicReference<ArrayList<ItemStack>> atomicPlankStacks = new AtomicReference<>(new ArrayList<>());

            // Calculate Logs and Planks based on registry
            ForgeRegistries.ITEMS.getValues().parallelStream().forEach(registeredItem -> {
                if (RegistryLookups.lookupItem(registeredItem).getPath().contains("log")){
                    atomicLogStacks.get().add(new ItemStack(registeredItem, Config.SAWMILL_LOG_CONSUMPTION_RATE.get()));
                } else if (RegistryLookups.lookupItem(registeredItem).getPath().contains("plank")) {
                    atomicPlankStacks.get().add(new ItemStack(registeredItem, Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get()));
                }
            });
            inputItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, atomicLogStacks.get());
            primaryItemOutputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, atomicPlankStacks.get());


            // Secondary Output
            ResourceLocation secondOutputItemResourceLocation = new ResourceLocation(Config.SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION.get());
            Item secondOutput = ForgeRegistries.ITEMS.getValue(secondOutputItemResourceLocation);
            if (secondOutput != null){
                secondaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(secondOutput, Config.SAWMILL_SECOND_OUTPUT_COUNT.get()));
            }

            // Fluid Output
            ResourceLocation fluidLocation = new ResourceLocation(Config.SAWMILL_FLUID_LOCATION.get());
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
            if (outputFluid != null){
                fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(outputFluid, Config.SAWMILL_FLUID_AMOUNT.get()));
            }
        } else if (!recipe.isLogRecipe()) {
            // Primary Input (Typically logs)
            ArrayList<ItemStack> inputStacks = new ArrayList<>(Arrays.asList(recipe.getIngredient(0).getItems()));
            inputItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);

            // First Item Output (Typically Planks)
            ItemStack resultStack = recipe.getResult(0).copy();
            primaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, resultStack);

            // Second Item Output
            ItemStack secondOutputStack = recipe.getResult(1).copy();
            secondaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, secondOutputStack);

            // Fluid Output
            fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid(0).copy());
        }

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, VEFluidSawmillRecipe recipe, IFocusGroup focusGroup) {
        IRecipeSlotBuilder inputItem = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);

        IRecipeSlotBuilder primaryOutputItem = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49,2);
        IRecipeSlotBuilder secondaryOutputItem = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49, 20);

        IRecipeSlotBuilder fluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 67, 10);

        this.ingredientHandler(recipe, inputItem, primaryOutputItem, secondaryOutputItem, fluidOutput);
    }

}