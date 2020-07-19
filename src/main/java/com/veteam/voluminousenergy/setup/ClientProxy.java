package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        ScreenManager.registerFactory(VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER, PrimitiveBlastFurnaceScreen::new);
        ScreenManager.registerFactory(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PrimitiveStirlingGeneratorScreen::new);
        ScreenManager.registerFactory(VEBlocks.CRUSHER_CONTAINER, CrusherScreen::new);
        ScreenManager.registerFactory(VEBlocks.ELECTROLYZER_CONTAINER, ElectrolyzerScreen::new);
        ScreenManager.registerFactory(VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER, CentrifugalAgitatorScreen::new);
        ScreenManager.registerFactory(VEBlocks.COMPRESSOR_CONTAINER, CompressorScreen::new);
        ScreenManager.registerFactory(VEBlocks.STIRLING_GENERATOR_CONTAINER, StirlingGeneratorScreen::new);
        ScreenManager.registerFactory(VEBlocks.COMBUSTION_GENERATOR_CONTAINER, CombustionGeneratorScreen::new);
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() { return Minecraft.getInstance().player; }
}
