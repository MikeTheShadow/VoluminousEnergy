package com.veteam.voluminousenergy.blocks.tiles.tank;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.tank.AluminumTankContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AluminumTankTile extends TankTile {

    public AluminumTankTile(BlockPos pos, BlockState state) {
        super(VEBlocks.ALUMINUM_TANK_TILE, pos, state, 128); // TODO: Config for capacity
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new AluminumTankContainer(i,level,this.worldPosition,playerInventory,player);
    }
}
