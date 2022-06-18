package com.veteam.voluminousenergy.world.modifiers;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VEAndedMultiBiomeModifier implements BiomeModifier {

    private AtomicReference<List<TagKey<Biome>>> whitelistedBiomeKeycache = new AtomicReference<>(new ArrayList<>());
    private AtomicReference<List<TagKey<Biome>>> blacklistedBiomeKeycache = new AtomicReference<>(new ArrayList<>());


    private String whitelistedBiome;
    private String blacklistedBiome;
    private Holder<PlacedFeature> feature;
    private float discard;
    private int count;
    private int size;
    private int bottomAnchor;
    private int topAnchor;
    private int rarity;

    public VEAndedMultiBiomeModifier(
            String whitelistedBiome,
            String blacklistedBiome,
            Holder<PlacedFeature> feature,
            float discard,
            int count,
            int size,
            int bottomAnchor,
            int topAnchor,
            int rarity
    ) {
        this.whitelistedBiome = whitelistedBiome; // is_hot, is_sandy (is_forest)
        this.blacklistedBiome = blacklistedBiome; // is_beach
        this.feature = feature;
        this.discard = discard;
        this.count = count;
        this.size = size;
        this.bottomAnchor = bottomAnchor;
        this.topAnchor = topAnchor;
        this.rarity = rarity;
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        if (whitelistedBiomeKeycache.get().isEmpty()) whitelistCacheBuilder();
        if (blacklistedBiomeKeycache.get().isEmpty()) blacklistCacheBuilder();

        //whitelistedBiomeKeycache.get().stream().allMatch()

        // Sizes should be identical when filtering, if so, all the tags needed are present
        if (biome.getTagKeys().filter(b -> whitelistedBiomeKeycache.get().contains(b)).count() != whitelistedBiomeKeycache.get().size()) return;

        // If any match, return, as a blacklisted tag has been hit
        if (biome.getTagKeys().anyMatch(b -> blacklistedBiomeKeycache.get().contains(b))) return;

        System.out.println("Voluminous Energy has received a successful biome modify event. ");
        builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
    }

    public void whitelistCacheBuilder(){
        if (whitelistedBiome.contains(",")){
            Arrays.stream(whitelistedBiome.split(",")).sequential().forEach(greenBiome -> {
                TagKey<Biome> biomeTag = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(greenBiome));
                whitelistedBiomeKeycache.get().add(biomeTag);
            });
        } else {
            TagKey<Biome> biomeTagKey = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(whitelistedBiome));
            whitelistedBiomeKeycache.get().add(biomeTagKey);
        }
    }

    public void blacklistCacheBuilder(){
        if (blacklistedBiome.contains(",")){
            Arrays.stream(blacklistedBiome.split(",")).sequential().forEach(redBiome -> {
                TagKey<Biome> biomeTag = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(redBiome));
                blacklistedBiomeKeycache.get().add(biomeTag);
            });
        } else {
            TagKey<Biome> biomeTagKey = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(blacklistedBiome));
            blacklistedBiomeKeycache.get().add(biomeTagKey);
        }
    }

    public String getWhitelistedBiome() {
        return whitelistedBiome;
    }

    public String getBlacklistedBiome() {
        return blacklistedBiome;
    }

    public Holder<PlacedFeature> getFeature() {
        return feature;
    }

    public float getDiscard() {
        return discard;
    }

    public int getCount() {
        return count;
    }

    public int getSize() {
        return size;
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

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return null;
    }
}