package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory.VEContainerFactoryBuilder;
import com.veteam.voluminousenergy.blocks.tiles.VETileFactory.*;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.*;
import static net.minecraft.core.Direction.*;

public class VEContainers {

    public static final VEContainerFactory AIR_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(AIR_COMPRESSOR_CONTAINER, AIR_COMPRESSOR_BLOCK)
            .addSlot(70, 18,new BucketInputSlot(UP,0)) // Air Compressor bucket input tilePos
            .addSlot(70, 49,new BucketOutputSlot(DOWN)) // Air Compressor bucket output tilePos
            .addSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory AQUEOULIZER_FACTORY = new VEContainerFactoryBuilder()
            .create(AQUEOULIZER_CONTAINER, AQUEOULIZER_BLOCK)
            .addSlot(38, 18, new BucketInputSlot(UP,0))
            .addSlot(38, 49, new BucketOutputSlot(DOWN))
            .addSlot(137, 18, new BucketInputSlot(NORTH,1))
            .addSlot(137, 49, new BucketOutputSlot(SOUTH))
            .addSlot(96, 32, new ItemInputSlot(EAST))
            .addUpgradeSlot(130, -14)
            .build();

    public static final VEContainerFactory BATTERY_BOX_FACTORY = new VEContainerFactoryBuilder()
            .create(BATTERY_BOX_CONTAINER, BATTERY_BOX_BLOCK)
            .addSlot(35, 17,new InputSlot(UP))
            .addSlot(53, 17,new InputSlot(UP))
            .addSlot(71, 17,new InputSlot(UP))
            .addSlot(89, 17,new InputSlot(UP))
            .addSlot(107, 17,new InputSlot(UP))
            .addSlot(125, 17,new InputSlot(UP))
            //Bottom Slots
            .addSlot(35, 54,new InputSlot(DOWN))
            .addSlot(53, 54,new InputSlot(DOWN))
            .addSlot(71, 54,new InputSlot(DOWN))
            .addSlot(89, 54,new InputSlot(DOWN))
            .addSlot(107, 54,new InputSlot(DOWN))
            .addSlot(125, 54,new InputSlot(DOWN))
            .build();

