package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.blocks.tiles.AirCompressorTile;
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

import javax.annotation.Nullable;

public class AirCompressorBlock extends VEFaceableMachineBlock {
    public AirCompressorBlock(){
        super(Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStone(this);
        setRName("air_compressor");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { // Replaces old createBlockEntity method
        return new AirCompressorTile(pos, state);
    }

    // NEW TICK SYSTEM
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends AirCompressorTile> airCompressorTile) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, airCompressorTile, AirCompressorTile::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends AirCompressorTile> airCompressorTile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == airCompressorTile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.AIR_COMPRESSOR_TILE.get());
    }
}
