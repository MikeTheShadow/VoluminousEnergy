package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class VEBlock extends Block {
    private String rName;

    public VEBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void setRName(String rName) {
        this.rName = rName;
    }

    public String getRName() {
        return rName;
    }
}
