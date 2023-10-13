package com.veteam.voluminousenergy.blocks.containers.tank;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NighaliteTankContainer extends TankContainer {

    public NighaliteTankContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(id, world, pos, inventory, player, VEBlocks.NIGHALITE_TANK_CONTAINER.get(),VEBlocks.NIGHALITE_TANK_BLOCK.get());
    }
}