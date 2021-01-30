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
        ScreenManager.registerFactory(VEBlocks.AQUEOULIZER_CONTAINER, AqueoulizerScreen::new);
        ScreenManager.registerFactory(VEBlocks.AIR_COMPRESSOR_CONTAINER, AirCompressorScreen::new);
        ScreenManager.registerFactory(VEBlocks.DISTILLATION_UNIT_CONTAINER, DistillationUnitScreen::new);
        ScreenManager.registerFactory(VEBlocks.PUMP_CONTAINER, PumpScreen::new);
        ScreenManager.registerFactory(VEBlocks.GAS_FIRED_FURNACE_CONTAINER, GasFiredFurnaceScreen::new);
        ScreenManager.registerFactory(VEBlocks.ELECTRIC_FURNACE_CONTAINER, ElectricFurnaceScreen::new);
        ScreenManager.registerFactory(VEBlocks.BATTERY_BOX_CONTAINER, BatteryBoxScreen::new);
        ScreenManager.registerFactory(VEBlocks.PRIMITIVE_SOLAR_PANEL_CONTAINER, PrimitiveSolarPanelScreen::new);
        ScreenManager.registerFactory(VEBlocks.SOLAR_PANEL_CONTAINER, SolarPanelScreen::new);
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() { return Minecraft.getInstance().player; }
}
