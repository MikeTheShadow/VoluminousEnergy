package com.veteam.voluminousenergy.world.biomes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class VEBiome {
    private Map<Biome.ClimateSettings, Integer> weightMap = new HashMap<Biome.ClimateSettings, Integer>();
    private ResourceKey<Biome> beachBiome = Biomes.BEACH;
    private ResourceKey<Biome> riverBiome = Biomes.RIVER;
    private BiFunction<Double, Double, Integer> foliageColorFunction;
    private BiFunction<Double, Double, Integer> grassColorFunction;
    private BiFunction<Double, Double, Integer> waterColorFunction;

    protected void configureBiome(Biome.BiomeBuilder builder) {}

    protected void configureGeneration(BiomeGenerationSettings.Builder builder) {}

    protected void configureMobSpawns(MobSpawnSettings.Builder builder) {}

    protected void configureDefaultMobSpawns(MobSpawnSettings.Builder builder) {
        builder.setPlayerCanSpawn();
    }

    public Biome build() {
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder();

        // Configure the biome generation
        BiomeGenerationSettings.Builder biomeGenBuilder = new BiomeGenerationSettings.Builder();
        this.configureGeneration(biomeGenBuilder);
        biomeBuilder.generationSettings(biomeGenBuilder.build());

        // Configure mob spawning
        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        this.configureDefaultMobSpawns(mobSpawnBuilder);
        this.configureMobSpawns(mobSpawnBuilder);
        biomeBuilder.mobSpawnSettings(mobSpawnBuilder.build());

        // Configure and build the biome
        this.configureBiome(biomeBuilder);
        return biomeBuilder.build();
    }

    public void setBeachBiome(ResourceKey<Biome> biome) {
        this.beachBiome = biome;
    }

    public void setRiverBiome(ResourceKey<Biome> biome) {
        this.riverBiome = biome;
    }

    public String getName(){
        return "";
    }

    public static int calculateColour(float temperature) {
        float var = temperature / 3.0F;
        var = Mth.clamp(var, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - var * 0.05F, 0.5F + var * 0.1F, 1.0F);
    }
}
