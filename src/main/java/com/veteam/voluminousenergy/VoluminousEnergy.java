package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEBlockItems;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.world.feature.VEFeatures;
import com.veteam.voluminousenergy.world.ores.VEOres;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VoluminousEnergy.MODID)
public class VoluminousEnergy {
    public static final String MODID = "voluminousenergy";

    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static VESetup setup = new VESetup();
    public static boolean JEI_LOADED = false;

    public static final Logger LOGGER = LogManager.getLogger();

    public VoluminousEnergy() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupWhenLoadingComplete);
        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        /** Deferred registration **/
        // Recipes
        VERecipes.VE_RECIPE_SERIALIZERS_REGISTRY.register(modEventBus);
        VERecipes.VERecipeTypes.VE_RECIPE_TYPES_REGISTRY.register(modEventBus);

        // Register fluids and respective items/blocks (differed)
        VEFluids.VE_FLUIDS.register(modEventBus);
        VEFluids.VE_FLUID_BLOCKS.register(modEventBus);
        VEFluids.VE_FLUID_ITEMS.register(modEventBus);

        // Register Blocks, Tiles, and Containers
        VEBlocks.VE_BLOCKS_REGISTRY.register(modEventBus);
        VEBlocks.VE_TILE_REGISTRY.register(modEventBus);
        VEBlocks.VE_CONTAINER_REGISTRY.register(modEventBus);

        // Deferred Item registration
        VEItems.VE_ITEM_REGISTRY.register(modEventBus);
        VEBlockItems.VE_BLOCK_ITEM_REGISTRY.register(modEventBus);
        VEMultitools.VE_MULTITOOL_ITEM_REGISTRY.register(modEventBus);
        VETools.VE_TOOL_REGISTRY.register(modEventBus);

        // TODO: Adding features due to BiomeLoadEvent being replaced
        // TODO: Port registering of Features to use Deferred register
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEOreGeneration::OreGeneration);
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEFeatureGeneration::addFeaturesToBiomes);

        // Config Files to load
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-common.toml"));
        
        JEI_LOADED = ModList.get().isLoaded("jei");
    }

    private void setup(final FMLCommonSetupEvent event) {
        //VEOreGeneration.OreGeneration(); // Setup custom ore generation
        //VEFeatureGeneration.VEFeatureGenerationSetup(); // Setup feature generation
        setup.init();
        proxy.init();
        VENetwork.init();
//        builtinRegisterConfiguredFeatures();
//        builtinRegisterPlacedFeatures();
        //VoluminousEnergy.LOGGER.debug("FMLCommonSetupEvent has ran.");
    }

    private void setupWhenLoadingComplete(final FMLLoadCompleteEvent event){
        /* //True Items
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","silicon"));
        */
    }

    /*@SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        //LOGGER.info("Hello from Voluminous Energy on server start!");
    }*/

    /*@SubscribeEvent
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        //VoluminousEnergy.LOGGER.info("Hello from Voluminous Energy about to server start!");
        server = event.getServer();
    }*/

    public static MinecraftServer server;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegisterEvent blockRegisteryEvent) {
            LOGGER.info("Voluminous Energy is now registering Blocks");

            //Tile Entities
//            registerBlock(blockRegisteryEvent,PRIMITIVE_BLAST_FURNACE_BLOCK, "primitiveblastfurnace");
//            registerBlock(blockRegisteryEvent,PRIMITIVE_STIRLING_GENERATOR_BLOCK, "primitivestirlinggenerator");
//            registerBlock(blockRegisteryEvent,CRUSHER_BLOCK, "crusher");
//            registerBlock(blockRegisteryEvent,ELECTROLYZER_BLOCK, "electrolyzer");
//            registerBlock(blockRegisteryEvent,CENTRIFUGAL_AGITATOR_BLOCK, "centrifugal_agitator");
//            registerBlock(blockRegisteryEvent,COMPRESSOR_BLOCK, "compressor");
//            registerBlock(blockRegisteryEvent,STIRLING_GENERATOR_BLOCK, "stirling_generator");
//            registerBlock(blockRegisteryEvent,COMBUSTION_GENERATOR_BLOCK, "combustion_generator");
//            registerBlock(blockRegisteryEvent,AQUEOULIZER_BLOCK, "aqueoulizer");
//            registerBlock(blockRegisteryEvent,AIR_COMPRESSOR_BLOCK, "air_compressor");
//            registerBlock(blockRegisteryEvent,DISTILLATION_UNIT_BLOCK, "distillation_unit");
//            registerBlock(blockRegisteryEvent,PUMP_BLOCK, "pump");
//            registerBlock(blockRegisteryEvent,GAS_FIRED_FURNACE_BLOCK, "gas_fired_furnace");
//            registerBlock(blockRegisteryEvent,ELECTRIC_FURNACE_BLOCK, "electric_furnace");
//            registerBlock(blockRegisteryEvent,BATTERY_BOX_BLOCK, "battery_box");
//            registerBlock(blockRegisteryEvent,PRIMITIVE_SOLAR_PANEL_BLOCK, "primitive_solar_panel");
//            registerBlock(blockRegisteryEvent,SOLAR_PANEL_BLOCK, "solar_panel");
//            registerBlock(blockRegisteryEvent,CENTRIFUGAL_SEPARATOR_BLOCK, "centrifugal_separator");
//            registerBlock(blockRegisteryEvent,IMPLOSION_COMPRESSOR_BLOCK, "implosion_compressor");
//            registerBlock(blockRegisteryEvent,BLAST_FURNACE_BLOCK, "blast_furnace");
//            registerBlock(blockRegisteryEvent,TOOLING_STATION_BLOCK, "tooling_station");
//            registerBlock(blockRegisteryEvent,SAWMILL_BLOCK, "sawmill");
//
//            // Tanks
//            registerBlock(blockRegisteryEvent,ALUMINUM_TANK_BLOCK, "aluminum_tank");
//            registerBlock(blockRegisteryEvent,TITANIUM_TANK_BLOCK, "titanium_tank");
//            registerBlock(blockRegisteryEvent,NETHERITE_TANK_BLOCK, "netherite_tank");
//            registerBlock(blockRegisteryEvent,NIGHALITE_TANK_BLOCK, "nighalite_tank");
//            registerBlock(blockRegisteryEvent,EIGHZO_TANK_BLOCK, "eighzo_tank");
//            registerBlock(blockRegisteryEvent,SOLARIUM_TANK_BLOCK, "solarium_tank");
//
//            // Ores
//            registerBlock(blockRegisteryEvent,SALTPETER_ORE, "saltpeterore");
//            registerBlock(blockRegisteryEvent,BAUXITE_ORE, "bauxiteore");
//            registerBlock(blockRegisteryEvent,CINNABAR_ORE, "cinnabarore");
//            registerBlock(blockRegisteryEvent,RUTILE_ORE, "rutileore");
//            registerBlock(blockRegisteryEvent,GALENA_ORE, "galena_ore");
//            registerBlock(blockRegisteryEvent,EIGHZO_ORE, "eighzo_ore");
//
//            // Deepslate ores
//            registerBlock(blockRegisteryEvent,DEEPSLATE_BAUXITE_ORE, "deepslate_bauxite_ore");
//            registerBlock(blockRegisteryEvent,DEEPSLATE_CINNABAR_ORE, "deepslate_cinnabar_ore");
//            registerBlock(blockRegisteryEvent,DEEPSLATE_RUTILE_ORE, "deepslate_rutile_ore");
//            registerBlock(blockRegisteryEvent,DEEPSLATE_GALENA_ORE, "deepslate_galena_ore");
//
//            // Red Sand ores
//            registerBlock(blockRegisteryEvent,RED_SALTPETER_ORE, "red_saltpeter_ore");
//
//            /*Shells and Machine Frames
//            registerBlock(blockRegisteryEvent,ALUMINUM_SHELL, "aluminum_shell");
//            registerBlock(blockRegisteryEvent,CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME, "carbon_shielded_aluminum_machine_frame");
//            registerBlock(blockRegisteryEvent,ALUMINUM_MACHINE_CASING_BLOCK, "aluminum_machine_casing");
//            registerBlock(blockRegisteryEvent,TITANIUM_MACHINE_CASING_BLOCK, "titanium_machine_casing");*/
//
//            //Crops
//            //registerBlock(blockRegisteryEvent,new VELandCrop(AbstractBlock.Properties.copy(Blocks.ALLIUM)));
//            registerBlock(blockRegisteryEvent,RICE_CROP,"rice_crop");
//
//            // Material Blocks
//            registerBlock(blockRegisteryEvent,SOLARIUM_BLOCK, "solarium_block");
//            registerBlock(blockRegisteryEvent,ALUMINUM_BLOCK, "aluminum_block");
//            registerBlock(blockRegisteryEvent,CARBON_BLOCK, "carbon_block");
//            registerBlock(blockRegisteryEvent,EIGHZO_BLOCK, "eighzo_block");
//            registerBlock(blockRegisteryEvent,NIGHALITE_BLOCK, "nighalite_block");
//            registerBlock(blockRegisteryEvent,SALTPETER_BLOCK, "saltpeter_block");
//            registerBlock(blockRegisteryEvent,TITANIUM_BLOCK, "titanium_block");
//            registerBlock(blockRegisteryEvent,TUNGSTEN_BLOCK, "tungsten_block");
//            registerBlock(blockRegisteryEvent,TUNGSTEN_STEEL_BLOCK, "tungsten_steel_block");
//
//            // Raw Material Blocks
//            registerBlock(blockRegisteryEvent,RAW_BAUXITE_BLOCK, "raw_bauxite_block");
//            registerBlock(blockRegisteryEvent,RAW_CINNABAR_BLOCK, "raw_cinnabar_block");
//            registerBlock(blockRegisteryEvent,RAW_EIGHZO_BLOCK, "raw_eighzo_block");
//            registerBlock(blockRegisteryEvent,RAW_GALENA_BLOCK,"raw_galena_block");
//            registerBlock(blockRegisteryEvent,RAW_RUTILE_BLOCK,"raw_rutile_block");
//            registerBlock(blockRegisteryEvent,RAW_BONE_BLOCK,"raw_bone_block");
//
//            registerBlock(blockRegisteryEvent,new PressureLadder(),"pressure_ladder");
        }
