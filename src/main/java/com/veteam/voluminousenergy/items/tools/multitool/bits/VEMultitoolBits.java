package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.tools.VETools;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ToolActions;

public class VEMultitoolBits {

    protected static final float DESTROY_SPEED_MULTIPLIER = 0.9F;


    // Drill bits
    public static final MultitoolBit IRON_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit DIAMOND_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit TITANIUM_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit NIGHALITE_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit EIGHZO_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit SOLARIUM_DRILL_BIT = new MultitoolBit(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_PICKAXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    // Chain bits
    public static final MultitoolBit IRON_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit DIAMOND_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit TITANIUM_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit NIGHALITE_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit EIGHZO_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit SOLARIUM_CHAIN_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_AXE,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    // Scooper Bit
    public static final MultitoolBit IRON_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit DIAMOND_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit TITANIUM_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit NIGHALITE_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit EIGHZO_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit SOLARIUM_SCOOPER_BIT = new MultitoolBit(ToolActions.DEFAULT_AXE_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_SHOVEL,
            DESTROY_SPEED_MULTIPLIER, 1, 1);

    public static final MultitoolBit IRON_TRIMMER_BIT = new TrimmerBit(ToolActions.DEFAULT_SHEARS_ACTIONS, Tiers.IRON, 0.9F, 1, 1);
    public static final MultitoolBit DIAMOND_TRIMMER_BIT = new TrimmerBit(ToolActions.DEFAULT_SHEARS_ACTIONS, Tiers.DIAMOND, Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getAttackDamageBonus(), Tiers.DIAMOND.getSpeed());

}
