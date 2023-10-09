package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PressureLadder extends LadderBlock {
    private String registryName; // Voluminous Energy 1.19 port
    protected static final AABB TOUCH_AABB = new AABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final PressurePlateBlock.Sensitivity sensitivity = PressurePlateBlock.Sensitivity.MOBS;

    public PressureLadder() {
        super(BlockBehaviour.Properties.copy(Blocks.LADDER)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
        );
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
    public boolean isLadder(BlockState blockState, LevelReader levelReader, BlockPos blockPos, LivingEntity livingEntity) {
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
        /*if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound((Player)null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        } else {*/
        accessor.playSound((Player) null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
        //}

    }

    protected void playOffSound(LevelAccessor accessor, BlockPos pos) {
        /*if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            accessor.playSound((Player)null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
        } else {*/
        accessor.playSound((Player) null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
        //}

    }

    protected int getSignalStrength(Level level, BlockPos blockPos) {
        AABB aabb = TOUCH_AABB.move(blockPos);
        List<? extends Entity> list;
        switch (this.sensitivity) {
            case EVERYTHING:
                list = level.getEntities((Entity) null, aabb);
                break;
            case MOBS:
                list = level.getEntitiesOfClass(LivingEntity.class, aabb);
                break;
            default:
                return 0;
        }

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }

        return 0;
    }


    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        Player player = serverLevel.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 2, EntitySelector.NO_SPECTATORS);
        if ((player == null || player.isSpectator()) && blockState.getValue(POWERED)) {
            VoluminousEnergy.LOGGER.info("Pressure Ladder active with no player! Might be jammed! Deactivating to unjam!");
            powerOff(serverLevel, blockState, blockPos, player);
        }
    }

    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!level.isClientSide) {
            if (entity instanceof LivingEntity) {
                // Calculate float differences from entity entering the ladder (double) to the actual blockPos (int)
                // NOTE: blockPos is the VERY TOP of the block (ie ceil rounded)
                double deltaX = entity.getX() - blockPos.getX();
                double deltaY = entity.getY() - blockPos.getY();
                double deltaZ = entity.getZ() - blockPos.getZ();

                // Vertical (Y) checks
                if (((deltaY > 0.91F) || (deltaY < -0.92F)) && blockState.getValue(POWERED)) { // If entity too high or low, deactivate
                    powerOff(level, blockState, blockPos, entity);
                } else if (((deltaY > -0.90F) && (deltaY < 0.9F)) && !blockState.getValue(POWERED)) { // If between target delta values, activate
                    powerOn(level, blockState, blockPos, entity);
                }

                // If entity too far to the sides, deactivate
                if ((deltaZ > 0.9F || deltaZ < -0.9F) || (deltaX > 0.9F || deltaX < -0.9F) && blockState.getValue(POWERED)) {
                    powerOff(level, blockState, blockPos, entity);
                }

            }
        }
    }

    private void powerOff(Level level, BlockState blockState, BlockPos blockPos, Entity entity) {
        BlockState newState = blockState.setValue(POWERED, false);
        this.setSignalForState(newState, 0);
        level.setBlock(blockPos, newState, 3);
        level.setBlocksDirty(blockPos, blockState, newState);
        this.updateNeighbours(level, blockPos);

        // Play sound
        this.playOffSound(level, blockPos);
        level.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, blockPos);
    }

    private void powerOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity) {
        BlockState newState = blockState.setValue(POWERED, true);
        this.setSignalForState(newState, 15);
        level.setBlock(blockPos, newState, 3);
        level.setBlocksDirty(blockPos, blockState, newState);
        this.updateNeighbours(level, blockPos);

        // Play sound
        this.playOnSound(level, blockPos);
        level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, blockPos);
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

//    @Override
//    public PushReaction getPistonPushReaction(BlockState blockState) {
//        return PushReaction.DESTROY;
//    }

    // Voluminous Energy 1.19 port
    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistryName() {
        return registryName;
    }
}
