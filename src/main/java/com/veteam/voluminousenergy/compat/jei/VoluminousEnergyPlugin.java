package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.*;
import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
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
    public static final ResourceLocation COMBUSTING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/combusting");
    public static final ResourceLocation STIRLING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/stirling");
    public static final ResourceLocation CENTRIFUGAL_AGITATION_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/centrifugal_agitation");
    public static final ResourceLocation AQUEOULIZING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/aqueoulizing");
    public static final ResourceLocation DISTILLING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/distilling");

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
        registration.addRecipeCategories(new CombustionCategory(guiHelper));
        registration.addRecipeCategories(new StirlingCategory(guiHelper));
        registration.addRecipeCategories(new CentrifugalAgitationCategory(guiHelper));
        registration.addRecipeCategories(new AqueoulizingCategory(guiHelper));
        registration.addRecipeCategories(new DistillingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration){// Add recipes
        registration.addRecipes(getRecipesOfType(CrusherRecipe.RECIPE_TYPE), CRUSHING_UID);
        registration.addRecipes(getRecipesOfType(ElectrolyzerRecipe.RECIPE_TYPE), ELECTROLYZING_UID);
        registration.addRecipes(getRecipesOfType(CompressorRecipe.RECIPE_TYPE), COMPRESSING_UID);
        registration.addRecipes(getRecipesOfType(CombustionGeneratorFuelRecipe.RECIPE_TYPE), COMBUSTING_UID);
        registration.addRecipes(getRecipesOfType(StirlingGeneratorRecipe.RECIPE_TYPE), STIRLING_UID);
        registration.addRecipes(getRecipesOfType(CentrifugalAgitatorRecipe.RECIPE_TYPE), CENTRIFUGAL_AGITATION_UID);
        registration.addRecipes(getRecipesOfType(AqueoulizerRecipe.RECIPE_TYPE), AQUEOULIZING_UID);
        registration.addRecipes(getRecipesOfType(DistillationRecipe.RECIPE_TYPE), DISTILLING_UID);
    }

    private static List<IRecipe<?>> getRecipesOfType(IRecipeType<?> recipeType) {
        return Minecraft.getInstance().level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .collect(Collectors.toList());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrusherScreen.class, 78, 32, 28, 23, CRUSHING_UID);
        registration.addRecipeClickArea(ElectrolyzerScreen.class, 78, 32, 28, 23, ELECTROLYZING_UID);
        registration.addRecipeClickArea(CompressorScreen.class, 78, 32,28,23, COMPRESSING_UID);
        registration.addRecipeClickArea(CombustionGeneratorScreen.class, 78,12,28,23, COMBUSTING_UID);
        registration.addRecipeClickArea(StirlingGeneratorScreen.class, 78,12,28,23, STIRLING_UID);
        registration.addRecipeClickArea(CentrifugalAgitatorScreen.class, 78, 11, 28, 23, CENTRIFUGAL_AGITATION_UID);
        registration.addRecipeClickArea(AqueoulizerScreen.class, 78, 32, 11, 23, AQUEOULIZING_UID);
        registration.addRecipeClickArea(DistillationUnitScreen.class, 78,32,11,23, DISTILLING_UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //registration.addRecipeTransferHandler(CrusherContainer.class, CRUSHING_UID, 0, 1, 3, 36);
        //registration.addRecipeTransferHandler(ElectrolyzerContainer.class, ELECTROLYZING_UID, 0, 1, 3, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CRUSHER_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/crushing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTROLYZER_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/electrolyzing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.COMPRESSOR_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/compressing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/centrifugal_agitation"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.AQUEOULIZER_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/aqueoulizing"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/stirling"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/combusting"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK).copy(), new ResourceLocation(VoluminousEnergy.MODID, "plugin/distilling"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.GAS_FIRED_FURNACE_BLOCK).copy(), new ResourceLocation("minecraft:smelting"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTRIC_FURNACE_BLOCK).copy(), new ResourceLocation("minecraft:smelting"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.GAS_FIRED_FURNACE_BLOCK).copy(), new ResourceLocation("minecraft:blasting"));
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTRIC_FURNACE_BLOCK).copy(), new ResourceLocation("minecraft:blasting"));

    }
}
