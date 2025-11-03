package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;

import java.util.Set;
import java.util.UUID;

public class BitItemData {
    private Set<ToolAction> action;
    private Tier tier;
    private TagKey<Block> mineableBlocks;
    private float destroySpeed;
    private float attackDamage;
    private final int toolTier;
    private final int toolType;
    protected static final float DESTROY_SPEED_MULTIPLIER = 0.9F;

    public BitItemData(Set<ToolAction> action, Tier bitTier, TagKey<Block> mineableBlockTag, ToolTier toolTier, ToolType toolType) {
        this(action, bitTier, mineableBlockTag,  toolTier.value(), toolType.value());
    }

    public BitItemData(Set<ToolAction> action, Tier bitTier, TagKey<Block> mineableBlockTag, int toolTier, int toolType) {
        this.action = action;
        this.tier = bitTier;
        this.mineableBlocks = mineableBlockTag;
        this.destroySpeed =  DESTROY_SPEED_MULTIPLIER * this.tier.getSpeed();
        this.attackDamage = tier.getAttackDamageBonus();
        this.toolTier = toolTier;
        this.toolType = toolType;
    }

    public Tier getTier(){
        return this.tier;
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return blockState.is(this.mineableBlocks) ? this.destroySpeed : 1.0F;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public boolean canPerformAction(net.neoforged.neoforge.common.ToolAction action) {
        return this.action.contains(action);
    }

    public boolean isCorrectToolForDrops(ItemStack itemStack, BlockState blockState) {
        Tool tool = itemStack.get(DataComponents.TOOL);
        boolean toolValid = true;

        if (tool != null) {
            toolValid = tool.isCorrectForDrops(blockState);
        }

        return blockState.is(this.mineableBlocks) && toolValid;
    }

    public int getToolTier() {
        return toolTier;
    }

    public int getToolType() {
        return toolType;
    }

    public TagKey<Block> getMineableBlocks() {
        return mineableBlocks;
    }
}
