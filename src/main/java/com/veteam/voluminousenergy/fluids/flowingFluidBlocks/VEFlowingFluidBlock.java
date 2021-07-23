package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VEFlowingFluidBlock extends LiquidBlock {
    private byte check = 0;

    public VEFlowingFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if(entityIn instanceof LivingEntity){
            if((entityIn.getY() - entityIn.yOld) > 0 && !entityIn.isCrouching()){
                entityIn.makeStuckInBlock(state, new Vec3(0.9F, -0.9F, 0.9F));
            } else if ((entityIn.getY() - entityIn.yOld) <= 0 || entityIn.isCrouching()) {
                entityIn.makeStuckInBlock(state, new Vec3(0.9F, 0.9F, 0.9F));
            }
        } else if (entityIn instanceof ItemEntity){
            entityIn.makeStuckInBlock(state, new Vec3(0.8F, -1, 0.8F));
        }

    }
}
