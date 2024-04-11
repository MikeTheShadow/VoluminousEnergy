package com.veteam.voluminousenergy.blocks.tiles.fluids;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraftforge.fluids.FluidStack;

public interface AbstractFluidValidator {

    boolean validateFluid(FluidStack fluid, VETileEntity tile);

}
