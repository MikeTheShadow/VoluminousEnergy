package com.veteam.voluminousenergy.world.modifiers;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record VEOreBiomeModifier(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) implements BiomeModifier {

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        if (Config.WORLD_GEN_LOGGING.get()){
            System.out.println("DEBUG: " + RegistryLookups.lookupBiome(biome) + " ACTIVE KEYS:\n"
                    + biome.getTagKeys().toString());
        }

        // add a feature to all specified biomes
        if (biomes.contains(biome)) {
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return VEModifiers.VE_ORE_BIOME_MODIFIER.get();
    }
}