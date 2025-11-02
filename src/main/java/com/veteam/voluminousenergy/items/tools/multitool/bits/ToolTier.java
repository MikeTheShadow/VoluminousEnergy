package com.veteam.voluminousenergy.items.tools.multitool.bits;

public enum ToolTier {
    IRON(1),
    DIAMOND(2),
    TITANIUM(3),
    NIGHALITE(4),
    EIGHZO(5),
    SOLARIUM(6);

    private final int value;

    ToolTier(int value) {
        this.value = value;
    }

    public int value(){
        return value;
    }
}
