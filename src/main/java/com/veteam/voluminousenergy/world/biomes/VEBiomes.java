package com.veteam.voluminousenergy.world.biomes;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;

import java.util.*;

public class VEBiomes {
    protected static ArrayList<Biome> veBiomeList = new ArrayList<>();

    public static void prepareRegistration(RegistryEvent.Register<Biome> event, VEBiome biome, BiomeManager.BiomeType biomeType, int weight, BiomeDictionary.Type... dictionaryType) {
        VEBiomes.registerBiome(biome, event);
        RegistryKey<Biome> registryKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(WorldGenRegistries.BIOME.getKey(VEBiomes.veBiomeList.get(veBiomeList.size()-1))));
        BiomeDictionary.addTypes(registryKey, dictionaryType);
        if (Config.GENERATE_VE_BIOMES.get()) BiomeManager.addBiome(biomeType, new BiomeManager.BiomeEntry(registryKey, weight));
    }

    public static void registerBiome(VEBiome veBiome, RegistryEvent.Register<Biome> event){
        Biome biome = veBiome.build();
        veBiomeList.add(biome);

        biome.setRegistryName(new ResourceLocation(VoluminousEnergy.MODID, veBiome.getName()));
        event.getRegistry().register(biome);
    }
}
