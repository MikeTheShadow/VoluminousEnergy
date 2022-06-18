package com.veteam.voluminousenergy.blocks.containers.tank;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class NetheriteTankContainer extends TankContainer {
    private Player playerEntity;

    public NetheriteTankContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(id, world, pos, inventory, player, VEBlocks.NETHERITE_TANK_CONTAINER.get());
        this.playerEntity = player;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(getTileEntity().getLevel(),getTileEntity().getBlockPos()),playerEntity, VEBlocks.NETHERITE_TANK_BLOCK.get());
    }
}
