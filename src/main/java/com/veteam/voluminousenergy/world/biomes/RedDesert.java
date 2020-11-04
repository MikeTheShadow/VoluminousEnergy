package com.veteam.voluminousenergy.world.biomes;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class RedDesert extends VEBiome {

    public RedDesert(){
        Biome.Climate climate = new Biome.Climate(Biome.RainType.NONE, 100F, Biome.TemperatureModifier.NONE, 1F);
        this.addWeight(climate, 5);
    }

    @Override
    public final Biome build() {
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
        BiomeAmbience ambience = (new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(4159204).setFogColor(329011).setFogColor(12638463).withSkyColor(2).withFoliageColor(1).setAmbientSound(MoodSoundAmbience.DEFAULT_CAVE.getSound()).build();
        this.configureBiome(biomeBuilder.category(Biome.Category.DESERT).depth(10).scale(10).temperature(100).downfall(0).precipitation(Biome.RainType.NONE).setEffects(ambience));
        return biomeBuilder.build();
    }

    @Override
    protected void configureGeneration(BiomeGenerationSettings.Builder builder){
        // Surface Bulider
        SurfaceBuilderConfig config = new SurfaceBuilderConfig(Blocks.RED_SAND.getDefaultState(),Blocks.RED_SANDSTONE.getDefaultState(),Blocks.RED_SAND.getDefaultState());
        ConfiguredSurfaceBuilder<SurfaceBuilderConfig> configuredSurfaceBuilder = new ConfiguredSurfaceBuilder<>(SurfaceBuilder.DEFAULT,config);
        builder.withSurfaceBuilder(configuredSurfaceBuilder);

        // Underground
        DefaultBiomeFeatures.withCavesAndCanyons(builder);
        DefaultBiomeFeatures.withDebrisOre(builder);
        DefaultBiomeFeatures.withFossils(builder);

        // Above Ground
        DefaultBiomeFeatures.withDesertDeadBushes(builder);
        DefaultBiomeFeatures.withDesertVegetation(builder);
        DefaultBiomeFeatures.withDesertWells(builder);
        //DefaultBiomeFeatures.withDesertMobs(builder);
    }

    @Override
    protected void configureMobSpawns(MobSpawnInfo.Builder builder){
        DefaultBiomeFeatures.withDesertMobs(builder);
    }

    @Override
    protected void configureBiome(Biome.Builder builder){
        builder.precipitation(Biome.RainType.NONE);
    }

    @Override
    public String getName(){
        return "red_desert";
    }
}
