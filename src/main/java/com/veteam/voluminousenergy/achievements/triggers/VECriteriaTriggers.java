package com.veteam.voluminousenergy.achievements.triggers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VECriteriaTriggers {

    public static final DeferredRegister<CriterionTrigger<?>> VE_TRIGGER_REGISTRY = DeferredRegister.create(Registries.TRIGGER_TYPE, VoluminousEnergy.MODID);

    public static final Supplier<ConstructDimensionalLaserTrigger> CONSTRUCT_DIMENSIONAL_LASER_TRIGGER = VE_TRIGGER_REGISTRY.register("construct_dimensional_laser", ConstructDimensionalLaserTrigger::new);
}
