package com.veteam.voluminousenergy.fluids.flowingFluidSource;

import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class CrudeOilFlowingFluidSource extends BaseFlowingFluid.Source {
    public CrudeOilFlowingFluidSource(Properties properties) {
        super(properties);
    }

    @Override
    public int getTickDelay(LevelReader worldReader) {
        return 20;
    }
}
