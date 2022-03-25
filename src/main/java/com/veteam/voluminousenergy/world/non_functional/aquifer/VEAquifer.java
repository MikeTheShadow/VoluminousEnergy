package com.veteam.voluminousenergy.world.non_functional.aquifer;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class VEAquifer implements Aquifer {

    private FluidState fluid; //= CrudeOil.CRUDE_OIL.defaultFluidState();
    private int fluidLevel;

    public VEAquifer(FluidState fluidState, int fluidLevel){
        this.fluid = fluidState;
        this.fluidLevel = fluidLevel;
    }

    @Nullable
    @Override
    public BlockState computeSubstance(DensityFunction.FunctionContext context, double d0) { // See Aquifer#createDisabled
        return context.blockY() < this.fluidLevel ? this.fluid.createLegacyBlock() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean shouldScheduleFluidUpdate() {
        return false;
    }
}