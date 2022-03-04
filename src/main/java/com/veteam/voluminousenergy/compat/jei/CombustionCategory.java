package com.veteam.voluminousenergy.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CombustionCategory implements IRecipeCategory<CombustionGeneratorFuelRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public CombustionCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/combustion_generator.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK));
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
    public void draw(CombustionGeneratorFuelRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {

        Minecraft.getInstance().font.draw(matrixStack,"Volumetric Energy: ",31,4,0x606060);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getVolumetricEnergy() + " FE",42,16, 0x606060);
        slotDrawable.draw(matrixStack,11,0);

        Minecraft.getInstance().font.draw(matrixStack,"Oxidizers: ",2,32,0x606060);
        int j = 0;

        ArrayList<FluidStack> usedFluids = new ArrayList<>();
        for(int i = 0; i < CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size(); i++){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i);
            for (int k = 0; k < oxidizerRecipe.nsFluidInputList.size(); k++){
                if(!usedFluids.isEmpty()){

                    AtomicBoolean fluidStackAlreadyUsed = new AtomicBoolean(false);
                    oxidizerRecipe.nsFluidInputList.forEach(fluidStack -> {
                        if(usedFluids.contains(fluidStack)){
                            fluidStackAlreadyUsed.set(true);
                        }
                    });

                    if(!fluidStackAlreadyUsed.get()){
                        usedFluids.addAll(oxidizerRecipe.nsFluidInputList);

                        // Core / original logic
                        j = orderOxidizers(i+1,j);
                        slotDrawable.draw(matrixStack,2 + j, 45);
                        int fePerTick = recipe.getVolumetricEnergy()/CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i).getProcessTime();
                        Minecraft.getInstance().font.draw(matrixStack,fePerTick+"",4+j,64,0x606060);
                    }

                } else { // Assume empty
                    usedFluids.addAll(oxidizerRecipe.nsFluidInputList);

                    // Core / original logic
                    j = orderOxidizers(i+1,j);
                    slotDrawable.draw(matrixStack,2 + j, 45);
                    int fePerTick = recipe.getVolumetricEnergy()/CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i).getProcessTime();
                    Minecraft.getInstance().font.draw(matrixStack,fePerTick+"",4+j,64,0x606060);
                }
            }
        }

        Minecraft.getInstance().font.draw(matrixStack,"FE/t:",-28,64,0x606060);

    }

    @Override
    public void setIngredients(CombustionGeneratorFuelRecipe recipe, IIngredients ingredients) {
        ArrayList<FluidStack> anthology = new ArrayList<>();
        anthology.addAll(recipe.fluidInputList);
        anthology.addAll(CombustionGeneratorOxidizerRecipe.fluidInputList);
        ingredients.setInputs(VanillaTypes.FLUID, anthology);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CombustionGeneratorFuelRecipe recipe, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
        fluidStacks.init(0, true, 12, 1);

        // Setup Oxidizers
        int j = 0;

        ArrayList<FluidStack> usedFluids = new ArrayList<>();
        for (int i = 1; i <= CombustionGeneratorOxidizerRecipe.oxidizerRecipes.size(); i++){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i-1);
            for (int k = 0; k < oxidizerRecipe.nsFluidInputList.size(); k++){
                if(!usedFluids.isEmpty()){

                    AtomicBoolean fluidStackAlreadyUsed = new AtomicBoolean(false);
                    oxidizerRecipe.nsFluidInputList.forEach(fluidStack -> {
                        if(usedFluids.contains(fluidStack)){
                            fluidStackAlreadyUsed.set(true);
                        }
                    });

                    if(!fluidStackAlreadyUsed.get()){
                        usedFluids.addAll(oxidizerRecipe.nsFluidInputList);

                        // Core / original logic
                        j = orderOxidizers(i,j);
                        fluidStacks.init(i, true, 3 + j, 46);
                        ArrayList<FluidStack> oxidizerList = new ArrayList(CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i-1).nsFluidInputList);
                        fluidStacks.set(i, oxidizerList);
                    }

                } else { // Assume empty
                    usedFluids.addAll(oxidizerRecipe.nsFluidInputList);

                    // Core / original logic
                    j = orderOxidizers(i,j);
                    fluidStacks.init(i, true, 3 + j, 46);
                    ArrayList<FluidStack> oxidizerList = new ArrayList(CombustionGeneratorOxidizerRecipe.oxidizerRecipes.get(i-1).nsFluidInputList);
                    fluidStacks.set(i, oxidizerList);
                }
            }
        }

        // Should only be one ingredient...
        fluidStacks.set(0, recipe.fluidInputList);
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