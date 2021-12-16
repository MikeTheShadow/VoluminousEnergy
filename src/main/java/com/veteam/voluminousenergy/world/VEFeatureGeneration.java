package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import com.veteam.voluminousenergy.world.feature.RiceFeature;
import com.veteam.voluminousenergy.world.feature.VEOreDepositFeature;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
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

            //TODO: Expremental, Configs
            addOreDeposit(event);
        }
    }

    public static void addOilLake(BiomeLoadingEvent event){
        // For surface oil lakes
        PlacedFeature surfaceCrudeOilLakeFeature = CrudeOilFeature.SURFACE_INSTANCE
                .configured(new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .placed(
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
                        InSquarePlacement.spread(),
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(Config.SURFACE_OIL_LAKE_CHANCE.get()) // 65 by default
                );

        // For underground oil lakes
        PlacedFeature undergroundCrudeOilLakeFeature = CrudeOilFeature.UNDERGROUND_INSTANCE
                .configured(new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .placed(
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
                        InSquarePlacement.spread(),
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(Config.UNDERGROUND_OIL_LAKE_CHANCE.get()) // 15 by default
                );
        //.decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(Config.OIL_LAKE_CHANCE.get())));

        if(Config.GENERATE_OIL_LAKES.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, surfaceCrudeOilLakeFeature);
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, undergroundCrudeOilLakeFeature);
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Lakes to generate in: " + event.getName().toString());
        }
    }

    public static void addOilGeyser(BiomeLoadingEvent event){
        PlacedFeature crudeOilGeyser = GeyserFeature.INSTANCE
                .configured(new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()))
                .placed(
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
                        InSquarePlacement.spread(),
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(Config.OIL_GEYSER_CHANCE.get()) // 100 by default

                );

        if(Config.GENERATE_OIL_GEYSER.get()){
            event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, crudeOilGeyser);
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Registered Oil Geysers to generate in: " + event.getName().toString());
        }
    }

    public static void addRice(BiomeLoadingEvent event){
        PlacedFeature riceFeature = RiceFeature.INSTANCE
                .configured(new BlockStateConfiguration(VEBlocks.RICE_CROP.defaultBlockState()))
                .placed(
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.RICE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RICE_TOP_ANCHOR.get())), // Default: 48 --> 320
                        InSquarePlacement.spread(),
                        CountPlacement.of(1),
                        RarityFilter.onAverageOnceEvery(Config.RICE_CHANCE.get())
                );

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

    public static void addOreDeposit(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND){
            // Vanilla Dposits
            PlacedFeature copperDeposit = VEOreDepositFeature.COPPER
                    .configured(new BlockStateConfiguration(Blocks.COPPER_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(26)
                    );

            PlacedFeature ironDeposit = VEOreDepositFeature.IRON
                    .configured(new BlockStateConfiguration(Blocks.IRON_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.absolute(74)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(20)
                    );

            PlacedFeature goldDeposit = VEOreDepositFeature.GOLD
                    .configured(new BlockStateConfiguration(Blocks.GOLD_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(18)
                    );


            // Voluminous Energy Deposits
            PlacedFeature bauxiteDeposit = VEOreDepositFeature.BAUXITE
                    .configured(new BlockStateConfiguration(VEBlocks.BAUXITE_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(10), VerticalAnchor.absolute(60)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(14)//24
                    );

            PlacedFeature cinnabarDeposit = VEOreDepositFeature.CINNABAR
                    .configured(new BlockStateConfiguration(VEBlocks.CINNABAR_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(12)//24
                    );

            PlacedFeature galenaDeposit = VEOreDepositFeature.GALENA
                    .configured(new BlockStateConfiguration(VEBlocks.GALENA_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(12)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(18)//24
                    );

            PlacedFeature rutileDeposit = VEOreDepositFeature.RUTILE
                    .configured(new BlockStateConfiguration(VEBlocks.RUTILE_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(56)//24
                    );

            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, copperDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ironDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, goldDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, bauxiteDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, cinnabarDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, galenaDeposit);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, rutileDeposit);

        } else if (event.getCategory() == Biome.BiomeCategory.THEEND){

            PlacedFeature eighzoDeposit = VEOreDepositFeature.EIGHZO
                    .configured(new BlockStateConfiguration(VEBlocks.EIGHZO_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(1), VerticalAnchor.absolute(36)),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(56)//24
                    );

            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, eighzoDeposit);
        }


        //event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, veinFeature);
    }
}