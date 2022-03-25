package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.Set;
import java.util.UUID;

public class MultitoolBit {
    private Set<ToolAction> action;
    private Tier tier;
    private TagKey<Block> mineableBlocks;
    private float destroySpeed;
    private float attackDamage;
    private float attackSpeed;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public MultitoolBit(Set<ToolAction> action, Tier bitTier, TagKey<Block> mineableBlockTag, float destroySpeedMultiplier, float attackDamage, float attackSpeed){
        this.action = action;
        this.tier = bitTier;
        this.mineableBlocks = mineableBlockTag;
        this.destroySpeed = destroySpeedMultiplier * this.tier.getSpeed();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "Tool modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "Tool modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    /*public Tier getTier(){
        return this.tier;
    }

    public Tag.Named<Block> getBlockTag(){
        return this.mineableBlocks;
    }*/

    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return blockState.is(this.mineableBlocks) ? this.destroySpeed : 1.0F;
    }

    public float getAttackDamage(){
        return this.attackDamage;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return this.defaultModifiers;
    }

    public boolean canPerformAction(net.minecraftforge.common.ToolAction action){
        return this.action.contains(action);
    }

    public boolean isCorrectToolForDrops(BlockState blockState){
        return blockState.is(this.mineableBlocks)
                && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(this.tier, blockState);
    }
}
