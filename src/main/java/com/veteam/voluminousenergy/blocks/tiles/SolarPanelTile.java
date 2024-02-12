package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SolarPanelTile extends VESolarTile {

    public SolarPanelTile(BlockPos pos, BlockState state) {
        super(VEBlocks.SOLAR_PANEL_TILE.get(), pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity){
        return VEContainers.SOLAR_PANEL_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int getGeneration(){
        return (int)(Config.SOLAR_PANEL_GENERATE.get()*this.solarIntensity());
    }

    @Override
    public int getMaxPower() {
        return Config.SOLAR_PANEL_MAX_POWER.get();
    }

    @Override
    public int getTransferRate() {
        return Config.SOLAR_PANEL_SEND.get();
    }
}