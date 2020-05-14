package com.veteam.voluminousenergy.fluids.RFNA;

import net.minecraft.fluid.IFluidState;

public class StillRedFumingNitricAcid extends RedFumingNitricAcidFluid {
    @Override
    public boolean isSource(IFluidState state) {
        return false;
    }

    @Override
    public int getLevel(IFluidState p_207192_1_) {
        return 0;
    }
}
