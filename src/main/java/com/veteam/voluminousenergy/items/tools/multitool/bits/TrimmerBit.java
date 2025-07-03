package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrimmerBit extends MultitoolBit {
    private Tier tier;
    private float destroySpeedMultiplier;

    public TrimmerBit(Set<ToolAction> action, Tier bitTier, float destroySpeedMultiplier, float attackDamage, float attackSpeed,int toolTier,int toolType) {
        super(action, bitTier, null, destroySpeedMultiplier, attackDamage, attackSpeed,toolTier,toolType);
        this.tier = bitTier;
        this.destroySpeedMultiplier = destroySpeedMultiplier;
    }

    public static Tool getToolBitProperties() {
        ArrayList<Block> blocks = TagUtil.getBlocksFromTagKey(BlockTags.LEAVES);

        blocks.addAll(List.of(
                Blocks.COBWEB,
                Blocks.VINE,
                Blocks.GLOW_LICHEN,
                Blocks.CAVE_VINES,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.DEAD_BUSH,
                Blocks.SHORT_GRASS,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.TRIPWIRE,
                Blocks.SEAGRASS,
                Blocks.TALL_SEAGRASS,
                Blocks.SEA_PICKLE,
                Blocks.KELP_PLANT,
                Blocks.KELP,
                Blocks.NETHER_SPROUTS,
                Blocks.NETHER_WART,
                Blocks.HANGING_ROOTS
        ));

        return new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(
                                blocks,
                                15.0F),
                        Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)
                ),
                0.0F,
                2
        );
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if (!blockState.is(Blocks.COBWEB) && !blockState.is(BlockTags.LEAVES)) {
            if (blockState.is(BlockTags.WOOL)) {
                return 5.0F * destroySpeedMultiplier;
            } else if (blockState.is(Blocks.VINE)
                    || blockState.is(Blocks.GLOW_LICHEN)
            ) {
                return destroySpeedMultiplier * 2.0F;
            } else if (blockState.is(BlockTags.CAVE_VINES)
                    || blockState.is(Blocks.TWISTING_VINES)
                    || blockState.is(Blocks.TWISTING_VINES_PLANT)
                    || blockState.is(Blocks.WEEPING_VINES)
                    || blockState.is(Blocks.WEEPING_VINES_PLANT)
                    || blockState.is(Blocks.DEAD_BUSH)
                    || blockState.is(Blocks.SHORT_GRASS)
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
            ) {
                return destroySpeedMultiplier;
            } else {
                return 1.0F;
            }
        }
        return 15.0F * destroySpeedMultiplier;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack itemStack,BlockState blockState) {
        // TODO: This if statement might be deprecated now, TO TEST!
        if (blockState.is(Blocks.COBWEB) || blockState.is(Blocks.REDSTONE_WIRE) || blockState.is(Blocks.TRIPWIRE) || blockState.is(BlockTags.LEAVES)) return true;

        return super.isCorrectToolForDrops(itemStack, blockState);
    }
}
