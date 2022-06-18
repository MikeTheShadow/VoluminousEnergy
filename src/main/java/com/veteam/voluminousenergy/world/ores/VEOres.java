package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class VEOres {
    public static final DeferredRegister<PlacedFeature> VE_PLACED_ORE_BLOBS_REGISTRY = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, VoluminousEnergy.MODID);
    public static final DeferredRegister<ConfiguredFeature<?,?>> VE_CONFIGURED_ORE_BLOBS_REGISTRY = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, VoluminousEnergy.MODID);

    //Note Eelt if it's not working it's not public static. You're welcome
    public static RegistryObject<PlacedFeature> SALTPETER_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("saltpeter_ore_blob", () -> createSaltpeterOre().get());
    public static RegistryObject<PlacedFeature> BAUXITE_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("bauxite_ore_blob", () -> createBauxiteOre().get());
    public static RegistryObject<PlacedFeature> CINNABAR_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("cinnabar_ore_blob", () -> createCinnabarOre().get());
    public static RegistryObject<PlacedFeature> GALENA_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("galena_ore_blob", () -> createGalenaOre().get());
    public static RegistryObject<PlacedFeature> RUTILE_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("rutile_ore_blob", () -> createRutileOre().get());
    public static RegistryObject<PlacedFeature> EIGHZO_ORE_BLOB_PLACED_REG = VE_PLACED_ORE_BLOBS_REGISTRY.register("eighzo_ore_blob", () -> createEighzoOre().get());

    protected static NoPlacement noPlacement = new NoPlacement();

    @NotNull
    public static Holder<PlacedFeature> createSaltpeterOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.SALTPETER_ORE_TARGETS, Config.SALTPETER_ORE_BLOBS_SIZE.get(), (float) ((double) Config.SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("saltpeter_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    @NotNull
    public static Holder<PlacedFeature> createBauxiteOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.BAUXITE_ORE_TARGETS, Config.BAUXITE_ORE_BLOBS_SIZE.get(), (float) ((double) Config.BAUXITE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("bauxite_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    @NotNull
    public static Holder<PlacedFeature> createCinnabarOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.CINNABAR_ORE_TARGETS, Config.CINNABAR_ORE_BLOBS_SIZE.get(), (float) ((double) Config.CINNABAR_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("cinnabar_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    @NotNull
    public static Holder<PlacedFeature> createGalenaOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.GALENA_ORE_TARGETS, Config.GALENA_ORE_BLOBS_SIZE.get(), (float) ((double) Config.GALENA_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("galena_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    @NotNull
    public static Holder<PlacedFeature> createRutileOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.RUTILE_ORE_TARGETS, Config.RUTILE_ORE_BLOBS_SIZE.get(), (float) ((double) Config.RUTILE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("rutile_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    @NotNull
    public static Holder<PlacedFeature> createEighzoOre(){
        OreConfiguration oreConfig = new OreConfiguration(VEOreGeneration.OreWithTargetStatesToReplace.EIGHZO_ORE_TARGETS, Config.EIGHZO_ORE_BLOBS_SIZE.get(), (float) ((double) Config.EIGHZO_ORE_BLOBS_EXPOSED_DISCARD_CHANCE.get()));
        return registerPlacedFeature("eighzo_ore_blob", new ConfiguredFeature<>(Feature.ORE, oreConfig),
                InSquarePlacement.spread(),
                noPlacement
        );
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
    }

    public static class NoPlacement extends PlacementFilter{
        public NoPlacement(){}

        @Override
        protected boolean shouldPlace(PlacementContext p_226382_, RandomSource p_226383_, BlockPos p_226384_) {
            return false;
        }

        @Override
        public PlacementModifierType<?> type() {
            return PlacementModifierType.BIOME_FILTER;
        }
    }

}
