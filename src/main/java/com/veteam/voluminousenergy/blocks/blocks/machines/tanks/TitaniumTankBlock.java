package com.veteam.voluminousenergy.blocks.blocks.machines.tanks;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.tank.*;
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
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

public class TitaniumTankBlock extends TankBlock implements EntityBlock {

    public TitaniumTankBlock(){
        super(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(4.0f)
                .lightLevel(l -> 0)
                .requiresCorrectToolForDrops()
        );
        setRName("titanium_tank");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresDiamond(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TitaniumTankTile(pos, state);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends TitaniumTankTile> tile) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, tile, TankTile::serverTick);
    }

    public static <T extends BlockEntity, E extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> blockEntityType, BlockEntityType<? extends TitaniumTankTile> tile, BlockEntityTicker<E> serverTick) {
        return blockEntityType == tile ? (BlockEntityTicker<T>)serverTick : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.TITANIUM_TANK_TILE.get());
    }

    @Override
    public int getTankCapacity() {
        return Config.TITANIUM_TANK_CAPACITY.get();
    }


}