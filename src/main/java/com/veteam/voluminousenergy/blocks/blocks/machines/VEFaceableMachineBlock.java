package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class VEFaceableMachineBlock extends FaceableBlock implements EntityBlock {
    public VEFaceableMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
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
}