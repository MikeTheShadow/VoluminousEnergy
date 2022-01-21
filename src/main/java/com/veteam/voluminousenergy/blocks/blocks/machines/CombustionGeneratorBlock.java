package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.blocks.tiles.CombustionGeneratorTile;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class CombustionGeneratorBlock extends FaceableBlock implements EntityBlock {
    public CombustionGeneratorBlock(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.METAL)
                .strength(2.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("combustion_generator");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresIron(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { // Replaces old createBlockEntity method
        return new CombustionGeneratorTile(VEBlocks.COMBUSTION_GENERATOR_TILE, pos, state);
    }

    // NEW TICK SYSTEM
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends CombustionGeneratorTile> tile) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, tile, CombustionGeneratorTile::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends CombustionGeneratorTile> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.COMBUSTION_GENERATOR_TILE);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider) {
                NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileEntity, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Combustion Generator named container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

}
