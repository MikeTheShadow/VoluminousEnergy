package com.veteam.voluminousenergy.items.tools.multitool;

import com.google.common.collect.Multimap;
import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import com.veteam.voluminousenergy.items.tools.multitool.bits.TrimmerBit;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Multitool extends VEItem /*implements Vanishable*/ {
    protected MultitoolBit bit;

    public Multitool(MultitoolBit bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.bit = bit;
        setRegistryName(registryName);
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockStateToMine) {
        return this.bit != null ? this.bit.getDestroySpeed(itemStack, blockStateToMine) : 0;
    }

    public boolean hurtEnemy(ItemStack itemStack, LivingEntity entity0, LivingEntity entity1) {
        itemStack.hurtAndBreak(2, entity1, (p_41007_) -> {
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
    public boolean onBlockStartBreak(ItemStack itemStack, BlockPos pos, Player player) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer) || player.isCrouching()) {
            return super.onBlockStartBreak(itemStack, pos, player);
        }

        CompoundTag tag = player.getMainHandItem().getTag();

        if (!tag.contains("energy")) {
            return super.onBlockStartBreak(itemStack, pos, player);
        }

        int energyLeft = tag.getInt("energy");
        if (!(energyLeft > 0)) {
            return super.onBlockStartBreak(itemStack, pos, player);
        }

        ServerLevel level = serverPlayer.server.getLevel(player.level().dimension());
        BlockState miningBlock = level.getBlockState(pos);

        // Tree Felling -- CHAIN BIT
        if (this.canPerformAction(new ItemStack(this), ToolActions.AXE_DIG)) {
            treeFelling(pos, player, level, miningBlock);
        }

        Direction lookingAt = getLookedAtBlockFace(player);

        if (/*true && */level.getBlockState(pos).canHarvestBlock(level, pos, player)) { // TODO: AOE Upgrade item check // This is only checking the validity of the current block to mine, not subsequent blocks
            Direction cameraDirection = ((ServerPlayer) player).getCamera().getDirection();
            Vec3 angle = ((ServerPlayer) player).getCamera().getLookAngle();

            if (lookingAt == Direction.UP || lookingAt == Direction.DOWN) { // Y

                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos offsetPos = pos.offset(x, 0, z);

                        mineIfHarvestable(player, level, offsetPos, itemStack);
                    }
                }

            } else if (cameraDirection == Direction.EAST || cameraDirection == Direction.WEST) { // Z

                for (int z = -1; z < 2; z++) {
                    for (int y = 1; y > -2; y--) { // Init top left
                        BlockPos offsetPos = pos.offset(0,y,z);

                        mineIfHarvestable(player, level, offsetPos, itemStack);
                    }
                }

            } else if (cameraDirection == Direction.NORTH || cameraDirection == Direction.SOUTH) { // X

                for (int x = -1; x < 2; x++) {
                    for (int y = 1; y > -2; y--) { // Init top left
                        BlockPos offsetPos = pos.offset(x,y,0);

                        mineIfHarvestable(player, level, offsetPos, itemStack);
                    }
                }
            }
        }

        return super.onBlockStartBreak(itemStack, pos, player);
    }

    private void mineIfHarvestable(Player player, ServerLevel level, BlockPos pos, ItemStack stack) {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock().canHarvestBlock(state, level, pos, player) && this.bit.getDestroySpeed(ItemStack.EMPTY, state) > 1.0F) {
            player.awardStat( Stats.BLOCK_MINED.get(state.getBlock()));

            Block.dropResources(state, level, pos, null, player, stack);
            level.destroyBlock(pos, false, player); // Disable handling of loot here

        }
    }

    private static void treeFelling(BlockPos pos, Player player, ServerLevel level, BlockState miningBlock) { // Extracted vertical Tree Felling code
        ArrayList<Item> taggedAsWood = TagUtil.getItemListFromTagResourceLocation("minecraft:logs");
        if (taggedAsWood.contains(miningBlock.getBlock().asItem())) {

            BlockPos offsetPos = pos;

            for (int yOffset = pos.getY(); yOffset < 320; yOffset++ ) {
                offsetPos = offsetPos.above();
                BlockState potentialStateToFell = level.getBlockState(offsetPos);

                if (taggedAsWood.contains(potentialStateToFell.getBlock().asItem())) {

                    if (potentialStateToFell.getBlock().canHarvestBlock(potentialStateToFell, level.getLevel(), offsetPos, player)) {
                        player.awardStat( Stats.BLOCK_MINED.get(potentialStateToFell.getBlock()));
                        level.destroyBlock(offsetPos, true, player);
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }
        }
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
            if (entity.level().isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (target.isShearable(stack, entity.level(), pos)) {
                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level(), pos,
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

    @Nullable
    public Direction getLookedAtBlockFace(Player player) {
//        double reachDistance = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE).getValue();
        double reachDistance = 10;
        HitResult result = player.pick(reachDistance, 0.0F, false);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) result;
            return blockHitResult.getDirection();
        }
        return null;
    }
}
