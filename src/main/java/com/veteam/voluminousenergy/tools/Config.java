package com.veteam.voluminousenergy.tools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_PRIMITIVE_STIRLING_GENERATOR = "primitiveStirlingGenerator";
    public static final String SUBCATEGORY_CRUSHER = "crusher";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_MAX_POWER;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_GENERATE;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_SEND;
    public static ForgeConfigSpec.IntValue PRIMITIVE_STIRLING_GENERATOR_TICKS;

    public static ForgeConfigSpec.IntValue CRUSHER_MAX_POWER;
    public static ForgeConfigSpec.IntValue CRUSHER_POWER_USAGE;
    public static ForgeConfigSpec.IntValue CRUSHER_TRANSFER;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();


        COMMON_BUILDER.comment("Primitive Stirling Generator Settings").push(CATEGORY_POWER);
        setupPrimitiveStirlingGeneratorConfig();
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Crusher Settings").push(CATEGORY_POWER);
        setupCrusher();
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupPrimitiveStirlingGeneratorConfig(){
        COMMON_BUILDER.comment("PrimitiveStirlingGenerator settings").push(SUBCATEGORY_PRIMITIVE_STIRLING_GENERATOR);

        PRIMITIVE_STIRLING_GENERATOR_MAX_POWER = COMMON_BUILDER.comment("Maximum power for the Primitive Stirling Generator")
                .defineInRange("maxPower",100000, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_GENERATE = COMMON_BUILDER.comment("Power generation per item")
                .defineInRange("generate",40, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_SEND = COMMON_BUILDER.comment("Power generation to send per tick")
                .defineInRange("send",1000, 0, Integer.MAX_VALUE);
        PRIMITIVE_STIRLING_GENERATOR_TICKS = COMMON_BUILDER.comment("Ticks needed per item")
                .defineInRange("ticks",1600, 0, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
    }

    private static void setupCrusher(){
        COMMON_BUILDER.comment("Crusher settings").push(SUBCATEGORY_CRUSHER);

        CRUSHER_MAX_POWER = COMMON_BUILDER.comment("Maximum power storage for the Crusher")
                .defineInRange("maxPower",5000,0,Integer.MAX_VALUE);
        CRUSHER_POWER_USAGE = COMMON_BUILDER.comment("Power consumption per tick for the Crusher")
                .defineInRange("usage",40,0,Integer.MAX_VALUE);
        CRUSHER_TRANSFER = COMMON_BUILDER.comment("Power I/O per tick for the Crusher")
                .defineInRange("maxTransfer",1000,0,Integer.MAX_VALUE);
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
