package com.veteam.voluminousenergy.world.modifiers;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VEAndedMultiBiomeModifier implements BiomeModifier {

    private final AtomicReference<List<TagKey<Biome>>> whitelistedBiomeKeycache = new AtomicReference<>(new ArrayList<>());
    private final AtomicReference<List<TagKey<Biome>>> blacklistedBiomeKeycache = new AtomicReference<>(new ArrayList<>());


    private final String whitelistedBiome;
    private final String blacklistedBiome;
    private final Holder<ConfiguredFeature<?, ?>> feature;
    private final boolean isTriangualar;
    private final int count;
    private final int bottomAnchor;
    private final int topAnchor;
    private final int rarity;
    private final String generationStep;

    public VEAndedMultiBiomeModifier(
            String whitelistedBiome,
            String blacklistedBiome,
            Holder<ConfiguredFeature<?, ?>> feature,
            boolean isTriangualar,
            int count,
            int bottomAnchor,
            int topAnchor,
            int rarity,
            String generationStep
    ) {
        this.whitelistedBiome = whitelistedBiome;
        this.blacklistedBiome = blacklistedBiome;
        this.feature = feature;
        this.isTriangualar = isTriangualar;
        this.count = count;
        this.bottomAnchor = bottomAnchor;
        this.topAnchor = topAnchor;
        this.rarity = rarity;
        this.generationStep = generationStep;
    }

    public void modify(@NotNull Holder<Biome> biome, @NotNull Phase phase,@NotNull ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        if (whitelistedBiomeKeycache.get().isEmpty()) whitelistCacheBuilder();
        if (blacklistedBiomeKeycache.get().isEmpty()) blacklistCacheBuilder();

        // Sizes should be identical when filtering, if so, all the tags needed are present
        if (biome.tags().filter(b -> whitelistedBiomeKeycache.get().contains(b)).count() != whitelistedBiomeKeycache.get().size())
            return;

        // If any match, return, as a blacklisted tag has been hit
        if (biome.tags().anyMatch(b -> blacklistedBiomeKeycache.get().contains(b))) return;

        if (Config.WORLD_GEN_LOGGING.get()) {
            //TODO review these loggers and then remove?
            VoluminousEnergy.LOGGER.info("Voluminous Energy has received a successful biome modify event. ");
//            VoluminousEnergy.LOGGER.info("Biome is: " + RegistryLookups.lookupBiome(biome.value()));
            VoluminousEnergy.LOGGER.info("Biome Keys of biome in question: ");
            biome.tags().forEach(key -> System.out.print(key.toString() + ", "));
            VoluminousEnergy.LOGGER.info("\nWhitelisted Keys for this AND rule: ");
            whitelistedBiomeKeycache.get().forEach(key -> System.out.print(key.toString() + ", "));
        }

        List<ConfiguredFeature<?, ?>> oreConfiguration = feature.value().getFeatures().toList();
        Holder<PlacedFeature> modifiedFeature;
        if (isTriangualar) {
            modifiedFeature = Holder.direct(new PlacedFeature(Holder.direct(oreConfiguration.get(0)), List.of(
                    HeightRangePlacement.triangle(VerticalAnchor.absolute(bottomAnchor), VerticalAnchor.absolute(topAnchor)),
                    CountPlacement.of(count),
                    RarityFilter.onAverageOnceEvery(rarity),
                    InSquarePlacement.spread()
            )));
        } else {
            modifiedFeature = Holder.direct(new PlacedFeature(Holder.direct(oreConfiguration.get(0)), List.of(
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(bottomAnchor), VerticalAnchor.absolute(topAnchor)),
                    CountPlacement.of(count),
                    RarityFilter.onAverageOnceEvery(rarity),
                    InSquarePlacement.spread()
            )));
        }


//        System.out.println("\nChecking if features are unique. Start with modified: ");
//        modifiedFeature.get().placement().forEach(rule -> System.out.print(rule.toString() + ", "));
//        System.out.println("\n");
//        feature.get().placement().forEach(rule -> System.out.print(rule.toString() + ", "));
//        System.out.println("");


        builder.getGenerationSettings().addFeature(getGenerationStepDecoration(), modifiedFeature);
    }

    public void whitelistCacheBuilder() {
        if (whitelistedBiome.contains(",")) {
            Arrays.stream(whitelistedBiome.split(",")).sequential().forEach(greenBiome -> {
                TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, ResourceLocation.parse(greenBiome));
                whitelistedBiomeKeycache.get().add(biomeTag);
            });
        } else if (!whitelistedBiome.isEmpty()) {
            TagKey<Biome> biomeTagKey = TagKey.create(Registries.BIOME, ResourceLocation.parse(whitelistedBiome));
            whitelistedBiomeKeycache.get().add(biomeTagKey);
        }
    }

    public void blacklistCacheBuilder() {
        if (blacklistedBiome.contains(",")) {
            Arrays.stream(blacklistedBiome.split(",")).sequential().forEach(redBiome -> {
                TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, ResourceLocation.parse(redBiome));
                blacklistedBiomeKeycache.get().add(biomeTag);
            });
        } else if (!blacklistedBiome.isEmpty()) {
            TagKey<Biome> biomeTagKey = TagKey.create(Registries.BIOME, ResourceLocation.parse(blacklistedBiome));
            blacklistedBiomeKeycache.get().add(biomeTagKey);
        }
    }

    public String getWhitelistedBiome() {
        return whitelistedBiome;
    }

    public String getBlacklistedBiome() {
        return blacklistedBiome;
    }

    public Holder<ConfiguredFeature<?, ?>> getFeature() {
        return feature;
    }

    public boolean getIsTriangular() {
        return this.isTriangualar;
    }

    public int getCount() {
        return count;
    }

    public int getBottomAnchor() {
        return bottomAnchor;
    }

    public int getTopAnchor() {
        return topAnchor;
    }

    public int getRarity() {
        return rarity;
    }

    public String getGenerationStep() {
        return generationStep;
    }

    public GenerationStep.Decoration getGenerationStepDecoration() {
        // DON'T DO THIS:
//        return GenerationStep.Decoration.valueOf(generationStep);
        // As it'll return: java.lang.IllegalArgumentException: No enum constant net.minecraft.world.level.levelgen.GenerationStep.Decoration.underground_decoration

        if (generationStep.equals("raw_generation")) return GenerationStep.Decoration.RAW_GENERATION;
        if (generationStep.equals("lakes")) return GenerationStep.Decoration.LAKES;
        if (generationStep.equals("local_modifications")) return GenerationStep.Decoration.LOCAL_MODIFICATIONS;
        if (generationStep.equals("underground_structures")) return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
        if (generationStep.equals("surface_structures")) return GenerationStep.Decoration.SURFACE_STRUCTURES;
        if (generationStep.equals("strongholds")) return GenerationStep.Decoration.STRONGHOLDS;
        if (generationStep.equals("underground_ores")) return GenerationStep.Decoration.UNDERGROUND_ORES;
        if (generationStep.equals("underground_decoration")) return GenerationStep.Decoration.UNDERGROUND_DECORATION;
        if (generationStep.equals("fluid_springs")) return GenerationStep.Decoration.FLUID_SPRINGS;
        if (generationStep.equals("vegetal_decoration")) return GenerationStep.Decoration.VEGETAL_DECORATION;
        if (generationStep.equals("top_layer_modification")) return GenerationStep.Decoration.TOP_LAYER_MODIFICATION;
        throw new JsonSyntaxException("Invalid JSON syntax for generation step: " + generationStep + ". Generation step can only be: " +
                GenerationStep.Decoration.values());
    }

    @Override
    @NotNull
    public MapCodec<? extends BiomeModifier> codec() {
        throw new NotImplementedException("Codec not impl'd for biome modifier!");
    }
}