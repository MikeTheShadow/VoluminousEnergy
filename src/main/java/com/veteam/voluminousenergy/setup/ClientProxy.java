package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.*;
import com.veteam.voluminousenergy.blocks.screens.tank.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        MenuScreens.register(VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER, PrimitiveBlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PrimitiveStirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.CRUSHER_CONTAINER, CrusherScreen::new);
        MenuScreens.register(VEBlocks.ELECTROLYZER_CONTAINER, ElectrolyzerScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER, CentrifugalAgitatorScreen::new);
        MenuScreens.register(VEBlocks.COMPRESSOR_CONTAINER, CompressorScreen::new);
        MenuScreens.register(VEBlocks.STIRLING_GENERATOR_CONTAINER, StirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.COMBUSTION_GENERATOR_CONTAINER, CombustionGeneratorScreen::new);
        MenuScreens.register(VEBlocks.AQUEOULIZER_CONTAINER, AqueoulizerScreen::new);
        MenuScreens.register(VEBlocks.AIR_COMPRESSOR_CONTAINER, AirCompressorScreen::new);
        MenuScreens.register(VEBlocks.DISTILLATION_UNIT_CONTAINER, DistillationUnitScreen::new);
        MenuScreens.register(VEBlocks.PUMP_CONTAINER, PumpScreen::new);
        MenuScreens.register(VEBlocks.GAS_FIRED_FURNACE_CONTAINER, GasFiredFurnaceScreen::new);
        MenuScreens.register(VEBlocks.ELECTRIC_FURNACE_CONTAINER, ElectricFurnaceScreen::new);
        MenuScreens.register(VEBlocks.BATTERY_BOX_CONTAINER, BatteryBoxScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_SOLAR_PANEL_CONTAINER, PrimitiveSolarPanelScreen::new);
        MenuScreens.register(VEBlocks.SOLAR_PANEL_CONTAINER, SolarPanelScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER, CentrifugalSeparatorScreen::new);
        MenuScreens.register(VEBlocks.IMPLOSION_COMPRESSOR_CONTAINER, ImplosionCompressorScreen::new);
        MenuScreens.register(VEBlocks.BLAST_FURNACE_CONTAINER, BlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.TOOLING_STATION_CONTAINER, ToolingStationScreen::new);
        MenuScreens.register(VEBlocks.SAWMILL_CONTAINER, SawmillScreen::new);
        MenuScreens.register(VEBlocks.ALUMINUM_TANK_CONTAINER, AluminumTankScreen::new);
        MenuScreens.register(VEBlocks.TITANIUM_TANK_CONTAINER, TitaniumTankScreen::new);
        MenuScreens.register(VEBlocks.NETHERITE_TANK_CONTAINER, NetheriteTankScreen::new);
        MenuScreens.register(VEBlocks.NIGHALITE_TANK_CONTAINER, NighaliteTankScreen::new);
        MenuScreens.register(VEBlocks.EIGHZO_TANK_CONTAINER, EighzoTankScreen::new);
        MenuScreens.register(VEBlocks.SOLARIUM_TANK_CONTAINER, SolariumTankScreen::new);
    }

    @Override
    public Level getClientWorld()
    {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() { return Minecraft.getInstance().player; }
}
