package com.veteam.voluminousenergy.fluids.flowingFluidSource;

import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CrudeOilFlowingFluidSource extends ForgeFlowingFluid.Source {
    public CrudeOilFlowingFluidSource(Properties properties) {
        super(properties);
    }

    @Override
    public int getTickRate(IWorldReader worldReader){return 20;}
}
