package com.veteam.voluminousenergy.fluids.flowingFluidBlocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.lang.ref.Reference;
import java.util.Optional;

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
