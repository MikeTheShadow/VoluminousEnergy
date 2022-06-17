package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEOres {
    public static final DeferredRegister<PlacedFeature> VE_PLACED_ORE_BLOBS_REGISTRY = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, VoluminousEnergy.MODID);
    public static final DeferredRegister<ConfiguredFeature<?,?>> VE_CONFIGURED_ORE_BLOBS_REGISTRY = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, VoluminousEnergy.MODID);

    //Note Eelt if it's not working it's not public static. You're welcome
    public static RegistryObject<PlacedFeature> SALTPETER_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("saltpeter_ore_blob", () -> createSaltpeterOre().get());

//    public static Holder<PlacedFeature> saltpeterOreBlobPlacement;

//    public static void registerConfiguredFeatures(){
//        OreConfiguration saltpeteroreConfiguration = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS, Config.SALTPETER_ORE_BLOBS_SIZE.get(), (float) ((double) Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
//        saltpeterOreBlobPlacement = registerPlacedFeature("saltpeter_ore_blob", new ConfiguredFeature<>(Feature.ORE, saltpeteroreConfiguration),
//                CountPlacement.of(Config.SALTPETER_ORE_BLOBS_COUNT.get()),
//                InSquarePlacement.spread(),
//                BiomeFilter.biome(),
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get())));
//    }

    /*
    @NotNull
    public static Holder<PlacedFeature> CreateSaltpeterOre() {
        OreConfiguration oreConfiguration = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS, Config.SALTPETER_ORE_BLOBS_SIZE.get(), (float) ((double) Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("saltpeter_ore_blob", new ConfiguredFeature<>(Feature.ORE,oreConfiguration),
                CountPlacement.of(Config.SALTPETER_ORE_BLOBS_COUNT.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get())));
    }*/
    @NotNull
    public static Holder<PlacedFeature> createSaltpeterOre(){
        OreConfiguration overworldConfig = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, VEBlocks.SALTPETER_ORE.get().defaultBlockState(), Config.SALTPETER_ORE_BLOBS_SIZE.get());
        return registerPlacedFeature("saltpeter_ore_blob", new ConfiguredFeature<>(Feature.ORE, overworldConfig),
                CountPlacement.of(Config.SALTPETER_ORE_BLOBS_COUNT.get()),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get())));
    }

//    // Eighzo
//    public static Holder<ConfiguredFeature<?,?>> EIGHZO_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.EIGHZO_ORE_TARGETS, Config.EIGHZO_ORE_BLOBS_SIZE.get(), (float) ((double) Config.EIGHZO_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> EIGHZO_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "eighzo_ore_blob",
//           EIGHZO_ORE_BLOB,
//            List.of(
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.EIGHZO_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.EIGHZO_ORE_BLOBS_TOP_ANCHOR.get())),
//                InSquarePlacement.spread(),
//                CountPlacement.of(Config.EIGHZO_ORE_BLOBS_COUNT.get()),
//                RarityFilter.onAverageOnceEvery(Config.EIGHZO_ORE_BLOBS_CHANCE.get())
//    ));
//    public static RegistryObject<PlacedFeature> EIGHZO_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("eighzo_ore_blob", () -> EIGHZO_ORE_BLOB_PLACEMENT.get());

    // Saltpeter
    // public static Holder<ConfiguredFeature<?,?>> SALTPETER_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS, Config.SALTPETER_ORE_BLOBS_SIZE.get(), (float) ((double) Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> SALTPETER_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "saltpeter_ore_blob",
//            SALTPETER_ORE_BLOB,
//            List.of(
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get())),
//                InSquarePlacement.spread(),
//                BiomeFilter.biome(),
//                CountPlacement.of(Config.SALTPETER_ORE_BLOBS_COUNT.get()),
//                RarityFilter.onAverageOnceEvery(Config.SALTPETER_ORE_BLOBS_CHANCE.get())
//    ));
//    public static Holder<PlacedFeature> SALTPETER_ORE_BLOB_PLACEMENT = createOre(
//            "saltpeter_ore_blob",
//            VEBlocks.SALTPETER_ORE.get().defaultBlockState(),//VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS,
//            Config.SALTPETER_ORE_BLOBS_SIZE.get(),
//            Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get(),
//            Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get(),
//            Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get(),
//            Config.SALTPETER_ORE_BLOBS_COUNT.get(),
//            Config.SALTPETER_ORE_BLOBS_CHANCE.get()
//    );
//    public static RegistryObject<PlacedFeature> SALTPETER_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("saltpeter_ore_blob", () -> createOre(
//            "saltpeter_ore_blob",
//            VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS,
//            Config.SALTPETER_ORE_BLOBS_SIZE.get(),
//            Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get(),
//            Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get(),
//            Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get(),
//            Config.SALTPETER_ORE_BLOBS_COUNT.get(),
//            Config.SALTPETER_ORE_BLOBS_CHANCE.get()
//    ).get());

