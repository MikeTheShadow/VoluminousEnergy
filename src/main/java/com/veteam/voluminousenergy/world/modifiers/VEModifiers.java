package com.veteam.voluminousenergy.world.modifiers;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class VEModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> VE_BIOME_MODIFIER_REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    public static Supplier<MapCodec<VEOreBiomeModifier>> VE_ORE_BIOME_MODIFIER = VE_BIOME_MODIFIER_REGISTRY.register("ore_biome_modifier", () ->
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                    // declare fields
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(VEOreBiomeModifier::biomes),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(VEOreBiomeModifier::feature)
                    // declare constructor
            ).apply(builder, VEOreBiomeModifier::new)));

    public static Supplier<MapCodec<VEAndedMultiBiomeModifier>> VE_ANDED_MULTI_BIOME_MODIFIER = VE_BIOME_MODIFIER_REGISTRY.register("anded_biome_modifier", () ->
            RecordCodecBuilder.mapCodec((test) -> {
                return test.group(
                        Codec.STRING.fieldOf("whitelisted_biomes").forGetter(VEAndedMultiBiomeModifier::getWhitelistedBiome),
                        Codec.STRING.fieldOf("blacklisted_biomes").forGetter(VEAndedMultiBiomeModifier::getBlacklistedBiome),
                        ConfiguredFeature.CODEC.fieldOf("feature").forGetter(VEAndedMultiBiomeModifier::getFeature),
                        Codec.BOOL.fieldOf("triangular_placement").forGetter(VEAndedMultiBiomeModifier::getIsTriangular),
                        Codec.INT.fieldOf("count").forGetter(VEAndedMultiBiomeModifier::getCount),
                        Codec.INT.fieldOf("bottom_anchor").forGetter(VEAndedMultiBiomeModifier::getBottomAnchor),
                        Codec.INT.fieldOf("top_anchor").forGetter(VEAndedMultiBiomeModifier::getTopAnchor),
                        Codec.INT.fieldOf("rarity").forGetter(VEAndedMultiBiomeModifier::getRarity),
                        Codec.STRING.fieldOf("generation_step").forGetter(VEAndedMultiBiomeModifier::getGenerationStep)
                ).apply(test, VEAndedMultiBiomeModifier::new);
            }));
}