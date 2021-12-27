package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

    @Override
    public boolean isLadder(BlockState blockState, LevelReader levelReader, BlockPos blockPos, LivingEntity livingEntity){
        return true;
    }

    // Pressure plate methods
    protected int getPressedTime() {
        return 20;
    }

    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    protected int getSignalForState(BlockState blockState) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState blockState, int signalPower) {
        return blockState.setValue(POWERED, Boolean.valueOf(signalPower > 0));
    }

    protected void playOnSound(LevelAccessor accessor, BlockPos pos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound((Player)null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        } else {
            accessor.playSound((Player)null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
        }

    }

    protected void playOffSound(LevelAccessor accessor, BlockPos pos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound((Player)null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
        } else {
            accessor.playSound((Player)null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
        }

    }

    protected int getSignalStrength(Level level, BlockPos blockPos) {
        AABB aabb = TOUCH_AABB.move(blockPos);
        List<? extends Entity> list;
        switch(this.sensitivity) {
            case EVERYTHING:
                list = level.getEntities((Entity)null, aabb);
                break;
            case MOBS:
                list = level.getEntitiesOfClass(LivingEntity.class, aabb);
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

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        int i = this.getSignalForState(blockState);
        if (i > 0) {
            this.checkPressed((Entity)null, serverLevel, blockPos, blockState, i);
        }
    }

    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!level.isClientSide) {
            int i = this.getSignalForState(blockState);
            if (i == 0) {
                this.checkPressed(entity, level, blockPos, blockState, i);
            }

        }
    }

    protected void checkPressed(@Nullable Entity entity, Level level, BlockPos blockPos, BlockState blockState, int iFlag) {
        int i = this.getSignalStrength(level, blockPos);
        boolean flag = iFlag > 0;
        boolean flag1 = i > 0;
        if (iFlag != i) {
            BlockState blockstate = this.setSignalForState(blockState, i);
            level.setBlock(blockPos, blockstate, 2);
            this.updateNeighbours(level, blockPos);
            level.setBlocksDirty(blockPos, blockState, blockstate);
        }

        if (!flag1 && flag) {
            this.playOffSound(level, blockPos);
            level.gameEvent(entity, GameEvent.BLOCK_UNPRESS, blockPos);
        } else if (flag1 && !flag) {
            this.playOnSound(level, blockPos);
            level.gameEvent(entity, GameEvent.BLOCK_PRESS, blockPos);
        }

        if (flag1) {
            level.scheduleTick(new BlockPos(blockPos), this, this.getPressedTime());
        }

    }

    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean flag) {
        if (!flag && !blockState.is(blockState1.getBlock())) {
            if (this.getSignalForState(blockState) > 0) {
                this.updateNeighbours(level, blockPos);
            }

            super.onRemove(blockState, level, blockPos, blockState1, flag);
        }
    }

    protected void updateNeighbours(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.below(), this);
        level.updateNeighborsAt(pos.above(), this);
        level.updateNeighborsAt(pos.east(), this);
        level.updateNeighborsAt(pos.west(), this);
        level.updateNeighborsAt(pos.north(), this);
        level.updateNeighborsAt(pos.south(), this);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return this.getSignalForState(blockState);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? this.getSignalForState(blockState) : 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

}
