package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ToolActions;

public class VEMultitoolBitData {

    private static final TagKey<Block> MINEABLE_WITH_TRIMMER = TagUtil.getBlockTagKeyFromLocation(new ResourceLocation(VoluminousEnergy.MODID, "mineable/trimmer"));

    // Drill bits 
    public static final BitItemData IRON_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.IRON, ToolType.PICKAXE);

    public static final BitItemData DIAMOND_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.DIAMOND, ToolType.PICKAXE);

    public static final BitItemData TITANIUM_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.TITANIUM, ToolType.PICKAXE);

    public static final BitItemData NIGHALITE_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.NIGHALITE, ToolType.PICKAXE);

    public static final BitItemData EIGHZO_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.EIGHZO, ToolType.PICKAXE);

    public static final BitItemData SOLARIUM_DRILL_BIT_DATA = new BitItemData(ToolActions.DEFAULT_PICKAXE_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_PICKAXE,
             ToolTier.SOLARIUM, ToolType.PICKAXE);

    // Chain bits
    public static final BitItemData IRON_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.IRON, ToolType.AXE);

    public static final BitItemData DIAMOND_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.DIAMOND, ToolType.AXE);

    public static final BitItemData TITANIUM_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.TITANIUM, ToolType.AXE);

    public static final BitItemData NIGHALITE_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.NIGHALITE, ToolType.AXE);

    public static final BitItemData EIGHZO_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.EIGHZO, ToolType.AXE);

    public static final BitItemData SOLARIUM_CHAIN_BIT_DATA = new BitItemData(ToolActions.DEFAULT_AXE_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_AXE,
             ToolTier.SOLARIUM, ToolType.AXE);

    // Scooper Bit
    public static final BitItemData IRON_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, Tiers.IRON, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.IRON, ToolType.SHOVEL);

    public static final BitItemData DIAMOND_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.DIAMOND, ToolType.SHOVEL);

    public static final BitItemData TITANIUM_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, VETools.TITANIUM, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.TITANIUM, ToolType.SHOVEL);

    public static final BitItemData NIGHALITE_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, VETools.NIGHALITE, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.NIGHALITE, ToolType.SHOVEL);

    public static final BitItemData EIGHZO_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, VETools.EIGHZO, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.EIGHZO, ToolType.SHOVEL);

    public static final BitItemData SOLARIUM_SCOOPER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHOVEL_ACTIONS, VETools.SOLARIUM, BlockTags.MINEABLE_WITH_SHOVEL,
             ToolTier.SOLARIUM, ToolType.SHOVEL);

    // Trimmer bit
    public static final BitItemData IRON_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, Tiers.IRON, MINEABLE_WITH_TRIMMER,
             ToolTier.IRON, ToolType.TRIMMER);

    public static final BitItemData DIAMOND_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, Tiers.DIAMOND, MINEABLE_WITH_TRIMMER,
             ToolTier.DIAMOND, ToolType.TRIMMER);

    public static final BitItemData TITANIUM_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, VETools.TITANIUM, MINEABLE_WITH_TRIMMER,
             ToolTier.TITANIUM, ToolType.TRIMMER);

    public static final BitItemData NIGHALITE_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, VETools.NIGHALITE, MINEABLE_WITH_TRIMMER,
             ToolTier.NIGHALITE, ToolType.TRIMMER);

    public static final BitItemData EIGHZO_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, VETools.EIGHZO, MINEABLE_WITH_TRIMMER,
             ToolTier.EIGHZO, ToolType.TRIMMER);

    public static final BitItemData SOLARIUM_TRIMMER_BIT_DATA = new BitItemData(ToolActions.DEFAULT_SHEARS_ACTIONS, VETools.SOLARIUM, MINEABLE_WITH_TRIMMER,
             ToolTier.SOLARIUM, ToolType.TRIMMER);
}