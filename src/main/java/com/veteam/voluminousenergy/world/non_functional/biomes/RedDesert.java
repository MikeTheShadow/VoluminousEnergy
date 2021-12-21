package com.veteam.voluminousenergy.world.non_functional.biomes;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class RedDesert extends VEBiome {

    public RedDesert(){}

    @Override
    public final Biome build() {
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
        BiomeSpecialEffects ambience = (new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(4159204)/*.fogColor(329011)*/.fogColor(12638463).skyColor(calculateColour(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285)/*.ambientMoodSound(justShutUp)*/.build();
        this.configureBiome(biomeBuilder.biomeCategory(Biome.BiomeCategory.DESERT).temperature(2.0F).downfall(0.05F).precipitation(Biome.Precipitation.NONE).specialEffects(ambience));
        return biomeBuilder.build();
    }

    @Override
    protected void configureGeneration(BiomeGenerationSettings.Builder builder){
        // Surface Builder TODO: port to 1.18
        //SurfaceBuilderBaseConfiguration config = new SurfaceBuilderBaseConfiguration(Blocks.RED_SAND.defaultBlockState(),Blocks.RED_SAND.defaultBlockState(),Blocks.RED_SANDSTONE.defaultBlockState());
        //ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> configuredSurfaceBuilder = new ConfiguredSurfaceBuilder<>(VESurfaceBuilders.RED_DESERT,config);
        //builder.surfaceBuilder(configuredSurfaceBuilder);

        // Structures Builder
        //builder.addStructureStart(StructureFeatures.DESERT_PYRAMID);
        //builder.addStructureStart(StructureFeatures.RUINED_PORTAL_DESERT);
        //builder.addStructureStart(StructureFeatures.VILLAGE_DESERT);
        //builder.addStructureStart(StructureFeatures.STRONGHOLD);

        // Underground
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addFossilDecoration(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder); // Granite, Andesite, Diorite, etc
        BiomeDefaultFeatures.addDefaultSoftDisks(builder); // Sand, Clay, Gravel, etc

        // Above Ground
        BiomeDefaultFeatures.addDesertVegetation(builder); // Dead bush patches
        BiomeDefaultFeatures.addDesertExtraVegetation(builder); // Sugar Cane patches, Pumpkins(?!), Cactus
        BiomeDefaultFeatures.addDesertExtraDecoration(builder); // Desert Wells
        BiomeDefaultFeatures.addDefaultMushrooms(builder); // add Mushrooms
    }

    @Override
    protected void configureMobSpawns(MobSpawnSettings.Builder builder){
        BiomeDefaultFeatures.desertSpawns(builder);
    }

    @Override
    protected void configureBiome(Biome.BiomeBuilder builder){
        builder.precipitation(Biome.Precipitation.NONE);
    }

    @Override
    public String getName(){
        return "red_desert";
    }
}
