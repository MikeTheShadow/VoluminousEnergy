package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class VEFlowingFluidBlock extends FlowingFluidBlock {
    private byte check = 0;

    public VEFlowingFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if(entityIn instanceof LivingEntity){
            if((entityIn.getY() - entityIn.yOld) > 0 && !entityIn.isCrouching()){
                entityIn.makeStuckInBlock(state, new Vector3d(0.9F, -0.9F, 0.9F));
            } else if ((entityIn.getY() - entityIn.yOld) <= 0 || entityIn.isCrouching()) {
                entityIn.makeStuckInBlock(state, new Vector3d(0.9F, 0.9F, 0.9F));
            }
        } else if (entityIn instanceof ItemEntity){
            entityIn.makeStuckInBlock(state, new Vector3d(0.8F, -1, 0.8F));
        }

    }
}
