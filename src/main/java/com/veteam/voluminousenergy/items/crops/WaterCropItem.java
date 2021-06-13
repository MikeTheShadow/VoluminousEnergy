package com.veteam.voluminousenergy.items.crops;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.crops.VEWaterCrop;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WaterCropItem extends BlockItem {

    public WaterCropItem(Item.Properties properties){
        super(VEBlocks.WATER_CROP.getBlock(), properties);
        setRegistryName("water_crop");
    }

    public VEWaterCrop getWaterCrop(){
        return (VEWaterCrop) VEBlocks.WATER_CROP.getBlock();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context){
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand){
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        BlockRayTraceResult rayTraceResult = getPlayerPOVHitResult(world, playerEntity, RayTraceContext.FluidMode.SOURCE_ONLY);

        if(rayTraceResult.getType() == RayTraceResult.Type.MISS){
            return ActionResult.pass(itemStack);
        }

        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK){
            BlockPos pos = rayTraceResult.getBlockPos();
            Direction dir = rayTraceResult.getDirection();

            if (!world.mayInteract(playerEntity, pos) || !playerEntity.mayUseItemAt(pos.relative(dir), dir, itemStack)) {
                return ActionResult.fail(itemStack);
            }

            BlockPos abovePos = pos.above();
            if (world.getBlockState(pos).is(Blocks.WATER) && world.getBlockState(abovePos).isAir()) { // Ensure placing on water with air above the water

                if (!world.isClientSide())
                    getWaterCrop().place(world, abovePos, 18); // place the crop on the server

                if (playerEntity instanceof ServerPlayerEntity) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, abovePos, itemStack);
                }

                if (!playerEntity.abilities.instabuild) { // this is checking if the player is not in creative mode, don't use item if in creative mode
                    itemStack.shrink(1);
                }

                world.playSound(playerEntity, pos, SoundEvents.CROP_PLANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return ActionResult.success(itemStack);
            }
        }
        return ActionResult.fail(itemStack);
    }
}
