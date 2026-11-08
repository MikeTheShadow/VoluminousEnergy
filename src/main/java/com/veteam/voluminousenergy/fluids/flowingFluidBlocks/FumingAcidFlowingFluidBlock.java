package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * An acid that is also a strong oxidizer: besides its acid damage it sets entities in it on fire.
 */
public class FumingAcidFlowingFluidBlock extends AcidFlowingFluidBlock {
    public FumingAcidFlowingFluidBlock(FlowingFluid flowingFluid, Properties properties, float acidStrength) {
        super(flowingFluid, properties, acidStrength);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        entityIn.setRemainingFireTicks(Math.max(entityIn.getRemainingFireTicks(), Config.ACID_FIRE_DURATION.get()));
        super.entityInside(state, worldIn, pos, entityIn);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            worldIn.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double) pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), (double) pos.getY() + random.nextDouble() + random.nextDouble(), (double) pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
        }
    }
}
