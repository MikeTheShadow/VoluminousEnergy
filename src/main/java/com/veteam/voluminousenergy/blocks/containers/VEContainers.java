package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory.VEContainerFactoryBuilder;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.*;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.*;
import static net.minecraft.core.Direction.*;

public class VEContainers {

    public static final VEContainerFactory AIR_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(AIR_COMPRESSOR.container(), AIR_COMPRESSOR.block())
            .addSlot(70, 18, new BucketInputSlot(UP, 0)) // Air Compressor bucket input slot
            .addSlot(70, 49, new BucketOutputSlot(DOWN)) // Air Compressor bucket output slot
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory AQUEOULIZER_FACTORY = new VEContainerFactoryBuilder()
            .create(AQUEOULIZER.container(), AQUEOULIZER.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0))
            .addSlot(38, 49, new BucketOutputSlot(DOWN))
            .addSlot(137, 18, new BucketInputSlot(NORTH, 1))
            .addSlot(137, 49, new BucketOutputSlot(SOUTH))
            .addSlot(96, 32, new ItemInputSlot(EAST))
            .addUpgradeSlot(130, -14)
            .build();

    public static final VEContainerFactory BATTERY_BOX_FACTORY = new VEContainerFactoryBuilder()
            .create(BATTERY_BOX.container(), BATTERY_BOX.block())
            .addSlot(35, 17, new InputSlot(UP)) // Top Slot
            .addSlot(35, 54, new ItemOutputSlot(DOWN)) //Bottom Slot
            .build();

    public static final VEContainerFactory BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(BLAST_FURNACE.container(), BLAST_FURNACE.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Fluid input slot
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from heat tank
            .addSlot(80, 25, new InputSlot(EAST)) // First input slot
            .addSlot(80, 43, new InputSlot(WEST)) // Second input slot
            .addSlot(134, 34, new ItemOutputSlot(NORTH)) // Third input slot
            .addUpgradeSlot(130, -14) // Upgrade slot.
            .build();

    public static final VEContainerFactory CENTRIFUGAL_AGITATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_AGITATOR.container(), CENTRIFUGAL_AGITATOR.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Bucket insert; input tank
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Bucket extract; input tank
            .addSlot(96, 18, new BucketInputSlot(NORTH, 1)) // Bucket insert; first output tank
            .addSlot(96, 49, new BucketOutputSlot(SOUTH)) // Bucket extract; first output tank
            .addSlot(137, 18, new BucketInputSlot(EAST, 2)) // Bucket insert; second output tank
            .addSlot(137, 49, new BucketOutputSlot(WEST)) // Bucket extract; second output tank
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
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Oxidizer input slot
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from oxidizer slot
            .addSlot(138, 18, new BucketInputSlot(NORTH, 1)) // Fuel input slot
            .addSlot(138, 49, new BucketOutputSlot(SOUTH)) // Extract fluid from fuel output
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
            .addSlot(71, 58, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(89, 58, new ItemOutputSlot(NORTH)) // RNG slot
            .addUpgradeSlot(154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory DIMENSIONAL_LASER_FACTORY = new VEContainerFactoryBuilder()
            .create(DIMENSIONAL_LASER.container(), DIMENSIONAL_LASER.block())
            .addSlot(138, 18, new BucketInputSlot(UP, 0)) // Bucket top slot
            .addSlot(138, 49, new BucketOutputSlot(DOWN)) // Bucket bottom slot
            .addSlot(38, 33, new ItemInputSlot(NORTH)) // RFID chip slot
            .addUpgradeSlot(130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory DISTILLATION_UNIT_FACTORY = new VEContainerFactoryBuilder()
            .create(DISTILLATION_UNIT.container(), DISTILLATION_UNIT.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Fluid input slot
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(96, 11, new BucketInputSlot(UP, 1)) // Top slot for first output
            .addSlot(96, 42, new BucketOutputSlot(DOWN)) // Bottom slot for first output
            .addSlot(137, 11, new BucketInputSlot(UP, 2)) // Top tilePos for from second output
            .addSlot(137, 42, new BucketOutputSlot(DOWN)) // Bottom tilePos for second output
            .addSlot(122, 64, new ItemOutputSlot(DOWN)) // Item Output Slot
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory ELECTRIC_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTRIC_FURNACE.container(), ELECTRIC_FURNACE.block())
            .addSlot(53, 33, new ItemInputSlot(UP)) // Furnace Input Slot
            .addSlot(116, 33, new ItemOutputSlot(DOWN)) // Furnace Output Slot
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
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Top input bucket
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Bottom input bucket
            .addSlot(96, 18, new BucketInputSlot(EAST, 1)) // Top output0 bucket
            .addSlot(96, 49, new BucketOutputSlot(WEST)) // Bottom output0 bucket
            .addSlot(137, 18, new BucketInputSlot(NORTH, 2)) // Top output1 bucket
            .addSlot(137, 49, new BucketOutputSlot(SOUTH)) // Bottom output1 bucket
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory FLUID_MIXER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_MIXER.container(), FLUID_MIXER.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Top input0 bucket
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Bottom input0 bucket
            .addSlot(86, 18, new BucketInputSlot(EAST, 1)) // Top input1 bucket
            .addSlot(86, 49, new BucketOutputSlot(WEST)) // Bottom input1 bucket
            .addSlot(136, 18, new BucketInputSlot(NORTH, 2)) // Top output0 bucket
            .addSlot(136, 49, new BucketOutputSlot(SOUTH)) // Bottom output0 bucket
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory GAS_FIRED_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(GAS_FIRED_FURNACE.container(), GAS_FIRED_FURNACE.block())
            .addSlot(8, 18, new BucketInputSlot(UP, 0)) // Fluid input tilePos
            .addSlot(8, 49, new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(53, 33, new ItemInputSlot(EAST)) // Item input tilePos
            .addSlot(116, 33, new ItemOutputSlot(WEST)) // Item output tilePos
            .addUpgradeSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory HYDROPONIC_INCUBATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(HYDROPONIC_INCUBATOR.container(), HYDROPONIC_INCUBATOR.block())
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Bucket top tilePos
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Bucket bottom tilePos
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
            .addSlot(70, 18, new BucketInputSlot(UP, 0)) // Pump bucket input tilePos
            .addSlot(70, 49, new BucketOutputSlot(DOWN)) // Pump bucket output tilePos
            .build();

    public static final VEContainerFactory SAWMILL_FACTORY = new VEContainerFactoryBuilder()
            .create(SAWMILL.container(), SAWMILL.block())
            .addSlot(44, 32, new ItemInputSlot(UP)) // Log input tilePos
            .addSlot(80, 24, new ItemOutputSlot(DOWN)) // Plank Output
            .addSlot(80, 42, new ItemOutputSlot(NORTH)) // Secondary Output
            .addSlot(115, 18, new BucketInputSlot(SOUTH, 0)) // Bucket Input
            .addSlot(115, 49, new BucketOutputSlot(EAST)) // Bucket Output
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
            .addSlot(38, 18, new BucketInputSlot(UP, 0)) // Fluid input tilePos
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(86, 32, new ItemInputSlot(NORTH)) // Main Tool tilePos
            .addSlot(134, 18, new ItemInputSlot(SOUTH)) // Bit Slot
            .addSlot(134, 49, new ItemInputSlot(EAST)) // Base Slot
            .build();

    public static final VEContainerFactory ALUMINUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(ALUMINUM_TANK.container(),ALUMINUM_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();

    public static final VEContainerFactory EIGHZO_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(EIGHZO_TANK.container(),EIGHZO_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();

    public static final VEContainerFactory NETHERITE_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(NETHERITE_TANK.container(),NETHERITE_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();

    public static final VEContainerFactory NIGHALITE_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(NIGHALITE_TANK.container(),NIGHALITE_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();

    public static final VEContainerFactory SOLARIUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLARIUM_TANK.container(),SOLARIUM_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();

    public static final VEContainerFactory TITANIUM_TANK_FACTORY = new VEContainerFactoryBuilder()
            .create(TITANIUM_TANK.container(),TITANIUM_TANK.block())
            .addSlot(70,19,new BucketInputSlot(UP,0))
            .addSlot(70,50,new BucketOutputSlot(DOWN))
            .build();
}
