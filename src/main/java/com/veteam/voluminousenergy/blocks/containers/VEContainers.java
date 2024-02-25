package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory.VEContainerFactoryBuilder;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.*;
import net.minecraft.core.Direction;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.*;

public class VEContainers {

    public static final VEContainerFactory AIR_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(AIR_COMPRESSOR_CONTAINER, AIR_COMPRESSOR_BLOCK)
            .addSlot(70, 18,new BucketInputSlot(Direction.UP,0)) // Air Compressor bucket input tilePos
            .addSlot(70, 49,new BucketOutputSlot(Direction.DOWN)) // Air Compressor bucket output tilePos
            .addSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory AQUEOULIZER_FACTORY = new VEContainerFactoryBuilder()
            .create(AQUEOULIZER_CONTAINER, AQUEOULIZER_BLOCK)
            .addSlot(38, 18, new BucketInputSlot(Direction.UP,0))
            .addSlot(38, 49, new BucketOutputSlot(Direction.DOWN))
            .addSlot(137, 18, new BucketInputSlot(Direction.NORTH,1))
            .addSlot(137, 49, new BucketOutputSlot(Direction.SOUTH))
            .addSlot(96, 32, new ItemInputSlot(Direction.EAST, 0))
            .addUpgradeSlot(130, -14)
            .build();

    public static final VEContainerFactory BATTERY_BOX_FACTORY = new VEContainerFactoryBuilder()
            .create(BATTERY_BOX_CONTAINER, BATTERY_BOX_BLOCK)
            .addSlot(35, 17,new InputSlot(Direction.UP))
            .addSlot(53, 17,new InputSlot(Direction.UP))
            .addSlot(71, 17,new InputSlot(Direction.UP))
            .addSlot(89, 17,new InputSlot(Direction.UP))
            .addSlot(107, 17,new InputSlot(Direction.UP))
            .addSlot(125, 17,new InputSlot(Direction.UP))
            //Bottom Slots
            .addSlot(35, 54,new InputSlot(Direction.DOWN))
            .addSlot(53, 54,new InputSlot(Direction.DOWN))
            .addSlot(71, 54,new InputSlot(Direction.DOWN))
            .addSlot(89, 54,new InputSlot(Direction.DOWN))
            .addSlot(107, 54,new InputSlot(Direction.DOWN))
            .addSlot(125, 54,new InputSlot(Direction.DOWN))
            .build();

    public static final VEContainerFactory BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(BLAST_FURNACE_CONTAINER, BLAST_FURNACE_BLOCK)
            .addSlot(38, 18) // Fluid input tilePos
            .addSlot(38, 49) // Extract fluid from heat tank
            .addSlot(80, 25) // First input tilePos
            .addSlot(80, 43) // Second input tilePos
            .addSlot(134, 34) // Third input tilePos
            .addSlot(130, -14) // Upgrade tilePos.
            .build();

