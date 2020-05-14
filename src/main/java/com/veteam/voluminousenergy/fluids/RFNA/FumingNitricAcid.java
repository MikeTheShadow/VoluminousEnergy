package com.veteam.voluminousenergy.fluids.RFNA;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public abstract class FumingNitricAcid extends FlowingFluid {

    @Override
    public Fluid getFlowingFluid() {
        return null;
    }

    @Override
    public Fluid getStillFluid() {
        return null;
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
        //this.triggerEffects(worldIn, pos);
        worldIn.playEvent(1501, pos, 0);
    }

    @Override
    protected int getSlopeFindDistance(IWorldReader worldIn) {
        return 4;
    }

    @Override
    protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public Item getFilledBucket() {
        return Items.BUCKET; // needs proper bucket
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
        BlockPos blockpos = pos.up();
        if (worldIn.getBlockState(blockpos).isAir() && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
            if (random.nextInt(100) == 0) {
                double d0 = (double)((float)pos.getX() + random.nextFloat());
                double d1 = (double)(pos.getY() + 1);
                double d2 = (double)((float)pos.getZ() + random.nextFloat());
                worldIn.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected boolean canDisplace(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
        return p_215665_1_.getActualHeight(p_215665_2_, p_215665_3_) >= 0.44444445F && p_215665_4_.isIn(FluidTags.WATER);
    }

    @Override
    public int getTickRate(IWorldReader p_205569_1_) {
        return 30;
    }

    @Override
    protected boolean ticksRandomly() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 100F;
    }

    @Override
    protected BlockState getBlockState(IFluidState state) {
        return null; // Needs a fluid block
    }

    public static class Flowing extends FumingNitricAcid {
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(IFluidState p_207192_1_) {
            return p_207192_1_.get(LEVEL_1_8);
        }

        public boolean isSource(IFluidState state) {
            return false;
        }
    }

    public static class Source extends FumingNitricAcid {
        public int getLevel(IFluidState p_207192_1_) {
            return 8;
        }

        public boolean isSource(IFluidState state) {
            return true;
        }
    }
}