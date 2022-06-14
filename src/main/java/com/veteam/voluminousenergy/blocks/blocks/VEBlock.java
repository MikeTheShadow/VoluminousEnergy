package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class VEBlock extends Block {
    private String registryName;

    public VEBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistryName() {
        return registryName;
    }
}
