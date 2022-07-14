package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.fluids.VEFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;


public class AmmoniumNitrateBucket extends BucketItem {
    public AmmoniumNitrateBucket(Supplier<? extends Fluid> fluidSupplier, Properties properties) {
        super(fluidSupplier, properties);
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
        if (applyFert(context.getItemInHand(), level, blockpos, context.getPlayer())) {
            if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }

            applyFert(context.getItemInHand(), level, blockpos, context.getPlayer());
            if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }

            if (context.getPlayer() != null && !level.isClientSide) {
                if (context.getPlayer().isCreative()){
                    context.getPlayer().getInventory().placeItemBackInInventory(new ItemStack(VEFluids.AMMONIUM_NITRATE_SOLUTION_BUCKET_REG.get()));
                } else {
                    context.getPlayer().getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            BlockState blockstate = level.getBlockState(blockpos);
            boolean flag = blockstate.isFaceSturdy(level, blockpos, context.getClickedFace());
            if (flag && growWaterPlant(context.getItemInHand(), level, blockpos1, context.getClickedFace())) {
                if (!level.isClientSide) {
                    level.levelEvent(1505, blockpos1, 0);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return super.useOn(context);
            }
        }
    }

    // Derived from applyBonemeal
    public static boolean applyFert(ItemStack itemStack, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        BlockState blockstate = level.getBlockState(pos);
        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, level, pos, blockstate, itemStack);
        if (hook != 0) return hook > 0;
        if (blockstate.getBlock() instanceof BonemealableBlock) {
            Block block = blockstate.getBlock();
            BonemealableBlock bonemealableblock = (BonemealableBlock)blockstate.getBlock();
            if (bonemealableblock.isValidBonemealTarget(level, pos, blockstate, level.isClientSide)) {


                // TODO: Add more checks to ensure more powerful than bonemeal
                if (level instanceof ServerLevel){
                    if (block instanceof SaplingBlock){
                        blockstate.setValue(BlockStateProperties.STAGE, 1);
                        ((SaplingBlock) block).advanceTree((ServerLevel) level, pos, blockstate, level.getRandom());
                        try {
                            ((SaplingBlock) block).advanceTree((ServerLevel) level, pos, blockstate, level.getRandom());
                        } catch (Exception e) {}

                        itemStack.shrink(1);
                    } else {
                        if (bonemealableblock.isBonemealSuccess(level, level.random, pos, blockstate)) {
                            bonemealableblock.performBonemeal((ServerLevel)level, level.random, pos, blockstate);
                        }

                        itemStack.shrink(1);
                    }
                }


                return true;
            }
        }

        return false;
    }

    public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos pos, @Nullable Direction direction) {
        if (level.getBlockState(pos).is(Blocks.WATER) && level.getFluidState(pos).getAmount() == 8) {
            if (!(level instanceof ServerLevel)) {
                return true;
            } else {
                Random random = level.getRandom();

                label78:
                for(int i = 0; i < 128; ++i) {
                    BlockPos blockpos = pos;
                    BlockState blockstate = Blocks.SEAGRASS.defaultBlockState();

                    for(int j = 0; j < i / 16; ++j) {
                        blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                        if (level.getBlockState(blockpos).isCollisionShapeFullBlock(level, blockpos)) {
                            continue label78;
                        }
                    }

                    Holder<Biome> holder = level.getBiome(blockpos);
                    if (holder.is(Biomes.WARM_OCEAN)) {
                        if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
                            blockstate = Registry.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((p_204098_) -> {
                                return p_204098_.getRandomElement(level.random);
                            }).map((p_204100_) -> {
                                return p_204100_.value().defaultBlockState();
                            }).orElse(blockstate);
                            if (blockstate.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                blockstate = blockstate.setValue(BaseCoralWallFanBlock.FACING, direction);
                            }
                        } else if (random.nextInt(4) == 0) {
                            blockstate = Registry.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS).flatMap((p_204091_) -> {
                                return p_204091_.getRandomElement(level.random);
                            }).map((p_204095_) -> {
                                return p_204095_.value().defaultBlockState();
                            }).orElse(blockstate);
                        }
                    }

                    if (blockstate.is(BlockTags.WALL_CORALS, (p_204093_) -> {
                        return p_204093_.hasProperty(BaseCoralWallFanBlock.FACING);
                    })) {
                        for(int k = 0; !blockstate.canSurvive(level, blockpos) && k < 4; ++k) {
                            blockstate = blockstate.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                        }
                    }

                    if (blockstate.canSurvive(level, blockpos)) {
                        BlockState blockstate1 = level.getBlockState(blockpos);
                        if (blockstate1.is(Blocks.WATER) && level.getFluidState(blockpos).getAmount() == 8) {
                            level.setBlock(blockpos, blockstate, 3);
                        } else if (blockstate1.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                            ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)level, random, blockpos, blockstate1);
                        }
                    }
                }

                itemStack.shrink(1);
                return true;
            }
        } else {
            return false;
        }
    }
}

