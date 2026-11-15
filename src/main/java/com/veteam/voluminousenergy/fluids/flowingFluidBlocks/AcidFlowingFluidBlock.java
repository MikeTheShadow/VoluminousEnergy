package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;


/**
 * A fluid block that deals acid damage to every entity in it, scaled from the configured base damage by
 * the acid's strength. Acid damage is not fire damage, so fire immunity does not protect against it.
 */
public class AcidFlowingFluidBlock extends VEFlowingFluidBlock {
    private final float acidStrength;

    public AcidFlowingFluidBlock(FlowingFluid flowingFluid, Properties properties, float acidStrength) {
        super(flowingFluid, properties);
        this.acidStrength = acidStrength;
    }

    @Override
    protected void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn, InsideBlockEffectApplier insideBlockEffectApplier, boolean movingBlock) {

        entityIn.hurt(VEDamageTypes.acid(worldIn), Config.ACID_DAMAGE.get().floatValue() * acidStrength);

        super.entityInside(state, worldIn, pos, entityIn, insideBlockEffectApplier, movingBlock);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return true;
    }
}
