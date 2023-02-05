package com.veteam.voluminousenergy.tools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.nio.file.Path;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    // COMMON Config variables and categories
    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_FOOD = "Food";
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
    public static final String CATEGORY_GAS_FIRED_FURNACE = "Gas Furnace";
    public static final String CATEGORY_ELECTRIC_FURNACE = "Electric Furnace";
    public static final String CATEGORY_BATTERY_BOX = "Battery Box";
    public static final String CATEGORY_PRIMITIVE_SOLAR_PANEL = "Primitive Solar Panel";
    public static final String CATEGORY_SOLAR_PANEL = "Solar Panel";
    public static final String CATEGORY_CENTRIFUGAL_SEPARATOR = "Centrifugal Separator";
    public static final String CATEGORY_IMPLOSION_COMPRESSOR = "Implosion Compressor";
    public static final String CATEGORY_BLAST_FURNACE = "Blast Furnace";
    public static final String CATEGORY_SAWMILL = "Sawmill";
    public static final String CATEGORY_TANK_BLOCKS = "Tank Blocks";
    public static final String CATEGORY_TOOLING_STATION = "Tooling Station";
    public static final String CATEGORY_FLUID_ELECTROLYZER = "Fluid Electrolyzer";
    public static final String CATEGORY_FLUID_MIXER = "Fluid Mixer";
    public static final String CATEGORY_HYDROPONIC_INCUBATOR = "Hydroponic Incubator";

    public static final String SUBCATEGORY_FEATURE_GENERATION = "Feature Generation";
    public static final String SUBCATEGORY_CLIMATE_SPAWNS = "Climate Spawns";
    public static final String SUBCATEGORY_ORE_GENERATION = "Ore Generation";
    public static final String SUBCATEGORY_ORE_DEPOSIT_GENERATION = "Ore Deposit Generation";
    public static final String SUBCATEGORY_SALTPETER = "Saltpeter Ore Settings";
    public static final String SUBCATEGORY_BAUXITE = "Bauxite Ore Settings";
    public static final String SUBCATEGORY_CINNABAR = "Cinnabar Ore Settings";
    public static final String SUBCATEGORY_RUTILE = "Rutile Ore Settings";
    public static final String SUBCATEGORY_GALENA = "Galena Ore Settings";
    public static final String SUBCATEGORY_EIGHZO = "Eighzo Ore Settings";

    // World Feature Settings
    public static ForgeConfigSpec.BooleanValue WORLD_GEN_LOGGING;

    // Climate Spawn Settings
    public static ForgeConfigSpec.BooleanValue PUNCH_HOLES_IN_CLIMATE_SPAWNS;
    public static ForgeConfigSpec.IntValue CLIMATE_SPAWNS_HOLE_PUNCH_MULTIPLIER;
    public static ForgeConfigSpec.IntValue CLIMATE_SPAWNS_HOLE_PUNCH_BOUNDING;
    public static ForgeConfigSpec.IntValue CLIMATE_SPAWNS_HOLE_PUNCH_RNG_MUST_BE_LARGER;

    // Food Settings
    public static ForgeConfigSpec.IntValue COOKED_RICE_NUTRITION;
    public static ForgeConfigSpec.DoubleValue COOKED_RICE_SATURATION;
    public static ForgeConfigSpec.DoubleValue RICE_TICK_CHANCE;

    // Ore Blob Settings
    // SALTPETER ORE
    public static ForgeConfigSpec.IntValue SALTPETER_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.DoubleValue SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;

    // BAUXITE ORE
    public static ForgeConfigSpec.IntValue BAUXITE_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.DoubleValue BAUXITE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;


    // CINNABAR ORE
    public static ForgeConfigSpec.IntValue CINNABAR_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.DoubleValue CINNABAR_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;


    // RUTILE ORE
    public static ForgeConfigSpec.IntValue RUTILE_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.DoubleValue RUTILE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;

    // GALENA ORE
    public static ForgeConfigSpec.IntValue GALENA_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.IntValue GALENA_ORE_BLOCK_LIGHT_LEVEL;
    public static ForgeConfigSpec.DoubleValue GALENA_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;

    // EIGHZO ORE
    public static ForgeConfigSpec.IntValue EIGHZO_ORE_BLOBS_SIZE;
    public static ForgeConfigSpec.DoubleValue EIGHZO_ORE_BLOBS_EXPOSED_DISCARD_CHANCE;

    // Ore Deposit
    public static ForgeConfigSpec.BooleanValue PREVENT_SURFACE_ORE_DEPOSITS;

    // General Settings
    public static ForgeConfigSpec.BooleanValue ALLOW_EXTRACTION_FROM_INPUT_TANKS;
    public static ForgeConfigSpec.DoubleValue ACID_DAMAGE;
    public static ForgeConfigSpec.IntValue ACID_FIRE_DURATION;
    public static ForgeConfigSpec.IntValue SOLARIUM_PROTECTIVE_SHEATH_HITS;
    public static ForgeConfigSpec.IntValue DECREMENT_SPEED_ON_NO_POWER;

    // Primitive Stirling Generator Variables
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_SEND;

    // Crusher Variables
    public static ForgeConfigSpec.IntValue CRUSHER_MAX_POWER;
    public static ForgeConfigSpec.IntValue CRUSHER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CRUSHER_TRANSFER;

    // Electrolyzer Variables
    public static ForgeConfigSpec.IntValue ELECTROLYZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue ELECTROLYZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue ELECTROLYZER_TRANSFER;

    // Centrifugal Agitator Variables
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_TRANSFER;

    // Compressor Variables
    public static ForgeConfigSpec.IntValue COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue COMPRESSOR_TRANSFER;

    // Stirling Generator Variables
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_SEND;

    // Combustion Generator Variables
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_SEND;
    public static ForgeConfigSpec.BooleanValue COMBUSTION_GENERATOR_BALANCED_MODE;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_FIXED_TICK_TIME;

    // Aqueoulizer Variables
    public static ForgeConfigSpec.IntValue AQUEOULIZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_TRANSFER;

    // Air Compressor Variables
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_TRANSFER;

    // Distillation Unit Variables
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_MAX_POWER;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_POWER_USAGE;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_TRANSFER;

    // Pump Variables
    public static ForgeConfigSpec.IntValue PUMP_MAX_POWER;
    public static ForgeConfigSpec.IntValue PUMP_POWER_USAGE;
    public static ForgeConfigSpec.IntValue PUMP_TRANSFER;

    // Electric Furnace Variables
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_MAX_POWER;
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_POWER_USAGE;
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_TRANSFER;

    // Battery Box Variables
    public static ForgeConfigSpec.IntValue BATTERY_BOX_MAX_POWER;
    public static ForgeConfigSpec.IntValue BATTERY_BOX_TRANSFER;

    // Primitive Solar Panel Variables
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_SEND;

    // Solar Panel Variables
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_MAX_POWER;
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_GENERATE;
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_SEND;

    // Centrifugal Separator Variables
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_TRANSFER;

    // Implosion Compressor Variables
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_TRANSFER;

    // Blast Furnace Variables
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_MAX_POWER;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_POWER_USAGE;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_TRANSFER;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION;

    // Sawmill Variables
    public static ForgeConfigSpec.IntValue SAWMILL_MAX_POWER;
    public static ForgeConfigSpec.IntValue SAWMILL_POWER_USAGE;
    public static ForgeConfigSpec.IntValue SAWMILL_TRANSFER;
    public static ForgeConfigSpec.BooleanValue SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED;
    public static ForgeConfigSpec.ConfigValue<String> SAWMILL_FLUID_LOCATION;
    public static ForgeConfigSpec.ConfigValue<String> SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION;
    public static ForgeConfigSpec.IntValue SAWMILL_FLUID_AMOUNT;
    public static ForgeConfigSpec.IntValue SAWMILL_SECOND_OUTPUT_COUNT;
    public static ForgeConfigSpec.IntValue SAWMILL_PROCESSING_TIME;
    public static ForgeConfigSpec.IntValue SAWMILL_PRIMARY_OUTPUT_COUNT;
    public static ForgeConfigSpec.IntValue SAWMILL_LOG_CONSUMPTION_RATE;

    // Tooling Station Variables
    public static ForgeConfigSpec.IntValue TOOLING_STATION_MAX_POWER;
    public static ForgeConfigSpec.IntValue TOOLING_STATION_TRANSFER;

    // Fluid Electrolyzer Variables
    public static ForgeConfigSpec.IntValue FLUID_ELECTROLYZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue FLUID_ELECTROLYZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue FLUID_ELECTROLYZER_TRANSFER;

    // Fluid Mixer Variables
    public static ForgeConfigSpec.IntValue FLUID_MIXER_MAX_POWER;
    public static ForgeConfigSpec.IntValue FLUID_MIXER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue FLUID_MIXER_TRANSFER;

    // Hydroponic Incubator Variables
    public static ForgeConfigSpec.IntValue HYDROPONIC_INCUBATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue HYDROPONIC_INCUBATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue HYDROPONIC_INCUBATOR_TRANSFER;

    // Tank variables
    public static ForgeConfigSpec.IntValue SOLARIUM_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue EIGHZO_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue NIGHALITE_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue NETHERITE_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue TITANIUM_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue ALUMINUM_TANK_CAPACITY;


    // CLIENT CONFIG Variables
    public static ForgeConfigSpec.BooleanValue USE_BIOME_WATER_COLOUR;
    public static ForgeConfigSpec.BooleanValue SHORTEN_ITEM_TOOLTIP_VALUES;
    public static ForgeConfigSpec.BooleanValue SHORTEN_POWER_BAR_VALUES;
    public static ForgeConfigSpec.BooleanValue SHORTEN_TANK_GUI_VALUES;

    static {
        buildCommonConfig();
        buildClientConfig();
    }

    private static void buildCommonConfig(){
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        setupGeneralSettings();
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Food Settings").push(CATEGORY_FOOD);
        setupFoodSettings();
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

        // Gas Fired Furnace
        COMMON_BUILDER.comment("Gas Furnace Settings").push(CATEGORY_GAS_FIRED_FURNACE);
        setupGasFiredFurnace();
        COMMON_BUILDER.pop();

        // Electric Furnace
        COMMON_BUILDER.comment("Electric Furnace Settings").push(CATEGORY_ELECTRIC_FURNACE);
        setupElectricFurnace();
        COMMON_BUILDER.pop();

        // Battery Box
        COMMON_BUILDER.comment("Battery Box Settings").push(CATEGORY_BATTERY_BOX);
        setupBatteryBox();
        COMMON_BUILDER.pop();

        // Primitive Solar Panel
        COMMON_BUILDER.comment("Primitive Solar Panel").push(CATEGORY_PRIMITIVE_SOLAR_PANEL);
        setupPrimitiveSolarPanel();
        COMMON_BUILDER.pop();

        // Solar Panel
        COMMON_BUILDER.comment("Solar Panel").push(CATEGORY_SOLAR_PANEL);
        setupSolarPanel();
        COMMON_BUILDER.pop();

        // Centrifugal Separator
        COMMON_BUILDER.comment("Centrifugal Separator").push(CATEGORY_CENTRIFUGAL_SEPARATOR);
        setupCentrifugalSeparator();
        COMMON_BUILDER.pop();

        // Implosion Compressor
        COMMON_BUILDER.comment("Implosion Compressor").push(CATEGORY_IMPLOSION_COMPRESSOR);
        setupImplosionCompressor();
        COMMON_BUILDER.pop();

        // Blast Furnace
        COMMON_BUILDER.comment("Blast Furnace").push(CATEGORY_BLAST_FURNACE);
        setupBlastFurnace();
        COMMON_BUILDER.pop();

        // Sawmill
        COMMON_BUILDER.comment("Sawmill").push(CATEGORY_SAWMILL);
        setupSawmill();
        COMMON_BUILDER.pop();

        // Tanks
        COMMON_BUILDER.comment("Tank Blocks").push(CATEGORY_TANK_BLOCKS);
        setupTankBlocks();
        COMMON_BUILDER.pop();

        // Tooling Station
        COMMON_BUILDER.comment("Tooling Station").push(CATEGORY_TOOLING_STATION);
        setupToolingStation();
        COMMON_BUILDER.pop();

        // Fluid Electrolyzer
        COMMON_BUILDER.comment("Fluid Electrolyzer").push(CATEGORY_FLUID_ELECTROLYZER);
        setupFluidElectrolyzer();
        COMMON_BUILDER.pop();

        // Fluid Mixer
        COMMON_BUILDER.comment("Fluid Mixer").push(CATEGORY_FLUID_MIXER);
        setupFluidMixer();
        COMMON_BUILDER.pop();

        // Fluid Mixer
        COMMON_BUILDER.comment("Hydroponic Incubator").push(CATEGORY_HYDROPONIC_INCUBATOR);
        setupHydroponicIncubatorMixer();
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void setupGeneralSettings(){
        ALLOW_EXTRACTION_FROM_INPUT_TANKS = COMMON_BUILDER.comment("Allow pipes to extract fluids from fluid input tanks. Disabling this means that only fluid outputs can be extracted from Tile Entites")
                .define("Allow Extraction from Input Tanks", false);
        ACID_DAMAGE = COMMON_BUILDER.comment("Damage taken from standing in an acid.")
                .defineInRange("Acid Damage", 3F, 0F, Float.MAX_VALUE);
        ACID_FIRE_DURATION = COMMON_BUILDER.comment("Duration of fire on an entity for stepping into an acid.")
                .defineInRange("Acid Fire Duration", 4, 0, Integer.MAX_VALUE);
        SOLARIUM_PROTECTIVE_SHEATH_HITS = COMMON_BUILDER.comment("How many uses a Solarium tool can have before the protective sheath is depleted")
                .defineInRange("Solarium Protective Sheath", 2560, 0, Integer.MAX_VALUE);
        DECREMENT_SPEED_ON_NO_POWER = COMMON_BUILDER.comment("Speed of which progress should be undone at a machine when the machine loses power/FE")
                .defineInRange("Decrement Speed On No Power", 5, 1, 20);
    }

    private static void setupFoodSettings(){
        COOKED_RICE_NUTRITION = COMMON_BUILDER.comment("Nutritional value of Cooked Rice.")
                .defineInRange("Cooked Rice Nutrition", 5, 1, Integer.MAX_VALUE);
        COOKED_RICE_SATURATION = COMMON_BUILDER.comment("Saturation of Cooked Rice.")
                .defineInRange("Cooked Rice Saturation", 0.3F, Float.MIN_NORMAL, Float.MAX_VALUE);
        RICE_TICK_CHANCE = COMMON_BUILDER.comment("Chance of Rice crop going to next life stage.")
                .defineInRange("Rice Tick Chance", 0.5F, Float.MIN_NORMAL, 1F);
    }

    private static void setupWorldGen(){
        COMMON_BUILDER.comment("Feature Generation").push(SUBCATEGORY_FEATURE_GENERATION);
            WORLD_GEN_LOGGING = COMMON_BUILDER.comment("Enable/Disable Logging of Word Generation information for " +
                    "Voluminous Energy features and Ores")
                    .define("Enable Logging", false);
        COMMON_BUILDER.pop(); // End of Feature Generation

        COMMON_BUILDER.comment("Climate Spawns").push(SUBCATEGORY_CLIMATE_SPAWNS);
            PUNCH_HOLES_IN_CLIMATE_SPAWNS = COMMON_BUILDER.comment("To prevent continuous valid chunks, 'punch holes' by removing spawns from valid chunks (Ex: Reduce non-water or lava appearances for the Dimensional Laser)")
                .define("Punch Holes in Climate Spawns", true);
            CLIMATE_SPAWNS_HOLE_PUNCH_MULTIPLIER = COMMON_BUILDER.comment("Multiply cumulative climate value by this multiplier ")
                    .defineInRange("Climate Spawns Hole Punch Multiplier", 10_000, 1, Integer.MAX_VALUE);
            CLIMATE_SPAWNS_HOLE_PUNCH_BOUNDING = COMMON_BUILDER.comment("Value to be used for binding the RNG ")
                .defineInRange("Climate Spawns Hole Punch Bounding", 100, 10, 10_000);
            CLIMATE_SPAWNS_HOLE_PUNCH_RNG_MUST_BE_LARGER = COMMON_BUILDER.comment("If generated number is smaller than this value, the chunk will be 'hole punched'.")
                .defineInRange("Climate Spawns Hole Punch Number Must Be Larger Than", 47, 1, 10_000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Ore Generation").push(SUBCATEGORY_ORE_GENERATION);
            //Saltpeter
            COMMON_BUILDER.comment("Saltpeter Ore Blob Settings").push(SUBCATEGORY_SALTPETER);
                SALTPETER_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Saltpeter Size", 33, 0, Integer.MAX_VALUE);
                SALTPETER_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                        .defineInRange("Saltpeter Exposed Discard Chance", 0F, 0F, 1F);
            COMMON_BUILDER.pop();

            //Bauxite
            COMMON_BUILDER.comment("Bauxite Ore Settings").push(SUBCATEGORY_BAUXITE);
                BAUXITE_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Bauxite Size", 8, 0, Integer.MAX_VALUE);
                BAUXITE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                    .defineInRange("Bauxite Exposed Discard Chance", 0F, 0F, 1F);
            COMMON_BUILDER.pop(); // End of Bauxite

            //Cinnabar
            COMMON_BUILDER.comment("Cinnabar Ore Settings").push(SUBCATEGORY_CINNABAR);
                CINNABAR_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Cinnabar Size", 6, 0, Integer.MAX_VALUE);
                CINNABAR_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                    .defineInRange("Cinnabar Exposed Discard Chance", 0F, 0F, 1F);
            COMMON_BUILDER.pop(); // End of Cinnabar

            //Rutile
            COMMON_BUILDER.comment("Rutile Ore Settings").push(SUBCATEGORY_RUTILE);
                RUTILE_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Rutile Size", 4, 0, Integer.MAX_VALUE);
                RUTILE_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                    .defineInRange("Rutile Exposed Discard Chance", 0.75F, 0F, 1F);
            COMMON_BUILDER.pop(); // End of Rutile

            //Galena
            COMMON_BUILDER.comment("Galena Ore Settings").push(SUBCATEGORY_GALENA);
                GALENA_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Galena Size", 6, 0, Integer.MAX_VALUE);
                GALENA_ORE_BLOCK_LIGHT_LEVEL = COMMON_BUILDER.defineInRange("Galena Light Glow Level", 4, 0, 16);
                GALENA_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                    .defineInRange("Galena Exposed Discard Chance", 0F, 0F, 1F);
            COMMON_BUILDER.pop(); // End of Galena

            //Eighzo
            COMMON_BUILDER.comment("Eighzo Ore Settings").push(SUBCATEGORY_EIGHZO);
                EIGHZO_ORE_BLOBS_SIZE = COMMON_BUILDER.defineInRange("Eighzo Size", 4, 0, Integer.MAX_VALUE);
                EIGHZO_ORE_BLOBS_EXPOSED_DISCARD_CHANCE = COMMON_BUILDER.comment("Chance that that the generated blob will be discarded (ungenerated) if exposed to air")
                    .defineInRange("Eighzo Exposed Discard Chance", 0.5F, 0F, 1F);
            COMMON_BUILDER.pop(); // End of Eighzo

            // DEPOSITS
            COMMON_BUILDER.comment("Ore Deposits: Large cluster of ore and raw block, more compact than an ore vein, but bigger than an ore blob.").push(SUBCATEGORY_ORE_DEPOSIT_GENERATION);
                PREVENT_SURFACE_ORE_DEPOSITS = COMMON_BUILDER.comment("Prevent the generation of ore deposits with exposure to the world's surface").define("Disallow surface ore deposits", false);
            COMMON_BUILDER.pop(); // End of Ore Deposit

        COMMON_BUILDER.pop(); // End of Ore Generation
    }

    private static void setupPrimitiveStirlingGeneratorConfig(){
        PRIMITIVE_STIRLING_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum power for the Primitive Stirling Generator to store")
                .defineInRange("Maximum Power",100000, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_GENERATE = COMMON_BUILDER.comment("Power generation per tick")
                .defineInRange("Generation (Per Tick)",40, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("Output Rate",1000, 0, Integer.MAX_VALUE);
    }

    private static void setupCrusher(){
        CRUSHER_MAX_POWER = COMMON_BUILDER.comment("Maximum power for the Crusher to store")
                .defineInRange("Maximum Power",5000,0,Integer.MAX_VALUE);
        CRUSHER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Crusher")
                .defineInRange("Power Consumption",40,0,Integer.MAX_VALUE);
        CRUSHER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Crusher")
                .defineInRange("Maximum Transfer",1000,0,Integer.MAX_VALUE);
    }

    private static void setupElectrolyzer(){
        ELECTROLYZER_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Electrolyzer to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        ELECTROLYZER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Electrolyzer")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        ELECTROLYZER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Electrolyzer")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupCentrifugalAgitator(){
        CENTRIFUGAL_AGITATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Centrifugal Agitator to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_AGITATOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Centrifugal Agitator")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_AGITATOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Centrifugal Agitator")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupCompressor(){
        COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Compressor to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Compressor")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Compressor")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupStirlingGenerator(){
        STIRLING_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Stirling Generator to store")
                .defineInRange("Maximum Power", 5120000, 0, Integer.MAX_VALUE);
        STIRLING_GENERATOR_GENERATE = COMMON_BUILDER.comment("Fallback value for power generation for the Stirling Generator")
                .defineInRange("Fallback Generation Rate", 128, 0, Integer.MAX_VALUE);
        STIRLING_GENERATOR_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Stirling Generator")
                .defineInRange("Maximum Transfer", 128000, 0, Integer.MAX_VALUE);
    }

    private static void setupCombustionGenerator(){
        COMBUSTION_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Combustion Generator to store")
                .defineInRange("Maximum Power", 5120000, 0, Integer.MAX_VALUE);
        COMBUSTION_GENERATOR_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Combustion Generator")
                .defineInRange("Maximum Transfer", 512000, 0, Integer.MAX_VALUE);
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
    }

    private static void setupAirCompressor(){
        AIR_COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Air Compressor to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        AIR_COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Air Compressor")
                .defineInRange("Power Consumption", 24, 0, Integer.MAX_VALUE);
        AIR_COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Air Compressor")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupDistillationUnit(){
        DISTILLATION_UNIT_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Distillation Unit to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        DISTILLATION_UNIT_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Distillation Unit")
                .defineInRange("Power Consumption", 64, 0, Integer.MAX_VALUE);
        DISTILLATION_UNIT_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Distillation Unit")
                .defineInRange("Maximum Transfer", 2500, 0, Integer.MAX_VALUE);
    }

    private static void setupPump(){
        PUMP_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Pump to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        PUMP_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Pump")
                .defineInRange("Power Consumption", 16, 0, Integer.MAX_VALUE);
        PUMP_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Pump")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    @Deprecated // Remove if it will never be utilized in the future, undeprecate if there is a use
    private static void setupGasFiredFurnace(){}

    private static void setupElectricFurnace(){
        ELECTRIC_FURNACE_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Electric Furnace to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        ELECTRIC_FURNACE_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Electric Furnace")
                .defineInRange("Power Consumption", 64, 0, Integer.MAX_VALUE);
        ELECTRIC_FURNACE_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Electric Furnace")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupBatteryBox(){
        BATTERY_BOX_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Battery Box to store")
                .defineInRange("Maximum Power", 50_000, 0, Integer.MAX_VALUE);
        BATTERY_BOX_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Battery Box")
                .defineInRange("Maximum Transfer", 5_000, 0, Integer.MAX_VALUE);
    }

    public static void setupPrimitiveSolarPanel(){
        PRIMITIVE_SOLAR_PANEL_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Primitive Solar Panel to store")
                .defineInRange("Maximum Power", 100_000, 0, Integer.MAX_VALUE);
        PRIMITIVE_SOLAR_PANEL_GENERATE = COMMON_BUILDER.comment("Value for power generation for the Primitive Solar Panel")
                .defineInRange("Generation Rate", 64, 0, Integer.MAX_VALUE);
        PRIMITIVE_SOLAR_PANEL_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Primitive Solar Panel")
                .defineInRange("Maximum Transfer", 1_000, 0, Integer.MAX_VALUE);
    }

    public static void setupSolarPanel(){
        SOLAR_PANEL_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Solar Panel to store")
                .defineInRange("Maximum Power", 250_000, 0, Integer.MAX_VALUE);
        SOLAR_PANEL_GENERATE = COMMON_BUILDER.comment("Value for power generation for the Solar Panel")
                .defineInRange("Generation Rate", 128, 0, Integer.MAX_VALUE);
        SOLAR_PANEL_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Solar Panel")
                .defineInRange("Maximum Transfer", 5_000, 0, Integer.MAX_VALUE);
    }

    private static void setupCentrifugalSeparator(){
        CENTRIFUGAL_SEPARATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Centrifugal Separator to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_SEPARATOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Centrifugal Separator")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_SEPARATOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Centrifugal Separator")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupImplosionCompressor(){
        IMPLOSION_COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Implosion Compressor to store")
                .defineInRange("Maximum Power", 128_000, 0, Integer.MAX_VALUE);
        IMPLOSION_COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Implosion Compressor")
                .defineInRange("Power Consumption", 128, 0, Integer.MAX_VALUE);
        IMPLOSION_COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Implosion Compressor")
                .defineInRange("Maximum Transfer", 2_500, 0, Integer.MAX_VALUE);
    }

    private static void setupBlastFurnace(){
        BLAST_FURNACE_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Blast Furnace to store")
                .defineInRange("Maximum Power", 512_000, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Blast Furnace")
                .defineInRange("Power Consumption", 256, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Blast Furnace")
                .defineInRange("Maximum Transfer", 10_000, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION = COMMON_BUILDER.comment("Amount of fluid that is consumed per blasting operation for heat")
                .defineInRange("Heat Source Consumption", 50, 0, 4_000);
    }

    private static void setupSawmill(){
        SAWMILL_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Sawmill to store")
                .defineInRange("Maximum Power", 512_000, 0, Integer.MAX_VALUE);
        SAWMILL_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Sawmill")
                .defineInRange("Power Consumption", 64, 0, Integer.MAX_VALUE);
        SAWMILL_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Sawmill")
                .defineInRange("Maximum Transfer", 10_000, 0, Integer.MAX_VALUE);
        SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED = COMMON_BUILDER.comment("true/false, Allow the use of logs that don't have a proper Sawmill recipe")
                .define("Allow logs without recipe", true);
        SAWMILL_FLUID_LOCATION = COMMON_BUILDER.comment("Resource Location of the fluid that will be generated when processing a log without a recipe")
                .define("Fluid location", "voluminousenergy:tree_sap");
        SAWMILL_FLUID_AMOUNT = COMMON_BUILDER.comment("Amount of fluid that will be generated when processing a log without a recipe")
                .defineInRange("Fluid Amount", 250, 0, 4_000);
        SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION = COMMON_BUILDER.comment("Resource Location of the second output item when processing a log without a recipe")
                .define("Second output item resource location", "voluminousenergy:saw_dust");
        SAWMILL_SECOND_OUTPUT_COUNT = COMMON_BUILDER.comment("Item count of the second output item when processing a log without a recipe")
                .defineInRange("Second output count", 1, 0, 64);
        SAWMILL_PROCESSING_TIME = COMMON_BUILDER.comment("Processing time when processing a log without a recipe")
                .defineInRange("Processing time", 200, 0, Integer.MAX_VALUE);
        SAWMILL_PRIMARY_OUTPUT_COUNT = COMMON_BUILDER.comment("Amount of the primary item (typically a plank) that will be generated when finished processing a log without a recipe")
                .defineInRange("Primary output count", 6, 1, 64);
        SAWMILL_LOG_CONSUMPTION_RATE = COMMON_BUILDER.comment("Amount of the input item (typically a log) that will be consumed when finished processing a log without a recipe")
                .defineInRange("Number of logs to consume", 1, 1, 64);
        }

        private static void setupTankBlocks(){
            SOLARIUM_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Solarium Tank Capacity", 2_147_483, 0, 2_147_483);

            EIGHZO_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Eighzo Tank Capacity", 536_870, 0, 2_147_483);

            NIGHALITE_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Nighalite Tank Capacity", 67_108, 0, 2_147_483);

            NETHERITE_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Netherite Tank Capacity", 8_388, 0, 2_147_483);

            TITANIUM_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Titanium Tank Capacity", 524, 0, 2_147_483);

            ALUMINUM_TANK_CAPACITY = COMMON_BUILDER.comment("Maximum tank capacity in Buckets")
                    .defineInRange("Aluminum Tank Capacity", 64, 0, 2_147_483);
        }

    private static void setupToolingStation(){
        TOOLING_STATION_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Tooling Station to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        TOOLING_STATION_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Tooling Station")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
    }

    private static void setupFluidElectrolyzer(){
        FLUID_ELECTROLYZER_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Fluid Electrolyzer to store")
                .defineInRange("Maximum Power", 128_000, 0, Integer.MAX_VALUE);
        FLUID_ELECTROLYZER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Fluid Electrolyzer")
                .defineInRange("Power Consumption", 128, 0, Integer.MAX_VALUE);
        FLUID_ELECTROLYZER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Fluid Electrolyzer")
                .defineInRange("Maximum Transfer", 25_000, 0, Integer.MAX_VALUE);
    }

    private static void setupFluidMixer(){
        FLUID_MIXER_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Fluid Mixer to store")
                .defineInRange("Maximum Power", 128_000, 0, Integer.MAX_VALUE);
        FLUID_MIXER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Fluid Mixer")
                .defineInRange("Power Consumption", 96, 0, Integer.MAX_VALUE);
        FLUID_MIXER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Fluid Mixer")
                .defineInRange("Maximum Transfer", 25_000, 0, Integer.MAX_VALUE);
    }

    private static void setupHydroponicIncubatorMixer(){
        HYDROPONIC_INCUBATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Hydroponic Incubator to store")
                .defineInRange("Maximum Power", 128_000, 0, Integer.MAX_VALUE);
        HYDROPONIC_INCUBATOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Hydroponic Incubator")
                .defineInRange("Power Consumption", 96, 0, Integer.MAX_VALUE);
        HYDROPONIC_INCUBATOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Hydroponic Incubator")
                .defineInRange("Maximum Transfer", 25_000, 0, Integer.MAX_VALUE);
    }

    // CLIENT CONFIG START
    private static void buildClientConfig(){
        CLIENT_BUILDER.comment("General Client Settings").push(CATEGORY_GENERAL);

        USE_BIOME_WATER_COLOUR = CLIENT_BUILDER.comment("When rendering water in tanks, use the water's biome appropriate colour, instead of the generic universal water colour.")
                .define("Use Biome Water Colour", true);
        SHORTEN_ITEM_TOOLTIP_VALUES = CLIENT_BUILDER.comment("When displaying larger numbers on a tooltip of an item you hover over, change the units to keep the numbers small (eg 1 B instead of 1000 mB).")
                .define("Shorten Item Tooltip Values", true);
        SHORTEN_POWER_BAR_VALUES = CLIENT_BUILDER.comment("When displaying larger numbers on a tooltip for the machines power bar, change the units to keep the numbers small (eg 1 kFE instead of 1000 FE).")
                .define("Shorten Power Bar Values", true);
        SHORTEN_TANK_GUI_VALUES = CLIENT_BUILDER.comment("When displaying larger numbers on a tooltip for a tank you hovered over, change the units to keep the numbers small (eg 1 B instead of 1000 mB).")
                .define("Shorten Tank Tooltip Values", true);

        CLIENT_BUILDER.pop();
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
}