//    // Bauxite
//    public static Holder<ConfiguredFeature<?,?>> BAUXITE_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.BAUXITE_ORE_TARGETS, Config.BAUXITE_ORE_BLOBS_SIZE.get(), (float) ((double) Config.BAUXITE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> BAUXITE_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "bauxite_ore_blob",
//            BAUXITE_ORE_BLOB,
//            List.of(
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.BAUXITE_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.BAUXITE_ORE_BLOBS_TOP_ANCHOR.get())),
//                InSquarePlacement.spread(),
//                BiomeFilter.biome(),
//                CountPlacement.of(Config.BAUXITE_ORE_BLOBS_COUNT.get()),
//                RarityFilter.onAverageOnceEvery(Config.BAUXITE_ORE_BLOBS_CHANCE.get())
//    ));
//    public static RegistryObject<PlacedFeature> BAUXITE_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("bauxite_ore_blob", () -> BAUXITE_ORE_BLOB_PLACEMENT.get());
//
//    // Cinnabar
//    public static Holder<ConfiguredFeature<?,?>> CINNABAR_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.CINNABAR_ORE_TARGETS, Config.CINNABAR_ORE_BLOBS_SIZE.get(), (float) ((double) Config.CINNABAR_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> CINNABAR_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "cinnabar_ore_blob",
//            CINNABAR_ORE_BLOB,
//            List.of(
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.CINNABAR_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_ORE_BLOBS_TOP_ANCHOR.get())),
//                InSquarePlacement.spread(),
//                BiomeFilter.biome(),
//                CountPlacement.of(Config.CINNABAR_ORE_BLOBS_COUNT.get()),
//                RarityFilter.onAverageOnceEvery(Config.CINNABAR_ORE_BLOBS_CHANCE.get())
//    ));
//    public static RegistryObject<PlacedFeature> CINNABAR_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("cinnabar_ore_blob", () -> CINNABAR_ORE_BLOB_PLACEMENT.get());
//
//    // Rutile
//    public static Holder<ConfiguredFeature<?,?>> RUTILE_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.RUTILE_ORE_TARGETS, Config.RUTILE_ORE_BLOBS_SIZE.get(), (float) ((double) Config.RUTILE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> RUTILE_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "rutile_ore_blob",
//            RUTILE_ORE_BLOB,
//            List.of(
//                HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.RUTILE_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RUTILE_ORE_BLOBS_TOP_ANCHOR.get())),
//                InSquarePlacement.spread(),
//                BiomeFilter.biome(),
//                CountPlacement.of(Config.RUTILE_ORE_BLOBS_COUNT.get()),
//                RarityFilter.onAverageOnceEvery(Config.RUTILE_ORE_BLOBS_CHANCE.get())
//    ));
//    public static RegistryObject<PlacedFeature> RUTILE_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("rutile_ore_blob", () -> RUTILE_ORE_BLOB_PLACEMENT.get());
//
//    // Galena
//    public static Holder<ConfiguredFeature<?,?>> GALENA_ORE_BLOB = Holder.direct(new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.GALENA_ORE_TARGETS, Config.GALENA_ORE_BLOBS_SIZE.get(), (float) ((double) Config.GALENA_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()))));
//    public static Holder<PlacedFeature> GALENA_ORE_BLOB_PLACEMENT = PlacementUtils.register(
//            "galena_ore_blob",
//            GALENA_ORE_BLOB,
//            List.of(
//                    HeightRangePlacement.uniform(VerticalAnchor.absolute(Config.GALENA_ORE_BLOBS_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GALENA_ORE_BLOBS_TOP_ANCHOR.get())),
//                    InSquarePlacement.spread(),
//                    BiomeFilter.biome(),
//                    CountPlacement.of(Config.GALENA_ORE_BLOBS_COUNT.get()),
//                    RarityFilter.onAverageOnceEvery(Config.GALENA_ORE_BLOBS_CHANCE.get())
//            )
//    );
//    public static RegistryObject<PlacedFeature> GALENA_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("galena_ore_blob", () -> GALENA_ORE_BLOB_PLACEMENT.get());


    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
    }

    /*
    public static Holder<PlacedFeature> createOre(
            String registryName,
            List<OreConfiguration.TargetBlockState> targetBlockStates,
            int size,
            float discard,
            int bottomAnchor,
            int topAnchor,
            int count,
            int chance
            ){
        OreConfiguration oreConfiguration = new OreConfiguration(targetBlockStates, size, discard);

        return PlacementUtils.register(
                registryName,
                Holder.direct(new ConfiguredFeature<>(Feature.ORE, oreConfiguration)),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(bottomAnchor), VerticalAnchor.absolute(topAnchor)),
                InSquarePlacement.spread(),
                CountPlacement.of(count),
                RarityFilter.onAverageOnceEvery(chance)
        );
    }

    public static Holder<PlacedFeature> createOre(
            String registryName,
            List<OreConfiguration.TargetBlockState> targetBlockStates,
            int size,
            double discard,
            int bottomAnchor,
            int topAnchor,
            int count,
            int chance
    ){
        return createOre(registryName, targetBlockStates, size, (float) discard, bottomAnchor, topAnchor, count, chance);
    }*/

}
