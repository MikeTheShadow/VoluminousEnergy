package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory.VEContainerFactoryBuilder;
import com.veteam.voluminousenergy.blocks.containers.iolisteners.ExperienceListener;
import com.veteam.voluminousenergy.blocks.containers.iolisteners.ToolingStationSlotWithIOListening;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.*;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.*;
import static net.minecraft.core.Direction.*;

public class VEContainers {

    public static final VEContainerFactory AIR_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(AIR_COMPRESSOR.container(), AIR_COMPRESSOR.block())
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory AQUEOULIZER_FACTORY = new VEContainerFactoryBuilder()
            .create(AQUEOULIZER.container(), AQUEOULIZER.block())
            .addSlot(85, 32, new ItemInputSlot(EAST))
            .addUpgradeSlot(130, -14)
            .build();

    public static final VEContainerFactory BATTERY_BOX_FACTORY = new VEContainerFactoryBuilder()
            .create(BATTERY_BOX.container(), BATTERY_BOX.block())
            .addSlot(80, 17, new InputSlot(UP)) // Top Slot
            .addSlot(80, 62, new ItemOutputSlot(DOWN)) //Bottom Slot
            .build();

    public static final VEContainerFactory BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(BLAST_FURNACE.container(), BLAST_FURNACE.block())
            .addSlot(80, 25, new InputSlot(EAST)) // First input slot
            .addSlot(80, 43, new InputSlot(WEST)) // Second input slot
            .addSlot(134, 34, new ItemOutputSlot(NORTH)) // Third input slot
            .addUpgradeSlot(130, -14) // Upgrade slot.
            .build();

