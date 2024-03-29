package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.blocks.tiles.DimensionalLaserTile;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DimensionalLaserBlock extends Block implements EntityBlock {

    public DimensionalLaserBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(value -> 7)
                .noOcclusion()
        );
        VETagDataGenerator.setRequiresSolarium(this);
        VETagDataGenerator.setRequiresPickaxe(this);
    }

    // NEW TICK SYSTEM
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends DimensionalLaserTile> tile) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, tile, DimensionalLaserTile::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends DimensionalLaserTile> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos,@Nonnull  BlockState blockState) {
        return new DimensionalLaserTile(blockPos,blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level,@Nonnull  BlockState state,@Nonnull  BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.DIMENSIONAL_LASER_TILE.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
        if(!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof MenuProvider menuProvider && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider,tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException( this.getClass().getName() + " named container provider is missing!");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
