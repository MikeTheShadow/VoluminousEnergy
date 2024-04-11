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
        MenuScreens.register(VEBlocks.PRIMITIVE_BLAST_FURNACE.container().get(), PrimitiveBlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_STIRLING_GENERATOR.container().get(), PrimitiveStirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.CRUSHER.container().get(), CrusherScreen::new);
        MenuScreens.register(VEBlocks.ELECTROLYZER.container().get(), ElectrolyzerScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_AGITATOR.container().get(), CentrifugalAgitatorScreen::new);
        MenuScreens.register(VEBlocks.COMPRESSOR.container().get(), CompressorScreen::new);
        MenuScreens.register(VEBlocks.STIRLING_GENERATOR.container().get(), StirlingGeneratorScreen::new);
        MenuScreens.register(VEBlocks.COMBUSTION_GENERATOR.container().get(), CombustionGeneratorScreen::new);
        MenuScreens.register(VEBlocks.AQUEOULIZER.container().get(), AqueoulizerScreen::new);
        MenuScreens.register(VEBlocks.AIR_COMPRESSOR.container().get(), AirCompressorScreen::new);
        MenuScreens.register(VEBlocks.DISTILLATION_UNIT.container().get(), DistillationUnitScreen::new);
        MenuScreens.register(VEBlocks.PUMP.container().get(), PumpScreen::new);
        MenuScreens.register(VEBlocks.GAS_FIRED_FURNACE.container().get(), GasFiredFurnaceScreen::new);
        MenuScreens.register(VEBlocks.ELECTRIC_FURNACE.container().get(), ElectricFurnaceScreen::new);
        MenuScreens.register(VEBlocks.BATTERY_BOX.container().get(), BatteryBoxScreen::new);
        MenuScreens.register(VEBlocks.PRIMITIVE_SOLAR_PANEL.container().get(), PrimitiveSolarPanelScreen::new);
        MenuScreens.register(VEBlocks.SOLAR_PANEL.container().get(), SolarPanelScreen::new);
        MenuScreens.register(VEBlocks.CENTRIFUGAL_SEPARATOR.container().get(), CentrifugalSeparatorScreen::new);
        MenuScreens.register(VEBlocks.IMPLOSION_COMPRESSOR.container().get(), ImplosionCompressorScreen::new);
        MenuScreens.register(VEBlocks.BLAST_FURNACE.container().get(), BlastFurnaceScreen::new);
        MenuScreens.register(VEBlocks.TOOLING_STATION.container().get(), ToolingStationScreen::new);
        MenuScreens.register(VEBlocks.SAWMILL.container().get(), SawmillScreen::new);
        MenuScreens.register(VEBlocks.ALUMINUM_TANK.container().get(), AluminumTankScreen::new);
        MenuScreens.register(VEBlocks.TITANIUM_TANK.container().get(), TitaniumTankScreen::new);
        MenuScreens.register(VEBlocks.NETHERITE_TANK.container().get(), NetheriteTankScreen::new);
        MenuScreens.register(VEBlocks.NIGHALITE_TANK.container().get(), NighaliteTankScreen::new);
        MenuScreens.register(VEBlocks.EIGHZO_TANK.container().get(), EighzoTankScreen::new);
        MenuScreens.register(VEBlocks.SOLARIUM_TANK.container().get(), SolariumTankScreen::new);
        MenuScreens.register(VEBlocks.DIMENSIONAL_LASER.container().get(), DimensionalLaserScreen::new);
        MenuScreens.register(VEBlocks.FLUID_ELECTROLYZER.container().get(), FluidElectrolyzerScreen::new);
        MenuScreens.register(VEBlocks.FLUID_MIXER.container().get(), FluidMixerScreen::new);
        MenuScreens.register(VEBlocks.HYDROPONIC_INCUBATOR.container().get(), HydroponicIncubatorScreen::new);
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
