package com.veteam.voluminousenergy.blocks.blocks.crops;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;


// Water crops are to be grown on the top of water, with roots that hang into the actual water source block.

public class VEWaterCrop extends BushBlock implements IGrowable, IWaterLoggable{

    public VEWaterCrop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.AGE_2,0)); // set the age of the crop to 0 by default
        //setRegistryName("water_crop");
    }

    public Item cropItem(){
        return null; // MUST override this to prevent NPE.
    }

    @Override
    public boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos){
        BlockPos abovePos = pos.above();
        return world.getBlockState(pos).is(Blocks.WATER) && world.getBlockState(abovePos).isAir();
    }

    // Voxel shape
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        Vector3d vector3d = state.getOffset(worldIn, pos);
        VoxelShape voxelShape = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        return voxelShape.move(vector3d.x, vector3d.y, vector3d.z);
        /*
        if(state.getValue(BlockStateProperties.AGE_2) == 0){
            return Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D);
        }
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
         */
    }

    //Placement
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos){
        if(state.getValue(BlockStateProperties.WATERLOGGED)){
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        DoubleBlockHalf doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if(facing.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)
                || facingState.is(this) && facingState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != doubleBlockHalf){
            if(doubleBlockHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(worldIn, currentPos)){
                return Blocks.AIR.defaultBlockState();
            }

        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        BlockPos pos = context.getClickedPos();
        if(pos.getY() <255 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context)){
            return super.getStateForPlacement(context);
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos){
        if(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER)
            return state.getValue(BlockStateProperties.WATERLOGGED);


        if(state.getBlockState().getBlock() != this) return super.canSurvive(state, worldIn, pos);

        BlockState stateBelow = worldIn.getBlockState(pos.below());
        return (stateBelow.is(this) && stateBelow.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                && stateBelow.getValue(BlockStateProperties.WATERLOGGED));
    }

    // Item place to block
    public void place(IWorld world, BlockPos pos, int flags){
        // Create bottom half/roots in the water
        world.setBlock(pos.below(), this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, true).setValue(BlockStateProperties.AGE_2, 0), flags);

        // Create top half of crop that is above the water (in the air)
        world.setBlock(pos, this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(BlockStateProperties.AGE_2, 0), flags);
    }

    // Ticks
    @Override
    public boolean isRandomlyTicking(BlockState state){
        return state.getValue(BlockStateProperties.AGE_2) < 2;
    }

    // On a random tick, increment the age of the crop to the next state
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random){
        int age = state.getValue(BlockStateProperties.AGE_2);

        if (age < 2 && worldIn.getBlockState(pos.above()).getBlock() != this.defaultBlockState().getBlock() && worldIn.getRawBrightness(pos.above(), 0) > 12){ // light level may need tweaking
            age++;
            // Perform on bottom half
            worldIn.setBlock(pos.below(), this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, true).setValue(BlockStateProperties.AGE_2, age), 18);

            // Perform on top half
            worldIn.setBlock(pos, this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(BlockStateProperties.AGE_2, age), 18);
        }
    }

    // Add properties to the block state
    // this includes aging, half block, and waterlogged status
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.AGE_2);
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    // The bottom needs to have water as it's fluidstate for the water to retain. Otherwise, it'll be converted to air
    // Additionally, upper half needs to NOT return water or else a new water source block will be generated. Which we don't want.
    public FluidState getFluidState(BlockState state){
        if(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            return Fluids.WATER.getSource(false);
        return super.getFluidState(state);
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
        if(age < 2 && serverWorld.getBlockState(pos.above()).getBlock() != this.defaultBlockState().getBlock()) { // Make sure the player isn't targetting the bottom
            age++;
            // Perform on bottom half
            serverWorld.setBlock(pos.below(), this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, true).setValue(BlockStateProperties.AGE_2, age), 18);

            // Perform on top half
            serverWorld.setBlock(pos, this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(BlockStateProperties.AGE_2, age), 18);
        }
    }

    // Action on use
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2 && player.getItemInHand(handIn).sameItem(new ItemStack(Items.BONE_MEAL,1))){
            return ActionResultType.PASS;
        } else if (age > 1 && world.getBlockState(pos.above()).getBlock() != this.defaultBlockState().getBlock()){ // Make sure the player isn't targetting the bottom
            popResource(world, pos, new ItemStack(cropItem(), 1));
            world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);  // to tweak

            place(world, pos, 18); // Place the crop in it's initial state
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return super.use(state, world, pos, player, handIn, hit);
    }
}
