package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import com.veteam.voluminousenergy.world.feature.RiceFeature;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEFeatureGeneration {

    public static void addFeaturesToBiomes(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND && Config.ENABLE_VE_FEATURE_GEN.get()){
            //if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has received a BiomeLoadingEvent for " + event.getName().toString() + ". Lookout for Oil in this biome. It should generate there.");
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has received a BiomeLoadingEvent for " + event.getName().toString() + ". Will start feature registration process now.");
            // Oil Features
            addOilLake(event);
            addOilGeyser(event);

            // Crop features
            addRice(event);
        }
    }

    public static void addOilLake(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> crudeOilLakeFeature = CrudeOilFeature.INSTANCE
                .configured(new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.aboveBottom(5), VerticalAnchor.top()))))
                .rarity(25) // 250 works, 80 original TODO: Update config
                .squared()
                .count(1); // 4 works
        if(Config.GENERATE_OIL_LAKES.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, crudeOilLakeFeature);
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Lakes to generate in: " + event.getName().toString());
        }
    }

    public static void addOilGeyser(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> crudeOilGeyser = GeyserFeature.INSTANCE
                .configured(new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.top()))))
                .rarity(55) // 2520 original TODO: Update config
                .squared()
                .count(1);
        if(Config.GENERATE_OIL_GEYSER.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, crudeOilGeyser);
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Geysers to generate in: " + event.getName().toString());
        }
    }

    public static void addRice(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> riceFeature = RiceFeature.INSTANCE
                .configured(new BlockStateConfiguration(VEBlocks.RICE_CROP.defaultBlockState()))
                // Place anchored to 55 through 256
                .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(55), VerticalAnchor.top())))
                        .squared()
                        .count(Config.RICE_CHANCE.get()));

        if (Config.GENERATE_RICE.get()) {
            if (event.getCategory() != Biome.BiomeCategory.OCEAN){
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, riceFeature);
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Rice to generate in: " + event.getName().toString());
            } else if (event.getCategory() == Biome.BiomeCategory.OCEAN && Config.GENERATE_RICE_IN_OCEAN.get()){
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, riceFeature);
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Rice to generate in: " + event.getName().toString() + ". Rice generation for oceans is enabled in the config.");
            }
        }
    }
}
