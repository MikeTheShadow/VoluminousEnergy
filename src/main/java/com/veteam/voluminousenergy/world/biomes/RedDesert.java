package com.veteam.voluminousenergy.world.biomes;

import com.veteam.voluminousenergy.world.surfaceBulider.VESurfaceBuilders;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class RedDesert extends VEBiome {

    public RedDesert(){}

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
        MoodSoundAmbience justShutUp = new MoodSoundAmbience(SoundEvents.ENTITY_SQUID_AMBIENT, Integer.MAX_VALUE/2,Integer.MAX_VALUE/2, Double.MAX_VALUE/2);
        BiomeAmbience ambience = (new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(4159204).setFogColor(329011).setFogColor(12638463).withSkyColor(calculateColour(2.0F)).withFoliageColor(10387789).withGrassColor(9470285).setMoodSound(justShutUp).build();
        this.configureBiome(biomeBuilder.category(Biome.Category.DESERT).depth(0.05F).scale(0.05F).temperature(2.0F).downfall(0.05F).precipitation(Biome.RainType.NONE).setEffects(ambience));
        return biomeBuilder.build();
    }

    @Override
    protected void configureGeneration(BiomeGenerationSettings.Builder builder){
        // Surface Bulider
        SurfaceBuilderConfig config = new SurfaceBuilderConfig(Blocks.RED_SAND.getDefaultState(),Blocks.RED_SAND.getDefaultState(),Blocks.RED_SANDSTONE.getDefaultState());
        ConfiguredSurfaceBuilder<SurfaceBuilderConfig> configuredSurfaceBuilder = new ConfiguredSurfaceBuilder<>(VESurfaceBuilders.RED_DESERT,config);
        builder.withSurfaceBuilder(configuredSurfaceBuilder);

        // Underground
        DefaultBiomeFeatures.withCavesAndCanyons(builder);
        DefaultBiomeFeatures.withDebrisOre(builder);
        DefaultBiomeFeatures.withOverworldOres(builder);
        DefaultBiomeFeatures.withFossils(builder);
        DefaultBiomeFeatures.withMonsterRoom(builder);

        // Above Ground
        DefaultBiomeFeatures.withDesertDeadBushes(builder);
        DefaultBiomeFeatures.withDesertVegetation(builder);
        DefaultBiomeFeatures.withDesertWells(builder);
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