//        @SubscribeEvent
//        public static void onItemsRegistry(final RegisterEvent event) {
//            //LOGGER.info("Hello from Voluminous Energy's item registry!");
//
//            //Item Properties
//            Item.Properties properties = new Item.Properties().tab(VESetup.itemGroup);
//
//            //Block Items
//            //Tile Entities
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK,properties).setRegistryName("primitiveblastfurnace"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK,properties).setRegistryName("primitivestirlinggenerator"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CRUSHER_BLOCK,properties).setRegistryName("crusher"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ELECTROLYZER_BLOCK,properties).setRegistryName("electrolyzer"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK,properties).setRegistryName("centrifugal_agitator"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.COMPRESSOR_BLOCK,properties).setRegistryName("compressor"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.STIRLING_GENERATOR_BLOCK,properties).setRegistryName("stirling_generator"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.COMBUSTION_GENERATOR_BLOCK,properties).setRegistryName("combustion_generator"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.AQUEOULIZER_BLOCK,properties).setRegistryName("aqueoulizer"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.AIR_COMPRESSOR_BLOCK,properties).setRegistryName("air_compressor"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DISTILLATION_UNIT_BLOCK,properties).setRegistryName("distillation_unit"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PUMP_BLOCK,properties).setRegistryName("pump"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.GAS_FIRED_FURNACE_BLOCK,properties).setRegistryName("gas_fired_furnace"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ELECTRIC_FURNACE_BLOCK,properties).setRegistryName("electric_furnace"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BATTERY_BOX_BLOCK,properties).setRegistryName("battery_box"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK,properties).setRegistryName("primitive_solar_panel"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLAR_PANEL_BLOCK,properties).setRegistryName("solar_panel"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK,properties).setRegistryName("centrifugal_separator"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.IMPLOSION_COMPRESSOR_BLOCK,properties).setRegistryName("implosion_compressor"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BLAST_FURNACE_BLOCK,properties).setRegistryName("blast_furnace"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TOOLING_STATION_BLOCK,properties).setRegistryName("tooling_station"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SAWMILL_BLOCK,properties).setRegistryName("sawmill"));
//
//            // Tanks
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_TANK_BLOCK,properties).setRegistryName("aluminum_tank"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_TANK_BLOCK,properties).setRegistryName("titanium_tank"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NETHERITE_TANK_BLOCK,properties).setRegistryName("netherite_tank"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NIGHALITE_TANK_BLOCK,properties).setRegistryName("nighalite_tank"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_TANK_BLOCK,properties).setRegistryName("eighzo_tank"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLARIUM_TANK_BLOCK,properties).setRegistryName("solarium_tank"));
//
//            // Crops
//            //itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.LAND_CROP,properties).setRegistryName("land_crop"));
//            itemRegisteryEvent.getRegistry().register(VEItems.RICE_GRAIN);
//
//            //True Blocks
//            //Ores
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_ORE,properties).setRegistryName("saltpeterore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BAUXITE_ORE,properties).setRegistryName("bauxiteore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CINNABAR_ORE,properties).setRegistryName("cinnabarore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RUTILE_ORE,properties).setRegistryName("rutileore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.GALENA_ORE,properties).setRegistryName("galena_ore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_ORE,properties).setRegistryName("eighzo_ore"));
//
//            //Deepslate ores
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_BAUXITE_ORE,properties).setRegistryName("deepslate_bauxite_ore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_CINNABAR_ORE,properties).setRegistryName("deepslate_cinnabar_ore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_RUTILE_ORE,properties).setRegistryName("deepslate_rutile_ore"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_GALENA_ORE,properties).setRegistryName("deepslate_galena_ore"));
//
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RED_SALTPETER_ORE,properties).setRegistryName("red_saltpeter_ore"));
//
//            //Shells
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_SHELL, properties).setRegistryName("aluminum_shell"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME, properties).setRegistryName("carbon_shielded_aluminum_machine_frame"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK, properties).setRegistryName("aluminum_machine_casing"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK, properties).setRegistryName("titanium_machine_casing"));
//
//            // Raw Material Storage Blocks
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_BAUXITE_BLOCK, properties).setRegistryName("raw_bauxite_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_CINNABAR_BLOCK, properties).setRegistryName("raw_cinnabar_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_EIGHZO_BLOCK, properties).setRegistryName("raw_eighzo_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_GALENA_BLOCK, properties).setRegistryName("raw_galena_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_RUTILE_BLOCK, properties).setRegistryName("raw_rutile_block"));
//            //itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_BONE_BLOCK, properties).setRegistryName("raw_bone_block"));
//
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRESSURE_LADDER, properties).setRegistryName("pressure_ladder"));
//
//            // Material Storage Blocks
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLARIUM_BLOCK,properties).setRegistryName("solarium_block"));
//            /*itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_BLOCK,properties).setRegistryName("aluminum_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CARBON_BLOCK,properties).setRegistryName("carbon_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_BLOCK,properties).setRegistryName("eighzo_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NIGHALITE_BLOCK,properties).setRegistryName("nighalite_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_BLOCK,properties).setRegistryName("saltpeter_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_BLOCK,properties).setRegistryName("titanium_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TUNGSTEN_BLOCK,properties).setRegistryName("tungsten_block"));
//            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TUNGSTEN_STEEL_BLOCK,properties).setRegistryName("tungsten_steel_block"));
//            */
//            //True Items
//            itemRegisteryEvent.getRegistry().register(VEItems.PETCOKE);
//            itemRegisteryEvent.getRegistry().register(VEItems.COALCOKE);
//            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERCHUNK);
//            itemRegisteryEvent.getRegistry().register(VEItems.SILICON);
//            itemRegisteryEvent.getRegistry().register(VEItems.SHREDDED_BIOMASS);
//            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_SAWBLADE);
//
//            //Dusts
//            itemRegisteryEvent.getRegistry().register(VEItems.COALDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.COKEDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.LAPISDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SULFURDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.CARBONDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERDUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.BAUXITE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.CINNABAR_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.IRON_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.QUARTZ_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SAND_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SOULSAND_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.RUTILE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.GALENA_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.PHOTOVOLTAIC_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.END_STONE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.BASALT_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.FLINT_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.NETHERRACK_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.NETHERITE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.EIGHZO_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.COPPER_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.COPPER_CARBONATE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.CUPRIC_OXIDE_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.SAW_DUST);
//            itemRegisteryEvent.getRegistry().register(VEItems.ROSIN);
//            itemRegisteryEvent.getRegistry().register(VEItems.STANDARD_TANK_FRAME);
//            itemRegisteryEvent.getRegistry().register(VEItems.ROBUST_TANK_FRAME);
//            itemRegisteryEvent.getRegistry().register(VEItems.IMPECCABLE_TANK_FRAME);
//
//            //Raw ore
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_BAUXITE);
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_CINNABAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_GALENA);
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_RUTILE);
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_SULFUR);
//            itemRegisteryEvent.getRegistry().register(VEItems.RAW_EIGHZO);
//
//            //Ingots and bricks
//            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_BRICK);
//            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.STEEL_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_STEEL_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.NIGHALITE_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.EIGHZO_INGOT);
//            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_INGOT);
//
//            //Gears
//            itemRegisteryEvent.getRegistry().register(VEItems.STONE_GEAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.IRON_GEAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_GEAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_GEAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_GEAR);
//            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_GEAR);
//
//            //Plates
//            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_PLATE);
//            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_PLATE);
//            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_PLATE);
//            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_PLATE);
//
//            //Microchips
//            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_MICROCHIP);
//            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_MICROCHIP);
//
//            //Upgrades
//            itemRegisteryEvent.getRegistry().register(VEItems.QUARTZ_MULTIPLIER);
//
//            //Batteries
//            itemRegisteryEvent.getRegistry().register(VEItems.MERCURY_BATTERY);
//            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_ACID_BATTERY);
//
//            //Tiny Fuels
//            itemRegisteryEvent.getRegistry().register(VEItems.TINY_CHARCOAL);
//            itemRegisteryEvent.getRegistry().register(VEItems.TINY_COAL);
//            itemRegisteryEvent.getRegistry().register(VEItems.TINY_COAL_COKE);
//            itemRegisteryEvent.getRegistry().register(VEItems.TINY_PETCOKE);
//            itemRegisteryEvent.getRegistry().register(VEItems.TINY_ROSIN);
//
//            // TOOLS
//
//            // Pickaxe
//            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.CARBON_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_PICKAXE);
//            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_PICKAXE);
//
//            // Axe
//            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.CARBON_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_AXE);
//            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_AXE);
//
//            // Shovel
//            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.CARBON_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_SHOVEL);
//            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_SHOVEL);
//
//            // Hoe
//            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.CARBON_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_HOE);
//            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_HOE);
//
//            // Sword
//            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.CARBON_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_SWORD);
//            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_SWORD);
//
//            // Foods
//            itemRegisteryEvent.getRegistry().register(VEItems.COOKED_RICE);
//
//            // Register Multitools
//            multitoolItemRegistry(itemRegisteryEvent);
//        }

