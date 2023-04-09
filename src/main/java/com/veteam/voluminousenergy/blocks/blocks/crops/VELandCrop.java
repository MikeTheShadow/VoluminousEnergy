package com.veteam.voluminousenergy.blocks.blocks.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VELandCrop extends BushBlock implements BonemealableBlock {

    private String registryName;

    public VELandCrop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.AGE_2,0)); // set the age of the crop to 0 by default
        setRegistryName("land_crop");
    }

    // Voxel shape
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
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
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random){
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.AGE_2);
    }

    // Handle bonemeal
    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state, boolean isRemote) {
        return state.getValue(BlockStateProperties.AGE_2) < 2; // valid if age is less than 2
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverWorld, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2) {
            age++;
            serverWorld.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_2, age));// May need flags
        }
    }

    // Action on use
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
        int age = state.getValue(BlockStateProperties.AGE_2);
        if(age < 2 && player.getItemInHand(handIn).sameItem(new ItemStack(Items.BONE_MEAL,1))){
            return InteractionResult.PASS;
        } else if (age > 1){
            popResource(world, pos, new ItemStack(Items.WHEAT_SEEDS, 1));
            world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);  // to tweak
            world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_2, 0)); // may not work
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return super.use(state, world, pos, player, handIn, hit);
    }

    // Voluminous Energy 1.19 port
    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistryName() {
        return registryName;
    }
}