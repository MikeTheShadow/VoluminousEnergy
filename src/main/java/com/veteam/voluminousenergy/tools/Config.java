package com.veteam.voluminousenergy.tools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.nio.file.Path;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

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

    public static final String SUBCATEGORY_FEATURE_GENERATION = "Feature Generation";
    public static final String SUBCATEGORY_ORE_GENERATION = "Ore Generation";
    public static final String SUBCATEGORY_SALTPETER = "Saltpeter Ore Settings";
    public static final String SUBCATEGORY_BAUXITE = "Bauxite Ore Settings";
    public static final String SUBCATEGORY_CINNABAR = "Cinnabar Ore Settings";
    public static final String SUBCATEGORY_RUTILE = "Rutile Ore Settings";
    public static final String SUBCATEGORY_GALENA = "Galena Ore Settings";
    public static final String SUBCATEGORY_EIGHZO = "Eighzo Ore Settings";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    // World Generation Settings
    public static ForgeConfigSpec.BooleanValue GENERATE_VE_BIOMES;

    // World Feature Settings
    public static ForgeConfigSpec.BooleanValue ENABLE_VE_FEATURE_GEN;
    public static ForgeConfigSpec.BooleanValue WORLD_GEN_LOGGING;
    public static ForgeConfigSpec.BooleanValue GENERATE_OIL_LAKES;
    public static ForgeConfigSpec.BooleanValue GENERATE_OIL_GEYSER;
    public static ForgeConfigSpec.IntValue SURFACE_OIL_LAKE_CHANCE;
    public static ForgeConfigSpec.IntValue UNDERGROUND_OIL_LAKE_CHANCE;
    public static ForgeConfigSpec.IntValue OIL_GEYSER_CHANCE;
    public static ForgeConfigSpec.BooleanValue GENERATE_RICE;
    public static ForgeConfigSpec.BooleanValue GENERATE_RICE_IN_OCEAN;
    public static ForgeConfigSpec.IntValue RICE_CHANCE;

    // Food Settings
    public static ForgeConfigSpec.IntValue COOKED_RICE_NUTRITION;
    public static ForgeConfigSpec.DoubleValue COOKED_RICE_SATURATION;
    public static ForgeConfigSpec.DoubleValue RICE_TICK_CHANCE;

    // Ore Settings
    // SALTPETER ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_SALTPETER_ORE;
    public static ForgeConfigSpec.IntValue SALTPETER_COUNT;
    public static ForgeConfigSpec.IntValue SALTPETER_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue SALTPETER_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue SALTPETER_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue SALTPETER_HARVEST_LEVEL;
    // BAUXITE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_BAUXITE_ORE;
    public static ForgeConfigSpec.IntValue BAUXITE_COUNT;
    public static ForgeConfigSpec.IntValue BAUXITE_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue BAUXITE_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue BAUXITE_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue BAUXITE_HARVEST_LEVEL;
    // CINNABAR ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_CINNABAR_ORE;
    public static ForgeConfigSpec.IntValue CINNABAR_COUNT;
    public static ForgeConfigSpec.IntValue CINNABAR_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue CINNABAR_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue CINNABAR_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue CINNABAR_HARVEST_LEVEL;
    // RUTILE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_RUTILE_ORE;
    public static ForgeConfigSpec.IntValue RUTILE_COUNT;
    public static ForgeConfigSpec.IntValue RUTILE_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue RUTILE_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue RUTILE_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue RUTILE_HARVEST_LEVEL;
    // GALENA ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_GALENA_ORE;
    public static ForgeConfigSpec.IntValue GALENA_COUNT;
    public static ForgeConfigSpec.IntValue GALENA_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue GALENA_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue GALENA_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue GALENA_HARVEST_LEVEL;
    public static ForgeConfigSpec.IntValue GALENA_GLOW;
    // EIGHZO ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_EIGHZO_ORE;
    public static ForgeConfigSpec.IntValue EIGHZO_COUNT;
    public static ForgeConfigSpec.IntValue EIGHZO_BOTTOM_ANCHOR;
    public static ForgeConfigSpec.IntValue EIGHZO_TOP_ANCHOR;
    public static ForgeConfigSpec.IntValue EIGHZO_SIZE;
    @Deprecated
    public static ForgeConfigSpec.IntValue EIGHZO_HARVEST_LEVEL;

    // General Settings
    public static ForgeConfigSpec.BooleanValue ALLOW_EXTRACTION_FROM_INPUT_TANKS;
    public static ForgeConfigSpec.DoubleValue ACID_DAMAGE;
    public static ForgeConfigSpec.IntValue ACID_FIRE_DURATION;
    public static ForgeConfigSpec.IntValue SOLARIUM_PROTECTIVE_SHEATH_HITS;

    // Primitive Stirling Generator Variables
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_SEND;
    @Deprecated
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
    @Deprecated
    public static ForgeConfigSpec.IntValue ELECTROLYZER_HARVEST_LEVEL;

    // Centrifugal Agitator Variables
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_AGITATOR_HARVEST_LEVEL;

    // Compressor Variables
    public static ForgeConfigSpec.IntValue COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue COMPRESSOR_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue COMPRESSOR_HARVEST_LEVEL;

    // Stirling Generator Variables
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_SEND;
    @Deprecated
    public static ForgeConfigSpec.IntValue STIRLING_GENERATOR_HARVEST_LEVEL;

    // Combustion Generator Variables
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_SEND;
    @Deprecated
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_HARVEST_LEVEL;
    public static ForgeConfigSpec.BooleanValue COMBUSTION_GENERATOR_BALANCED_MODE;
    public static ForgeConfigSpec.IntValue COMBUSTION_GENERATOR_FIXED_TICK_TIME;

    // Aqueoulizer Variables
    public static ForgeConfigSpec.IntValue AQUEOULIZER_MAX_POWER;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AQUEOULIZER_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue AQUEOULIZER_HARVEST_LEVEL;

    // Air Compressor Variables
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue AIR_COMPRESSOR_HARVEST_LEVEL;

    // Distillation Unit Variables
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_MAX_POWER;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_POWER_USAGE;
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue DISTILLATION_UNIT_HARVEST_LEVEL;

    // Pump Variables
    public static ForgeConfigSpec.IntValue PUMP_MAX_POWER;
    public static ForgeConfigSpec.IntValue PUMP_POWER_USAGE;
    public static ForgeConfigSpec.IntValue PUMP_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue PUMP_HARVEST_LEVEL;

    // Gas Fired Furnace Variables
    @Deprecated
    public static ForgeConfigSpec.IntValue GAS_FIRED_FURNACE_HARVEST_LEVEL;

    // Electric Furnace Variables
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_MAX_POWER;
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_POWER_USAGE;
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue ELECTRIC_FURNACE_HARVEST_LEVEL;

    // Battery Box Variables
    public static ForgeConfigSpec.IntValue BATTERY_BOX_MAX_POWER;
    public static ForgeConfigSpec.IntValue BATTERY_BOX_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue BATTERY_BOX_HARVEST_LEVEL;

    // Primitive Solar Panel Variables
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_SEND;
    @Deprecated
    public static ForgeConfigSpec.IntValue PRIMITIVE_SOLAR_PANEL_HARVEST_LEVEL;

    // Solar Panel Variables
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_MAX_POWER;
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_GENERATE;
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_SEND;
    @Deprecated
    public static ForgeConfigSpec.IntValue SOLAR_PANEL_HARVEST_LEVEL;

    // Centrifugal Separator Variables
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue CENTRIFUGAL_SEPARATOR_HARVEST_LEVEL;

    // Implosion Compressor Variables
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_POWER_USAGE;
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue IMPLOSION_COMPRESSOR_HARVEST_LEVEL;

    // Blast Furnace Variables
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_MAX_POWER;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_POWER_USAGE;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_TRANSFER;
    @Deprecated
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_HARVEST_LEVEL;
    public static ForgeConfigSpec.IntValue BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION;

    static {
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
        GENERATE_VE_BIOMES = COMMON_BUILDER.comment("Enable/Disable Voluminous Energy biomes")
                .define("Enable VE Biomes", true);

        ENABLE_VE_FEATURE_GEN = COMMON_BUILDER.comment("Enable/Disable all Voluminous Energy changes to feature generation")
                .define("World Generation", true);

        COMMON_BUILDER.comment("Feature Generation").push(SUBCATEGORY_FEATURE_GENERATION);
            WORLD_GEN_LOGGING = COMMON_BUILDER.comment("Enable/Disable Logging of Word Generation information for " +
                    "Voluminous Energy features and Ores")
                    .define("Enable Logging", true);
            GENERATE_OIL_LAKES = COMMON_BUILDER.comment("Enable/Disable Oil Lakes")
                    .define("Oil Lakes", true);
            SURFACE_OIL_LAKE_CHANCE = COMMON_BUILDER.comment("Surface Oil Lake Chance (Lower = Higher chance)")
                    .defineInRange("Surface Oil Lake Chance", 65, 10, Integer.MAX_VALUE);
            UNDERGROUND_OIL_LAKE_CHANCE = COMMON_BUILDER.comment("Underground Oil Lake Chance (Lower = Higher chance)")
                .defineInRange("Underground Oil Lake Chance", 15, 10, Integer.MAX_VALUE);
            GENERATE_OIL_GEYSER = COMMON_BUILDER.comment("Enable/Disable Oil Geysers")
                    .define("Oil Geysers", true);
            OIL_GEYSER_CHANCE = COMMON_BUILDER.comment("Oil Geyser Chance (Lower = Higher chance)")
                    .defineInRange("Oil Geyser Chance", 100, 10, Integer.MAX_VALUE);
            GENERATE_RICE = COMMON_BUILDER.comment("Enable/Disable Generation of Rice Crop")
                    .define("Generate Rice", true);
            GENERATE_RICE_IN_OCEAN = COMMON_BUILDER.comment("Enable/Disable Generation of Rice Crop in Oceans")
                    .define("Generate Rice in Oceans", false);
            RICE_CHANCE = COMMON_BUILDER.comment("Rice Chance (Lower = Higher chance)")
                    .defineInRange("Rice Chance", 32, 10, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Ore Generation").push(SUBCATEGORY_ORE_GENERATION);
            //Saltpeter
            COMMON_BUILDER.comment("Saltpeter Ore Settings").push(SUBCATEGORY_SALTPETER);
            ENABLE_SALTPETER_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Saltpeter Ore")
                    .define("Enable Saltpeter Ore", true);
            SALTPETER_COUNT = COMMON_BUILDER.defineInRange("Saltpeter Weight",4,1, Integer.MAX_VALUE);
            SALTPETER_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Saltpeter Bottom Anchor", 55, 1, 256);
            SALTPETER_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Saltpeter Top Anchor", 256, 0, 256);
            SALTPETER_SIZE = COMMON_BUILDER.defineInRange("Saltpeter Size", 33, 0, Integer.MAX_VALUE);
            SALTPETER_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 0, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Bauxite
            COMMON_BUILDER.comment("Bauxite Ore Settings").push(SUBCATEGORY_BAUXITE);
            ENABLE_BAUXITE_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Bauxite Ore")
                .define("Enable Bauxite Ore", true);
            BAUXITE_COUNT = COMMON_BUILDER.defineInRange("Bauxite Weight",16,1, Integer.MAX_VALUE);
            BAUXITE_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Bauxite Bottom Anchor", 10, 1, 256);
            BAUXITE_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Bauxite Top Anchor", 60, 0, 256);
            BAUXITE_SIZE = COMMON_BUILDER.defineInRange("Bauxite Size", 8, 0, Integer.MAX_VALUE);
            BAUXITE_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Cinnabar
            COMMON_BUILDER.comment("Cinnabar Ore Settings").push(SUBCATEGORY_CINNABAR);
            ENABLE_CINNABAR_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Cinnabar Ore")
                .define("Enable Cinnabar Ore", true);
            CINNABAR_COUNT = COMMON_BUILDER.defineInRange("Cinnabar Weight",9,1, Integer.MAX_VALUE);
            CINNABAR_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Cinnabar Bottom Anchor", 1, 1, 256);
            CINNABAR_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Cinnabar Top Anchor", 256, 0, 256);
            CINNABAR_SIZE = COMMON_BUILDER.defineInRange("Cinnabar Size", 6, 0, Integer.MAX_VALUE);
            CINNABAR_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 2, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Rutile
            COMMON_BUILDER.comment("Rutile Ore Settings").push(SUBCATEGORY_RUTILE);
            ENABLE_RUTILE_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Rutile Ore")
                .define("Enable Rutile Ore", true);
            RUTILE_COUNT = COMMON_BUILDER.defineInRange("Rutile Weight",3,1, Integer.MAX_VALUE);
            RUTILE_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Rutile Bottom Anchor", 1, 1, 256);
            RUTILE_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Rutile Top Anchor", 16, 0, 256);
            RUTILE_SIZE = COMMON_BUILDER.defineInRange("Rutile Size", 4, 0, Integer.MAX_VALUE);
            RUTILE_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 3, 0, Integer.MAX_VALUE);
            COMMON_BUILDER.pop();

            //Galena
            COMMON_BUILDER.comment("Galena Ore Settings").push(SUBCATEGORY_GALENA);
            ENABLE_GALENA_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Galena Ore")
                .define("Enable Galena Ore", true);
            GALENA_COUNT = COMMON_BUILDER.defineInRange("Galena Weight",3,1, Integer.MAX_VALUE);
            GALENA_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Galena Bottom Anchor", 12, 1, 256);
            GALENA_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Galena Top Anchor", 32, 0, 256);
            GALENA_SIZE = COMMON_BUILDER.defineInRange("Galena Size", 6, 0, Integer.MAX_VALUE);
            GALENA_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 2, 0, Integer.MAX_VALUE);
            GALENA_GLOW = COMMON_BUILDER.defineInRange("Galena Light Glow Level", 4, 0, 16);
            COMMON_BUILDER.pop();

            //Eighzo
            COMMON_BUILDER.comment("Eighzo Ore Settings").push(SUBCATEGORY_EIGHZO);
            ENABLE_EIGHZO_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Eighzo Ore")
                .define("Enable Eighzo Ore", true);
            EIGHZO_COUNT = COMMON_BUILDER.defineInRange("Eighzo Weight",1,1, Integer.MAX_VALUE);
            EIGHZO_BOTTOM_ANCHOR = COMMON_BUILDER.defineInRange("Eighzo Bottom Anchor", 1, 1, 256);
            EIGHZO_TOP_ANCHOR = COMMON_BUILDER.defineInRange("Eighzo Top Anchor", 36, 0, 256);
            EIGHZO_SIZE = COMMON_BUILDER.defineInRange("Eighzo Size", 4, 0, Integer.MAX_VALUE);
            EIGHZO_HARVEST_LEVEL = COMMON_BUILDER.comment("NOTE: Deprecated. Use a datapack to change the tag.").defineInRange("Harvest Level", 5, 0, Integer.MAX_VALUE);

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
        PUMP_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Pump to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        PUMP_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Pump")
                .defineInRange("Power Consumption", 16, 0, Integer.MAX_VALUE);
        PUMP_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Pump")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        PUMP_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupGasFiredFurnace(){
        GAS_FIRED_FURNACE_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupElectricFurnace(){
        ELECTRIC_FURNACE_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Electric Furnace to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        ELECTRIC_FURNACE_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Electric Furnace")
                .defineInRange("Power Consumption", 64, 0, Integer.MAX_VALUE);
        ELECTRIC_FURNACE_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Electric Furnace")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        ELECTRIC_FURNACE_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupBatteryBox(){
        BATTERY_BOX_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Battery Box to store")
                .defineInRange("Maximum Power", 50_000, 0, Integer.MAX_VALUE);
        BATTERY_BOX_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Battery Box")
                .defineInRange("Maximum Transfer", 5_000, 0, Integer.MAX_VALUE);
        BATTERY_BOX_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    public static void setupPrimitiveSolarPanel(){
        PRIMITIVE_SOLAR_PANEL_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Primitive Solar Panel to store")
                .defineInRange("Maximum Power", 100_000, 0, Integer.MAX_VALUE);
        PRIMITIVE_SOLAR_PANEL_GENERATE = COMMON_BUILDER.comment("Value for power generation for the Primitive Solar Panel")
                .defineInRange("Generation Rate", 64, 0, Integer.MAX_VALUE);
        PRIMITIVE_SOLAR_PANEL_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Primitive Solar Panel")
                .defineInRange("Maximum Transfer", 1_000, 0, Integer.MAX_VALUE);
        PRIMITIVE_SOLAR_PANEL_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    public static void setupSolarPanel(){
        SOLAR_PANEL_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Solar Panel to store")
                .defineInRange("Maximum Power", 250_000, 0, Integer.MAX_VALUE);
        SOLAR_PANEL_GENERATE = COMMON_BUILDER.comment("Value for power generation for the Solar Panel")
                .defineInRange("Generation Rate", 128, 0, Integer.MAX_VALUE);
        SOLAR_PANEL_SEND = COMMON_BUILDER.comment("Maximum power to send out per tick for the Solar Panel")
                .defineInRange("Maximum Transfer", 5_000, 0, Integer.MAX_VALUE);
        SOLAR_PANEL_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupCentrifugalSeparator(){
        CENTRIFUGAL_SEPARATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Centrifugal Separator to store")
                .defineInRange("Maximum Power", 5000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_SEPARATOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Centrifugal Separator")
                .defineInRange("Power Consumption", 40, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_SEPARATOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Centrifugal Separator")
                .defineInRange("Maximum Transfer", 1000, 0, Integer.MAX_VALUE);
        CENTRIFUGAL_SEPARATOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 1, 0, Integer.MAX_VALUE);
    }

    private static void setupImplosionCompressor(){
        IMPLOSION_COMPRESSOR_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Implosion Compressor to store")
                .defineInRange("Maximum Power", 128_000, 0, Integer.MAX_VALUE);
        IMPLOSION_COMPRESSOR_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Implosion Compressor")
                .defineInRange("Power Consumption", 128, 0, Integer.MAX_VALUE);
        IMPLOSION_COMPRESSOR_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Implosion Compressor")
                .defineInRange("Maximum Transfer", 2_500, 0, Integer.MAX_VALUE);
        IMPLOSION_COMPRESSOR_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 2, 0, Integer.MAX_VALUE);
    }

    private static void setupBlastFurnace(){
        BLAST_FURNACE_MAX_POWER = COMMON_BUILDER.comment("Maximum Power for the Blast Furnace to store")
                .defineInRange("Maximum Power", 512_000, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Blast Furnace")
                .defineInRange("Power Consumption", 256, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Blast Furnace")
                .defineInRange("Maximum Transfer", 10_000, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_HARVEST_LEVEL = COMMON_BUILDER.comment("Harvest level of the tool that is required to receive the block as an item when mined")
                .defineInRange("Harvest Level", 3, 0, Integer.MAX_VALUE);
        BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION = COMMON_BUILDER.comment("Amount of fluid that is consumed per blasting operation for heat")
                .defineInRange("Heat Source Consumption", 50, 0, 4_000);
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

    /*@SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent){

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent){

    }*/

}
