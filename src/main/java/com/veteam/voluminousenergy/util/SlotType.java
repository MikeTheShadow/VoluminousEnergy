package com.veteam.voluminousenergy.util;

public enum SlotType {
    INPUT("slot.voluminousenergy.input_slot"),
    OUTPUT("slot.voluminousenergy.output_slot"),
    RNG_OUTPUT("slot.voluminousenergy.rng_slot"),
    FLUID_INPUT("slot.voluminousenergy.input_slot",true),
    FLUID_OUTPUT("slot.voluminousenergy.output_slot",true),
    @Deprecated
    FLUID_HYBRID("slot.voluminousenergy.output_slot",true);
    private final String translationKey;
    private boolean isFluidBucketIORelated = false;
    private
    SlotType(String translationKey) {

        this.translationKey = translationKey;
    }

    SlotType(String translationKey, boolean isFluidBucketIORelated) {
        this.translationKey = translationKey;
        this.isFluidBucketIORelated = true;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getNBTName(int id) {
        return this.name().toLowerCase() + "_" + id;
    }

    public boolean isFluidBucketIORelated() {
        return isFluidBucketIORelated;
    }
}
