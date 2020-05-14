package com.veteam.voluminousenergy.fluids.RFNA;

import com.veteam.voluminousenergy.fluids.VEFluids;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;

public abstract class RedFumingNitricAcidFluid extends FumingNitricAcid {

    @Override
    public Fluid getFlowingFluid() {
        return VEFluids.FLOWING_RED_FUMING_NITRIC_ACID;
    }

    @Override
    public Fluid getStillFluid() {
        return VEFluids.RED_FUMING_NITRIC_ACID;
    }

    @Override
    public Item getFilledBucket() {
        return VEFluids.RED_FUMING_NITRIC_ACID_BUCKET;
    }


    public static class Flowing extends FumingNitricAcid {
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(IFluidState p_207192_1_) {
            return p_207192_1_.get(LEVEL_1_8);
        }

        public boolean isSource(IFluidState state) {
            return false;
        }
    }

    public static class Source extends FumingNitricAcid {
        public int getLevel(IFluidState p_207192_1_) {
            return 8;
        }

        public boolean isSource(IFluidState state) {
            return true;
        }
    }

}
