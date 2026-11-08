package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/**
 * Keys and sources for the mod's damage types, which are defined as datapack entries under
 * {@code data/voluminousenergy/damage_type}.
 */
public class VEDamageTypes {
    public static final ResourceKey<DamageType> ASPHYXIATION = key("asphyxiation");
    public static final ResourceKey<DamageType> ACID = key("acid");

    private VEDamageTypes() {
    }

    public static DamageSource asphyxiation(Level level) {
        return source(level, ASPHYXIATION);
    }

    public static DamageSource acid(Level level) {
        return source(level, ACID);
    }

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, name));
    }

    private static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }
}
