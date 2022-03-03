package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class VEOres {
    // Eighzo
    public static ConfiguredFeature<?,?> EIGHZO_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.EIGHZO_ORE_TARGETS, Config.EIGHZO_SIZE.get(), (float) ((double) Config.EIGHZO_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature EIGHZO_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(EIGHZO_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.EIGHZO_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.EIGHZO_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            CountPlacement.of(Config.EIGHZO_COUNT.get())
    ));

    // Saltpeter
    public static ConfiguredFeature<?,?> SALTPETER_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS, Config.SALTPETER_SIZE.get(), (float) ((double) Config.SALTPETER_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature SALTPETER_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(SALTPETER_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.SALTPETER_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            CountPlacement.of(Config.SALTPETER_COUNT.get())
    ));

    // Bauxite
    public static ConfiguredFeature<?,?> BAUXITE_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.BAUXITE_ORE_TARGETS, Config.BAUXITE_SIZE.get(), (float) ((double) Config.BAUXITE_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature BAUXITE_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(BAUXITE_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.BAUXITE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.BAUXITE_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            CountPlacement.of(Config.BAUXITE_COUNT.get())
    ));

    // Cinnabar
    public static ConfiguredFeature<?,?> CINNABAR_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.CINNABAR_ORE_TARGETS, Config.CINNABAR_SIZE.get(), (float) ((double) Config.CINNABAR_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature CINNABAR_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(CINNABAR_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.CINNABAR_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            CountPlacement.of(Config.CINNABAR_COUNT.get())
    ));

    // Rutile
    public static ConfiguredFeature<?,?> RUTILE_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.RUTILE_ORE_TARGETS, Config.RUTILE_SIZE.get(), (float) ((double) Config.RUTILE_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature RUTILE_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(RUTILE_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.RUTILE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RUTILE_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            CountPlacement.of(Config.RUTILE_COUNT.get())
    ));

    // Galena
    public static ConfiguredFeature<?,?> GALENA_ORE_BLOB = new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.GALENA_ORE_TARGETS, Config.GALENA_SIZE.get(), (float) ((double) Config.GALENA_EXPOSED_DISCARD_CHANCE.get())));
    public static PlacedFeature GALENA_ORE_BLOB_PLACEMENT = new PlacedFeature(Holder.direct(GALENA_ORE_BLOB), List.of(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.GALENA_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GALENA_TOP_ANCHOR.get())),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            CountPlacement.of(Config.GALENA_COUNT.get())
    ));
}
