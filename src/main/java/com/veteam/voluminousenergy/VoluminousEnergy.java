package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.blocks.*;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.blocks.storage.materials.*;
import com.veteam.voluminousenergy.blocks.blocks.storage.raw.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBits;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.world.VEFeatureGeneration;
import com.veteam.voluminousenergy.world.biomes.RedDesert;
import com.veteam.voluminousenergy.world.biomes.ShallowWarmOcean;
import com.veteam.voluminousenergy.world.biomes.VEBiomes;
import com.veteam.voluminousenergy.world.ores.VEOreGeneration;
import com.veteam.voluminousenergy.world.surfaceBulider.VESurfaceBuilders;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VoluminousEnergy.MODID)
public class VoluminousEnergy {
    public static final String MODID = "voluminousenergy";

    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static VESetup setup = new VESetup();

    public static final Logger LOGGER = LogManager.getLogger();

    public VoluminousEnergy() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupWhenLoadingComplete);
        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register recipes (deferred)
        VERecipes.RECIPE_SERIALIZERS.register(modEventBus);
        // Register fluids and respective items/blocks (differed)
        VEFluids.VE_FLUIDS.register(modEventBus);
        VEFluids.VE_FLUID_BLOCKS.register(modEventBus);
        VEFluids.VE_FLUID_ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEOreGeneration::OreGeneration);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEFeatureGeneration::addFeaturesToBiomes);

        // Config Files to load
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-common.toml"));
    }

    private void setup(final FMLCommonSetupEvent event) {
        //VEOreGeneration.OreGeneration(); // Setup custom ore generation
        //VEFeatureGeneration.VEFeatureGenerationSetup(); // Setup feature generation
        setup.init();
        proxy.init();
        VENetwork.init();
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

    @SubscribeEvent
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        //VoluminousEnergy.LOGGER.info("Hello from Voluminous Energy about to server start!");
        server = event.getServer();
    }

    public static MinecraftServer server;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegisteryEvent) {
            LOGGER.info("Hello from Voluminous Energy's block registry!");
            //Tile Entities
            blockRegisteryEvent.getRegistry().register(new PrimitiveBlastFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new PrimitiveStirlingGeneratorBlock());
            blockRegisteryEvent.getRegistry().register(new CrusherBlock());
            blockRegisteryEvent.getRegistry().register(new ElectrolyzerBlock());
            blockRegisteryEvent.getRegistry().register(new CentrifugalAgitatorBlock());
            blockRegisteryEvent.getRegistry().register(new CompressorBlock());
            blockRegisteryEvent.getRegistry().register(new StirlingGeneratorBlock());
            blockRegisteryEvent.getRegistry().register(new CombustionGeneratorBlock());
            blockRegisteryEvent.getRegistry().register(new AqueoulizerBlock());
            blockRegisteryEvent.getRegistry().register(new AirCompressorBlock());
            blockRegisteryEvent.getRegistry().register(new DistillationUnitBlock());
            blockRegisteryEvent.getRegistry().register(new PumpBlock());
            blockRegisteryEvent.getRegistry().register(new GasFiredFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new ElectricFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new BatteryBoxBlock());
            blockRegisteryEvent.getRegistry().register(new PrimitiveSolarPanelBlock());
            blockRegisteryEvent.getRegistry().register(new SolarPanelBlock());
            blockRegisteryEvent.getRegistry().register(new CentrifugalSeparatorBlock());
            blockRegisteryEvent.getRegistry().register(new ImplosionCompressorBlock());
            blockRegisteryEvent.getRegistry().register(new BlastFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new ToolingStationBlock());
            blockRegisteryEvent.getRegistry().register(new SawmillBlock());

            //Ores
            blockRegisteryEvent.getRegistry().register(new SaltpeterOre());
            blockRegisteryEvent.getRegistry().register(new BauxiteOre());
            blockRegisteryEvent.getRegistry().register(new CinnabarOre());
            blockRegisteryEvent.getRegistry().register(new RutileOre());
            blockRegisteryEvent.getRegistry().register(new GalenaOre());
            blockRegisteryEvent.getRegistry().register(new EighzoOre());

            //Shells and Machine Frames
            blockRegisteryEvent.getRegistry().register(new AluminumShellBlock());
            blockRegisteryEvent.getRegistry().register(new CarbonShieldedAluminumMachineFrame());
            blockRegisteryEvent.getRegistry().register(new AluminumMachineCasingBlock());
            blockRegisteryEvent.getRegistry().register(new TitaniumMachineCasingBlock());

            //Crops
            //blockRegisteryEvent.getRegistry().register(new VELandCrop(AbstractBlock.Properties.copy(Blocks.ALLIUM)));
            blockRegisteryEvent.getRegistry().register(new RiceCrop(BlockBehaviour.Properties.copy(Blocks.ALLIUM))); // TODO: better properties

            // Material Blocks
            blockRegisteryEvent.getRegistry().register(new SolariumBlock());
            blockRegisteryEvent.getRegistry().register(new AluminumBlock());
            blockRegisteryEvent.getRegistry().register(new CarbonBlock());
            blockRegisteryEvent.getRegistry().register(new EighzoBlock());
            blockRegisteryEvent.getRegistry().register(new NighaliteBlock());
            blockRegisteryEvent.getRegistry().register(new SaltpeterBlock());
            blockRegisteryEvent.getRegistry().register(new TitaniumBlock());
            blockRegisteryEvent.getRegistry().register(new TungstenBlock());
            blockRegisteryEvent.getRegistry().register(new TungstenSteelBlock());

            // Raw Material Blocks
            blockRegisteryEvent.getRegistry().register(new RawBauxiteBlock());
            blockRegisteryEvent.getRegistry().register(new RawCinnabarBlock());
            blockRegisteryEvent.getRegistry().register(new RawEighzoBlock());
            blockRegisteryEvent.getRegistry().register(new RawGalenaBlock());
            blockRegisteryEvent.getRegistry().register(new RawRutileBlock());
            blockRegisteryEvent.getRegistry().register(new RawBoneBlock());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent) {
            //LOGGER.info("Hello from Voluminous Energy's item registry!");

            //Item Properties
            Item.Properties properties = new Item.Properties().tab(VESetup.itemGroup);

            //Block Items
            //Tile Entities
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK,properties).setRegistryName("primitiveblastfurnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK,properties).setRegistryName("primitivestirlinggenerator"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CRUSHER_BLOCK,properties).setRegistryName("crusher"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ELECTROLYZER_BLOCK,properties).setRegistryName("electrolyzer"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK,properties).setRegistryName("centrifugal_agitator"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.COMPRESSOR_BLOCK,properties).setRegistryName("compressor"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.STIRLING_GENERATOR_BLOCK,properties).setRegistryName("stirling_generator"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.COMBUSTION_GENERATOR_BLOCK,properties).setRegistryName("combustion_generator"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.AQUEOULIZER_BLOCK,properties).setRegistryName("aqueoulizer"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.AIR_COMPRESSOR_BLOCK,properties).setRegistryName("air_compressor"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DISTILLATION_UNIT_BLOCK,properties).setRegistryName("distillation_unit"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PUMP_BLOCK,properties).setRegistryName("pump"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.GAS_FIRED_FURNACE_BLOCK,properties).setRegistryName("gas_fired_furnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ELECTRIC_FURNACE_BLOCK,properties).setRegistryName("electric_furnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BATTERY_BOX_BLOCK,properties).setRegistryName("battery_box"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK,properties).setRegistryName("primitive_solar_panel"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLAR_PANEL_BLOCK,properties).setRegistryName("solar_panel"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK,properties).setRegistryName("centrifugal_separator"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.IMPLOSION_COMPRESSOR_BLOCK,properties).setRegistryName("implosion_compressor"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BLAST_FURNACE_BLOCK,properties).setRegistryName("blast_furnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TOOLING_STATION_BLOCK,properties).setRegistryName("tooling_station"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SAWMILL_BLOCK,properties).setRegistryName("sawmill"));

            // Crops
            //itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.LAND_CROP,properties).setRegistryName("land_crop"));
            itemRegisteryEvent.getRegistry().register(VEItems.RICE_GRAIN);

            //True Blocks
            //Ores
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_ORE,properties).setRegistryName("saltpeterore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BAUXITE_ORE,properties).setRegistryName("bauxiteore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CINNABAR_ORE,properties).setRegistryName("cinnabarore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RUTILE_ORE,properties).setRegistryName("rutileore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.GALENA_ORE,properties).setRegistryName("galena_ore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_ORE,properties).setRegistryName("eighzo_ore"));

            //Shells
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_SHELL, properties).setRegistryName("aluminum_shell"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME, properties).setRegistryName("carbon_shielded_aluminum_machine_frame"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK, properties).setRegistryName("aluminum_machine_casing"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK, properties).setRegistryName("titanium_machine_casing"));

            // Raw Material Storage Blocks
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_BAUXITE_BLOCK, properties).setRegistryName("raw_bauxite_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_CINNABAR_BLOCK, properties).setRegistryName("raw_cinnabar_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_EIGHZO_BLOCK, properties).setRegistryName("raw_eighzo_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_GALENA_BLOCK, properties).setRegistryName("raw_galena_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_RUTILE_BLOCK, properties).setRegistryName("raw_rutile_block"));
            //itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RAW_BONE_BLOCK, properties).setRegistryName("raw_bone_block"));


            // Material Storage Blocks
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLARIUM_BLOCK,properties).setRegistryName("solarium_block"));
            /*itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_BLOCK,properties).setRegistryName("aluminum_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CARBON_BLOCK,properties).setRegistryName("carbon_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_BLOCK,properties).setRegistryName("eighzo_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NIGHALITE_BLOCK,properties).setRegistryName("nighalite_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_BLOCK,properties).setRegistryName("saltpeter_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_BLOCK,properties).setRegistryName("titanium_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TUNGSTEN_BLOCK,properties).setRegistryName("tungsten_block"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TUNGSTEN_STEEL_BLOCK,properties).setRegistryName("tungsten_steel_block"));
            */
            //True Items
            itemRegisteryEvent.getRegistry().register(VEItems.PETCOKE);
            itemRegisteryEvent.getRegistry().register(VEItems.COALCOKE);
            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERCHUNK);
            itemRegisteryEvent.getRegistry().register(VEItems.SILICON);
            itemRegisteryEvent.getRegistry().register(VEItems.SHREDDED_BIOMASS);

            //Dusts
            itemRegisteryEvent.getRegistry().register(VEItems.COALDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.COKEDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.LAPISDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SULFURDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBONDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.BAUXITE_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.CINNABAR_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.IRON_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.QUARTZ_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SAND_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SOULSAND_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.RUTILE_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.GALENA_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.PHOTOVOLTAIC_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.END_STONE_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.BASALT_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.FLINT_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.NETHERRACK_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.NETHERITE_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.EIGHZO_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.COPPER_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.COPPER_CARBONATE_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.CUPRIC_OXIDE_DUST);

            //Raw ore
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_BAUXITE);
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_CINNABAR);
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_GALENA);
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_RUTILE);
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_SULFUR);
            itemRegisteryEvent.getRegistry().register(VEItems.RAW_EIGHZO);

            //Ingots and bricks
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_BRICK);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.STEEL_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.TUNGSTEN_STEEL_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.NIGHALITE_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.EIGHZO_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_INGOT);

            //Gears
            itemRegisteryEvent.getRegistry().register(VEItems.STONE_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.IRON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_GEAR);

            //Plates
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_PLATE);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_PLATE);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_PLATE);
            itemRegisteryEvent.getRegistry().register(VEItems.SOLARIUM_PLATE);

            //Microchips
            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_MICROCHIP);
            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_MICROCHIP);

            //Upgrades
            itemRegisteryEvent.getRegistry().register(VEItems.QUARTZ_MULTIPLIER);

            //Batteries
            itemRegisteryEvent.getRegistry().register(VEItems.MERCURY_BATTERY);
            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_ACID_BATTERY);

            // TOOLS

            // Pickaxe
            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.CARBON_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_PICKAXE);
            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_PICKAXE);

            // Axe
            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.CARBON_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_AXE);
            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_AXE);

            // Shovel
            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.CARBON_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_SHOVEL);
            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_SHOVEL);

            // Hoe
            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.CARBON_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_HOE);
            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_HOE);

            // Sword
            itemRegisteryEvent.getRegistry().register(VETools.ALUMINUM_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.CARBON_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.TITANIUM_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.TUNGSTEN_STEEL_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.NIGHALITE_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.EIGHZO_SWORD);
            itemRegisteryEvent.getRegistry().register(VETools.SOLARIUM_SWORD);

            // Foods
            itemRegisteryEvent.getRegistry().register(VEItems.COOKED_RICE);

            itemRegisteryEvent.getRegistry().register(VEMultitools.EMPTY_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_MULTITOOL);

            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_BIT);
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<BlockEntityType<?>> event) {
            //LOGGER.info("Hello from Voluminous Energy's tile entity registry!");
            event.getRegistry().register(BlockEntityType.Builder.of(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null).setRegistryName("primitiveblastfurnace"));
            event.getRegistry().register(BlockEntityType.Builder.of(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("primitivestirlinggenerator"));
            event.getRegistry().register(BlockEntityType.Builder.of(CrusherTile::new,VEBlocks.CRUSHER_BLOCK).build(null).setRegistryName("crusher"));
            event.getRegistry().register(BlockEntityType.Builder.of(ElectrolyzerTile::new,VEBlocks.ELECTROLYZER_BLOCK).build(null).setRegistryName("electrolyzer"));
            event.getRegistry().register(BlockEntityType.Builder.of(CentrifugalAgitatorTile::new,VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK).build(null).setRegistryName("centrifugal_agitator"));
            event.getRegistry().register(BlockEntityType.Builder.of(CompressorTile::new,VEBlocks.COMPRESSOR_BLOCK).build(null).setRegistryName("compressor"));
            event.getRegistry().register(BlockEntityType.Builder.of(StirlingGeneratorTile::new,VEBlocks.STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("stirling_generator"));
            event.getRegistry().register(BlockEntityType.Builder.of(CombustionGeneratorTile::new,VEBlocks.COMBUSTION_GENERATOR_BLOCK).build(null).setRegistryName("combustion_generator"));
            event.getRegistry().register(BlockEntityType.Builder.of(AqueoulizerTile::new,VEBlocks.AQUEOULIZER_BLOCK).build(null).setRegistryName("aqueoulizer"));
            event.getRegistry().register(BlockEntityType.Builder.of(AirCompressorTile::new,VEBlocks.AIR_COMPRESSOR_BLOCK).build(null).setRegistryName("air_compressor"));
            event.getRegistry().register(BlockEntityType.Builder.of(DistillationUnitTile::new,VEBlocks.DISTILLATION_UNIT_BLOCK).build(null).setRegistryName("distillation_unit"));
            event.getRegistry().register(BlockEntityType.Builder.of(PumpTile::new,VEBlocks.PUMP_BLOCK).build(null).setRegistryName("pump"));
            event.getRegistry().register(BlockEntityType.Builder.of(GasFiredFurnaceTile::new,VEBlocks.GAS_FIRED_FURNACE_BLOCK).build(null).setRegistryName("gas_fired_furnace"));
            event.getRegistry().register(BlockEntityType.Builder.of(ElectricFurnaceTile::new,VEBlocks.ELECTRIC_FURNACE_BLOCK).build(null).setRegistryName("electric_furnace"));
            event.getRegistry().register(BlockEntityType.Builder.of(BatteryBoxTile::new,VEBlocks.BATTERY_BOX_BLOCK).build(null).setRegistryName("battery_box"));
            event.getRegistry().register(BlockEntityType.Builder.of(PrimitiveSolarPanelTile::new,VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK).build(null).setRegistryName("primitive_solar_panel"));
            event.getRegistry().register(BlockEntityType.Builder.of(SolarPanelTile::new,VEBlocks.SOLAR_PANEL_BLOCK).build(null).setRegistryName("solar_panel"));
            event.getRegistry().register(BlockEntityType.Builder.of(CentrifugalSeparatorTile::new,VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK).build(null).setRegistryName("centrifugal_separator"));
            event.getRegistry().register(BlockEntityType.Builder.of(ImplosionCompressorTile::new,VEBlocks.IMPLOSION_COMPRESSOR_BLOCK).build(null).setRegistryName("implosion_compressor"));
            event.getRegistry().register(BlockEntityType.Builder.of(BlastFurnaceTile::new,VEBlocks.BLAST_FURNACE_BLOCK).build(null).setRegistryName("blast_furnace"));
            event.getRegistry().register(BlockEntityType.Builder.of(ToolingStationTile::new,VEBlocks.TOOLING_STATION_BLOCK).build(null).setRegistryName("tooling_station"));
            event.getRegistry().register(BlockEntityType.Builder.of(SawmillTile::new,VEBlocks.SAWMILL_BLOCK).build(null).setRegistryName("sawmill"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<MenuType<?>> event){

            event.getRegistry().register(IForgeContainerType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveBlastFurnaceContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitiveblastfurnace"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveStirlingGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitivestirlinggenerator"));

            event.getRegistry().register(IForgeContainerType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CrusherContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("crusher"));

            event.getRegistry().register(IForgeContainerType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectrolyzerContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("electrolyzer"));

            event.getRegistry().register(IForgeContainerType.create((id, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalAgitatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("centrifugal_agitator"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("compressor"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new StirlingGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("stirling_generator"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CombustionGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("combustion_generator"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new AqueoulizerContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("aqueoulizer"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new AirCompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("air_compressor"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new DistillationUnitContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("distillation_unit"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PumpContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("pump"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new GasFiredFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("gas_fired_furnace"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ElectricFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("electric_furnace"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new BatteryBoxContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("battery_box"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveSolarPanelContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitive_solar_panel"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SolarPanelContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("solar_panel"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalSeparatorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("centrifugal_separator"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ImplosionCompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("implosion_compressor"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new BlastFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("blast_furnace"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ToolingStationContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("tooling_station"));

            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SawmillContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("sawmill"));
        }

        @SubscribeEvent
        public static void onRegisterBiome(RegistryEvent.Register<Biome> event) {
            VEBiomes.prepareRegistration(event, new RedDesert(), BiomeManager.BiomeType.DESERT, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
            //VEBiomes.prepareRegistration(event, new ShallowWarmOcean(), BiomeManager.BiomeType.WARM, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WATER, BiomeDictionary.Type.WET);
        }

        @SubscribeEvent
        public static void onRegisterSurfaceBuilder(RegistryEvent.Register<SurfaceBuilder<?>> event){
            VESurfaceBuilders.init();
            VESurfaceBuilders.surfaceBuilders.forEach(surfaceBuilder -> event.getRegistry().register(surfaceBuilder));
        }
    }

    @Mod.EventBusSubscriber(modid = VoluminousEnergy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientRegister {

        @SubscribeEvent
        public static void RegisterClientOnSetupEvent(FMLClientSetupEvent event){
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.RICE_CROP, RenderType.cutout()));
        }

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class OnDatagenEvent {

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event){
            DataGenerator dataGenerator = event.getGenerator();

            if(event.includeServer()) {
                dataGenerator.addProvider(new VETagDataGenerator(dataGenerator, event.getExistingFileHelper()));
            }
        }
    }
}
