package com.veteam.voluminousenergy.world.biomes;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class VEBiome {
    private Map<Biome.Climate, Integer> weightMap = new HashMap<Biome.Climate, Integer>();
    private RegistryKey<Biome> beachBiome = Biomes.BEACH;
    private RegistryKey<Biome> riverBiome = Biomes.RIVER;
    private BiFunction<Double, Double, Integer> foliageColorFunction;
    private BiFunction<Double, Double, Integer> grassColorFunction;
    private BiFunction<Double, Double, Integer> waterColorFunction;

    protected void configureBiome(Biome.Builder builder) {}

    protected void configureGeneration(BiomeGenerationSettings.Builder builder) {}

    protected void configureMobSpawns(MobSpawnInfo.Builder builder) {}

    protected void configureDefaultMobSpawns(MobSpawnInfo.Builder builder) {
        builder.isValidSpawnBiomeForPlayer();
    }

    public Biome build() {
        Biome.Builder biomeBuilder = new Biome.Builder();

        // Configure the biome generation
        BiomeGenerationSettings.Builder biomeGenBuilder = new BiomeGenerationSettings.Builder();
        this.configureGeneration(biomeGenBuilder);
        biomeBuilder.withGenerationSettings(biomeGenBuilder.build());

        // Configure mob spawning
        MobSpawnInfo.Builder mobSpawnBuilder = new MobSpawnInfo.Builder();
        this.configureDefaultMobSpawns(mobSpawnBuilder);
        this.configureMobSpawns(mobSpawnBuilder);
        biomeBuilder.withMobSpawnSettings(mobSpawnBuilder.copy());

        // Configure and build the biome
        this.configureBiome(biomeBuilder);
        return biomeBuilder.build();
    }

    public void setBeachBiome(RegistryKey<Biome> biome) {
        this.beachBiome = biome;
    }

    public void setRiverBiome(RegistryKey<Biome> biome) {
        this.riverBiome = biome;
    }

    public String getName(){
        return "";
    }

    public static int calculateColour(float temperature) {
        float var = temperature / 3.0F;
        var = MathHelper.clamp(var, -1.0F, 1.0F);
        return MathHelper.hsvToRGB(0.62222224F - var * 0.05F, 0.5F + var * 0.1F, 1.0F);
    }
}
