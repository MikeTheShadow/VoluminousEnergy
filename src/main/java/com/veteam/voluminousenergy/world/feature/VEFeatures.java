package com.veteam.voluminousenergy.world.feature;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class VEFeatures { // TODO: Investigate `BlockTags.FEATURES_CANNOT_REPLACE` as seen in LakeFeature.java
    // "High Level" Features
    public static final Feature<BlockStateConfiguration> VE_BSC_LAKE_FEATURE = new VELakesFeature(BlockStateConfiguration.CODEC); // Lake using BlockStateConfiguration. AKA How MC used to do lakes
    public static final Feature<BlockStateConfiguration> VE_GEYSER_FEATURE = new GeyserFeature(BlockStateConfiguration.CODEC); // Geyser using BlockStateConfiguration
    public static final Feature<BlockStateConfiguration> VE_RICE_FEATURE = new RiceFeature(BlockStateConfiguration.CODEC); // Rice crop using BlockStateConfiguration
    public static final Feature<VEOreDepositFeature.Configuration> VE_ORE_DEPOSIT_FEATURE = new VEOreDepositFeature(VEOreDepositFeature.Configuration.CODEC);

    /*** Configs and Placements for impls of features ***/

    // Oil Lake
    public static ConfiguredFeature<?,?> SURFACE_OIL_LAKE_FEATURE = new ConfiguredFeature<>(SurfaceMattersLakesFeature.SURFACE_INSTANCE, new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()));
    public static PlacedFeature SURFACE_OIL_LAKE_PLACEMENT = new PlacedFeature(Holder.direct(SURFACE_OIL_LAKE_FEATURE), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
            InSquarePlacement.spread(),
            CountPlacement.of(1),
            RarityFilter.onAverageOnceEvery(Config.SURFACE_OIL_LAKE_CHANCE.get()) // 65 by default
    ));

    public static ConfiguredFeature<?,?> UNDERGROUND_OIL_LAKE_FEATURE = new ConfiguredFeature<>(SurfaceMattersLakesFeature.UNDERGROUND_INSTANCE, new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()));
    public static PlacedFeature UNDERGROUND_OIL_LAKE_PLACEMENT = new PlacedFeature(Holder.direct(UNDERGROUND_OIL_LAKE_FEATURE), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
            InSquarePlacement.spread(),
            CountPlacement.of(1),
            RarityFilter.onAverageOnceEvery(Config.UNDERGROUND_OIL_LAKE_CHANCE.get()) // 15 by default
    ));

    // Oil Geyser
    public static ConfiguredFeature<?,?> OIL_GEYSER_FEATURE = new ConfiguredFeature<>(VEFeatures.VE_GEYSER_FEATURE, new BlockStateConfiguration(CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock()));
    public static PlacedFeature OIL_GEYSER_PLACEMENT = new PlacedFeature(Holder.direct(OIL_GEYSER_FEATURE), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()), // TODO: Config
            InSquarePlacement.spread(),
            CountPlacement.of(1),
            RarityFilter.onAverageOnceEvery(Config.OIL_GEYSER_CHANCE.get()) // 100 by default
    ));

    // Rice
    public static ConfiguredFeature<?,?> RICE_FEATURE_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_RICE_FEATURE, new BlockStateConfiguration(VEBlocks.RICE_CROP.defaultBlockState()));
    public static PlacedFeature RICE_FEATURE_PLACEMENT = new PlacedFeature(Holder.direct(RICE_FEATURE_CONFIG), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.RICE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RICE_TOP_ANCHOR.get())), // Default: 48 --> 320
            InSquarePlacement.spread(),
            CountPlacement.of(1),
            RarityFilter.onAverageOnceEvery(Config.RICE_CHANCE.get())
    ));

    /*** ORE DEPOSITS ***/
    /* Vanilla Deposits */
            // Copper
    public static ConfiguredFeature<?,?> COPPER_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(Blocks.COPPER_ORE.defaultBlockState()),
                    BlockStateProvider.simple(Blocks.RAW_COPPER_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature COPPER_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(COPPER_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.COPPER_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.COPPER_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.COPPER_ORE_DEPOSIT_CHANCE.get())
    ));

    // Iron
    public static ConfiguredFeature<?,?> IRON_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(Blocks.IRON_ORE.defaultBlockState()),
                    BlockStateProvider.simple(Blocks.RAW_IRON_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature IRON_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(IRON_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.IRON_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.IRON_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.IRON_ORE_DEPOSIT_CHANCE.get())
    ));

    // Gold
    public static ConfiguredFeature<?,?> GOLD_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(Blocks.GOLD_ORE.defaultBlockState()),
                    BlockStateProvider.simple(Blocks.RAW_GOLD_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature GOLD_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(GOLD_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.GOLD_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GOLD_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.GOLD_ORE_DEPOSIT_CHANCE.get())
    ));

    /* Voluminous Energy Deposits */
    // Bauxite
    public static ConfiguredFeature<?,?> BAUXITE_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(VEBlocks.BAUXITE_ORE.defaultBlockState()),
                    BlockStateProvider.simple(VEBlocks.RAW_BAUXITE_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature BAUXITE_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(BAUXITE_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.BAUXITE_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.BAUXITE_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.BAUXITE_ORE_DEPOSIT_CHANCE.get())
    ));

    // Cinnabar
    public static ConfiguredFeature<?,?> CINNABAR_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(VEBlocks.CINNABAR_ORE.defaultBlockState()),
                    BlockStateProvider.simple(VEBlocks.RAW_CINNABAR_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature CINNABAR_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(CINNABAR_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.CINNABAR_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.CINNABAR_ORE_DEPOSIT_CHANCE.get())
    ));

    // Rutile
    public static ConfiguredFeature<?,?> RUTILE_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(VEBlocks.RUTILE_ORE.defaultBlockState()),
                    BlockStateProvider.simple(VEBlocks.RAW_RUTILE_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature RUTILE_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(RUTILE_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.RUTILE_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RUTILE_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.RUTILE_ORE_DEPOSIT_CHANCE.get())
    ));

    // Galena
    public static ConfiguredFeature<?,?> GALENA_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(VEBlocks.GALENA_ORE.defaultBlockState()),
                    BlockStateProvider.simple(VEBlocks.RAW_GALENA_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature GALENA_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(GALENA_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.GALENA_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GALENA_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.GALENA_ORE_DEPOSIT_CHANCE.get())
    ));

    // Eighzo
    public static ConfiguredFeature<?,?> EIGHZO_ORE_DEPOSIT_CONFIG = new ConfiguredFeature<>(VEFeatures.VE_ORE_DEPOSIT_FEATURE,
            new VEOreDepositFeature.Configuration(
                    BlockStateProvider.simple(VEBlocks.EIGHZO_ORE.defaultBlockState()),
                    BlockStateProvider.simple(VEBlocks.RAW_EIGHZO_BLOCK.defaultBlockState())
            ));
    public static PlacedFeature EIGHZO_ORE_DEPOSIT_PLACEMENT = new PlacedFeature(Holder.direct(EIGHZO_ORE_DEPOSIT_CONFIG), List.of(
            HeightRangePlacement.triangle(VerticalAnchor.absolute(Config.EIGHZO_ORE_DEPOSIT_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.EIGHZO_ORE_DEPOSIT_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            RarityFilter.onAverageOnceEvery(Config.EIGHZO_ORE_DEPOSIT_CHANCE.get())
    ));
}
