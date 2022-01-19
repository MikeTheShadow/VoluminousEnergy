package com.veteam.voluminousenergy.items.crops;

import com.veteam.voluminousenergy.blocks.blocks.crops.VEWaterCrop;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterCropItem extends BlockItem {

    public WaterCropItem(Block block, Properties properties){
        super(block, properties);
        //setRegistryName("water_crop");
    }

    public VEWaterCrop getWaterCrop(){
        return null;
    } // MUST override

    @Override
    public InteractionResult useOn(UseOnContext context){
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand){
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        BlockHitResult rayTraceResult = getPlayerPOVHitResult(world, playerEntity, ClipContext.Fluid.SOURCE_ONLY);

        if(rayTraceResult.getType() == HitResult.Type.MISS){
            return InteractionResultHolder.pass(itemStack);
        }

        if (rayTraceResult.getType() == HitResult.Type.BLOCK){
            BlockPos pos = rayTraceResult.getBlockPos();
            Direction dir = rayTraceResult.getDirection();

            if (!world.mayInteract(playerEntity, pos) || !playerEntity.mayUseItemAt(pos.relative(dir), dir, itemStack)) {
                return InteractionResultHolder.fail(itemStack);
            }

            BlockPos abovePos = pos.above();
            if (world.getBlockState(pos).is(Blocks.WATER) && world.getBlockState(abovePos).isAir()) { // Ensure placing on water with air above the water

                if (!world.isClientSide())
                    getWaterCrop().place(world, abovePos, 18); // place the crop on the server

                if (playerEntity instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, abovePos, itemStack);
                }

                if (!playerEntity.getAbilities().instabuild) { // this is checking if the player is not in creative mode, don't use item if in creative mode
                    itemStack.shrink(1);
                }

                world.playSound(playerEntity, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResultHolder.success(itemStack);
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }
}
