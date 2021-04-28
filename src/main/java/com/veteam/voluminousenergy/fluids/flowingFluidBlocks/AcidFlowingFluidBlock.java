package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class AcidFlowingFluidBlock extends VEFlowingFluidBlock {
    public AcidFlowingFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {

        if (!entityIn.fireImmune() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn)) {
            entityIn.hurt(DamageSource.IN_FIRE, Config.ACID_DAMAGE.get().floatValue());
            entityIn.setSecondsOnFire(Config.ACID_FIRE_DURATION.get());
        }

        super.onEntityCollision(state, worldIn, pos, entityIn);
    }


}
