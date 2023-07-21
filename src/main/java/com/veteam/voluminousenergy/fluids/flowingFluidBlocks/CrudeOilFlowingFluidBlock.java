package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class CrudeOilFlowingFluidBlock extends VEFlowingFluidBlock {

    public CrudeOilFlowingFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            entityIn.makeStuckInBlock(state, new Vec3(0.8F, 0.75F, 0.8F));
        } else if (entityIn instanceof ItemEntity){
            entityIn.makeStuckInBlock(state, new Vec3(0.8F, 0.75f, 0.8F));
        }
    }

}
