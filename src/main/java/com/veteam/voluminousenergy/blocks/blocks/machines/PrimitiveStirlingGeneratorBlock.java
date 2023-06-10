package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class PrimitiveStirlingGeneratorBlock extends FaceableBlock implements EntityBlock {

    public PrimitiveStirlingGeneratorBlock() {

        super(Properties.of()
                .sound(SoundType.STONE)
                .strength(2.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );

        setRName("primitivestirlinggenerator");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresWood(this);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { // Replaces old createBlockEntity method
        return new PrimitiveStirlingGeneratorTile(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE.get(), pos, state);
    }

    // NEW TICK SYSTEM
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends PrimitiveStirlingGeneratorTile> tile) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, tile, PrimitiveStirlingGeneratorTile::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends PrimitiveStirlingGeneratorTile> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!world.isClientSide){
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider){
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) tileEntity, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Primitive Stirling named container provider is missing!");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
