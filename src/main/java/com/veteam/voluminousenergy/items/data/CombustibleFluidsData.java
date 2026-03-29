package com.veteam.voluminousenergy.items.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.REGISTRY_COMBUSTION_FLUID_CODEC;

public class CombustibleFluidsData {

    static final List<VERecipeCodecs.RegistryFluidValue> rawData = new ArrayList<>();
    static HashMap<Fluid, Integer> combustibleFluidData = new HashMap<>();

    //TODO I REALLY DON'T LIKE THE BUILDCACHE SPAM. TEST A FIX FOR CLIENT/SERVER BY CALLING IT AT THE END OF LOADDATA?

    public static void loadData(ResourceManager manager) {
        resetCache();
        ResourceLocation prefix = ResourceLocation.fromNamespaceAndPath("voluminousenergy", "fluid_data/combustion");
        Map<ResourceLocation, Resource> resourceLocations = manager.listResources(prefix.getPath(), s -> s.getPath().endsWith(".json"));

        for (Resource resource : resourceLocations.values()) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                if (jsonElement != null) {
                    DataResult<VERecipeCodecs.RegistryFluidValue> result = REGISTRY_COMBUSTION_FLUID_CODEC.parse(JsonOps.INSTANCE, jsonElement);
                    result.resultOrPartial(VoluminousEnergy.LOGGER::error)
                            .ifPresent(rawData::add);
                }
            } catch (IOException e) {
                VoluminousEnergy.LOGGER.error("Unable to read combustion fluid data ", e);
            }
        }
    }

    public static boolean isCombustible(FluidStack stack) {
        buildCache();
        return combustibleFluidData.containsKey(stack.getFluid());
    }

    public static boolean isCombustible(Fluid fluid) {
        buildCache();
        return combustibleFluidData.containsKey(fluid);
    }

    public static int getEnergyPerTick(FluidStack stack) {
        buildCache();
        return combustibleFluidData.getOrDefault(stack.getFluid(), 0);
    }

    public static List<Fluid> getAllCombustibleFluids() {
        buildCache();
        return new ArrayList<>(combustibleFluidData.keySet());
    }

    private static void buildCache() {
        if (!combustibleFluidData.isEmpty()) {
            return;
        }
        for (VERecipeCodecs.RegistryFluidValue rawItem : rawData) {
            for (Fluid fluid : rawItem.getAsValuePair().fluids) {
                combustibleFluidData.put(fluid, (int) rawItem.value());
            }
        }
    }

    public static HashMap<Fluid, Integer> getDataForNetworkTransfer() {
        buildCache();
        return combustibleFluidData;
    }

    public static void updateFromPacket(HashMap<Fluid, Integer> data) {
        combustibleFluidData = data;
    }

    private static void resetCache() {
        combustibleFluidData.clear();
    }
}
