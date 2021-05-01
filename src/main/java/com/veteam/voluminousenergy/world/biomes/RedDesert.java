package com.veteam.voluminousenergy.world.biomes;

import com.veteam.voluminousenergy.world.surfaceBulider.VESurfaceBuilders;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.*;
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
        biomeBuilder.generationSettings(biomeGenBuilder.build());

        // Configure mob spawning
        MobSpawnInfo.Builder mobSpawnBuilder = new MobSpawnInfo.Builder();
        this.configureDefaultMobSpawns(mobSpawnBuilder);
        this.configureMobSpawns(mobSpawnBuilder);
        biomeBuilder.mobSpawnSettings(mobSpawnBuilder.build());

        // Configure and build the biome
        BiomeAmbience ambience = (new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(4159204)/*.fogColor(329011)*/.fogColor(12638463).skyColor(calculateColour(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285)/*.ambientMoodSound(justShutUp)*/.build();
        this.configureBiome(biomeBuilder.biomeCategory(Biome.Category.DESERT).depth(0.05F).scale(0.05F).temperature(2.0F).downfall(0.05F).precipitation(Biome.RainType.NONE).specialEffects(ambience));
        return biomeBuilder.build();
    }

    @Override
    protected void configureGeneration(BiomeGenerationSettings.Builder builder){
        // Surface Bulider
        SurfaceBuilderConfig config = new SurfaceBuilderConfig(Blocks.RED_SAND.defaultBlockState(),Blocks.RED_SAND.defaultBlockState(),Blocks.RED_SANDSTONE.defaultBlockState());
        ConfiguredSurfaceBuilder<SurfaceBuilderConfig> configuredSurfaceBuilder = new ConfiguredSurfaceBuilder<>(VESurfaceBuilders.RED_DESERT,config);
        builder.surfaceBuilder(configuredSurfaceBuilder);

        // Underground
        DefaultBiomeFeatures.addDefaultCarvers(builder);
        DefaultBiomeFeatures.addAncientDebris(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addFossilDecoration(builder);
        DefaultBiomeFeatures.addDefaultMonsterRoom(builder);

        // Above Ground
        DefaultBiomeFeatures.addDesertVegetation(builder);
        DefaultBiomeFeatures.addDesertExtraVegetation(builder);
        DefaultBiomeFeatures.addDesertExtraDecoration(builder);
    }

    @Override
    protected void configureMobSpawns(MobSpawnInfo.Builder builder){
        DefaultBiomeFeatures.desertSpawns(builder);
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
