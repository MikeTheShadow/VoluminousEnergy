package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItemData;
import com.veteam.voluminousenergy.items.tools.multitool.bits.ToolType;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Multitool extends VEItem {

    public Multitool(BitItemData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        setRegistryName(registryName);
    }

    public Multitool() {
        super(new Item.Properties()
                .stacksTo(1));
        setRegistryName("multitool");
    }



    public void setToolState(@NotNull ItemStack itemStack, @Nullable BlockState blockState) {

        if (blockState == null || blockState.isAir()) {
            itemStack.set(VEDataComponents.TOOL_TYPE, 0);
            itemStack.set(VEDataComponents.TOOL_TIER, 0);
            return;
        }

        BitItem selected = getBestBitForBlock(itemStack, blockState);

        if (selected == null) {
            itemStack.set(VEDataComponents.TOOL_TYPE, 0);
            itemStack.set(VEDataComponents.TOOL_TIER, 0);
            return;
        }

        itemStack.set(VEDataComponents.TOOL_TYPE, selected.getBitItemData().getToolType());
        itemStack.set(VEDataComponents.TOOL_TIER, selected.getBitItemData().getToolTier());
    }

    @Nullable
    private BitItem getBestBitForBlock(ItemStack multitool,BlockState blockState) {

        List<ItemStack> inventory = multitool.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT,new ArrayList<>());

        float miningSpeed = 0;
        BitItem selected = null;

        for (ItemStack stack : inventory) {
            if (stack.getItem() instanceof BitItem bitItem) {
                Tool tool = bitItem.getTool();
                float tempSpeed = tool.getMiningSpeed(blockState);

                if (tempSpeed > miningSpeed && tool.isCorrectForDrops(blockState)) {
                    miningSpeed = tempSpeed;
                    selected = bitItem;
                }
            }
        }

        return selected;
    }

    @Nullable
    private BitItem getBestBitForDamage(ItemStack multitool) {

        List<ItemStack> inventory = multitool.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT,new ArrayList<>());

        float attackDamage = 0;
        BitItem selected = null;

        for (ItemStack stack : inventory) {
            if (stack.getItem() instanceof BitItem bitItem) {
                BitItemData data = bitItem.getBitItemData();
                float tempDamage = data.getAttackDamage();

                if (tempDamage > attackDamage) {
                    attackDamage = tempDamage;
                    selected = bitItem;
                }
            }
        }

        return selected;
    }


    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, @NotNull BlockState blockStateToMine) {
        BitItem bit = getBestBitForBlock(itemStack, blockStateToMine);

        if(bit == null) {
            return blockStateToMine.requiresCorrectToolForDrops() ? 0.0F : 1;
        }

        return bit.getBitItemData().getTier().getSpeed();
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
    @Deprecated
    public BitItemData getBit() {
        return null;
    }

    @Override
    public float getAttackDamageBonus(@NotNull Player pPlayer, float pBaseAttackDamage) {
        ItemStack stack = pPlayer.getMainHandItem();
        if(!(stack.getItem() instanceof Multitool)) return 0f;
        
        BitItem bit = getBestBitForDamage(stack);
        return bit != null ? bit.getBitItemData().getAttackDamage() : 0;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction toolAction) {

        List<ItemStack> inventory = stack.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT,new ArrayList<>());

        for(ItemStack itemStack : inventory) {
            if(itemStack.getItem() instanceof BitItem bitItem) {
                if(bitItem.getBitItemData().canPerformAction(toolAction)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState blockState) {
        if(!blockState.requiresCorrectToolForDrops()) return true;
        BitItem bit = getBestBitForBlock(stack, blockState);
        if(bit == null) return false;
        return bit.getTool().isCorrectForDrops(blockState);
    }

    // Trimmer Multitool stuff
    @Override
    public net.minecraft.world.@NotNull InteractionResult interactLivingEntity(@NotNull ItemStack multitool, net.minecraft.world.entity.player.@NotNull Player playerIn, @NotNull LivingEntity entity, net.minecraft.world.@NotNull InteractionHand hand) {

        List<ItemStack> inventory = multitool.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT,new ArrayList<>());

        BitItem bit = null;

        for(ItemStack itemStack : inventory) {
            if(itemStack.getItem() instanceof BitItem bitItem && bitItem.getBitItemData().getToolType() == ToolType.TRIMMER.value())
                bit = bitItem;
        }

        if (bit != null && bit.getBitItemData().getToolType() == ToolType.TRIMMER.value() && entity instanceof net.neoforged.neoforge.common.IShearable target) {
            if (entity.level().isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (target.isShearable(playerIn, multitool, entity.level(), pos)) {

                java.util.List<ItemStack> drops = target.onSheared(playerIn, multitool, entity.level(), pos);

                // TODO: Fortune for drops when shearing entities
//                Integer fortuneLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FORTUNE, stack);
                java.util.Random rand = new java.util.Random();

                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double) (rand.nextFloat() * 0.05F), (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });


                switch (hand) {
                    case InteractionHand.MAIN_HAND -> multitool.hurtAndBreak(1, playerIn, EquipmentSlot.MAINHAND);
                    case InteractionHand.OFF_HAND -> multitool.hurtAndBreak(1, playerIn, EquipmentSlot.OFFHAND);
                }

            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(multitool, playerIn, entity, hand); // Revert to previous super code if not trimmer
    }
}
