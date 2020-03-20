package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.PrimitiveBlastFurnaceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        ScreenManager.registerFactory(VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER, PrimitiveBlastFurnaceScreen::new);
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() { return Minecraft.getInstance().player; }
}
