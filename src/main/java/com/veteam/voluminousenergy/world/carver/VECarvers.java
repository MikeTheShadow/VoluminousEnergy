package com.veteam.voluminousenergy.world.carver;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VECarvers {
    public static final DeferredRegister<WorldCarver<?>> VE_CARVER_REGISTRY = DeferredRegister.create(Registries.CARVER, VoluminousEnergy.MODID);

    public static Supplier<VEFluidCaveCarver> VE_FLUID_CAVE_CARVER = VE_CARVER_REGISTRY.register("ve_fluid_cave_carver", () -> new VEFluidCaveCarver(VEFluidCaveCarver.Configuration.CODEC));
}
