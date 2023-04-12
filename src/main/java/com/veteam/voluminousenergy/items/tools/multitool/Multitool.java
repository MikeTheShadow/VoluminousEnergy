package com.veteam.voluminousenergy.items.tools.multitool;

import com.google.common.collect.Multimap;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Multitool extends Item /*implements Vanishable*/ {
    protected MultitoolBit bit;

    public Multitool(MultitoolBit bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.bit = bit;
        setRegistryName(registryName);
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockStateToMine) {
        return this.bit != null ? this.bit.getDestroySpeed(itemStack, blockStateToMine) : 0;
    }

    public boolean hurtEnemy(ItemStack p_40994_, LivingEntity p_40995_, LivingEntity p_40996_) {
        p_40994_.hurtAndBreak(2, p_40996_, (p_41007_) -> {
            p_41007_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public boolean mineBlock(ItemStack p_40998_, Level p_40999_, BlockState p_41000_, BlockPos p_41001_, LivingEntity p_41002_) {
        if (!p_40999_.isClientSide && p_41000_.getDestroySpeed(p_40999_, p_41001_) != 0.0F) {
            p_40998_.hurtAndBreak(1, p_41002_, (p_40992_) -> {
                p_40992_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Nullable
    public MultitoolBit getBit(){
        return this.bit != null ? this.bit : null;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if(this.bit != null)
            return equipmentSlot == EquipmentSlot.MAINHAND ? this.bit.getDefaultAttributeModifiers(equipmentSlot) : super.getDefaultAttributeModifiers(equipmentSlot);
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public float getAttackDamage() {
        return this.bit != null ? this.bit.getAttackDamage() : 0;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return this.bit != null ? this.bit.canPerformAction(toolAction) : false;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockState){
        return this.bit != null ? this.bit.isCorrectToolForDrops(blockState) : false;
    }
}
