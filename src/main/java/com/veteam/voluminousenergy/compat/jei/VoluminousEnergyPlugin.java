package com.veteam.voluminousenergy.compat.jei;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.screens.*;
import com.veteam.voluminousenergy.compat.jei.category.*;
import com.veteam.voluminousenergy.compat.jei.containerHandler.*;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.recipe.*;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
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
    public static final ResourceLocation CENTRIFUGAL_SEPARATION_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/centrifugal_separation");
    public static final ResourceLocation IMPLOSION_COMPRESSION_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/implosion_compressing");
    public static final ResourceLocation INDUSTRIAL_BLASTING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/industrial_blasting");
    public static final ResourceLocation TOOLING_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/tooling");
    public static final ResourceLocation SAWMILL_UID = new ResourceLocation(VoluminousEnergy.MODID, "plugin/sawmilling");
    
    public static final Component SHOW_RECIPES = new TranslatableComponent("jei.tooltip.show.recipes");

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
        registration.addRecipeCategories(new CentrifugalSeparationCategory(guiHelper));
        registration.addRecipeCategories(new ImplosionCompressionCategory(guiHelper));
        registration.addRecipeCategories(new IndustrialBlastingCategory(guiHelper));
        registration.addRecipeCategories(new ToolingCategory(guiHelper));
        registration.addRecipeCategories(new SawmillCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration){// Add recipes
        registration.addRecipes(CrushingCategory.RECIPE_TYPE, getRecipesOfType(CrusherRecipe.RECIPE_TYPE));
        registration.addRecipes(ElectrolyzingCategory.RECIPE_TYPE, getRecipesOfType(ElectrolyzerRecipe.RECIPE_TYPE));
        registration.addRecipes(CompressingCategory.RECIPE_TYPE, getRecipesOfType(CompressorRecipe.RECIPE_TYPE));
        registration.addRecipes(CombustionCategory.RECIPE_TYPE, getRecipesOfType(CombustionGeneratorFuelRecipe.RECIPE_TYPE));
        registration.addRecipes(StirlingCategory.RECIPE_TYPE, getRecipesOfType(StirlingGeneratorRecipe.RECIPE_TYPE));
        registration.addRecipes(CentrifugalAgitationCategory.RECIPE_TYPE, getRecipesOfType(CentrifugalAgitatorRecipe.RECIPE_TYPE));
        registration.addRecipes(AqueoulizingCategory.RECIPE_TYPE, getRecipesOfType(AqueoulizerRecipe.RECIPE_TYPE));
        registration.addRecipes(DistillingCategory.RECIPE_TYPE, getRecipesOfType(DistillationRecipe.RECIPE_TYPE));
        registration.addRecipes(CentrifugalSeparationCategory.RECIPE_TYPE, getRecipesOfType(CentrifugalSeparatorRecipe.RECIPE_TYPE));
        registration.addRecipes(ImplosionCompressionCategory.RECIPE_TYPE, getRecipesOfType(ImplosionCompressorRecipe.RECIPE_TYPE));
        registration.addRecipes(IndustrialBlastingCategory.RECIPE_TYPE, getRecipesOfType(IndustrialBlastingRecipe.RECIPE_TYPE));
        registration.addRecipes(ToolingCategory.RECIPE_TYPE, getRecipesOfType(ToolingRecipe.RECIPE_TYPE));
        registration.addRecipes(SawmillCategory.RECIPE_TYPE, getRecipesOfType(SawmillingRecipe.RECIPE_TYPE));

        // Register info for certain ingredients that could use additional explanation for end users
        registerInfo(registration);
    }

    private void registerInfo(IRecipeRegistration registration){
        registration.addIngredientInfo(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), 1000), VanillaTypes.FLUID, TextUtil.translateString("jei.voluminousenergy.air_compressor_fluid_info"));

        ArrayList<ItemStack> compressedAirInfo = new ArrayList<>();
        compressedAirInfo.add(new ItemStack(VEBlocks.AIR_COMPRESSOR_BLOCK.asItem()));
        compressedAirInfo.add(new ItemStack(VEFluids.COMPRESSED_AIR_BUCKET_REG.get()));
        registration.addIngredientInfo(compressedAirInfo, VanillaTypes.ITEM, TextUtil.translateString("jei.voluminousenergy.air_compressor_item_info"));

        registration.addIngredientInfo(new FluidStack(VEFluids.CRUDE_OIL_REG.get(),1000), VanillaTypes.FLUID, TextUtil.translateString("jei.voluminousenergy.crude_oil_info"));
        registration.addIngredientInfo(new ItemStack(VEFluids.CRUDE_OIL_BUCKET_REG.get()), VanillaTypes.ITEM, TextUtil.translateString("jei.voluminousenergy.crude_oil_info"));
    }

    private static List<Recipe<?>> getRecipesOfType(RecipeType<?> recipeType) {
        return Minecraft.getInstance().level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .collect(Collectors.toList());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(CrusherScreen.class, new CrusherContainerHandler());
        registration.addGuiContainerHandler(ElectrolyzerScreen.class, new ElectrolyzingContainerHandler());
        registration.addGuiContainerHandler(CompressorScreen.class, new CompressorContainerHandler());
        registration.addGuiContainerHandler(CombustionGeneratorScreen.class, new CombustionGeneratorContainerHandler());
        registration.addGuiContainerHandler(PrimitiveStirlingGeneratorScreen.class, new PrimitiveStirlingGeneratorContainerHandler());
        registration.addGuiContainerHandler(StirlingGeneratorScreen.class, new StirlingGeneratorContainerHandler());
        registration.addGuiContainerHandler(CentrifugalAgitatorScreen.class, new CentrifugalAgitatorContainerHandler());
        registration.addRecipeClickArea(AqueoulizerScreen.class, 79, 31, 11, 18, AqueoulizingCategory.RECIPE_TYPE);
        registration.addGuiContainerHandler(AqueoulizerScreen.class, new AqueoulizerContainerHandler());
        registration.addGuiContainerHandler(DistillationUnitScreen.class, new DistillationUnitContainerHandler());
        registration.addGuiContainerHandler(GasFiredFurnaceScreen.class, new GasFiredFurnaceContainerHandler());
        registration.addGuiContainerHandler(ElectricFurnaceScreen.class, new ElectricFurnaceContainerHandler());
        registration.addGuiContainerHandler(CentrifugalSeparatorScreen.class, new CentrifugalSeparatorContainerHandler());
        registration.addGuiContainerHandler(ImplosionCompressorScreen.class, new ImplosionCompressorContainerHandler());
        registration.addGuiContainerHandler(BlastFurnaceScreen.class, new BlastFurnaceContainerHandler());
        registration.addGuiContainerHandler(ToolingStationScreen.class, new ToolingStationContainerHandler());
        registration.addGuiContainerHandler(SawmillScreen.class, new SawmillContainerHandler());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CrusherContainer.class, CrushingCategory.RECIPE_TYPE, 0, 1, CrusherContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(ElectrolyzerContainer.class, ElectrolyzingCategory.RECIPE_TYPE, 0, 2, ElectrolyzerContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(CompressorContainer.class, CompressingCategory.RECIPE_TYPE, 0, 1, CompressorContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(PrimitiveStirlingGeneratorContainer.class, StirlingCategory.RECIPE_TYPE, 0, 1, PrimitiveStirlingGeneratorContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(StirlingGeneratorContainer.class, StirlingCategory.RECIPE_TYPE, 0, 1, StirlingGeneratorContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(AqueoulizerContainer.class, AqueoulizingCategory.RECIPE_TYPE, 3, 1, AqueoulizerContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(CentrifugalSeparatorContainer.class, CentrifugalSeparationCategory.RECIPE_TYPE, 0, 2, CentrifugalSeparatorContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(ImplosionCompressorContainer.class, ImplosionCompressionCategory.RECIPE_TYPE, 0, 2, ImplosionCompressorContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(BlastFurnaceContainer.class, IndustrialBlastingCategory.RECIPE_TYPE, 2, 2, BlastFurnaceContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(ToolingStationContainer.class, ToolingCategory.RECIPE_TYPE, 3, 2, ToolingStationContainer.NUMBER_OF_SLOTS, 36);
        registration.addRecipeTransferHandler(SawmillContainer.class,SawmillCategory.RECIPE_TYPE,0,3,SawmillContainer.NUMBER_OF_SLOTS,36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CRUSHER_BLOCK).copy(), CrushingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTROLYZER_BLOCK).copy(), ElectrolyzingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.COMPRESSOR_BLOCK).copy(),CompressingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK).copy(), CentrifugalAgitationCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.AQUEOULIZER_BLOCK).copy(), AqueoulizingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK).copy(), StirlingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).copy(), StirlingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.COMBUSTION_GENERATOR_BLOCK).copy(), CombustionCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK).copy(), DistillingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.GAS_FIRED_FURNACE_BLOCK).copy(), RecipeTypes.SMELTING, RecipeTypes.BLASTING, CombustionCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.ELECTRIC_FURNACE_BLOCK).copy(), RecipeTypes.SMELTING, RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK).copy(), CentrifugalSeparationCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.IMPLOSION_COMPRESSOR_BLOCK).copy(), ImplosionCompressionCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK).copy(), IndustrialBlastingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.TOOLING_STATION_BLOCK).copy(), ToolingCategory.RECIPE_TYPE, CombustionCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(VEBlocks.SAWMILL_BLOCK).copy(), SawmillCategory.RECIPE_TYPE);
    }
}