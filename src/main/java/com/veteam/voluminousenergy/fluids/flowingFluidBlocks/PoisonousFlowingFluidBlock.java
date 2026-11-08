package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

/**
 * A fluid block that keeps living entities in it poisoned. The Poison is refreshed to the given duration
 * only once it has run down by a second, so contact does not resend the effect every tick.
 */
public class PoisonousFlowingFluidBlock extends VEFlowingFluidBlock {
    private static final int REFRESH_MARGIN_TICKS = 20;

    private final Supplier<Integer> poisonDuration;

    public PoisonousFlowingFluidBlock(FlowingFluid flowingFluid, Properties properties, Supplier<Integer> poisonDuration) {
        super(flowingFluid, properties);
        this.poisonDuration = poisonDuration;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean movingBlock) {
        if (level.isClientSide() || !(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        int duration = poisonDuration.get();
        MobEffectInstance poison = livingEntity.getEffect(MobEffects.POISON);
        if (poison != null && poison.getDuration() > duration - REFRESH_MARGIN_TICKS) {
            return;
        }
        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, duration));
    }
}
