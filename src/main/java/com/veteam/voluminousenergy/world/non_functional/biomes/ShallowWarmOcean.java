package com.veteam.voluminousenergy.world.non_functional.biomes;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class ShallowWarmOcean extends VEBiome {

    public ShallowWarmOcean(){}

    @Override
    public final Biome build(){
        //super((new Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG).precipitation(RainType.RAIN).category(Category.OCEAN).depth(-0.3F).scale(0.03F).temperature(0.5F).downfall(0.5F).waterColor(4445678).waterFogColor(270131).parent((String)null));
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
        BiomeSpecialEffects ambience = (new BiomeSpecialEffects.Builder()).waterColor(4445678).waterFogColor(270131)/*.fogColor(329011)*/.fogColor(12638463).skyColor(calculateColour(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285).build();
        this.configureBiome(biomeBuilder.biomeCategory(Biome.BiomeCategory.OCEAN).temperature(0.5F).downfall(0.5F).precipitation(Biome.Precipitation.RAIN).specialEffects(ambience));
        return biomeBuilder.build();
    }

    @Override
    protected void configureGeneration(BiomeGenerationSettings.Builder builder){
        // Surface builder
        //SurfaceBuilderBaseConfiguration surfaceBuilderConfig = new SurfaceBuilderBaseConfiguration(Blocks.SAND.defaultBlockState(),Blocks.GRAVEL.defaultBlockState(), Blocks.STONE.defaultBlockState());
        //ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> configuredSurfaceBuilder = new ConfiguredSurfaceBuilder<>(SurfaceBuilder.DEFAULT.DEFAULT,surfaceBuilderConfig);
        //builder.surfaceBuilder(configuredSurfaceBuilder);

        // Structures Builder
        //builder.addStructureStart(StructureFeatures.SHIPWRECK);
        //builder.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
        //builder.addStructureStart(StructureFeatures.OCEAN_RUIN_WARM);

        // Underground
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder); // Granite, Andesite, Diorite, etc
        BiomeDefaultFeatures.addDefaultSoftDisks(builder); // Sand, Clay, Gravel, etc

        // Above Ground
        //BiomeDefaultFeatures.addOceanCarvers(builder);
        BiomeDefaultFeatures.addWarmFlowers(builder);
        BiomeDefaultFeatures.addLukeWarmKelp(builder);

        //builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION /*Features.WARM_OCEAN_VEGETATION*/);
    }

    @Override
    protected void configureMobSpawns(MobSpawnSettings.Builder builder){
        BiomeDefaultFeatures.warmOceanSpawns(builder, 1, 1);
    }

    @Override
    protected void configureBiome(Biome.BiomeBuilder builder){
        builder.precipitation(Biome.Precipitation.RAIN);
    }

    @Override
    public String getName(){
        return "shallow_warm_ocean";
    }
}