package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

/**
 * Burn times of the solid fuels, defined in {@code data/voluminousenergy/context_int_provider/cooking}
 * in the same form as vanilla's, so smokers and blast furnaces burn them for half as long.
 */
public class VECookingFuels {
    public static final ResourceKey<ContextIntProvider> TIME_PETCOKE = createKey("cooking/time_petcoke");
    public static final ResourceKey<ContextIntProvider> TIME_COAL_COKE = createKey("cooking/time_coal_coke");
    public static final ResourceKey<ContextIntProvider> TIME_ROSIN = createKey("cooking/time_rosin");
    public static final ResourceKey<ContextIntProvider> TIME_ANIMAL_FAT = createKey("cooking/time_animal_fat");
    public static final ResourceKey<ContextIntProvider> TIME_TINY_FUEL = createKey("cooking/time_tiny_fuel");

    private VECookingFuels() {
    }

    private static ResourceKey<ContextIntProvider> createKey(String path) {
        return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, path));
    }
}
