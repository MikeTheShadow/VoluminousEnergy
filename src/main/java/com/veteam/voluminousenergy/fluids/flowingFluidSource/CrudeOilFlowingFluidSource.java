package com.veteam.voluminousenergy.fluids.flowingFluidSource;

import net.minecraft.world.level.LevelReader;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CrudeOilFlowingFluidSource extends ForgeFlowingFluid.Source {
    public CrudeOilFlowingFluidSource(Properties properties) {
        super(properties);
    }

    @Override
    public int getTickDelay(LevelReader worldReader){return 20;}
}
