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

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.REGISTRY_OXIDIZER_FLUID_CODEC;

public class OxidizerFluidsData {

    static final List<VERecipeCodecs.RegistryFluidValue> rawData = new ArrayList<>();
    static HashMap<Fluid, Float> oxidizerFluidData = new HashMap<>();

    public static void loadData(ResourceManager manager) {
        resetCache();
        ResourceLocation prefix = new ResourceLocation("voluminousenergy", "fluid_data/oxidizers");
        Map<ResourceLocation, Resource> resourceLocations = manager.listResources(prefix.getPath(), s -> s.getPath().endsWith(".json"));

        for (Resource resource : resourceLocations.values()) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                if (jsonElement != null) {
                    DataResult<VERecipeCodecs.RegistryFluidValue> result = REGISTRY_OXIDIZER_FLUID_CODEC.parse(JsonOps.INSTANCE, jsonElement);
                    result.resultOrPartial(VoluminousEnergy.LOGGER::error)
                            .ifPresent(rawData::add);
                }
            } catch (IOException e) {
                VoluminousEnergy.LOGGER.error("Unable to read oxidizer fluid data ", e);
            }
        }
    }


    public static boolean isOxidizer(FluidStack stack) {
        buildCache();
        return oxidizerFluidData.containsKey(stack.getFluid());
    }


    public static float getOxidizerMultiplier(FluidStack stack) {
        for(var data : oxidizerFluidData.entrySet()) {
            if(data.getKey().isSame(stack.getFluid())) return data.getValue();
        }
        return 0;
    }

    public static List<Fluid> getAllOxidizerFluids() {
        buildCache();
        return new ArrayList<>(oxidizerFluidData.keySet());
    }

    private static void buildCache() {
        if (!oxidizerFluidData.isEmpty()) {
            return;
        }
        for (VERecipeCodecs.RegistryFluidValue rawItem : rawData) {

            for (Fluid fluid : rawItem.getAsValuePair().fluids) {
                oxidizerFluidData.put(fluid, rawItem.value());
            }
        }
    }

    public static HashMap<Fluid, Float> getDataForNetworkTransfer() {
        buildCache();
        return oxidizerFluidData;
    }

    public static void updateFromPacket(HashMap<Fluid, Float> data) {
        oxidizerFluidData = data;
    }

    private static void resetCache() {
        oxidizerFluidData.clear();
    }
}