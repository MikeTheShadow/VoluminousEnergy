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

            if (Config.ENABLE_ORE_DEPOSIT.get()) addOreDeposit(event);
        } else if (event.getCategory().equals(Biome.BiomeCategory.THEEND)){
            if (Config.ENABLE_ORE_DEPOSIT.get()) addOreDeposit(event);
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
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has registered Rice to generate in: " + event.getName().toString());
            } else if (event.getCategory() == Biome.BiomeCategory.OCEAN && Config.GENERATE_RICE_IN_OCEAN.get()){
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, riceFeature);
                if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy has registered Rice to generate in: " + event.getName().toString() + ". Rice generation for oceans is enabled in the config.");
            }
        }
    }

    public static void addOreDeposit(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND){
            if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info("Voluminous Energy is now registering all enabled Ore Deposits to generate in: " + event.getName());
            // Vanilla Deposits
            PlacedFeature copperDeposit = VEOreDepositFeature.COPPER
                    .configured(new BlockStateConfiguration(Blocks.COPPER_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.COPPER_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.COPPER_ORE_DEPOSIT_CHANCE.get())
                    );

            PlacedFeature ironDeposit = VEOreDepositFeature.IRON
                    .configured(new BlockStateConfiguration(Blocks.IRON_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.IRON_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.IRON_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.IRON_ORE_DEPOSIT_CHANCE.get())//56
                    );

            PlacedFeature goldDeposit = VEOreDepositFeature.GOLD
                    .configured(new BlockStateConfiguration(Blocks.GOLD_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.GOLD_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GOLD_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.GOLD_ORE_DEPOSIT_CHANCE.get())//112
                    );


            // Voluminous Energy Deposits
            PlacedFeature bauxiteDeposit = VEOreDepositFeature.BAUXITE
                    .configured(new BlockStateConfiguration(VEBlocks.BAUXITE_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.BAUXITE_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.BAUXITE_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.BAUXITE_ORE_DEPOSIT_CHANCE.get())//64
                    );

            PlacedFeature cinnabarDeposit = VEOreDepositFeature.CINNABAR
                    .configured(new BlockStateConfiguration(VEBlocks.CINNABAR_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.CINNABAR_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_ORE_DEPOSIT_TOP_ANCHOR.get()) ),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.CINNABAR_ORE_DEPOSIT_CHANCE.get())  //56
                    );

            PlacedFeature galenaDeposit = VEOreDepositFeature.GALENA
                    .configured(new BlockStateConfiguration(VEBlocks.GALENA_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.GALENA_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GALENA_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.GALENA_ORE_DEPOSIT_CHANCE.get()) // 78
                    );

            PlacedFeature rutileDeposit = VEOreDepositFeature.RUTILE
                    .configured(new BlockStateConfiguration(VEBlocks.RUTILE_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.RUTILE_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RUTILE_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.RUTILE_ORE_DEPOSIT_CHANCE.get())//128
                    );

            if (Config.ENABLE_COPPER_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, copperDeposit);
            if (Config.ENABLE_IRON_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ironDeposit);
            if (Config.ENABLE_GOLD_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, goldDeposit);
            if (Config.ENABLE_BAUXITE_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, bauxiteDeposit);
            if (Config.ENABLE_CINNABAR_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, cinnabarDeposit);
            if (Config.ENABLE_GALENA_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, galenaDeposit);
            if (Config.ENABLE_RUTILE_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, rutileDeposit);

        } else if (event.getCategory() == Biome.BiomeCategory.THEEND){

            PlacedFeature eighzoDeposit = VEOreDepositFeature.EIGHZO
                    .configured(new BlockStateConfiguration(VEBlocks.EIGHZO_ORE.defaultBlockState()))
                    .placed(
                            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.EIGHZO_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.EIGHZO_ORE_DEPOSIT_TOP_ANCHOR.get())),
                            InSquarePlacement.spread(),
                            RarityFilter.onAverageOnceEvery(Config.EIGHZO_ORE_DEPOSIT_CHANCE.get())//192
                    );

            if (Config.ENABLE_EIGHZO_ORE_DEPOSIT.get()) event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, eighzoDeposit);
        }
    }
}