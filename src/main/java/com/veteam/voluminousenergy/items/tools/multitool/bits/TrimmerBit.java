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
            } else if (blockState.is(Blocks.VINE)
                    || blockState.is(Blocks.GLOW_LICHEN)
            ){
                return destroySpeedMultiplier * 2.0F;
            } else if (blockState.is(BlockTags.CAVE_VINES)
                    || blockState.is(Blocks.TWISTING_VINES)
                    || blockState.is(Blocks.TWISTING_VINES_PLANT)
                    || blockState.is(Blocks.WEEPING_VINES)
                    || blockState.is(Blocks.WEEPING_VINES_PLANT)
                    || blockState.is(Blocks.DEAD_BUSH)
                    || blockState.is(Blocks.GRASS)
                    || blockState.is(Blocks.TALL_GRASS)
                    || blockState.is(Blocks.FERN)
                    || blockState.is(Blocks.TRIPWIRE)
                    || blockState.is(Blocks.SEAGRASS)
                    || blockState.is(Blocks.TALL_SEAGRASS)
                    || blockState.is(Blocks.SEA_PICKLE)
                    || blockState.is(Blocks.KELP_PLANT)
                    || blockState.is(Blocks.KELP)
                    || blockState.is(Blocks.NETHER_SPROUTS)
                    || blockState.is(Blocks.NETHER_WART)
                    || blockState.is(Blocks.HANGING_ROOTS)
            ){
                return destroySpeedMultiplier;
            } else {
                return 1.0F;
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
