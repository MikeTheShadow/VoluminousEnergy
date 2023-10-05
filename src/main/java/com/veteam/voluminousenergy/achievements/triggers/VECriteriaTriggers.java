package com.veteam.voluminousenergy.achievements.triggers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;

public class VECriteriaTriggers {

    public static final ConstructDimensionalLaserTrigger CONSTRUCT_DIMENSIONAL_LASER_TRIGGER = register(new ConstructDimensionalLaserTrigger());

    public static <T extends CriterionTrigger<?>> T register(T p_10596_) {
        return CriteriaTriggers.register("volumniousenergy:construct_dimensional_laser",p_10596_);
    }

    public static void init() {

    }
}
