package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class FumingAcidFlowingFluidBlock extends AcidFlowingFluidBlock {
    public FumingAcidFlowingFluidBlock(FlowingFluid flowingFluid, Properties properties) {
        super(flowingFluid, properties);
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            worldIn.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double) pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), (double) pos.getY() + random.nextDouble() + random.nextDouble(), (double) pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        }
    }
}
