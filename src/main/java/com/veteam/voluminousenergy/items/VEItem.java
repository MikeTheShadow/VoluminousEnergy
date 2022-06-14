package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public abstract class VEItem extends Item {

    private String registryName;

    public VEItem(Properties properties) {
        super(properties);
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistryName() {
        return registryName;
    }
}
