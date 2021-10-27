package com.veteam.voluminousenergy.items.tools.multitool.bits;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.Set;

public class TrimmerBit extends MultitoolBit{
    private Tier tier;
    private float destroySpeedMultiplier;

    public TrimmerBit(Set<ToolAction> action, Tier bitTier, float destroySpeedMultiplier, float attackDamage, float attackSpeed) {
        super(action, bitTier, null, destroySpeedMultiplier, attackDamage, attackSpeed);
        this.tier = bitTier;
        this.destroySpeedMultiplier = destroySpeedMultiplier;
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if (!blockState.is(Blocks.COBWEB) && !blockState.is(BlockTags.LEAVES)) {
            if (blockState.is(BlockTags.WOOL)) {
                return 5.0F * destroySpeedMultiplier;
            } else {
                return !blockState.is(Blocks.VINE) && !blockState.is(Blocks.GLOW_LICHEN) ?
                        destroySpeedMultiplier : destroySpeedMultiplier * 2.0F;
            }
        }
        return 15.0F * destroySpeedMultiplier;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockState){
        return (blockState.is(Blocks.COBWEB) || blockState.is(Blocks.REDSTONE_WIRE) || blockState.is(Blocks.TRIPWIRE))
                && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(this.tier, blockState);
    }
}
