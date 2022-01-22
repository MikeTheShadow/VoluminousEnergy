package com.veteam.voluminousenergy.blocks.containers.tank;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class SolariumTankContainer extends TankContainer {
    private Player playerEntity;

    public SolariumTankContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(id, world, pos, inventory, player, VEBlocks.SOLARIUM_TANK_CONTAINER);
        this.playerEntity = player;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(getTileEntity().getLevel(),getTileEntity().getBlockPos()),playerEntity, VEBlocks.SOLARIUM_TANK_BLOCK);
    }
}