//        public static void multitoolItemRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent){
//            /*
//             *    MULTITOOLS
//             */
//
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EMPTY_MULTITOOL);
//
//            // Iron
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_MULTITOOL);
//
//            // Diamond
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_TRIMMER_MULTITOOL);
//
//            // Titanium
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_TRIMMER_MULTITOOL);
//
//            // Nighalite
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_TRIMMER_MULTITOOL);
//
//            // Eighzo
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_TRIMMER_MULTITOOL);
//
//            // Solarium
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_DRILL_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_CHAIN_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_SCOOPER_MULTITOOL);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_TRIMMER_MULTITOOL);
//
//            /*
//             *    BITS
//             */
//
//            // Iron
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_BIT);
//
//            // Diamond
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_TRIMMER_BIT);
//
//            // Titanium
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_TRIMMER_BIT);
//
//            // Nighalite
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_TRIMMER_BIT);
//
//            // Eighzo
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_TRIMMER_BIT);
//
//            // Solarium
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_DRILL_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_CHAIN_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_SCOOPER_BIT);
//            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_TRIMMER_BIT);
//        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegisterEvent event) {
            //LOGGER.info("Hello from Voluminous Energy's tile entity registry!");
//
//
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "primitiveblastfurnace"), VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"primitivestirlinggenerator"), VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"crusher"), VEBlocks.CRUSHER_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"electrolyzer"), VEBlocks.ELECTROLYZER_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"centrifugal_agitator"), VEBlocks.CENTRIFUGAL_AGITATOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"compressor"), VEBlocks.COMPRESSOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"stirling_generator"), VEBlocks.STIRLING_GENERATOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"combustion_generator"), VEBlocks.COMBUSTION_GENERATOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"aqueoulizer"), VEBlocks.AQUEOULIZER_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"air_compressor"), VEBlocks.AIR_COMPRESSOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"distillation_unit"), VEBlocks.DISTILLATION_UNIT_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"pump"), VEBlocks.PUMP_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"gas_fired_furnace"), VEBlocks.GAS_FIRED_FURNACE_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"electric_furnace"), VEBlocks.ELECTRIC_FURNACE_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"battery_box"), VEBlocks.BATTERY_BOX_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"primitive_solar_panel"), VEBlocks.PRIMITIVE_SOLAR_PANEL_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"solar_panel"), VEBlocks.SOLAR_PANEL_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"centrifugal_separator"), VEBlocks.CENTRIFUGAL_SEPARATOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"implosion_compressor"), VEBlocks.IMPLOSION_COMPRESSOR_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"blast_furnace"), VEBlocks.BLAST_FURNACE_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"tooling_station"), VEBlocks.TOOLING_STATION_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"sawmill"), VEBlocks.SAWMILL_TILE));
//
//            // Tanks
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"aluminum_tank"), VEBlocks.ALUMINUM_TANK_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"titanium_tank"), VEBlocks.TITANIUM_TANK_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"netherite_tank"), VEBlocks.NETHERITE_TANK_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"nighalite_tank"), VEBlocks.NIGHALITE_TANK_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"eighzo_tank"), VEBlocks.EIGHZO_TANK_TILE));
//            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID,"solarium_tank"), VEBlocks.SOLARIUM_TANK_TILE));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegisterEvent event){
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "primitiveblastfurnace"), VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "primitivestirlinggenerator"), VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "crusher"), VEBlocks.CRUSHER_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "electrolyzer"), VEBlocks.ELECTROLYZER_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "centrifugal_agitator"), VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "compressor"), VEBlocks.COMPRESSOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "stirling_generator"), VEBlocks.STIRLING_GENERATOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "combustion_generator"), VEBlocks.COMBUSTION_GENERATOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "aqueoulizer"), VEBlocks.AQUEOULIZER_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "air_compressor"), VEBlocks.AIR_COMPRESSOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "distillation_unit"), VEBlocks.DISTILLATION_UNIT_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "pump"), VEBlocks.PUMP_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "gas_fired_furnace"), VEBlocks.GAS_FIRED_FURNACE_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "electric_furnace"), VEBlocks.ELECTRIC_FURNACE_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "battery_box"), VEBlocks.BATTERY_BOX_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "primitive_solar_panel"), VEBlocks.PRIMITIVE_SOLAR_PANEL_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "solar_panel"), VEBlocks.SOLAR_PANEL_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "centrifugal_separator"), VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "implosion_compressor"), VEBlocks.IMPLOSION_COMPRESSOR_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "blast_furnace"), VEBlocks.BLAST_FURNACE_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "tooling_station"), VEBlocks.TOOLING_STATION_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "sawmill"), VEBlocks.SAWMILL_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "aluminum_tank"), VEBlocks.ALUMINUM_TANK_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "titanium_tank"), VEBlocks.TITANIUM_TANK_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "netherite_tank"), VEBlocks.NETHERITE_TANK_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "nighalite_tank"), VEBlocks.NIGHALITE_TANK_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "eighzo_tank"), VEBlocks.EIGHZO_TANK_CONTAINER));
//
//            event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper ->
//                    helper.register(new ResourceLocation(VoluminousEnergy.MODID, "solarium_tank"), VEBlocks.SOLARIUM_TANK_CONTAINER));
        }

        //@SubscribeEvent
        //public static void onRegisterBiome(RegistryEvent.Register<Biome> event) {
        //VEBiomes.prepareRegistration(event, new RedDesert(), BiomeManager.BiomeType.DESERT, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        //VEBiomes.prepareRegistration(event, new ShallowWarmOcean(), BiomeManager.BiomeType.WARM, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WATER, BiomeDictionary.Type.WET);
        //}

        /*@SubscribeEvent
        public static void onRegisterSurfaceBuilder(RegistryEvent.Register<SurfaceBuilder<?>> event){
            VESurfaceBuilders.init();
            VESurfaceBuilders.surfaceBuilders.forEach(surfaceBuilder -> event.getRegistry().register(surfaceBuilder));
        }*/


        @SubscribeEvent
        public static void onRegisterFeature(RegisterEvent event) { // REGISTER STRAIGHT UP FEATURES HERE
            // Straight up Features

//            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
//                    new ResourceLocation(VoluminousEnergy.MODID, "ve_bsc_lake_feature"),VEFeatures.VE_BSC_LAKE_FEATURE));
//
//            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
//                    new ResourceLocation(VoluminousEnergy.MODID, "ve_bsc_surface_lake_feature"),VEFeatures.VE_BSC_LAKE_SURFACE_FEATURE));
//
//
//            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
//                    new ResourceLocation(VoluminousEnergy.MODID, "ve_bsc_underground_lakes_feature"),VEFeatures.VE_BSC_LAKE_UNDERGROUND_FEATURE));
//
//
//            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
//                    new ResourceLocation(VoluminousEnergy.MODID, "ve_geyser_feature"),VEFeatures.VE_GEYSER_FEATURE));


            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
                    new ResourceLocation(VoluminousEnergy.MODID, "ve_rice_feature"),VEFeatures.VE_RICE_FEATURE));


            event.register(ForgeRegistries.Keys.FEATURES, helper -> helper.register(
                    new ResourceLocation(VoluminousEnergy.MODID, "ve_ore_deposit_feature"),VEFeatures.VE_ORE_DEPOSIT_FEATURE));
        }
    }

    public static void builtinRegisterConfiguredFeatures(){
        // Ore Blobs
        builtinConfiguredFeaturesRegistry(VEOres.EIGHZO_ORE_BLOB, "eighzo_ore_blob");
        builtinConfiguredFeaturesRegistry(VEOres.SALTPETER_ORE_BLOB, "saltpeter_ore_blob");
        builtinConfiguredFeaturesRegistry(VEOres.BAUXITE_ORE_BLOB, "bauxite_ore_blob");
        builtinConfiguredFeaturesRegistry(VEOres.CINNABAR_ORE_BLOB, "cinnabar_ore_blob");
        builtinConfiguredFeaturesRegistry(VEOres.RUTILE_ORE_BLOB, "rutile_ore_blob");
        builtinConfiguredFeaturesRegistry(VEOres.GALENA_ORE_BLOB, "galena_ore_blob");

        // Oil
//        builtinConfiguredFeaturesRegistry(VEFeatures.SURFACE_OIL_LAKE_FEATURE, "oil_lake");
//        builtinConfiguredFeaturesRegistry(VEFeatures.OIL_GEYSER_FEATURE, "oil_geyser");

        // Rice
        builtinConfiguredFeaturesRegistry(VEFeatures.RICE_FEATURE_CONFIG, "rice_crop");

        // Ore Deposits
        builtinConfiguredFeaturesRegistry(VEFeatures.COPPER_ORE_DEPOSIT_CONFIG, "copper_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.IRON_ORE_DEPOSIT_CONFIG, "iron_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.GOLD_ORE_DEPOSIT_CONFIG, "gold_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.BAUXITE_ORE_DEPOSIT_CONFIG, "bauxite_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.CINNABAR_ORE_DEPOSIT_CONFIG, "cinnabar_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.GALENA_ORE_DEPOSIT_CONFIG, "galena_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.RUTILE_ORE_DEPOSIT_CONFIG, "rutile_ore_deposit");
        builtinConfiguredFeaturesRegistry(VEFeatures.EIGHZO_ORE_DEPOSIT_CONFIG, "eighzo_ore_deposit");
    }

    public static void builtinRegisterPlacedFeatures(){
        // Ore Blobs
        builtinFeaturePlacementRegistry(VEOres.EIGHZO_ORE_BLOB_PLACEMENT, "eighzo_ore_blob");
        builtinFeaturePlacementRegistry(VEOres.SALTPETER_ORE_BLOB_PLACEMENT, "saltpeter_ore_blob");
        builtinFeaturePlacementRegistry(VEOres.BAUXITE_ORE_BLOB_PLACEMENT, "bauxite_ore_blob");
        builtinFeaturePlacementRegistry(VEOres.CINNABAR_ORE_BLOB_PLACEMENT, "cinnabar_ore_blob");
        builtinFeaturePlacementRegistry(VEOres.RUTILE_ORE_BLOB_PLACEMENT, "rutile_ore_blob");
        builtinFeaturePlacementRegistry(VEOres.GALENA_ORE_BLOB_PLACEMENT, "galena_ore_blob");

        // Oil
//        builtinFeaturePlacementRegistry(VEFeatures.SURFACE_OIL_LAKE_PLACEMENT, "surface_oil_lake");
//        builtinFeaturePlacementRegistry(VEFeatures.UNDERGROUND_OIL_LAKE_PLACEMENT, "underground_oil_lake");
//        builtinFeaturePlacementRegistry(VEFeatures.OIL_GEYSER_PLACEMENT, "oil_geyser");

        // Rice
        builtinFeaturePlacementRegistry(VEFeatures.RICE_FEATURE_PLACEMENT, "rice_crop");

        // Ore Deposits
        builtinFeaturePlacementRegistry(VEFeatures.COPPER_ORE_DEPOSIT_PLACEMENT, "copper_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.IRON_ORE_DEPOSIT_PLACEMENT, "iron_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.GOLD_ORE_DEPOSIT_PLACEMENT, "gold_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.BAUXITE_ORE_DEPOSIT_PLACEMENT, "bauxite_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.CINNABAR_ORE_DEPOSIT_PLACEMENT, "cinnabar_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.GALENA_ORE_DEPOSIT_PLACEMENT, "galena_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.RUTILE_ORE_DEPOSIT_PLACEMENT, "rutile_ore_deposit");
        builtinFeaturePlacementRegistry(VEFeatures.EIGHZO_ORE_DEPOSIT_PLACEMENT, "eighzo_ore_deposit");
    }

    public static void builtinConfiguredFeaturesRegistry(ConfiguredFeature<?,?> configuredFeature, String setRegistryName){
        Registry<ConfiguredFeature<?,?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;
        Registry.register(registry, new ResourceLocation(VoluminousEnergy.MODID, setRegistryName), configuredFeature);
    }

    public static void builtinFeaturePlacementRegistry(PlacedFeature placedFeature, String setRegistryName){
        Registry<PlacedFeature> registry = BuiltinRegistries.PLACED_FEATURE;
        Registry.register(registry, new ResourceLocation(VoluminousEnergy.MODID, setRegistryName), placedFeature);
    }

    @Mod.EventBusSubscriber(modid = VoluminousEnergy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientRegister {

        @SubscribeEvent
        public static void RegisterClientOnSetupEvent(FMLClientSetupEvent event){
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.RICE_CROP.get(), RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.SAWMILL_BLOCK.get(), RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.PRESSURE_LADDER.get(), RenderType.cutout()));
        }

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class OnDatagenEvent {

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event){
            DataGenerator dataGenerator = event.getGenerator();

            if(event.includeServer()) {
                dataGenerator.addProvider(true, new VETagDataGenerator(dataGenerator, event.getExistingFileHelper()));
            }
        }
    }
}
