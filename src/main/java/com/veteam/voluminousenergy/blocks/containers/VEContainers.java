package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory.VEContainerFactoryBuilder;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.*;

public class VEContainers {

    public static final VEContainerFactory AIR_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(AIR_COMPRESSOR_CONTAINER, AIR_COMPRESSOR_BLOCK)
            .addSlot(0, 70, 18) // Air Compressor bucket input slot
            .addSlot(1, 70, 49) // Air Compressor bucket output slot
            .addSlot(2, 154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory AQUEOULIZER_FACTORY = new VEContainerFactoryBuilder()
            .create(AQUEOULIZER_CONTAINER, AQUEOULIZER_BLOCK)
            .addSlot(0, 38, 18) // Bucket input; input tank
            .addSlot(1, 38, 49) // Bucket output; input tank
            .addSlot(2, 137, 18) // Bucket insert; output tank
            .addSlot(3, 137, 49) // Bucket output; output tank
            .addSlot(4, 96, 32) // Item to be dissolved
            .addSlot(5, 130, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory BATTERY_BOX_FACTORY = new VEContainerFactoryBuilder()
            .create(BATTERY_BOX_CONTAINER, BATTERY_BOX_BLOCK)
            .addSlot(0, 35, 17)
            .addSlot(1, 53, 17)
            .addSlot(2, 71, 17)
            .addSlot(3, 89, 17)
            .addSlot(4, 107, 17)
            .addSlot(5, 125, 17)
            //Bottom Slots
            .addSlot(6, 35, 54)
            .addSlot(7, 53, 54)
            .addSlot(8, 71, 54)
            .addSlot(9, 89, 54)
            .addSlot(10, 107, 54)
            .addSlot(11, 125, 54)
            .build();

    public static final VEContainerFactory BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(BLAST_FURNACE_CONTAINER, BLAST_FURNACE_BLOCK)
            .addSlot(0, 38, 18) // Fluid input slot
            .addSlot(1, 38, 49) // Extract fluid from heat tank
            .addSlot(2, 80, 25) // First input slot
            .addSlot(3, 80, 43) // Second input slot
            .addSlot(4, 134, 34) // Third input slot
            .addSlot(5, 130, -14) // Upgrade slot.
            .build();

    public static final VEContainerFactory CENTRIFUGAL_AGITATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_AGITATOR_CONTAINER, CENTRIFUGAL_AGITATOR_BLOCK)
            .addSlot(0, 38, 18) // Bucket insert; input tank
            .addSlot(1, 38, 49) // Bucket extract; input tank
            .addSlot(2, 96, 18) // Bucket insert; first output tank
            .addSlot(3, 96, 49) // Bucket extract; first output tank
            .addSlot(4, 137, 18) // Bucket insert; second output tank
            .addSlot(5, 137, 49) // Bucket extract; second output tank
            .addSlot(6, 130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory CENTRIFUGAL_SEPARATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_SEPARATOR_CONTAINER, CENTRIFUGAL_SEPARATOR_BLOCK)
            .addSlot(0, 53, 24) // Primary input slot
            .addSlot(1, 53, 42) // Empty Bucket slot
            .addSlot(2, 99, 33) //Main Output
            .addSlot(3, 117, 15) //RNG #1 Slot
            .addSlot(4, 135, 33) //RNG #2 Slot
            .addSlot(5, 117, 51) //RNG #3 Slot
            .addSlot(6, 155, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory COMBUSTION_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMBUSTION_GENERATOR_CONTAINER, COMBUSTION_GENERATOR_BLOCK)
            .addSlot(0, 38, 18) // Oxidizer input slot
            .addSlot(1, 38, 49) // Extract fluid from oxidizer slot
            .addSlot(2, 138, 18) // Fuel input slot
            .addSlot(3, 138, 49) // Extract fluid from fuel output
            .build();

    public static final VEContainerFactory COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMPRESSOR_CONTAINER, COMPRESSOR_BLOCK)
            .addSlot(0, 80, 13)
            .addSlot(1, 80, 58) //Main Output
            .addSlot(2, 154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory CRUSHER_FACTORY = new VEContainerFactoryBuilder()
            .create(CRUSHER_CONTAINER, CRUSHER_BLOCK)
            .addSlot(0, 80, 13)
            .addSlot(1, 80, 58) //Main Output
            .addSlot(2, 154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory DIMENSIONAL_LASER_FACTORY = new VEContainerFactoryBuilder()
            .create(DIMENSIONAL_LASER_CONTAINER, DIMENSIONAL_LASER_BLOCK)
            .addSlot(0, 138, 18) // Bucket top slot
            .addSlot(1, 138, 49) // Bucket bottom slot
            .addSlot(2, 38, 33) // RFID chip slot
            .addSlot(3, 130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory DISTILLATION_UNIT_FACTORY = new VEContainerFactoryBuilder()
            .create(DISTILLATION_UNIT_CONTAINER, DISTILLATION_UNIT_BLOCK)
            .addSlot(0, 38, 18) // Fluid input slot
            .addSlot(1, 38, 49) // Extract fluid from input
            .addSlot(2, 96, 11) // Top slot for first output
            .addSlot(3, 96, 42) // Bottom slot for first output
            .addSlot(4, 137, 11) // Top slot for from second output
            .addSlot(5, 137, 42) // Bottom slot for second output
            .addSlot(6, 122, 64) // Item Output Slot
            .addSlot(7, 130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory ELECTRIC_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTRIC_FURNACE_CONTAINER, ELECTRIC_FURNACE_BLOCK)
            .addSlot(0, 53, 33) // Furnace Input Slot
            .addSlot(1, 116, 33) // Furnace Output Slot
            .addSlot(2, 154, -14)// Upgrade Slot
            .build();

    public static final VEContainerFactory ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTROLYZER_CONTAINER, ELECTROLYZER_BLOCK)
            .addSlot(0, 71, 13)
            .addSlot(1, 89, 13) // Empty Bucket slot
            .addSlot(2, 53, 57) //Main Output
            .addSlot(3, 71, 57) //RNG #1 Slot
            .addSlot(4, 89, 57) //RNG #2 Slot
            .addSlot(5, 107, 57) //RNG #3 Slot
            .addSlot(6, 154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory FLUID_ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_ELECTROLYZER_CONTAINER, FLUID_ELECTROLYZER_BLOCK)
            .addSlot(0, 38, 18) // Top input bucket
            .addSlot(1, 38, 49) // Bottom input bucket
            .addSlot(2, 96, 18) // Top output0 bucket
            .addSlot(3, 96, 49) // Bottom output0 bucket
            .addSlot(4, 137, 18) // Top output1 bucket
            .addSlot(5, 137, 49) // Bottom output1 bucket
            .addSlot(6, 130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory FLUID_MIXER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_MIXER_CONTAINER, FLUID_MIXER_BLOCK)
            .addSlot(0, 38, 18) // Top input0 bucket
            .addSlot(1, 38, 49) // Bottom input0 bucket
            .addSlot(2, 86, 18) // Top input1 bucket
            .addSlot(3, 86, 49) // Bottom input1 bucket
            .addSlot(4, 136, 18) // Top output0 bucket
            .addSlot(5, 136, 49) // Bottom output0 bucket
            .addSlot(6, 130, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory GAS_FIRED_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(GAS_FIRED_FURNACE_CONTAINER, GAS_FIRED_FURNACE_BLOCK)
            .addSlot(0, 8, 18) // Fluid input slot
            .addSlot(1, 8, 49) // Extract fluid from input
            .addSlot(2, 53, 33) // Item input slot
            .addSlot(3, 116, 33) // Item output slot
            .addSlot(4, 154, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory HYDROPONIC_INCUBATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(HYDROPONIC_INCUBATOR_CONTAINER, HYDROPONIC_INCUBATOR_BLOCK)
            .addSlot(0, 38, 18) // Bucket top slot
            .addSlot(1, 38, 49) // Bucket bottom slot
            .addSlot(2, 83, 34) // Primary input
            .addSlot(3, 123, 8) // Primary output
            .addSlot(4, 123, 26) // RNG0 output
            .addSlot(5, 123, 44) // RNG1 output
            .addSlot(6, 123, 62) // RNG2 output
            .addSlot(7, 154, -14) // Upgrade slot
            .build();

    public static final VEContainerFactory IMPLOSION_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(IMPLOSION_COMPRESSOR_CONTAINER, IMPLOSION_COMPRESSOR_BLOCK)
            .addSlot(0, 53, 23) // Main input
            .addSlot(1, 53, 41) // Gunpowder slot
            .addSlot(2, 116, 33) //Main Output
            .addSlot(3, 154, -14) //Upgrade slot
            .build();

    public static final VEContainerFactory PRIMITIVE_BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_BLAST_FURNACE_CONTAINER, PRIMITIVE_BLAST_FURNACE_BLOCK)
            .addSlot(0, 53, 33)
            .addSlot(1, 116, 33)
            .addSlot(2, 154, -14)
            .build();

    public static final VEContainerFactory PRIMITIVE_SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_SOLAR_PANEL_CONTAINER, PRIMITIVE_SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory PRIMITIVE_STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PRIMITIVE_STIRLING_GENERATOR_BLOCK)
            .addSlot(0, 80, 35)
            .build();

    public static final VEContainerFactory PUMP_FACTORY = new VEContainerFactoryBuilder()
            .create(PUMP_CONTAINER, PUMP_BLOCK)
            .addSlot(0, 70, 18) // Pump bucket input slot
            .addSlot(1, 70, 49) // Pump bucket output slot
            .build();

    public static final VEContainerFactory SAWMILL_FACTORY = new VEContainerFactoryBuilder()
            .create(SAWMILL_CONTAINER, SAWMILL_BLOCK)
            .addSlot(0, 44, 32) // Log input slot
            .addSlot(1, 80, 24) // Plank Output
            .addSlot(2, 80, 42) // Secondary Output
            .addSlot(3, 115, 18) // Bucket Input
            .addSlot(4, 115, 49) // Bucket Output
            .addSlot(5, 154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLAR_PANEL_CONTAINER, SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(STIRLING_GENERATOR_CONTAINER, STIRLING_GENERATOR_BLOCK)
            .addSlot(0, 80, 35)
            .build();

    // TOOLING STATION UNUSED
    public static final VEContainerFactory TOOLING_STATION_FACTORY = new VEContainerFactoryBuilder()
            .create(TOOLING_STATION_CONTAINER,TOOLING_STATION_BLOCK)
            .addSlot(0, 38, 18) // Fluid input slot
            .addSlot(1, 38, 49) // Extract fluid from input
            .addSlot(2, 86, 32) // Main Tool slot
            .addSlot(3, 134, 18) // Bit Slot
            .addSlot(4, 134,49) // Base Slot
            .addSlot(5,154, -14) // Upgrade Slot
            .build();
}
