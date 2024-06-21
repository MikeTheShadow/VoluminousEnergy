package com.veteam.voluminousenergy.util.records;

import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

public record ChunkFluidData(int x, int z, List<FluidStack> fluids) {

}
