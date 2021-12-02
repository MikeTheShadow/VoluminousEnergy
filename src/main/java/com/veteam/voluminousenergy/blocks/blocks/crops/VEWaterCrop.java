package com.veteam.voluminousenergy.blocks.blocks.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

public class VEWaterCrop extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock{

    public VEWaterCrop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.AGE_2,0)); // set the age of the crop to 0 by default
        //setRegistryName("water_crop");
    }

    public Item cropItem(){
        return null; // MUST override this to prevent NPE.
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos){
        BlockPos abovePos = pos.above();
        return world.getBlockState(pos).is(Blocks.WATER) && world.getBlockState(abovePos).isAir();
    }

    // Voxel shape
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
        Vec3 vector3d = state.getOffset(worldIn, pos);
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
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos){
        if(state.getValue(BlockStateProperties.WATERLOGGED)){
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        DoubleBlockHalf doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if(facing.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)
                || facingState.is(this) && facingState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != doubleBlockHalf){
            if(doubleBlockHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(worldIn, currentPos)){
                return Blocks.AIR.defaultBlockState();
            }
            return super.updateShape(state, facing, facingState, worldIn, currentPos, facingPos); // Excluding this super will cause neighbours to break
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context){
        BlockPos pos = context.getClickedPos();
        if(pos.getY() <255 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context)){
            return super.getStateForPlacement(context);
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos){
        if(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER)
            return state.getValue(BlockStateProperties.WATERLOGGED);

        if(state.getBlock() != this) return super.canSurvive(state, worldIn, pos);

        BlockState stateBelow = worldIn.getBlockState(pos.below());
        return (stateBelow.is(this) && stateBelow.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                && stateBelow.getValue(BlockStateProperties.WATERLOGGED));
    }

    // Item place to block
    public void place(LevelAccessor world, BlockPos pos, int flags){
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
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random){
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
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
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isRemote) {
        return state.getValue(BlockStateProperties.AGE_2) < 2; // valid if age is less than 2
    }

    @Override
    public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverWorld, Random random, BlockPos pos, BlockState state) {
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2 && player.getItemInHand(handIn).sameItem(new ItemStack(Items.BONE_MEAL,1))){
            return InteractionResult.PASS;
        } else if (age > 1 && world.getBlockState(pos.above()).getBlock() != this.defaultBlockState().getBlock()){ // Make sure the player isn't targetting the bottom
            popResource(world, pos, new ItemStack(cropItem(), 1));
            world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);  // to tweak

            place(world, pos, 18); // Place the crop in it's initial state
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return super.use(state, world, pos, player, handIn, hit);
    }
}
