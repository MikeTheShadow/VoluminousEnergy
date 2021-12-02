package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PressureLadder extends LadderBlock {
    protected static final AABB TOUCH_AABB = new AABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final PressurePlateBlock.Sensitivity sensitivity = PressurePlateBlock.Sensitivity.MOBS;

    public PressureLadder() {
        super(BlockBehaviour.Properties.copy(Blocks.LADDER).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(WATERLOGGED, Boolean.valueOf(false))
                        .setValue(POWERED, Boolean.valueOf(false))
        );
        VETagDataGenerator.setRequiresAxe(this);
        VETagDataGenerator.setRequiresWood(this);
        setRegistryName("pressure_ladder");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, WATERLOGGED, POWERED);
    }

    // Pressure plate methods
    protected int getPressedTime() {
        return 20;
    }

    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        int i = this.getSignalForState(blockState);
        if (i > 0) {
            this.checkPressed(null, serverLevel, blockPos, blockState, i);
        }

    }

    public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
        if (!world.isClientSide) {
            int i = this.getSignalForState(blockState);
            if (i == 0) {
                this.checkPressed(entity, world, blockPos, blockState, i);
            }
        }
        super.entityInside(blockState, world, blockPos, entity);
    }

    protected void checkPressed(@Nullable Entity entity, Level world, BlockPos blockPos, BlockState blockState, int num) {
        int i = this.getSignalStrength(world, blockPos);
        boolean flag = num > 0;
        boolean flag1 = i > 0;
        if (num != i) {
            BlockState blockstate = this.setSignalForState(blockState, i);
            world.setBlock(blockPos, blockstate, 2);
            this.updateNeighbours(world, blockPos);
            world.setBlocksDirty(blockPos, blockState, blockstate);
        }

        if (!flag1 && flag) {
            this.playOffSound(world, blockPos);
            world.gameEvent(entity, GameEvent.BLOCK_UNPRESS, blockPos);
        } else if (flag1 && !flag) {
            this.playOnSound(world, blockPos);
            world.gameEvent(entity, GameEvent.BLOCK_PRESS, blockPos);
        }

        if (flag1) {
            world.getBlockTicks().hasScheduledTick(new BlockPos(blockPos),this);
            //world.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getPressedTime());
        }

    }

    protected void playOnSound(LevelAccessor accessor, BlockPos blockPos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound(null, blockPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        } else {
            accessor.playSound(null, blockPos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
        }

    }

    protected void playOffSound(LevelAccessor accessor, BlockPos blockPos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound(null, blockPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
        } else {
            accessor.playSound(null, blockPos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
        }

    }

    public void onRemove(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState1, boolean b) {
        if (!b && !blockState.is(blockState1.getBlock())) {
            if (this.getSignalForState(blockState) > 0) {
                this.updateNeighbours(world, blockPos);
            }

            super.onRemove(blockState, world, blockPos, blockState1, b);
        }
    }

    protected void updateNeighbours(Level world, BlockPos blockPos) {
        world.updateNeighborsAt(blockPos, this);
        world.updateNeighborsAt(blockPos.below(), this);
    }

    public int getSignal(BlockState p_49309_, BlockGetter p_49310_, BlockPos p_49311_, Direction p_49312_) {
        return this.getSignalForState(p_49309_);
    }

    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return direction == Direction.UP ? this.getSignalForState(blockState) : 0;
    }

    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    protected int getSignalStrength(Level world, BlockPos blockPos) {
        AABB aabb = TOUCH_AABB.move(blockPos);
        List<? extends Entity> list;
        switch(this.sensitivity) {
            case EVERYTHING:
                list = world.getEntities(null, aabb);
                break;
            case MOBS:
                list = world.getEntitiesOfClass(LivingEntity.class, aabb);
                break;
            default:
                return 0;
        }

        if (!list.isEmpty()) {
            for(Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    protected int getSignalForState(BlockState blockState) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState blockState, int signalStrength) {
        return blockState.setValue(POWERED, Boolean.valueOf(signalStrength > 0));
    }

    @Override
    public boolean isLadder(BlockState blockState, LevelReader levelReader, BlockPos blockPos, LivingEntity livingEntity){
        return true;
    }
}
