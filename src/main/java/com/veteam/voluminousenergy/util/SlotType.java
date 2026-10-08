package com.veteam.voluminousenergy.util;

public enum SlotType {
    INPUT("slot.voluminousenergy.input_slot"),
    OUTPUT("slot.voluminousenergy.output_slot"),
    UPGRADE("slot.voluminousenergy.upgrade_slot");
    private final String translationKey;

    private SlotType(String translationKey) {

        this.translationKey = translationKey;
    }

    public String getHoverName() {
        return translationKey;
    }

    public String getNBTName(int id) {
        return this.name().toLowerCase() + "_" + id;
    }

}
