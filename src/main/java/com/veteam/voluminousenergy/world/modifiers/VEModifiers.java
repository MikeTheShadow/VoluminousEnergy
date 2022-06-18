package com.veteam.voluminousenergy.world.modifiers;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VEModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> VE_BIOME_MODIFIER_REGISTRY =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    public static RegistryObject<Codec<VEOreBiomeModifier>> VE_ORE_BIOME_MODIFIER = VE_BIOME_MODIFIER_REGISTRY.register("ore_biome_modifier", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    // declare fields
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(VEOreBiomeModifier::biomes),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(VEOreBiomeModifier::feature)
                    // declare constructor
            ).apply(builder, VEOreBiomeModifier::new)));

}