    public static final VEContainerFactory CENTRIFUGAL_AGITATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_AGITATOR_CONTAINER, CENTRIFUGAL_AGITATOR_BLOCK)
            .addSlot(38, 18) // Bucket insert; input tank
            .addSlot(38, 49) // Bucket extract; input tank
            .addSlot(96, 18) // Bucket insert; first output tank
            .addSlot(96, 49) // Bucket extract; first output tank
            .addSlot(137, 18) // Bucket insert; second output tank
            .addSlot(137, 49) // Bucket extract; second output tank
            .addSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory CENTRIFUGAL_SEPARATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_SEPARATOR_CONTAINER, CENTRIFUGAL_SEPARATOR_BLOCK)
            .addSlot(53, 24) // Primary input tilePos
            .addSlot(53, 42) // Empty Bucket tilePos
            .addSlot(99, 33) //Main Output
            .addSlot(117, 15) //RNG #1 Slot
            .addSlot(135, 33) //RNG #2 Slot
            .addSlot(117, 51) //RNG #3 Slot
            .addSlot(155, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory COMBUSTION_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMBUSTION_GENERATOR_CONTAINER, COMBUSTION_GENERATOR_BLOCK)
            .addSlot(38, 18) // Oxidizer input tilePos
            .addSlot(38, 49) // Extract fluid from oxidizer tilePos
            .addSlot(138, 18) // Fuel input tilePos
            .addSlot(138, 49) // Extract fluid from fuel output
            .build();

    public static final VEContainerFactory COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMPRESSOR_CONTAINER, COMPRESSOR_BLOCK)
            .addSlot(80, 13)
            .addSlot(80, 58) //Main Output
            .addSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory CRUSHER_FACTORY = new VEContainerFactoryBuilder()
            .create(CRUSHER_CONTAINER, CRUSHER_BLOCK)
            .addSlot(80, 13)
            .addSlot(80, 58) //Main Output
            .addSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory DIMENSIONAL_LASER_FACTORY = new VEContainerFactoryBuilder()
            .create(DIMENSIONAL_LASER_CONTAINER, DIMENSIONAL_LASER_BLOCK)
            .addSlot(138, 18) // Bucket top tilePos
            .addSlot(138, 49) // Bucket bottom tilePos
            .addSlot(38, 33) // RFID chip tilePos
            .addSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory DISTILLATION_UNIT_FACTORY = new VEContainerFactoryBuilder()
            .create(DISTILLATION_UNIT_CONTAINER, DISTILLATION_UNIT_BLOCK)
            .addSlot(38, 18) // Fluid input tilePos
            .addSlot(38, 49) // Extract fluid from input
            .addSlot(96, 11) // Top tilePos for first output
            .addSlot(96, 42) // Bottom tilePos for first output
            .addSlot(137, 11) // Top tilePos for from second output
            .addSlot(137, 42) // Bottom tilePos for second output
            .addSlot(122, 64) // Item Output Slot
            .addSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory ELECTRIC_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTRIC_FURNACE_CONTAINER, ELECTRIC_FURNACE_BLOCK)
            .addSlot(53, 33) // Furnace Input Slot
            .addSlot(116, 33) // Furnace Output Slot
            .addSlot(154, -14)// Upgrade Slot
            .build();

    public static final VEContainerFactory ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTROLYZER_CONTAINER, ELECTROLYZER_BLOCK)
            .addSlot(71, 13)
            .addSlot(89, 13) // Empty Bucket tilePos
            .addSlot(53, 57) //Main Output
            .addSlot(71, 57) //RNG #1 Slot
            .addSlot(89, 57) //RNG #2 Slot
            .addSlot(107, 57) //RNG #3 Slot
            .addSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory FLUID_ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_ELECTROLYZER_CONTAINER, FLUID_ELECTROLYZER_BLOCK)
            .addSlot(38, 18) // Top input bucket
            .addSlot(38, 49) // Bottom input bucket
            .addSlot(96, 18) // Top output0 bucket
            .addSlot(96, 49) // Bottom output0 bucket
            .addSlot(137, 18) // Top output1 bucket
            .addSlot(137, 49) // Bottom output1 bucket
            .addSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory FLUID_MIXER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_MIXER_CONTAINER, FLUID_MIXER_BLOCK)
            .addSlot(38, 18) // Top input0 bucket
            .addSlot(38, 49) // Bottom input0 bucket
            .addSlot(86, 18) // Top input1 bucket
            .addSlot(86, 49) // Bottom input1 bucket
            .addSlot(136, 18) // Top output0 bucket
            .addSlot(136, 49) // Bottom output0 bucket
            .addSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory GAS_FIRED_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(GAS_FIRED_FURNACE_CONTAINER, GAS_FIRED_FURNACE_BLOCK)
            .addSlot(8, 18) // Fluid input tilePos
            .addSlot(8, 49) // Extract fluid from input
            .addSlot(53, 33) // Item input tilePos
            .addSlot(116, 33) // Item output tilePos
            .addSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory HYDROPONIC_INCUBATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(HYDROPONIC_INCUBATOR_CONTAINER, HYDROPONIC_INCUBATOR_BLOCK)
            .addSlot(38, 18) // Bucket top tilePos
            .addSlot(38, 49) // Bucket bottom tilePos
            .addSlot(83, 34) // Primary input
            .addSlot(123, 8) // Primary output
            .addSlot(123, 26) // RNG0 output
            .addSlot(123, 44) // RNG1 output
            .addSlot(123, 62) // RNG2 output
            .addSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory IMPLOSION_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(IMPLOSION_COMPRESSOR_CONTAINER, IMPLOSION_COMPRESSOR_BLOCK)
            .addSlot(53, 23) // Main input
            .addSlot(53, 41) // Gunpowder tilePos
            .addSlot(116, 33) //Main Output
            .addSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory PRIMITIVE_BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_BLAST_FURNACE_CONTAINER, PRIMITIVE_BLAST_FURNACE_BLOCK)
            .addSlot(53, 33)
            .addSlot(116, 33)
            .addSlot(154, -14)
            .build();

    public static final VEContainerFactory PRIMITIVE_SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_SOLAR_PANEL_CONTAINER, PRIMITIVE_SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory PRIMITIVE_STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PRIMITIVE_STIRLING_GENERATOR_BLOCK)
            .addSlot(80, 35, new ItemInputSlot(Direction.UP, 0))
            .build();

    public static final VEContainerFactory PUMP_FACTORY = new VEContainerFactoryBuilder()
            .create(PUMP_CONTAINER, PUMP_BLOCK)
            .addSlot(70, 18) // Pump bucket input tilePos
            .addSlot(70, 49) // Pump bucket output tilePos
            .build();

    public static final VEContainerFactory SAWMILL_FACTORY = new VEContainerFactoryBuilder()
            .create(SAWMILL_CONTAINER, SAWMILL_BLOCK)
            .addSlot(44, 32) // Log input tilePos
            .addSlot(80, 24) // Plank Output
            .addSlot(80, 42) // Secondary Output
            .addSlot(115, 18) // Bucket Input
            .addSlot(115, 49) // Bucket Output
            .addSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLAR_PANEL_CONTAINER, SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(STIRLING_GENERATOR_CONTAINER, STIRLING_GENERATOR_BLOCK)
            .addSlot(80, 35)
            .build();

    // TOOLING STATION UNUSED
    public static final VEContainerFactory TOOLING_STATION_FACTORY = new VEContainerFactoryBuilder()
            .create(TOOLING_STATION_CONTAINER, TOOLING_STATION_BLOCK)
            .addSlot(38, 18) // Fluid input tilePos
            .addSlot(38, 49) // Extract fluid from input
            .addSlot(86, 32) // Main Tool tilePos
            .addSlot(134, 18) // Bit Slot
            .addSlot(134, 49) // Base Slot
            .addSlot(154, -14) // Upgrade Slot
            .build();
}
