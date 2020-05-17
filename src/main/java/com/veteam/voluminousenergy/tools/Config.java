package com.veteam.voluminousenergy.tools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_WORLDGEN = "World Generation";
    public static final String CATEGORY_PRIMITIVE_STIRLING_GENERATOR = "Primitive Stirling Generator";
    public static final String CATEGORY_CRUSHER = "Crusher";
    public static final String CATEGORY_ELECTROLYZER = "Electrolyzer";

    public static final String SUBCATEGORY_FEATURE_GENERATION = "Feature Generation";
    public static final String SUBCATEGORY_ORE_GENERATION = "Ore Generation";
    public static final String SUBCATEGORY_SALTPETER = "Saltpeter Ore Settings";
    public static final String SUBCATEGORY_BAUXITE = "Bauxite Ore Settings";
    public static final String SUBCATEGORY_CINNABAR = "Cinnabar Ore Settings";
    public static final String SUBCATEGORY_RUTILE = "Rutile Ore Settings";
    public static final String SUBCATEGORY_GALENA = "Galena Ore Settings";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    // World Feature Settings
    public static ForgeConfigSpec.BooleanValue ENABLE_VE_FEATURE_GEN;
    public static ForgeConfigSpec.BooleanValue GENERATE_OIL_LAKES;
    public static ForgeConfigSpec.IntValue OIL_LAKE_CHANCE;
    // Ore Settings
    // SALTPETER ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_SALTPETER_ORE;
    public static ForgeConfigSpec.IntValue SALTPETER_COUNT;
    public static ForgeConfigSpec.IntValue SALTPETER_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue SALTPETER_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue SALTPETER_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue SALTPETER_SIZE;
    // BAUXITE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_BAUXITE_ORE;
    public static ForgeConfigSpec.IntValue BAUXITE_COUNT;
    public static ForgeConfigSpec.IntValue BAUXITE_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue BAUXITE_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue BAUXITE_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue BAUXITE_SIZE;
    // CINNABAR ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_CINNABAR_ORE;
    public static ForgeConfigSpec.IntValue CINNABAR_COUNT;
    public static ForgeConfigSpec.IntValue CINNABAR_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue CINNABAR_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue CINNABAR_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue CINNABAR_SIZE;
    // RUTILE ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_RUTILE_ORE;
    public static ForgeConfigSpec.IntValue RUTILE_COUNT;
    public static ForgeConfigSpec.IntValue RUTILE_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue RUTILE_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue RUTILE_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue RUTILE_SIZE;
    // GALENA ORE
    public static ForgeConfigSpec.BooleanValue ENABLE_GALENA_ORE;
    public static ForgeConfigSpec.IntValue GALENA_COUNT;
    public static ForgeConfigSpec.IntValue GALENA_BOTTOM_OFFSET;
    public static ForgeConfigSpec.IntValue GALENA_HEIGHT_OFFSET;
    public static ForgeConfigSpec.IntValue GALENA_MAXIMUM_HEIGHT;
    public static ForgeConfigSpec.IntValue GALENA_SIZE;
    public static ForgeConfigSpec.IntValue GALENA_GLOW;

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

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void setupWorldGen(){
        ENABLE_VE_FEATURE_GEN = COMMON_BUILDER.comment("Enable/Disable all Voluminous Energy changes to world generation")
                .define("World Generation", true);

        COMMON_BUILDER.comment("Feature Generation").push(SUBCATEGORY_FEATURE_GENERATION);
            GENERATE_OIL_LAKES = COMMON_BUILDER.comment("Enable/Disable Oil Lakes")
                    .define("Oil Lakes", true);
            OIL_LAKE_CHANCE = COMMON_BUILDER.comment("Oil Lake Chance (Lower = Higher chance)")
                    .defineInRange("Oil Lake Chance", 80, 10, Integer.MAX_VALUE);
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
            COMMON_BUILDER.pop();

            //Rutile
            COMMON_BUILDER.comment("Rutile Ore Settings").push(SUBCATEGORY_RUTILE);
            ENABLE_RUTILE_ORE = COMMON_BUILDER.comment("Enable/Disable generation of Rutile Ore")
                .define("Enable Rutile Ore", true);
            RUTILE_COUNT = COMMON_BUILDER.defineInRange("Rutile Weight",3,1, Integer.MAX_VALUE);
            RUTILE_BOTTOM_OFFSET = COMMON_BUILDER.defineInRange("Rutile Bottom Offset", 1, 1, 256);
            RUTILE_HEIGHT_OFFSET = COMMON_BUILDER.defineInRange("Rutile Height Offset", 0, 0, 256);
            RUTILE_MAXIMUM_HEIGHT = COMMON_BUILDER.defineInRange("Rutile Maximum Height", 10, 0, 256);
            RUTILE_SIZE = COMMON_BUILDER.defineInRange("Rutile Size", 4, 0, Integer.MAX_VALUE);
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
    public static void onReload(final ModConfig.ConfigReloading configEvent){

    }

}
