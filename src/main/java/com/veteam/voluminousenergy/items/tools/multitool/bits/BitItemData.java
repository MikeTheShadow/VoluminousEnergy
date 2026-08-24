package com.veteam.voluminousenergy.items.tools.multitool.bits;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class BitItemData {
    private Set<ItemAbility> action;
    private ToolMaterial tier;
    private TagKey<Block> mineableBlocks;
    private float destroySpeed;
    private float attackDamage;
    private final int toolTier;
    private final int toolType;
    protected static final float DESTROY_SPEED_MULTIPLIER = 1;

    public BitItemData(Set<ItemAbility> action, ToolMaterial bitTier, TagKey<Block> mineableBlockTag, ToolTier toolTier, ToolType toolType) {
        this(action, bitTier, mineableBlockTag,  toolTier.value(), toolType.value());
    }

    public BitItemData(Set<ItemAbility> action, ToolMaterial bitTier, TagKey<Block> mineableBlockTag, int toolTier, int toolType) {
        this.action = action;
        this.tier = bitTier;
        this.mineableBlocks = mineableBlockTag;
        this.destroySpeed = this.tier.speed();
        this.attackDamage = tier.attackDamageBonus();
        this.toolTier = toolTier;
        this.toolType = toolType;
    }

    public ToolMaterial getTier(){
        return this.tier;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public boolean canPerformAction(net.neoforged.neoforge.common.ItemAbility action) {
        return this.action.contains(action);
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
