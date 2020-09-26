package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.containers.ElectrolyzerContainer;
import com.veteam.voluminousenergy.blocks.screens.CompressorScreen;
import com.veteam.voluminousenergy.blocks.screens.CrusherScreen;
import com.veteam.voluminousenergy.blocks.screens.ElectrolyzerScreen;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class VoluminousEnergyPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/main");
    public static final ResourceLocation CRUSHING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/crushing");
    public static final ResourceLocation ELECTROLYZING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/electrolyzing");
    public static final ResourceLocation COMPRESSING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/compressing");

    @Override
    public ResourceLocation getPluginUid(){
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration){
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrushingCategory(guiHelper));
        registration.addRecipeCategories(new ElectrolyzingCategory(guiHelper));
        registration.addRecipeCategories(new CompressingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration){// Add recipes
        registration.addRecipes(getRecipesOfType(CrusherRecipe.recipeType), CRUSHING_UID);
        registration.addRecipes(getRecipesOfType(ElectrolyzerRecipe.recipeType), ELECTROLYZING_UID);
        registration.addRecipes(getRecipesOfType(CompressorRecipe.recipeType), COMPRESSING_UID);
    }

    private static List<IRecipe<?>> getRecipesOfType(IRecipeType<?> recipeType) {
        return Minecraft.getInstance().world.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .collect(Collectors.toList());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrusherScreen.class, 78, 32, 28, 23, CRUSHING_UID);
        registration.addRecipeClickArea(ElectrolyzerScreen.class, 78, 32, 28, 23, ELECTROLYZING_UID);
        registration.addRecipeClickArea(CompressorScreen.class, 78, 32,28,23, COMPRESSING_UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //registration.addRecipeTransferHandler(CrusherContainer.class, CRUSHING_UID, 0, 1, 3, 36);
        //registration.addRecipeTransferHandler(ElectrolyzerContainer.class, ELECTROLYZING_UID, 0, 1, 3, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CRUSHER_BLOCK), new ResourceLocation(VoluminousEnergy.MODID + ":crushing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTROLYZER_BLOCK), new ResourceLocation(VoluminousEnergy.MODID + ":electrolyzing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.COMPRESSOR_BLOCK), new ResourceLocation(VoluminousEnergy.MODID + ":compressing"));
    }
}
