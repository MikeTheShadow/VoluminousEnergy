package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
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
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> { //TODO: GUI (file and alignment)

    private static final Logger LOGGER = LogManager.getLogger();

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public CombustionCategory(IGuiHelper guiHelper){
        LOGGER.debug("CombustionCategory Called!");
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        //arrow = guiHelper.drawableBuilder(new ResourceLocation(VoluminousEnergy.MODID + "textures/gui/combustion_generator.png"), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.COMBUSTING_UID;
    }

    @Override
    public Class<? extends CombustionGeneratorFuelRecipe> getRecipeClass() {
        LOGGER.debug("Getting Combustion Generator Fuel recipy class");
        return CombustionGeneratorFuelRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Combustion";
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
    public void draw(CombustionGeneratorFuelRecipe recipe, double mouseX, double mouseY) {

        Minecraft.getInstance().fontRenderer.drawString("Volumetric Energy: ",31,4,0x606060);
        Minecraft.getInstance().fontRenderer.drawString(recipe.getVolumetricEnergy() + " FE",42,16, 0x606060);
        slotDrawable.draw(11,0);

        int j = 0;
        for(int i = 0; i < CombustionGeneratorOxidizerRecipe.oxidizerList.size(); i++){
            j = orderOxidizers(i+1,j);
            slotDrawable.draw(2 + j, 45);
            int fePerTick = recipe.getVolumetricEnergy()/CombustionGeneratorOxidizerRecipe.oxidizerList.get(i).getProcessTime();
            Minecraft.getInstance().fontRenderer.drawString(fePerTick+"",4+j,64,0x606060);
        }
        Minecraft.getInstance().fontRenderer.drawString(" FE/t",20+j,64,0xFFFFFF);

    }

    @Override
    public void setIngredients(CombustionGeneratorFuelRecipe recipe, IIngredients ingredients) {
        LOGGER.debug("Combustion Generator Setting Ingredients! ");
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredientMap().keySet().stream()
                .map(ingredient -> Arrays.asList(ingredient.getMatchingStacks()))
                .collect(Collectors.toList()));

        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        for (ItemStack testStack : recipe.getIngredient().getMatchingStacks()){
            testStack.setCount(1);
            ingredients.setInput(VanillaTypes.ITEM, testStack);
        }

        for (Item oxi : CombustionGeneratorOxidizerRecipe.ingredientList){
            ingredients.setInput(VanillaTypes.ITEM, new ItemStack(oxi,1));
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CombustionGeneratorFuelRecipe recipe, IIngredients ingredients) {
        LOGGER.debug("Combustion Generator Setting recipe!");
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 11, 0);

        // Setup Oxidizers
        int j = 0;
        for (int i = 1; i <= CombustionGeneratorOxidizerRecipe.oxidizerList.size(); i++){
            j = orderOxidizers(i,j);
            itemStacks.init(i, false, 2 + j, 45);
            ItemStack oxidizerStack = CombustionGeneratorOxidizerRecipe.oxidizerList.get(i-1).getBucketItem();
            itemStacks.set(i,oxidizerStack);
        }


        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getMatchingStacks()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);
    }

    public int orderOxidizers(int i, int j){
        if(i > 1){
            switch (i) {
                case 2:
                    return j + i * 12;
                default:
                    return 2*j;
            }
        }
        return j;
    }
}
