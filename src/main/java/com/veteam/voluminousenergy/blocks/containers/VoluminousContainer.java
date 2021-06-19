package com.veteam.voluminousenergy.blocks.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class VoluminousContainer extends Container {
    TileEntity tileEntity;

    protected VoluminousContainer(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
        super(p_i50105_1_, p_i50105_2_);
    }

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        return false;
    }

    public TileEntity getTileEntity(){
        return tileEntity;
    }
}