    public static final VEContainerFactory BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(BLAST_FURNACE_CONTAINER, BLAST_FURNACE_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Fluid input tilePos
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from heat tank
            .addSlot(80, 25, new InputSlot(EAST)) // First input tilePos
            .addSlot(80, 43, new InputSlot(WEST)) // Second input tilePos
            .addSlot(134, 34, new ItemOutputSlot(NORTH)) // Third input tilePos
            .addUpgradeSlot(130, -14) // Upgrade tilePos.
            .build();

    public static final VEContainerFactory CENTRIFUGAL_AGITATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_AGITATOR_CONTAINER, CENTRIFUGAL_AGITATOR_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Bucket insert; input tank
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Bucket extract; input tank
            .addSlot(96, 18, new BucketInputSlot(NORTH,1)) // Bucket insert; first output tank
            .addSlot(96, 49, new BucketOutputSlot(SOUTH)) // Bucket extract; first output tank
            .addSlot(137, 18, new BucketInputSlot(EAST,2)) // Bucket insert; second output tank
            .addSlot(137, 49, new BucketOutputSlot(WEST)) // Bucket extract; second output tank
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory CENTRIFUGAL_SEPARATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(CENTRIFUGAL_SEPARATOR_CONTAINER, CENTRIFUGAL_SEPARATOR_BLOCK)
            .addSlot(53, 24,new ItemInputSlot(UP)) // Primary input tilePos
            .addSlot(53, 42,new ItemInputSlot(WEST)) // Empty Bucket tilePos
            .addSlot(99, 33, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(117, 15, new ItemOutputSlot(NORTH)) //RNG #1 Slot
            .addSlot(135, 33, new ItemOutputSlot(SOUTH)) //RNG #2 Slot
            .addSlot(117, 51, new ItemOutputSlot(EAST)) //RNG #3 Slot
            .addUpgradeSlot(155, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory COMBUSTION_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMBUSTION_GENERATOR_CONTAINER, COMBUSTION_GENERATOR_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Oxidizer input tilePos
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Extract fluid from oxidizer tilePos
            .addSlot(138, 18,new BucketInputSlot(NORTH,1)) // Fuel input tilePos
            .addSlot(138, 49,new BucketOutputSlot(SOUTH)) // Extract fluid from fuel output
            .build();

    public static final VEContainerFactory COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(COMPRESSOR_CONTAINER, COMPRESSOR_BLOCK)
            .addSlot(80, 13,new ItemInputSlot(UP))
            .addSlot(80, 58,new ItemOutputSlot(DOWN)) //Main Output
            .addUpgradeSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory CRUSHER_FACTORY = new VEContainerFactoryBuilder()
            .create(CRUSHER_CONTAINER, CRUSHER_BLOCK)
            .addSlot(80, 13,new ItemInputSlot(UP)) // Input Slot
            .addSlot(71, 58, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(89,58,new ItemOutputSlot(NORTH)) // RNG slot
            .addUpgradeSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory DIMENSIONAL_LASER_FACTORY = new VEContainerFactoryBuilder()
            .create(DIMENSIONAL_LASER_CONTAINER, DIMENSIONAL_LASER_BLOCK)
            .addSlot(138, 18,new BucketInputSlot(UP,0)) // Bucket top tilePos
            .addSlot(138, 49, new BucketOutputSlot(DOWN)) // Bucket bottom tilePos
            .addSlot(38, 33, new ItemInputSlot(NORTH)) // RFID chip tilePos
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory DISTILLATION_UNIT_FACTORY = new VEContainerFactoryBuilder()
            .create(DISTILLATION_UNIT_CONTAINER, DISTILLATION_UNIT_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Fluid input tilePos
            .addSlot(38, 49, new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(96, 11, new BucketInputSlot(UP,1)) // Top tilePos for first output
            .addSlot(96, 42, new BucketOutputSlot(DOWN)) // Bottom tilePos for first output
            .addSlot(137, 11,new BucketInputSlot(UP,2)) // Top tilePos for from second output
            .addSlot(137, 42, new BucketOutputSlot(DOWN)) // Bottom tilePos for second output
            .addSlot(122, 64,new ItemOutputSlot(DOWN)) // Item Output Slot
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory ELECTRIC_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTRIC_FURNACE_CONTAINER, ELECTRIC_FURNACE_BLOCK)
            .addSlot(53, 33,new ItemInputSlot(UP)) // Furnace Input Slot
            .addSlot(116, 33, new ItemOutputSlot(DOWN)) // Furnace Output Slot
            .addUpgradeSlot(154, -14)// Upgrade Slot
            .build();

    public static final VEContainerFactory ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(ELECTROLYZER_CONTAINER, ELECTROLYZER_BLOCK)
            .addSlot(71, 13,new ItemInputSlot(UP))
            .addSlot(89, 13,new ItemInputSlot(WEST)) // Empty Bucket tilePos
            .addSlot(53, 57, new ItemOutputSlot(DOWN)) //Main Output
            .addSlot(71, 57, new ItemOutputSlot(NORTH)) //RNG #1 Slot
            .addSlot(89, 57, new ItemOutputSlot(SOUTH)) //RNG #2 Slot
            .addSlot(107, 57, new ItemOutputSlot(EAST)) //RNG #3 Slot
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory FLUID_ELECTROLYZER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_ELECTROLYZER_CONTAINER, FLUID_ELECTROLYZER_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Top input bucket
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Bottom input bucket
            .addSlot(96, 18,new BucketInputSlot(EAST,1)) // Top output0 bucket
            .addSlot(96, 49,new BucketOutputSlot(WEST)) // Bottom output0 bucket
            .addSlot(137, 18,new BucketInputSlot(NORTH,2)) // Top output1 bucket
            .addSlot(137, 49,new BucketOutputSlot(SOUTH)) // Bottom output1 bucket
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory FLUID_MIXER_FACTORY = new VEContainerFactoryBuilder()
            .create(FLUID_MIXER_CONTAINER, FLUID_MIXER_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Top input0 bucket
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Bottom input0 bucket
            .addSlot(86, 18,new BucketInputSlot(EAST,1)) // Top input1 bucket
            .addSlot(86, 49,new BucketOutputSlot(WEST)) // Bottom input1 bucket
            .addSlot(136, 18,new BucketInputSlot(NORTH,2)) // Top output0 bucket
            .addSlot(136, 49,new BucketOutputSlot(SOUTH)) // Bottom output0 bucket
            .addUpgradeSlot(130, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory GAS_FIRED_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(GAS_FIRED_FURNACE_CONTAINER, GAS_FIRED_FURNACE_BLOCK)
            .addSlot(8, 18,new BucketInputSlot(UP,0)) // Fluid input tilePos
            .addSlot(8, 49,new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(53, 33,new ItemInputSlot(EAST)) // Item input tilePos
            .addSlot(116, 33,new ItemOutputSlot(WEST)) // Item output tilePos
            .addUpgradeSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory HYDROPONIC_INCUBATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(HYDROPONIC_INCUBATOR_CONTAINER, HYDROPONIC_INCUBATOR_BLOCK)
            .addSlot(38, 18, new BucketInputSlot(UP,0)) // Bucket top tilePos
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Bucket bottom tilePos
            .addSlot(83, 34, new ItemInputSlot(NORTH)) // Primary input
            .addSlot(123, 8, new ItemOutputSlot(NORTH)) // Primary output
            .addSlot(123, 26, new ItemOutputSlot(NORTH)) // RNG0 output
            .addSlot(123, 44, new ItemOutputSlot(NORTH)) // RNG1 output
            .addSlot(123, 62, new ItemOutputSlot(NORTH)) // RNG2 output
            .addUpgradeSlot(154, -14) // Upgrade tilePos
            .build();

    public static final VEContainerFactory IMPLOSION_COMPRESSOR_FACTORY = new VEContainerFactoryBuilder()
            .create(IMPLOSION_COMPRESSOR_CONTAINER, IMPLOSION_COMPRESSOR_BLOCK)
            .addSlot(53, 23,new ItemInputSlot(UP)) // Main input
            .addSlot(53, 41, new ItemInputSlot(EAST)) // Gunpowder tilePos
            .addSlot(116, 33, new ItemOutputSlot(DOWN)) //Main Output
            .addUpgradeSlot(154, -14) //Upgrade tilePos
            .build();

    public static final VEContainerFactory PRIMITIVE_BLAST_FURNACE_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_BLAST_FURNACE_CONTAINER, PRIMITIVE_BLAST_FURNACE_BLOCK)
            .addSlot(53, 33,new ItemInputSlot(UP))
            .addSlot(116, 33, new ItemOutputSlot(DOWN))
            .addUpgradeSlot(154, -14)
            .build();

    public static final VEContainerFactory PRIMITIVE_SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_SOLAR_PANEL_CONTAINER, PRIMITIVE_SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory PRIMITIVE_STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(PRIMITIVE_STIRLING_GENERATOR_CONTAINER, PRIMITIVE_STIRLING_GENERATOR_BLOCK)
            .addSlot(80, 35, new ItemInputSlot(UP))
            .build();

    public static final VEContainerFactory PUMP_FACTORY = new VEContainerFactoryBuilder()
            .create(PUMP_CONTAINER, PUMP_BLOCK)
            .addSlot(70, 18,new BucketInputSlot(UP, 0)) // Pump bucket input tilePos
            .addSlot(70, 49, new BucketOutputSlot(DOWN)) // Pump bucket output tilePos
            .build();

    public static final VEContainerFactory SAWMILL_FACTORY = new VEContainerFactoryBuilder()
            .create(SAWMILL_CONTAINER, SAWMILL_BLOCK)
            .addSlot(44, 32,new ItemInputSlot(UP)) // Log input tilePos
            .addSlot(80, 24,new ItemOutputSlot(DOWN)) // Plank Output
            .addSlot(80, 42, new ItemOutputSlot(NORTH)) // Secondary Output
            .addSlot(115, 18, new BucketInputSlot(SOUTH,0)) // Bucket Input
            .addSlot(115, 49, new BucketOutputSlot(EAST)) // Bucket Output
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();

    public static final VEContainerFactory SOLAR_PANEL_FACTORY = new VEContainerFactoryBuilder()
            .create(SOLAR_PANEL_CONTAINER, SOLAR_PANEL_BLOCK)
            .build();

    public static final VEContainerFactory STIRLING_GENERATOR_FACTORY = new VEContainerFactoryBuilder()
            .create(STIRLING_GENERATOR_CONTAINER, STIRLING_GENERATOR_BLOCK)
            .addSlot(80, 35,new ItemInputSlot(UP))
            .build();

    // TOOLING STATION UNUSED
    public static final VEContainerFactory TOOLING_STATION_FACTORY = new VEContainerFactoryBuilder()
            .create(TOOLING_STATION_CONTAINER, TOOLING_STATION_BLOCK)
            .addSlot(38, 18,new BucketInputSlot(UP,0)) // Fluid input tilePos
            .addSlot(38, 49,new BucketOutputSlot(DOWN)) // Extract fluid from input
            .addSlot(86, 32, new ItemInputSlot(NORTH)) // Main Tool tilePos
            .addSlot(134, 18, new ItemInputSlot(SOUTH)) // Bit Slot
            .addSlot(134, 49, new ItemInputSlot(EAST)) // Base Slot
            .addUpgradeSlot(154, -14) // Upgrade Slot
            .build();
}
