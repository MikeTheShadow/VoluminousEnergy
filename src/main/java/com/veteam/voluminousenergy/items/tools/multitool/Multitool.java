package com.veteam.voluminousenergy.items.tools.multitool;

import com.google.common.collect.Multimap;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import com.veteam.voluminousenergy.items.tools.multitool.bits.TrimmerBit;
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

    // Trimmer Multitool stuff
    @Override
    public net.minecraft.world.InteractionResult interactLivingEntity(ItemStack stack, net.minecraft.world.entity.player.Player playerIn, LivingEntity entity, net.minecraft.world.InteractionHand hand) {
        if (this.bit != null && this.bit instanceof TrimmerBit && entity instanceof net.minecraftforge.common.IForgeShearable target) {
            if (entity.level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
            if (target.isShearable(stack, entity.level, pos)) {
                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level, pos,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, stack));
                java.util.Random rand = new java.util.Random();
                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });
                stack.hurtAndBreak(1, playerIn, e -> e.broadcastBreakEvent(hand));
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, playerIn, entity, hand); // Revert to previous super code if not trimmer
    }
}
