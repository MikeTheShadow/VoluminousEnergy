package com.veteam.voluminousenergy.world.aquifer;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
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
    public BlockState computeSubstance(int computeValue0, int someSortOfY, int computeValue1, double d0, double d1) { // See Aquifer#createDisabled
        return someSortOfY < this.fluidLevel ? this.fluid.createLegacyBlock() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean shouldScheduleFluidUpdate() {
        return false;
    }
}