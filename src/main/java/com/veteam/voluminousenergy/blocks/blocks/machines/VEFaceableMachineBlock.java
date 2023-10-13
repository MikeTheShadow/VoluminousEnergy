package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class VEFaceableMachineBlock extends FaceableBlock implements EntityBlock {
    public VEFaceableMachineBlock(Properties properties) {
        super(properties);
    }

    public VEFaceableMachineBlock(Properties properties,String rnName) {
        super(properties);
        setRName(rnName);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider menuProvider && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException(this.getClass().getName() + " named container provider is missing!");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    @Nullable
    public abstract  <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType);

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends VETileEntity> tileEntity) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, tileEntity, VETileEntity::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends VETileEntity> tileEntity, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tileEntity ? (BlockEntityTicker<T>) serverTick : null;
    }
}