    public static final VEContainerFactory CENTRIFUGAL_AGITATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_AGITATOR.container(), CENTRIFUGAL_AGITATOR.block())
            .addUpgradeSlot(130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory CENTRIFUGAL_SEPARATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_SEPARATOR.container(), CENTRIFUGAL_SEPARATOR.block())
            .addSlot(53, 24, new ItemInputSlot(UP)) // Primary input slot
            .addSlot(53, 42, new ItemInputSlot(WEST)) // Empty Bucket slot
            .addSlot(99, 33, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(117, 15, new ItemOutputSlot(NORTH)) //RNG #1 Slot
            .addSlot(135, 33, new ItemOutputSlot(SOUTH)) //RNG #2 Slot
            .addSlot(117, 51, new ItemOutputSlot(EAST)) //RNG #3 Slot
            .addUpgradeSlot(155, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory COMBUSTION_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMBUSTION_GENERATOR.container(), COMBUSTION_GENERATOR.block())
            .build();

    public static final VEContainerFactory COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMPRESSOR.container(), COMPRESSOR.block())
            .addSlot(80, 13, new ItemInputSlot(UP))
            .addSlot(80, 58, new ItemOutputSlot(DOWN)) //Main Output
            .addUpgradeSlot(154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory CRUSHER_FACTORY = new VEContainerFactoryBuilder()
            .create(CRUSHER.container(), CRUSHER.block())
            .addSlot(80, 13, new ItemInputSlot(UP)) // Input Slot
            .addSlot(71, 58, new ListenedItemOutputSlot(DOWN, new ExperienceListener())) //Main Output
            .addSlot(89, 58, new ListenedItemOutputSlot(NORTH, new ExperienceListener())) // RNG slot
            .addUpgradeSlot(154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory DIMENSIONAL_LASER_FACTORY = new VEContainerFactoryBuilder()
            .create(DIMENSIONAL_LASER.container(), DIMENSIONAL_LASER.block())
            .addSlot(60, 33, new ItemInputSlot(NORTH)) // RFID chip slot
            .addUpgradeSlot(130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory DISTILLATION_UNIT_FACTORY = new VEContainerFactoryBuilder()
            .create(DISTILLATION_UNIT.container(), DISTILLATION_UNIT.block())
            .addSlot(126, 62, new ItemOutputSlot(DOWN)) // Item Output Slot
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory ELECTRIC_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTRIC_FURNACE.container(), ELECTRIC_FURNACE.block())
            .addSlot(53, 33, new ItemInputSlot(UP)) // Furnace Input Slot
            .addSlot(116, 33, new ListenedItemOutputSlot(DOWN, new ExperienceListener())) // Furnace Output Slot
            .addUpgradeSlot(154, -14)// Upgrade Slot
            .build();

    public static final VEContainerFactory ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTROLYZER.container(), ELECTROLYZER.block())
            .addSlot(71, 13, new ItemInputSlot(UP))
            .addSlot(89, 13, new ItemInputSlot(WEST)) // Empty Bucket tilePos
            .addSlot(53, 57, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(71, 57, new ItemOutputSlot(NORTH)) //RNG #1 Slot
            .addSlot(89, 57, new ItemOutputSlot(SOUTH)) //RNG #2 Slot
            .addSlot(107, 57, new ItemOutputSlot(EAST)) //RNG #3 Slot
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory FLUID_ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_ELECTROLYZER.container(), FLUID_ELECTROLYZER.block())
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory FLUID_MIXER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_MIXER.container(), FLUID_MIXER.block())
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory GAS_FIRED_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(GAS_FIRED_FURNACE.container(), GAS_FIRED_FURNACE.block())
            .addSlot(65, 33, new ItemInputSlot(EAST)) // Item input tilePos
            .addSlot(125, 33, new ListenedItemOutputSlot(WEST, new ExperienceListener())) // Item output tilePos
            .addUpgradeSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory HYDROPONIC_INCUBATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(HYDROPONIC_INCUBATOR.container(), HYDROPONIC_INCUBATOR.block())
            .addSlot(83, 34, new ItemInputSlot(NORTH)) // Primary input
            .addSlot(123, 8, new ItemOutputSlot(NORTH)) // Primary output
            .addSlot(123, 26, new ItemOutputSlot(NORTH)) // RNG0 output
            .addSlot(123, 44, new ItemOutputSlot(NORTH)) // RNG1 output
            .addSlot(123, 62, new ItemOutputSlot(NORTH)) // RNG2 output
            .addUpgradeSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory IMPLOSION_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(IMPLOSION_COMPRESSOR.container(), IMPLOSION_COMPRESSOR.block())
            .addSlot(53, 23, new ItemInputSlot(UP)) // Main input
            .addSlot(53, 41, new ItemInputSlot(EAST)) // Gunpowder tilePos
            .addSlot(116, 33, new ItemOutputSlot(DOWN)) //Main Output
            .addUpgradeSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory PRIMITIVE_BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_BLAST_FURNACE.container(), PRIMITIVE_BLAST_FURNACE.block())
            .addSlot(53, 33, new ItemInputSlot(UP))
            .addSlot(116, 33, new ItemOutputSlot(DOWN))
            .addUpgradeSlot(154, -14)
            .build();

    public static final VEContainerFactory PRIMITIVE_SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_SOLAR_PANEL.container(), PRIMITIVE_SOLAR_PANEL.block())
            .build();

    public static final VEContainerFactory PRIMITIVE_STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_STIRLING_GENERATOR.container(), PRIMITIVE_STIRLING_GENERATOR.block())
            .addSlot(80, 35, new ItemInputSlot(UP))
            .build();

    public static final VEContainerFactory PUMP_FACTORY = new VEContainerFactoryBuilder()
            .create(PUMP.container(), PUMP.block())
            .build();

    public static final VEContainerFactory SAWMILL_FACTORY = new VEContainerFactoryBuilder()
            .create(SAWMILL.container(), SAWMILL.block())
            .addSlot(44, 32, new ItemInputSlot(UP)) // Log input tilePos
            .addSlot(80, 24, new ItemOutputSlot(DOWN)) // Plank Output
            .addSlot(80, 42, new ItemOutputSlot(NORTH)) // Secondary Output
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLAR_PANEL.container(), SOLAR_PANEL.block())
            .build();

    public static final VEContainerFactory STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(STIRLING_GENERATOR.container(), STIRLING_GENERATOR.block())
            .addSlot(80, 35, new ItemInputSlot(UP))
            .build();

    public static final VEContainerFactory TOOLING_STATION_FACTORY = new VEContainerFactoryBuilder()
            .create(TOOLING_STATION.container(), TOOLING_STATION.block())
            .addSlot(86, 32, new ListenedItemInputSlot(NORTH, new ToolingStationSlotWithIOListening())) // Main Tool tilePos
            .addSlot(134, 7, new ItemInputSlot(SOUTH)) // Bit Slot 1
            .addSlot(134, 25, new ItemInputSlot(EAST)) // Bit Slot 2
            .addSlot(134, 43, new ItemInputSlot(EAST)) // Bit Slot 3
            .addSlot(134, 61, new ItemInputSlot(EAST)) // Bit Slot 4
            .build();

    public static final VEContainerFactory ALUMINUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(ALUMINUM_TANK.container(),ALUMINUM_TANK.block())
            .build();

    public static final VEContainerFactory EIGHZO_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(EIGHZO_TANK.container(),EIGHZO_TANK.block())
            .build();

    public static final VEContainerFactory NETHERITE_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(NETHERITE_TANK.container(),NETHERITE_TANK.block())
            .build();

    public static final VEContainerFactory NIGHALITE_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(NIGHALITE_TANK.container(),NIGHALITE_TANK.block())
            .build();

    public static final VEContainerFactory SOLARIUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLARIUM_TANK.container(),SOLARIUM_TANK.block())
            .build();

    public static final VEContainerFactory TITANIUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(TITANIUM_TANK.container(),TITANIUM_TANK.block())
            .build();
}
