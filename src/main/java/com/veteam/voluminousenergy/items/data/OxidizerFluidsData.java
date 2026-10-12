package com.veteam.voluminousenergy.items.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.REGISTRY_OXIDIZER_FLUID_CODEC;

public class OxidizerFluidsData {

    static final List<VERecipeCodecs.RegistryOxidizerValue> rawData = new ArrayList<>();
    static HashMap<Fluid, OxidizerProperties> oxidizerFluidData = new HashMap<>();

    /**
     * How strongly an oxidizer boosts a burn and how many mB of it one Combustion Generator burn consumes.
     */
    public record OxidizerProperties(float multiplier, int amount) {
    }

    public static void loadData(ResourceManager manager) {
        resetCache();
        Identifier prefix = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "fluid_data/oxidizers");
        Map<Identifier, Resource> resourceLocations = manager.listResources(prefix.getPath(), s -> s.getPath().endsWith(".json"));

        for (Resource resource : resourceLocations.values()) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                if (jsonElement != null) {
                    DataResult<VERecipeCodecs.RegistryOxidizerValue> result = REGISTRY_OXIDIZER_FLUID_CODEC.parse(JsonOps.INSTANCE, jsonElement);
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
        OxidizerProperties properties = getProperties(stack.getFluid());
        return properties == null ? 0 : properties.multiplier();
    }

    public static int getOxidizerAmount(Fluid fluid) {
        OxidizerProperties properties = getProperties(fluid);
        return properties == null ? 0 : properties.amount();
    }

    @Nullable
    private static OxidizerProperties getProperties(Fluid fluid) {
        for (var data : oxidizerFluidData.entrySet()) {
            if (data.getKey().isSame(fluid)) {
                return data.getValue();
            }
        }
        return null;
    }

    public static List<Fluid> getAllOxidizerFluids() {
        buildCache();
        return new ArrayList<>(oxidizerFluidData.keySet());
    }

    private static void buildCache() {
        if (!oxidizerFluidData.isEmpty()) {
            return;
        }
        for (VERecipeCodecs.RegistryOxidizerValue rawItem : rawData) {
            OxidizerProperties properties = new OxidizerProperties(rawItem.multiplier(), rawItem.amount());
            for (Fluid fluid : rawItem.getFluids()) {
                oxidizerFluidData.put(fluid, properties);
            }
        }
    }

    public static HashMap<Fluid, OxidizerProperties> getDataForNetworkTransfer() {
        buildCache();
        return oxidizerFluidData;
    }

    public static void updateFromPacket(HashMap<Fluid, OxidizerProperties> data) {
        oxidizerFluidData = data;
    }

    private static void resetCache() {
        oxidizerFluidData.clear();
    }
}