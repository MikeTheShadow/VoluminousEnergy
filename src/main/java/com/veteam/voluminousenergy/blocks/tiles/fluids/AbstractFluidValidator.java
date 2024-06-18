package com.veteam.voluminousenergy.blocks.tiles.fluids;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public interface AbstractFluidValidator {

    boolean validateFluid(FluidStack fluid, VETileEntity tile);

}
