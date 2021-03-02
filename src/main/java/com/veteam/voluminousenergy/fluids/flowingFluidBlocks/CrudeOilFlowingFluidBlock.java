package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class CrudeOilFlowingFluidBlock extends VEFlowingFluidBlock {

    public CrudeOilFlowingFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            entityIn.setMotionMultiplier(state, new Vector3d(0.8F, 0.75F, 0.8F));
        } else if (entityIn instanceof ItemEntity){
            entityIn.setMotionMultiplier(state, new Vector3d(0.8F, 0.75f, 0.8F));
        }
    }

}
