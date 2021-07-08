package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import com.veteam.voluminousenergy.world.feature.RiceFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEFeatureGeneration {

    public static void addFeaturesToBiomes(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND && Config.ENABLE_VE_FEATURE_GEN.get()){
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has received a BiomeLoadingEvent for " + event.getName().toString() + ". Lookout for Oil in this biome. It should generate there.");

            // Oil Features
            addOilLake(event);
            addOilGeyser(event);

            // Crop features
            addRice(event);
        }
    }

    public static void addOilLake(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> crudeOilLakeFeature = CrudeOilFeature.INSTANCE
                .configured(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(Config.OIL_LAKE_CHANCE.get())));

        if(Config.GENERATE_OIL_LAKES.get()) event.getGeneration().addFeature(GenerationStage.Decoration.LAKES, crudeOilLakeFeature);
    }

    public static void addOilGeyser(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> crudeOilGeyser = GeyserFeature.INSTANCE
                .configured(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(Config.OIL_GEYSER_CHANCE.get())));

        if(Config.GENERATE_OIL_GEYSER.get()) event.getGeneration().addFeature(GenerationStage.Decoration.LAKES, crudeOilGeyser);
    }

    public static void addRice(BiomeLoadingEvent event){
        ConfiguredFeature<?, ?> riceFeature = RiceFeature.INSTANCE
                .configured(new BlockStateFeatureConfig(VEBlocks.RICE_CROP.defaultBlockState()))
                // Place between 0 and 256 with no offset, HOWEVER, feature needs to see sky so shouldn't spawn underground
                .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 256))
                        .squared()
                        .count(Config.RICE_CHANCE.get()));

        if (Config.GENERATE_RICE.get()) {
            if (event.getCategory() != Biome.Category.OCEAN){
                event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, riceFeature);
            } else if (event.getCategory() == Biome.Category.OCEAN && Config.GENERATE_RICE_IN_OCEAN.get()){
                event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, riceFeature);
            }
        }
    }
}
