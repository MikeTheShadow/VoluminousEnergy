package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndustrialBlastingCategory implements IRecipeCategory<IndustrialBlastingRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public IndustrialBlastingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 42, 5, 120, 60).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build(); // 176, 0, 23, 17
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true); // 199,0,23,17
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.INDUSTRIAL_BLASTING_UID;
    }

    @Override
    public Class<? extends IndustrialBlastingRecipe> getRecipeClass() {
        return IndustrialBlastingRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Industrial Blasting";
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
    public void draw(IndustrialBlastingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,54, 12); // 24, 12
        emptyArrow.draw(matrixStack,54,12); // 24, 12
        slotDrawable.draw(matrixStack,30,1); // 2, 1
        slotDrawable.draw(matrixStack,30,19); // 2, 19
        slotDrawable.draw(matrixStack,78,10); // 48, 10
        slotDrawable.draw(matrixStack,5,10);

        Minecraft.getInstance().font.draw(matrixStack,
                recipe.getMinimumHeat() + " K (" + (recipe.getMinimumHeat() - 273) + " °C; " +
                        ((int) ((recipe.getMinimumHeat()-273) * 1.8)+32) + " °F)",
                1,45,0x606060);
    }

    @Override
    public void setIngredients(IndustrialBlastingRecipe recipe, IIngredients ingredients) {
        //ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
        //        .map(ingredient -> Arrays.asList(ingredient.getItems()))
        //        .collect(Collectors.toList()));

        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        ArrayList<ItemStack> allInputs = new ArrayList<>();
        //allInputs.addAll()

        //for (ItemStack testStack : recipe.getIngredient().getItems()) {
        //    testStack.setCount(64);
        //    allInputs.add(testStack);
        //   //ingredients.setInput(VanillaTypes.ITEM, testStack);

        for (Item item : recipe.ingredientListIncludingSeconds){ // Grab second items
            allInputs.add(new ItemStack(item, 64));
        }

        ingredients.setInputs(VanillaTypes.ITEM, allInputs);

        ArrayList<FluidStack> fluidStacks = new ArrayList<>();
        ForgeRegistries.FLUIDS.getValues().forEach(fluid -> {
            if (fluid.getAttributes().getTemperature() > recipe.getMinimumHeat()) fluidStacks.add(new FluidStack(fluid , Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()));
        });

        ingredients.setInputs(VanillaTypes.FLUID, fluidStacks);

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getResultItem()); // Normal output

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IndustrialBlastingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, false, 30, 1);
        itemStacks.init(2, false, 30, 19);
        itemStacks.init(1, false, 78, 10);
        fluidStacks.init(3, false, 6, 11);


        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getItems()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        List<ItemStack> seconds = new ArrayList<>();
        recipe.onlySecondInput.forEach(ingredient -> {
            seconds.add(new ItemStack(ingredient, recipe.getSecondInputAmount()));
        });

        itemStacks.set(2, seconds);

        // Valid fluid input
        ArrayList<FluidStack> validFluidList = new ArrayList<>();
        ForgeRegistries.FLUIDS.getValues().forEach(fluid -> {
            if (fluid.getAttributes().getTemperature() >= recipe.getMinimumHeat()){
                validFluidList.add(new FluidStack(fluid, Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()));
            }
        });
        fluidStacks.set(3,validFluidList);

        // Calculate output
        ItemStack tempStack = recipe.getResultItem(); // Get Item since amount will be wrong
        Item outputItem = tempStack.getItem();
        ItemStack jeiStack = new ItemStack(outputItem, recipe.getOutputAmount()); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);
    }

}
