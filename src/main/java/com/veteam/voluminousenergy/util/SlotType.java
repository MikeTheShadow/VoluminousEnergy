package com.veteam.voluminousenergy.util;

public enum SlotType {
    INPUT("tilePos.voluminousenergy.input_slot"),
    OUTPUT("tilePos.voluminousenergy.output_slot"),
    UPGRADE("tilePos.voluminousenergy.upgrade_slot"),
    FLUID_INPUT("tilePos.voluminousenergy.input_slot",true),
    FLUID_OUTPUT("tilePos.voluminousenergy.output_slot",true);
    private final String translationKey;
    private boolean isFluidBucketIORelated = false;
    private
    SlotType(String translationKey) {

        this.translationKey = translationKey;
    }

    SlotType(String translationKey, boolean isFluidBucketIORelated) {
        this.translationKey = translationKey;
        this.isFluidBucketIORelated = isFluidBucketIORelated;
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
