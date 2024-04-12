package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VEBlock extends Block {
    private String rName;

    public VEBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public VEBlock(BlockBehaviour.Properties properties, String rName) {
        super(properties);
        this.rName = rName;
    }

    public void setRName(String rName) {
        this.rName = rName;
    }

    public String getRName() {
        return rName;
    }
}
