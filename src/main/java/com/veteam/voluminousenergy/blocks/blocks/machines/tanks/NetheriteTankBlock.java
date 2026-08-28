package com.veteam.voluminousenergy.blocks.blocks.machines.tanks;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntities;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class NetheriteTankBlock extends TankBlock implements EntityBlock {

    public NetheriteTankBlock() {
        super(Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(6.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );
        setRName("netherite_tank");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresNetheriteAndBlacklistLowerTiers(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return VETileEntities.NETHERITE_TANK_FACTORY.create(pos, state);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends VETileEntity> tile) {
        return level.isClientSide() ? null : createTickerHelper(passedBlockEntity, tile, VETileEntity::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends VETileEntity> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>) serverTick : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.NETHERITE_TANK.tile().get());
    }

    @Override
    public int getTankCapacity() {
        return Config.NETHERITE_TANK_CAPACITY.get();
    }

}