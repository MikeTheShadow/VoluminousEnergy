package com.veteam.voluminousenergy.world.biomes;

import com.google.common.collect.Maps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.logging.Logger;

public class BiomeRegistry {
    public static ArrayList<Biome> VE_BIOME_LIST = new ArrayList<>();

    public static void bioReg(RegistryEvent.Register<Biome> event) {
        // Red Desert
        BiomeRegistry.Register(new RedDesert(), event);
        RegistryKey<Biome> registryKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(WorldGenRegistries.BIOME.getKey(BiomeRegistry.VE_BIOME_LIST.get(0))));
        BiomeDictionary.addTypes(registryKey, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(registryKey, 5));
    }

    public static void Register(VEBiome veBiome, RegistryEvent.Register<Biome> event){
        Biome biome = veBiome.build();
        VE_BIOME_LIST.add(biome);
        String name = veBiome.getName();

        biome.setRegistryName(new ResourceLocation(VoluminousEnergy.MODID, name));
        event.getRegistry().register(biome);
    }
}
