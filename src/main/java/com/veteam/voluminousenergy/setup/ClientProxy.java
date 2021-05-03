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
        ScreenManager.register(VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER, PrimitiveBlastFurnaceScreen::new);
        ScreenManager.register(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PrimitiveStirlingGeneratorScreen::new);
        ScreenManager.register(VEBlocks.CRUSHER_CONTAINER, CrusherScreen::new);
        ScreenManager.register(VEBlocks.ELECTROLYZER_CONTAINER, ElectrolyzerScreen::new);
        ScreenManager.register(VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER, CentrifugalAgitatorScreen::new);
        ScreenManager.register(VEBlocks.COMPRESSOR_CONTAINER, CompressorScreen::new);
        ScreenManager.register(VEBlocks.STIRLING_GENERATOR_CONTAINER, StirlingGeneratorScreen::new);
        ScreenManager.register(VEBlocks.COMBUSTION_GENERATOR_CONTAINER, CombustionGeneratorScreen::new);
        ScreenManager.register(VEBlocks.AQUEOULIZER_CONTAINER, AqueoulizerScreen::new);
        ScreenManager.register(VEBlocks.AIR_COMPRESSOR_CONTAINER, AirCompressorScreen::new);
        ScreenManager.register(VEBlocks.DISTILLATION_UNIT_CONTAINER, DistillationUnitScreen::new);
        ScreenManager.register(VEBlocks.PUMP_CONTAINER, PumpScreen::new);
        ScreenManager.register(VEBlocks.GAS_FIRED_FURNACE_CONTAINER, GasFiredFurnaceScreen::new);
        ScreenManager.register(VEBlocks.ELECTRIC_FURNACE_CONTAINER, ElectricFurnaceScreen::new);
        ScreenManager.register(VEBlocks.BATTERY_BOX_CONTAINER, BatteryBoxScreen::new);
        ScreenManager.register(VEBlocks.PRIMITIVE_SOLAR_PANEL_CONTAINER, PrimitiveSolarPanelScreen::new);
        ScreenManager.register(VEBlocks.SOLAR_PANEL_CONTAINER, SolarPanelScreen::new);
        ScreenManager.register(VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER, CentrifugalSeparatorScreen::new);
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().level;
    }

    @Override
    public PlayerEntity getClientPlayer() { return Minecraft.getInstance().player; }
}
