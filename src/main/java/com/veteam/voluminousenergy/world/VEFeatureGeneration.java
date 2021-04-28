package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEFeatureGeneration {
    public static void addFeaturesToBiomes(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND && Config.ENABLE_VE_FEATURE_GEN.get()){
            VoluminousEnergy.LOGGER.info("Voluminous Energy has received a BiomeLoadingEvent for " + event.getName().toString() + ". Lookout for Oil in this biome. It should generate there.");
             ConfiguredFeature<?, ?> crudeOilLakeFeature = CrudeOilFeature.INSTANCE
                     .configured(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                     .decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(Config.OIL_LAKE_CHANCE.get())));

            ConfiguredFeature<?, ?> crudeOilGeyser = GeyserFeature.INSTANCE
                    .configured(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                    .decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(Config.OIL_GEYSER_CHANCE.get())));

            if(Config.GENERATE_OIL_LAKES.get()) event.getGeneration().addFeature(GenerationStage.Decoration.LAKES, crudeOilLakeFeature);
            if(Config.GENERATE_OIL_GEYSER.get()) event.getGeneration().addFeature(GenerationStage.Decoration.LAKES, crudeOilGeyser);
        }
    }
}
