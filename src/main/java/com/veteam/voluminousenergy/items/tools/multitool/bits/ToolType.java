package com.veteam.voluminousenergy.items.tools.multitool.bits;

public enum ToolType {
    PICKAXE(1),
    AXE(2),
    SHOVEL(3),
    TRIMMER(4);

    private final int value;

    ToolType(int value){
        this.value = value;
    }

    public int value(){
        return value;
    }
}
