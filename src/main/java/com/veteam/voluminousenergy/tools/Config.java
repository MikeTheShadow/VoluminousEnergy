package com.veteam.voluminousenergy.tools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static boolean aBoolean;
    public static int anInt;

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == Config.CLIENT_SPEC) {
            bakeConfig();
        }
    }

    public static void bakeConfig() {
        aBoolean = CLIENT.aBoolean.get();
        anInt = CLIENT.anInt.get();
    }

    public static class ClientConfig {

        public final ForgeConfigSpec.BooleanValue aBoolean;
        public final ForgeConfigSpec.IntValue anInt;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            aBoolean = builder
                    .comment("aBoolean usage description")
                    .translation(VoluminousEnergy.MODID + ".config." + "aBoolean")
                    .define("aBoolean", false);

            builder.push("category");
            anInt = builder
                    .comment("anInt usage description")
                    .translation(VoluminousEnergy.MODID + ".config." + "anInt")
                    .defineInRange("anInt", 10, 0, 100);
            builder.pop();
        }

    }

    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_WORLDGEN = "World Generation";
    public static final String CATEGORY_PRIMITIVE_STIRLING_GENERATOR = "Primitive Stirling Generator";
    public static final String CATEGORY_CRUSHER = "Crusher";
    public static final String CATEGORY_ELECTROLYZER = "Electrolyzer";
    public static final String CATEGORY_CENTRIFUGAL_AGITATOR = "Centrifugal Agitator";
    public static final String CATEGORY_COMPRESSOR = "Compressor";
    public static final String CATEGORY_STIRLING_GENERATOR = "Stirling Generator";
    public static final String CATEGORY_COMBUSTION_GENERATOR = "Combustion Generator";
    public static final String CATEGORY_AQUEOULIZER = "Aqueoulizer";
    public static final String CATEGORY_AIR_COMPRESSOR = "Air Compressor";
    public static final String CATEGORY_DISTILLATION_UNIT = "Distillation Unit";
    public static final String CATEGORY_PUMP = "Pump";

    public static final String SUBCATEGORY_FEATURE_GENERATION = "Feature Generation";
    public static final String SUBCATEGORY_ORE_GENERATION = "Ore Generation";
    public static final String SUBCATEGORY_SALTPETER = "Saltpeter Ore Settings";
    public static final String SUBCATEGORY_BAUXITE = "Bauxite Ore Settings";
    public static final String SUBCATEGORY_CINNABAR = "Cinnabar Ore Settings";
    public static final String SUBCATEGORY_RUTILE = "Rutile Ore Settings";
    public static final String SUBCATEGORY_GALENA = "Galena Ore Settings";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    // World Generation Settings
    public static ForgeConfigSpec.BooleanValue GENERATE_VE_BIOMES;

    // World Feature Settings
    public static ForgeConfigSpec.BooleanValue ENABLE_VE_FEATURE_GEN;
    public static ForgeConfigSpec.BooleanValue GENERATE_OIL_LAKES;
    public static ForgeConfigSpec.BooleanValue GENERATE_OIL_GEYSER;
    public static ForgeConfigSpec.IntValue OIL_LAKE_CHANCE;
    public static ForgeConfigSpec.IntValue OIL_GEYSER_CHANCE;
    // Ore Settings
    // SALTPETER ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_SALTPETER_ORE;
    public static ForgeConfigSpec.IntValue SALTPETER_COUNT;
    public static ForgeConfigSpec.IntValue SALTPETER_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue SALTPETER_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue SALTPETER_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue SALTPETER_SIZE;
    public static ForgeConfigSpec.IntValue SALTPETER_HARVEST_LEVEL;
    // BAUXITE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_BAUXITE_ORE;
    public static ForgeConfigSpec.IntValue BAUXITE_COUNT;
    public static ForgeConfigSpec.IntValue BAUXITE_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue BAUXITE_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue BAUXITE_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue BAUXITE_SIZE;
    public static ForgeConfigSpec.IntValue BAUXITE_HARVEST_LEVEL;
    // CINNABAR ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_CINNABAR_ORE;
    public static ForgeConfigSpec.IntValue CINNABAR_COUNT;
    public static ForgeConfigSpec.IntValue CINNABAR_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue CINNABAR_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue CINNABAR_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue CINNABAR_SIZE;
    public static ForgeConfigSpec.IntValue CINNABAR_HARVEST_LEVEL;
    // RUTILE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_RUTILE_ORE;
    public static ForgeConfigSpec.IntValue RUTILE_COUNT;
    public static ForgeConfigSpec.IntValue RUTILE_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue RUTILE_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue RUTILE_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue RUTILE_SIZE;
    public static ForgeConfigSpec.IntValue RUTILE_HARVEST_LEVEL;
    // GALENA ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_GALENA_ORE;
    public static ForgeConfigSpec.IntValue GALENA_COUNT;
    public static ForgeConfigSpec.IntValue GALENA_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue GALENA_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue GALENA_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue GALENA_SIZE;
    public static ForgeConfigSpec.IntValue GALENA_HARVEST_LEVEL;
    public static ForgeConfigSpec.IntValue GALENA_GLOW;

    // Primitive Stirling Generator Variables
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_SEND;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_HARVEST_LEVEL;

    // Crusher Variables
    public static ForgeConfigSpec.IntValue CRUSHER_MAX_POWER;
    public static ForgeConfigSpec.IntValue CRUSHER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CRUSHER_TRANSFER;
    public static ForgeConfigSpec.IntValue CRUSHER_HARVEST_LEVEL;

    // Electrolyzer Variables
    public static ForgeConfigSpec.IntValue ELECTROLYZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue ELECTROLYZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue ELECTROLYZER_TRANSFER;
    public static ForgeConfigSpec.IntValue ELECTROLYZER_HARVEST_LEVEL;

    // Centrifugal Agitator Variables
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_TRANSFER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_HARVEST_LEVEL;

    // Compressor Variables
    public static ForgeConfigSpec.IntValue COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue COMPRESSOR_TRANSFER;
    public static ForgeConfigSpec.IntValue COMPRESSOR_HARVEST_LEVEL;

    // Stirling Generator Variables
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_SEND;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_HARVEST_LEVEL;

    // Combustion Generator Variables
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_SEND;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_HARVEST_LEVEL;
    public static ForgeConfigSpec.BooleanValue COMBUSTION_GENERATOR_BALANCED_MODE;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_FIXED_TICK_TIME;

    // Aqueoulizer Variables
    public static ForgeConfigSpec.IntValue AQUEOULIZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_TRANSFER;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_HARVEST_LEVEL;

    // Air Compressor Variables
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_TRANSFER;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_HARVEST_LEVEL;

    // Distillation Unit Variables
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_MAX_POWER;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_POWER_USAGE;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_TRANSFER;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_HARVEST_LEVEL;

    // Pump Variables
    public static ForgeConfigSpec.IntValue PUMP_MAX_POWER;
    public static ForgeConfigSpec.IntValue PUMP_POWER_USAGE;
    public static ForgeConfigSpec.IntValue PUMP_TRANSFER;
    public static ForgeConfigSpec.IntValue PUMP_HARVEST_LEVEL;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();

        // World Feature Settings
        COMMON_BUILDER.comment("World Generation Settings").push(CATEGORY_WORLDGEN);
        setupWorldGen();
        COMMON_BUILDER.pop();

        // Primitive Stirling Generator
        COMMON_BUILDER.comment("Primitive Stirling Generator Settings").push(CATEGORY_PRIMITIVE_STIRLING_GENERATOR);
        setupPrimitiveStirlingGeneratorConfig();
        COMMON_BUILDER.pop();

        // Crusher
        COMMON_BUILDER.comment("Crusher Settings").push(CATEGORY_CRUSHER);
        setupCrusher();
        COMMON_BUILDER.pop();

        // Electrolyzer
        COMMON_BUILDER.comment("Electrolyzer Settings").push(CATEGORY_ELECTROLYZER);
        setupElectrolyzer();
        COMMON_BUILDER.pop();

        // Centrifugal Agitator
        COMMON_BUILDER.comment("Centrifugal Agitator Settings").push(CATEGORY_CENTRIFUGAL_AGITATOR);
        setupCentrifugalAgitator();
        COMMON_BUILDER.pop();

        // Compressor
        COMMON_BUILDER.comment("Compressor Settings").push(CATEGORY_COMPRESSOR);
        setupCompressor();
        COMMON_BUILDER.pop();

        // Stirling Generator
        COMMON_BUILDER.comment("Stirling Generator Settings").push(CATEGORY_STIRLING_GENERATOR);
        setupStirlingGenerator();
        COMMON_BUILDER.pop();

        // Combustion Generator
        COMMON_BUILDER.comment("Combustion Generator Settings").push(CATEGORY_COMBUSTION_GENERATOR);
        setupCombustionGenerator();
        COMMON_BUILDER.pop();

        // Aqueoulizer
        COMMON_BUILDER.comment("Aqueoulizer Settings").push(CATEGORY_AQUEOULIZER);
        setupAqueoulizer();
        COMMON_BUILDER.pop();

        // Air Compressor
        COMMON_BUILDER.comment("Air Compressor Settings").push(CATEGORY_AIR_COMPRESSOR);
        setupAirCompressor();
        COMMON_BUILDER.pop();

        // Distillation Unit
        COMMON_BUILDER.comment("Distillation Unit Settings").push(CATEGORY_DISTILLATION_UNIT);
        setupDistillationUnit();
        COMMON_BUILDER.pop();

        // Pump
        COMMON_BUILDER.comment("Pump Settings").push(CATEGORY_PUMP);
        setupPump();
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void setupWorldGen(){
        GENERATE_VE_BIOMES = COMMON_BUILDER.comment("Enable/Disable Voluminous Energy biomes")
                .define("Enable VE Biomes", true);

        ENABLE_VE_FEATURE_GEN = COMMON_BUILDER.comment("Enable/Disable all Voluminous Energy changes to feature generation")
                .define("World Generation", true);

        COMMON_BUILDER.comment("Feature Generation").push(SUBCATEGORY_FEATURE_GENERATION);
            GENERATE_OIL_LAKES = COMMON_BUILDER.comment("Enable/Disable Oil Lakes")
                    .define("Oil Lakes", true);
            OIL_LAKE_CHANCE = COMMON_BUILDER.comment("Oil Lake Chance (Lower = Higher chance)")
                    .defineInRange("Oil Lake Chance", 80, 10, Integer.MAX_VALUE);
            GENERATE_OIL_GEYSER = COMMON_BUILDER.comment("Enable/Disable Oil Geysers")
                    .define("Oil Geysers", true);
            OIL_GEYSER_CHANCE = COMMON_BUILDER.comment("Oil Geyser Chance (Lower = Higher chance)")
                    .defineInRange("Oil Geyser Chance", 2520, 10, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Ore Generation").push(SUBCATEGORY_ORE_GENERATION);
            //Saltpeter
            COMMON_BUILDER.comment("Saltpeter Ore Settings").push(SUBCATEGORY_SALTPETER);
            ENABLE_SALTPETER_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Saltpeter Ore")
                    .define("Enable Saltpeter Ore", true);
            SALTPETER_COUNT = COMMON_BUILDER.defineInRange("Saltpeter Weight",4,1, Integer.MAX_VALUE);
            SALTPETER_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Saltpeter Bottom Offset", 55, 1, 256);
            SALTPETER_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Saltpeter Height Offset", 0, 0, 256);
            SALTPETER_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Saltpeter Maximum Height", 256, 0, 256);
            SALTPETER_SIZE = COMMON_BUILDER.defineInRange("Saltpeter Size", 33, 0, Integer.MAX_VALUE);
            SALTPETER_HARVEST_LEVEL = COMMON_BUILDER.defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Bauxite
            COMMON_BUILDER.comment("Bauxite Ore Settings").push(SUBCATEGORY_BAUXITE);
            ENABLE_BAUXITE_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Bauxite Ore")
                .define("Enable Bauxite Ore", true);
            BAUXITE_COUNT = COMMON_BUILDER.defineInRange("Bauxite Weight",16,1, Integer.MAX_VALUE);
            BAUXITE_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Bauxite Bottom Offset", 10, 1, 256);
            BAUXITE_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Bauxite Height Offset", 0, 0, 256);
            BAUXITE_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Bauxite Maximum Height", 60, 0, 256);
            BAUXITE_SIZE = COMMON_BUILDER.defineInRange("Bauxite Size", 8, 0, Integer.MAX_VALUE);
            BAUXITE_HARVEST_LEVEL = COMMON_BUILDER.defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Cinnabar
            COMMON_BUILDER.comment("Cinnabar Ore Settings").push(SUBCATEGORY_CINNABAR);
            ENABLE_CINNABAR_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Cinnabar Ore")
                .define("Enable Cinnabar Ore", true);
            CINNABAR_COUNT = COMMON_BUILDER.defineInRange("Cinnabar Weight",9,1, Integer.MAX_VALUE);
            CINNABAR_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Cinnabar Bottom Offset", 1, 1, 256);
            CINNABAR_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Cinnabar Height Offset", 0, 0, 256);
            CINNABAR_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Cinnabar Maximum Height", 256, 0, 256);
            CINNABAR_SIZE = COMMON_BUILDER.defineInRange("Cinnabar Size", 6, 0, Integer.MAX_VALUE);
            CINNABAR_HARVEST_LEVEL = COMMON_BUILDER.defineInRange("Harvest Level", 2, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Rutile
            COMMON_BUILDER.comment("Rutile Ore Settings").push(SUBCATEGORY_RUTILE);
            ENABLE_RUTILE_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Rutile Ore")
                .define("Enable Rutile Ore", true);
            RUTILE_COUNT = COMMON_BUILDER.defineInRange("Rutile Weight",3,1, Integer.MAX_VALUE);
            RUTILE_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Rutile Bottom Offset", 1, 1, 256);
            RUTILE_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Rutile Height Offset", 0, 0, 256);
            RUTILE_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Rutile Maximum Height", 16, 0, 256);
            RUTILE_SIZE = COMMON_BUILDER.defineInRange("Rutile Size", 4, 0, Integer.MAX_VALUE);
            RUTILE_HARVEST_LEVEL = COMMON_BUILDER.defineInRange("Harvest Level", 3, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Galena
            COMMON_BUILDER.comment("Galena Ore Settings").push(SUBCATEGORY_GALENA);
            ENABLE_GALENA_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Galena Ore")
                .define("Enable Galena Ore", true);
            GALENA_COUNT = COMMON_BUILDER.defineInRange("Galena Weight",3,1, Integer.MAX_VALUE);
            GALENA_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Galena Bottom Offset", 12, 1, 256);
            GALENA_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Galena Height Offset", 0, 0, 256);
            GALENA_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Galena Maximum Height", 32, 0, 256);
            GALENA_SIZE = COMMON_BUILDER.defineInRange("Galena Size", 6, 0, Integer.MAX_VALUE);
            GALENA_HARVEST_LEVEL = COMMON_BUILDER.defineInRange("Harvest Level", 2, 0, Integer.MAX_VALUE);
            GALENA_GLOW = COMMON_BUILDER.defineInRange("Galena Light Glow Level", 4, 0, 16);
            COMMON_BUILDER.pop();
        COMMON_BUILDER.pop();
    }

    private static void setupPrimitiveStirlingGeneratorConfig(){
        PRIMITIVE_STIRLING_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum power for the Primitive Stirling Generator to store")
                .defineInRange("Maximum Power",100000, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_GENERATE = COMMON_BUILDER.comment("Power generation per tick")
                .defineInRange("Generation (Per Tick)",40, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("Output Rate",1000, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupCrusher(){
        CRUSHER_MAX_POWER = COMMON_BUILDER.comment("Maximum power for the Crusher to store")
                .defineInRange("Maximum Power",5000,0,Integer.MAX_VALUE);
        CRUSHER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Crusher")
                .defineInRange("Power Consumption",40,0,Integer.MAX_VALUE);
        CRUSHER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Crusher")
                .defineInRange("Maximum Transfer",1000,0,Integer.MAX_VALUE);
        CRUSHER_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupElectrolyzer(){
        ELECTROLYZER_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Electrolyzer to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        ELECTROLYZER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Electrolyzer")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        ELECTROLYZER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Electrolyzer")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        ELECTROLYZER_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupCentrifugalAgitator(){
        CENTRIFUGAL_AGITATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Centrifugal Agitator to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_AGITATOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Centrifugal Agitator")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_AGITATOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Centrifugal Agitator")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_AGITATOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupCompressor(){
        COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Compressor to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Compressor")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Compressor")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        COMPRESSOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupStirlingGenerator(){
        STIRLING_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Stirling Generator to store")
                .defineInRange("Maximum Power", 5120000, 0, Integer.MAX_VALUE);
        STIRLING_GENERATOR_GENERATE = COMMON_BUILDER.comment("Fallback value for power generation for the Stirling Generator")
                .defineInRange("Fallback Generation Rate", 128, 0, Integer.MAX_VALUE);
        STIRLING_GENERATOR_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Stirling Generator")
                .defineInRange("Maximum Transfer", 128000, 0, Integer.MAX_VALUE);
        STIRLING_GENERATOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupCombustionGenerator(){
        COMBUSTION_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Combustion Generator to store")
                .defineInRange("Maximum Power", 5120000, 0, Integer.MAX_VALUE);
        COMBUSTION_GENERATOR_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Combustion Generator")
                .defineInRange("Maximum Transfer", 512000, 0, Integer.MAX_VALUE);
        COMBUSTION_GENERATOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
        COMBUSTION_GENERATOR_BALANCED_MODE = COMMON_BUILDER.comment("If true, the Combustion Generator will use the process time from the oxidizer's recipe rather than the static process time")
                .define("Balanced Mode", false);
        COMBUSTION_GENERATOR_FIXED_TICK_TIME = COMMON_BUILDER.comment("The fixed process time for the Combustion Generator is Balanced Mode is not used.")
                .defineInRange("Fixed Process Time", 1600, 0, Integer.MAX_VALUE);
    }

    private static void setupAqueoulizer(){
        AQUEOULIZER_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Aqueoulizer to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        AQUEOULIZER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Aqueoulizer")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        AQUEOULIZER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Aqueoulizer")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        AQUEOULIZER_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupAirCompressor(){
        AIR_COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Air Compressor to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        AIR_COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Air Compressor")
                .defineInRange("Power Consumption", 24, 0, Integer.MAX_VALUE);
        AIR_COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Air Compressor")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        AIR_COMPRESSOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupDistillationUnit(){
        DISTILLATION_UNIT_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Distillation Unit to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        DISTILLATION_UNIT_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Distillation Unit")
                .defineInRange("Power Consumption", 64, 0, Integer.MAX_VALUE);
        DISTILLATION_UNIT_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Distillation Unit")
                .defineInRange("Maximum Transfer", 2500, 0, Integer.MAX_VALUE);
        DISTILLATION_UNIT_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupPump(){
        PUMP_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Distillation Unit to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        PUMP_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Distillation Unit")
                .defineInRange("Power Consumption", 16, 0, Integer.MAX_VALUE);
        PUMP_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Distillation Unit")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        PUMP_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path){
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent){

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent){

    }

}
