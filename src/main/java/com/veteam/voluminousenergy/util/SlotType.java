package com.veteam.voluminousenergy.util;

public enum SlotType {
    INPUT("slot.voluminousenergy.input_slot"),
    OUTPUT("slot.voluminousenergy.output_slot"),
    RNG_OUTPUT("slot.voluminousenergy.rng_slot"),
    FLUID_INPUT("slot.voluminousenergy.input_slot"),
    FLUID_OUTPUT("slot.voluminousenergy.output_slot"),
    FLUID_HYBRID("slot.voluminousenergy.output_slot");
    private final String translationKey;

    private
    SlotType(String translationKey) {

        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getNBTName(int id) {
        return this.name().toLowerCase() + "_" + id;
    }
}
