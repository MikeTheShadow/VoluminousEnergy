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
        MenuScreens.register(VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER.get(), PrimitiveBlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER.get(), PrimitiveStirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.CRUSHER_CONTAINER.get(), CrusherScreen::new);
        MenuScreens.register(VEBlocks.ELECTROLYZER_CONTAINER.get(), ElectrolyzerScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER.get(), CentrifugalAgitatorScreen::new);
        MenuScreens.register(VEBlocks.COMPRESSOR_CONTAINER.get(), CompressorScreen::new);
        MenuScreens.register(VEBlocks.STIRLING_GENERATOR_CONTAINER.get(), StirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.COMBUSTION_GENERATOR_CONTAINER.get(), CombustionGeneratorScreen::new);
        MenuScreens.register(VEBlocks.AQUEOULIZER_CONTAINER.get(), AqueoulizerScreen::new);
        MenuScreens.register(VEBlocks.AIR_COMPRESSOR_CONTAINER.get(), AirCompressorScreen::new);
        MenuScreens.register(VEBlocks.DISTILLATION_UNIT_CONTAINER.get(), DistillationUnitScreen::new);
        MenuScreens.register(VEBlocks.PUMP_CONTAINER.get(), PumpScreen::new);
        MenuScreens.register(VEBlocks.GAS_FIRED_FURNACE_CONTAINER.get(), GasFiredFurnaceScreen::new);
        MenuScreens.register(VEBlocks.ELECTRIC_FURNACE_CONTAINER.get(), ElectricFurnaceScreen::new);
        MenuScreens.register(VEBlocks.BATTERY_BOX_CONTAINER.get(), BatteryBoxScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_SOLAR_PANEL_CONTAINER.get(), PrimitiveSolarPanelScreen::new);
        MenuScreens.register(VEBlocks.SOLAR_PANEL_CONTAINER.get(), SolarPanelScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER.get(), CentrifugalSeparatorScreen::new);
        MenuScreens.register(VEBlocks.IMPLOSION_COMPRESSOR_CONTAINER.get(), ImplosionCompressorScreen::new);
        MenuScreens.register(VEBlocks.BLAST_FURNACE_CONTAINER.get(), BlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.TOOLING_STATION_CONTAINER.get(), ToolingStationScreen::new);
        MenuScreens.register(VEBlocks.SAWMILL_CONTAINER.get(), SawmillScreen::new);
        MenuScreens.register(VEBlocks.ALUMINUM_TANK_CONTAINER.get(), AluminumTankScreen::new);
        MenuScreens.register(VEBlocks.TITANIUM_TANK_CONTAINER.get(), TitaniumTankScreen::new);
        MenuScreens.register(VEBlocks.NETHERITE_TANK_CONTAINER.get(), NetheriteTankScreen::new);
        MenuScreens.register(VEBlocks.NIGHALITE_TANK_CONTAINER.get(), NighaliteTankScreen::new);
        MenuScreens.register(VEBlocks.EIGHZO_TANK_CONTAINER.get(), EighzoTankScreen::new);
        MenuScreens.register(VEBlocks.SOLARIUM_TANK_CONTAINER.get(), SolariumTankScreen::new);
        MenuScreens.register(VEBlocks.DIMENSIONAL_LASER_CONTAINER.get(), DimensionalLaserScreen::new);
        MenuScreens.register(VEBlocks.FLUID_ELECTROLYZER_CONTAINER.get(), FluidElectrolyzerScreen::new);
        MenuScreens.register(VEBlocks.FLUID_MIXER_CONTAINER.get(), FluidMixerScreen::new);
    }

    @Override
    public Level getClientWorld()
    {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() { return Minecraft.getInstance().player; }
}
