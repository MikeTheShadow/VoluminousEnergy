package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.VEFeatures;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEFeatureGeneration {

    public static void addFeaturesToBiomes(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND && Config.ENABLE_VE_FEATURE_GEN.get()){
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has received a BiomeLoadingEvent for " + event.getName().toString() + ". Will start feature registration process now.");
            // Oil Features
            addOilLake(event);
            addOilGeyser(event);

            // Crop features
            addRice(event);

            if (Config.ENABLE_ORE_DEPOSIT.get()) addOreDeposit(event);
        } else if (event.getCategory().equals(Biome.BiomeCategory.THEEND)){
            if (Config.ENABLE_ORE_DEPOSIT.get()) addOreDeposit(event);
        }
    }

    public static void addOilLake(BiomeLoadingEvent event){
        if(Config.GENERATE_OIL_LAKES.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, Holder.direct(VEFeatures.SURFACE_OIL_LAKE_PLACEMENT));
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, Holder.direct(VEFeatures.UNDERGROUND_OIL_LAKE_PLACEMENT));
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Lakes to generate in: " + event.getName().toString());
        }
    }

    public static void addOilGeyser(BiomeLoadingEvent event){
        if(Config.GENERATE_OIL_GEYSER.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, Holder.direct(VEFeatures.OIL_GEYSER_PLACEMENT));
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Geysers to generate in: " + event.getName().toString());
        }
    }

    public static void addRice(BiomeLoadingEvent event){
        if (Config.GENERATE_RICE.get()) {
            if (event.getCategory() != Biome.BiomeCategory.OCEAN){
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Holder.direct(VEFeatures.RICE_FEATURE_PLACEMENT));
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has registered Rice to generate in: " + event.getName().toString());
            } else if (event.getCategory() == Biome.BiomeCategory.OCEAN && Config.GENERATE_RICE_IN_OCEAN.get()){
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Holder.direct(VEFeatures.RICE_FEATURE_PLACEMENT));
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has registered Rice to generate in: " + event.getName().toString() + ". Rice generation for oceans is enabled in the config.");
            }
        }
    }

    public static void addOreDeposit(BiomeLoadingEvent event){
        if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy is now registering all enabled Ore Deposits to generate in: " + event.getName());

        if(event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND){
            // Vanilla Deposits
            if (Config.ENABLE_COPPER_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.COPPER_ORE_DEPOSIT_PLACEMENT));
            if (Config.ENABLE_IRON_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.IRON_ORE_DEPOSIT_PLACEMENT));
            if (Config.ENABLE_GOLD_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.GOLD_ORE_DEPOSIT_PLACEMENT));

            // Voluminous Energy Deposits
            if (Config.ENABLE_BAUXITE_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.BAUXITE_ORE_DEPOSIT_PLACEMENT));
            if (Config.ENABLE_CINNABAR_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.CINNABAR_ORE_DEPOSIT_PLACEMENT));
            if (Config.ENABLE_GALENA_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.GALENA_ORE_DEPOSIT_PLACEMENT));
            if (Config.ENABLE_RUTILE_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.RUTILE_ORE_DEPOSIT_PLACEMENT));

        } else if (event.getCategory() == Biome.BiomeCategory.THEEND){
            if (Config.ENABLE_EIGHZO_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEFeatures.EIGHZO_ORE_DEPOSIT_PLACEMENT));
        }
    }
}