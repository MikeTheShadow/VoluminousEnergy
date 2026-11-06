package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class VEFlowingFluidBlock extends LiquidBlock {
    private byte check = 0;

    public VEFlowingFluidBlock(FlowingFluid flowingFluid, Properties properties) {
        super(flowingFluid, properties);
    }
}
