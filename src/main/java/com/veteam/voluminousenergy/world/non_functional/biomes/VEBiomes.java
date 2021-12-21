package com.veteam.voluminousenergy.world.non_functional.biomes;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.Objects;

public class VEBiomes {
    protected static ArrayList<Biome> veBiomeList = new ArrayList<>();

    public static void prepareRegistration(RegistryEvent.Register<Biome> event, VEBiome biome, BiomeManager.BiomeType biomeType, int weight, BiomeDictionary.Type... dictionaryType) {
        VEBiomes.registerBiome(biome, event);
        ResourceKey<Biome> registryKey = ResourceKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(BuiltinRegistries.BIOME.getKey(VEBiomes.veBiomeList.get(veBiomeList.size()-1))));
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
