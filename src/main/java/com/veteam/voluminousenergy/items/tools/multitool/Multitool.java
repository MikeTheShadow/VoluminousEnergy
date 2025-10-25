package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBitData;
import com.veteam.voluminousenergy.items.tools.multitool.bits.TrimmerBitData;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.VECodecs;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Multitool extends VEItem /*implements Vanishable*/ {
    protected MultitoolBitData bit;

    private final MultiToolItemStackHandler handler = new MultiToolItemStackHandler();

    private Block lastBlock = null;

    public Multitool(MultitoolBitData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.bit = bit;
        setRegistryName(registryName);
    }

    public Multitool() {
        super(new Item.Properties()
                .stacksTo(1));
        setRegistryName("multitool");
    }

    boolean test = false;

    public void setToolState(@NotNull ItemStack itemStack,@Nullable BlockState blockState) {
        if (!test) {
            test = true;

            this.handler.insertItem(0, new ItemStack(VEMultitools.DIAMOND_SCOOPER_BIT.get(), 1), false);
            this.handler.insertItem(1, new ItemStack(VEMultitools.DIAMOND_CHAIN_BIT.get(), 1), false);
            this.handler.insertItem(2, new ItemStack(VEMultitools.DIAMOND_DRILL_BIT.get(), 1), false);
            this.handler.insertItem(3, new ItemStack(VEMultitools.DIAMOND_TRIMMER_BIT.get(), 1), false);
        }

        if(blockState == null) {
            itemStack.set(VEDataComponents.TOOL_TYPE,0);
            itemStack.set(VEDataComponents.TOOL_TIER,0);
            return;
        }

        Block lastBlock = blockState.getBlock();

        for (int i = 0; i < this.handler.getSlots(); i++) {

        }

        if (this.lastBlock != lastBlock) {
            this.lastBlock = lastBlock;
            if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                itemStack.set(VEDataComponents.TOOL_TYPE,1);
                itemStack.set(VEDataComponents.TOOL_TIER,3);
            } else if (blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                itemStack.set(VEDataComponents.TOOL_TYPE,3);
                itemStack.set(VEDataComponents.TOOL_TIER,3);
            } else if(blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
                itemStack.set(VEDataComponents.TOOL_TYPE,2);
                itemStack.set(VEDataComponents.TOOL_TIER,2);
            } else if (blockState.is(BlockTags.MINEABLE_WITH_HOE)) {
                itemStack.set(VEDataComponents.TOOL_TYPE,4);
                itemStack.set(VEDataComponents.TOOL_TIER,4);
            }
        }
    }


    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, BlockState blockStateToMine) {
        return this.bit != null ? this.bit.getDestroySpeed(itemStack, blockStateToMine) : 0;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity attackee, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, Level level, @NotNull BlockState blockState, @NotNull BlockPos pos, @NotNull LivingEntity player) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    @Nullable
    public MultitoolBitData getBit() {
        return this.bit != null ? this.bit : null;
    }

//    @Override // TODO: Test and fix or remove if not needed
//    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
//        if (this.bit != null)
//            return equipmentSlot == EquipmentSlot.MAINHAND ? this.bit.getDefaultAttributeModifiers(equipmentSlot) : super.getDefaultAttributeModifiers(equipmentSlot);
//        return super.getDefaultAttributeModifiers(equipmentSlot);
//    }

    @Override
    public float getAttackDamageBonus(@NotNull Player pPlayer, float pBaseAttackDamage) {
        return this.bit != null ? this.bit.getAttackDamage() : 0F;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ToolAction toolAction) {
        return this.bit != null && this.bit.canPerformAction(toolAction);
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState blockState) {
        return this.bit != null && this.bit.isCorrectToolForDrops(stack, blockState);
    }

    // Trimmer Multitool stuff
    @Override
    public net.minecraft.world.@NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, net.minecraft.world.entity.player.@NotNull Player playerIn, @NotNull LivingEntity entity, net.minecraft.world.@NotNull InteractionHand hand) {
        if (this.bit != null && this.bit instanceof TrimmerBitData && entity instanceof net.neoforged.neoforge.common.IShearable target) {
            if (entity.level().isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (target.isShearable(playerIn, stack, entity.level(), pos)) {

                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level(), pos);

                // TODO: Fortune for drops when shearing entities
//                Integer fortuneLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FORTUNE, stack);
                java.util.Random rand = new java.util.Random();

                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double) (rand.nextFloat() * 0.05F), (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });


                switch (hand) {
                    case InteractionHand.MAIN_HAND -> stack.hurtAndBreak(1, playerIn, EquipmentSlot.MAINHAND);
                    case InteractionHand.OFF_HAND -> stack.hurtAndBreak(1, playerIn, EquipmentSlot.OFFHAND);
                }

            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, playerIn, entity, hand); // Revert to previous super code if not trimmer
    }
}
