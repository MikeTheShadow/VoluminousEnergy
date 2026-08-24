package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItemData;
import com.veteam.voluminousenergy.items.tools.multitool.bits.ToolType;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBitData;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Multitool extends VEItem {

    //TODO add to config
    private static final int TEMP_TANK_CAPACITY = VETileEntity.DEFAULT_TANK_CAPACITY;

    public Multitool() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(1));
        setRegistryName("multitool");
    }

    @Nullable
    private static IFluidHandler getFluidHandler(ItemStack itemStack) {
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(itemStack).getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : IFluidHandler.of(handler);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        IFluidHandler fluidHandler = getFluidHandler(itemStack);
        return (int) Math.round(13 * (fluidHandler.getFluidInTank(0).getAmount() / (double) TEMP_TANK_CAPACITY));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        IFluidHandler fluidHandler = getFluidHandler(itemStack);
        return Mth.hsvToRgb(fluidHandler.getFluidInTank(0).getAmount() / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {

        IFluidHandler fluidHandler = getFluidHandler(itemStack);
        FluidStack fluidStack = fluidHandler.getFluidInTank(0).copy();

        if(fluidStack.isEmpty()) {
            tooltip.accept(TextUtil.translateString("tank.voluminousenergy.tank_empty").copy());
        } else {
            tooltip.accept(
                    TextUtil.translateString(fluidStack.getHoverName().getString()).copy()
                            .append(": "
                                    + NumberUtil.formatNumber(fluidStack.getAmount())
                                    + " mB / "
                                    + NumberUtil.formatNumber(TEMP_TANK_CAPACITY)
                                    + " mB"
                            )
            );
        }

        Integer energy = itemStack.getOrDefault(VEDataComponents.MULTI_TOOL_ENERGY,0);

        tooltip.accept(TextUtil.translateString("text.voluminousenergy.energy").copy()
                .append(": " + NumberUtil.formatNumber(energy)));
    }

    public void setToolState(@NotNull ItemStack itemStack, @Nullable BlockState blockState) {
        if (blockState == null || blockState.isAir()) {
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

                if(bitItem.getBitItemData().getToolType() == ToolType.TRIMMER.value()
                    && blockState.is(VEMultitoolBitData.MINEABLE_WITH_TRIMMER)) {
                    return bitItem;
                }

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
        int energy = itemStack.getOrDefault(VEDataComponents.MULTI_TOOL_ENERGY,0);
        if(energy < 1) {
            IFluidHandler capability = getFluidHandler(itemStack);
            FluidStack drainedFluid = capability.drain(50, IFluidHandler.FluidAction.EXECUTE);
            if(drainedFluid.isEmpty()) {
                return 0f;
            }

            int energyPerTick = CombustibleFluidsData.getEnergyPerTick(drainedFluid);
            int multiplier = 20;
            int inputAmount = drainedFluid.getAmount();

            int usages = Math.round(inputAmount * ((energyPerTick * multiplier) / 1000f));
            itemStack.set(VEDataComponents.MULTI_TOOL_ENERGY, usages + energy); // Add energy because if it's 0.2 for example we want to keep that
        }

        BitItem bit = getBestBitForBlock(itemStack, blockStateToMine);

        if(bit == null) {
            return blockStateToMine.requiresCorrectToolForDrops() ? 0.0F : 1;
        }

        return bit.getBitItemData().getTier().speed();
    }

    @Override
    public void hurtEnemy(ItemStack stack, @NotNull LivingEntity attackee, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(0, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState blockState, @NotNull BlockPos pos, @NotNull LivingEntity player) {
        Integer energy = stack.getOrDefault(VEDataComponents.MULTI_TOOL_ENERGY,0);
        stack.set(VEDataComponents.MULTI_TOOL_ENERGY,--energy);
        BitItem selected = getBestBitForBlock(stack, blockState);
        if (selected == null) {
            stack.set(VEDataComponents.TOOL_TYPE, 0);
            stack.set(VEDataComponents.TOOL_TIER, 0);
        } else {
            stack.set(VEDataComponents.TOOL_TYPE, selected.getBitItemData().getToolType());
            stack.set(VEDataComponents.TOOL_TIER, selected.getBitItemData().getToolTier());
        }
        return true;
    }

    @Override
    public float getAttackDamageBonus(@NotNull Entity target, float damage, DamageSource damageSource) {

        if(damageSource.getEntity() == null) return 0f;

        ItemStack stack = damageSource.getEntity().getWeaponItem();
        if((stack != null) && !(stack.getItem() instanceof Multitool)) return 0f;

        BitItem bit = getBestBitForDamage(stack);
        if (bit == null) {
            stack.set(VEDataComponents.TOOL_TYPE, 0);
            stack.set(VEDataComponents.TOOL_TIER, 0);
        } else {
            stack.set(VEDataComponents.TOOL_TYPE, bit.getBitItemData().getToolType());
            stack.set(VEDataComponents.TOOL_TIER, bit.getBitItemData().getToolTier());
        }
        return bit != null ? bit.getBitItemData().getAttackDamage() : 0;
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, @NotNull ItemAbility itemAbility) {

        List<ItemStack> inventory = stack.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT,new ArrayList<>());

        for(ItemStack itemStack : inventory) {
            if(itemStack.getItem() instanceof BitItem bitItem) {
                if(bitItem.getBitItemData().canPerformAction(itemAbility)) return true;
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
            if (entity.level().isClientSide()) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (target.isShearable(playerIn, multitool, entity.level(), pos)) {

                java.util.List<ItemStack> drops = target.onSheared(playerIn, multitool, entity.level(), pos);

                // TODO: Fortune for drops when shearing entities
//                Integer fortuneLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FORTUNE, stack);
                java.util.Random rand = new java.util.Random();

                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation((net.minecraft.server.level.ServerLevel) entity.level(), d, 1.0F);
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
