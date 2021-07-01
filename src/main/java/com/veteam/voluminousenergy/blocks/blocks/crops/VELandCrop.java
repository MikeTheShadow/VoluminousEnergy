package com.veteam.voluminousenergy.blocks.blocks.crops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

// Land Crops are crops that can be grown on land rather than on tilled soil.
public class VELandCrop extends BushBlock implements IGrowable {

    public VELandCrop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.AGE_2,0)); // set the age of the crop to 0 by default
        setRegistryName("land_crop");
    }

    // Voxel shape
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        if(state.getValue(BlockStateProperties.AGE_2) == 0){
            return Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D);
        }
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }

    // Ticks
    @Override
    public boolean isRandomlyTicking(BlockState state){
        return state.getValue(BlockStateProperties.AGE_2) < 2;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random){
        //VoluminousEnergy.LOGGER.debug("LAND CROP TICK RANDOMLY!");

        int age = state.getValue(BlockStateProperties.AGE_2);

        if (age < 2 && worldIn.getRawBrightness(pos.above(), 0) > 12){ // light level may need tweaking
            //VoluminousEnergy.LOGGER.debug("LAND CROP GOING TO INCREMENT AGE!");
            age++;
            worldIn.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_2, age));
        }
    }

    // Add aging property to the block state
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.AGE_2);
    }

    // Handle bonemeal
    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isRemote) {
        return state.getValue(BlockStateProperties.AGE_2) < 2; // valid if age is less than 2
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld serverWorld, Random random, BlockPos pos, BlockState state) {
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2) {
            age++;
            serverWorld.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_2, age));// May need flags
        }
    }

    // Action on use
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2 && player.getItemInHand(handIn).sameItem(new ItemStack(Items.BONE_MEAL,1))){
            return ActionResultType.PASS;
        } else if (age > 1){
            popResource(world, pos, new ItemStack(Items.WHEAT_SEEDS.getItem(), 1));
            world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);  // to tweak
            world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_2, 0)); // may not work
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return super.use(state, world, pos, player, handIn, hit);
    }
}