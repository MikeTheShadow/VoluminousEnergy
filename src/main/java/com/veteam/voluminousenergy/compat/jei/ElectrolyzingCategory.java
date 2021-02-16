package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ElectrolyzingCategory implements IRecipeCategory<ElectrolyzerRecipe> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public ElectrolyzingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/electrolyzer_gui.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.ELECTROLYZER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.ELECTROLYZING_UID;
    }

    @Override
    public Class<? extends ElectrolyzerRecipe> getRecipeClass() {
        return ElectrolyzerRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Electrolyzing";
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
    public void draw(ElectrolyzerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,25, 30);

        if (recipe.getRngItemSlot0() != null && recipe.getRngItemSlot0().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance0()*100);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack,chance + "%",74,26,0x606060);
        }

        if (recipe.getRngItemSlot1() != null && recipe.getRngItemSlot1().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance1()*100);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack,chance + "%",74,44,0x606060);
        }

        if (recipe.getRngItemSlot2() != null && recipe.getRngItemSlot2().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance2()*100);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack,chance + "%",74,62,0x606060);
        }

    }

    @Override
    public void setIngredients(ElectrolyzerRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getMatchingStacks()))
                .collect(Collectors.toList()));


        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        for (ItemStack testStack : recipe.getIngredient().getMatchingStacks()){
            testStack.setCount(64);
            ingredients.setInput(VanillaTypes.ITEM, testStack);
        }

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getRecipeOutput()); // Normal output

        if (recipe.getRngItemSlot0() != null && recipe.getRngItemSlot0().getItem() != Items.AIR){ // Check RNG 0 if it's not air
            outputStacks.add(recipe.getRngItemSlot0());
        }

        if (recipe.getRngItemSlot1() != null && recipe.getRngItemSlot1().getItem() != Items.AIR){ // Check RNG 1 if it's not air
            outputStacks.add(recipe.getRngItemSlot1());
        }

        if (recipe.getRngItemSlot2() != null && recipe.getRngItemSlot2().getItem() != Items.AIR){ // Check RNG 2 if it's not air
            outputStacks.add(recipe.getRngItemSlot2());
        }

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ElectrolyzerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 5, 20);
        itemStacks.init(1, false, 49, 2);

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getMatchingStacks()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        // Calculate output
        ItemStack tempStack = recipe.getRecipeOutput(); // Get Item since amount will be wrong
        Item outputItem = tempStack.getItem();
        ItemStack jeiStack = new ItemStack(outputItem, recipe.getOutputAmount()); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);

        // Calculate RNG stack, only if RNG stack exists
        if (recipe.getRngItemSlot0() != null && recipe.getRngItemSlot0().getItem() != Items.AIR){ // Don't create the slot if the slot will be empty!
            itemStacks.init(2, true, 49, 20);
            tempStack = recipe.getRngItemSlot0();
            Item rngItem = tempStack.getItem();
            ItemStack rngStack = new ItemStack(rngItem, recipe.getOutputRngAmount0());
            itemStacks.set(2, rngStack);
        }

        if (recipe.getRngItemSlot1() != null && recipe.getRngItemSlot1().getItem() != Items.AIR){ // Don't create the slot if the slot will be empty!
            itemStacks.init(3, true, 49, 38);
            tempStack = recipe.getRngItemSlot1();
            Item rngItem = tempStack.getItem();
            ItemStack rngStack = new ItemStack(rngItem, recipe.getOutputRngAmount1());
            itemStacks.set(3, rngStack);
        }

        if (recipe.getRngItemSlot2() != null && recipe.getRngItemSlot2().getItem() != Items.AIR){ // Don't create the slot if the slot will be empty!
            itemStacks.init(4, true, 49, 56);
            tempStack = recipe.getRngItemSlot2();
            Item rngItem = tempStack.getItem();
            ItemStack rngStack = new ItemStack(rngItem, recipe.getOutputRngAmount2());
            itemStacks.set(4, rngStack);
        }

        if (recipe.isUsesBucket()){
            itemStacks.init(5, true, 5, 38);
            itemStacks.set(5, new ItemStack(Items.BUCKET, 1));
        }
    }
}
