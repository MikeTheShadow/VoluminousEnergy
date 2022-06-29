package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.crops.RiceCrop;
import com.veteam.voluminousenergy.blocks.blocks.machines.*;
import com.veteam.voluminousenergy.blocks.blocks.machines.tanks.*;
import com.veteam.voluminousenergy.blocks.blocks.multiblocks.DimensionalLaserBlock;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateBauxiteOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateCinnabarOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateGalenaOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.deepslate.DeepslateRutileOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.red_sand.RedSaltpeterOre;
import com.veteam.voluminousenergy.blocks.blocks.storage.materials.*;
import com.veteam.voluminousenergy.blocks.blocks.storage.raw.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.containers.tank.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import com.veteam.voluminousenergy.blocks.tiles.tank.*;
import com.veteam.voluminousenergy.client.renderers.VEBlockEntities;
import com.veteam.voluminousenergy.client.renderers.entity.LaserBlockEntityRenderer;
import com.veteam.voluminousenergy.datagen.VELootInjectionData;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.loot.AnimalFat.AnimalFatLootModifier;
import com.veteam.voluminousenergy.loot.VELoot;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.world.VEFeatureGeneration;
import com.veteam.voluminousenergy.world.feature.VEFeatures;
import com.veteam.voluminousenergy.world.ores.VEOreGeneration;
import com.veteam.voluminousenergy.world.ores.VEOres;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

        // Register recipes (deferred)
        VERecipes.RECIPE_SERIALIZERS.register(modEventBus);
        // Register fluids and respective items/blocks (differed)
        VEFluids.VE_FLUIDS.register(modEventBus);
        VEFluids.VE_FLUID_BLOCKS.register(modEventBus);
        VEFluids.VE_FLUID_ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerRenderers);
        VELoot.registerLoot(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEOreGeneration::OreGeneration);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEFeatureGeneration::addFeaturesToBiomes);

        //Register triggers
        VECriteriaTriggers.init();

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
        builtinRegisterConfiguredFeatures();
        builtinRegisterPlacedFeatures();
        //VoluminousEnergy.LOGGER.debug("FMLCommonSetupEvent has ran.");
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VEBlockEntities.DIMENSIONAL_LASER.get(), LaserBlockEntityRenderer::new);
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

            // Tanks
            blockRegisteryEvent.getRegistry().register(new AluminumTankBlock());
            blockRegisteryEvent.getRegistry().register(new TitaniumTankBlock());
            blockRegisteryEvent.getRegistry().register(new NetheriteTankBlock());
            blockRegisteryEvent.getRegistry().register(new NighaliteTankBlock());
            blockRegisteryEvent.getRegistry().register(new EighzoTankBlock());
            blockRegisteryEvent.getRegistry().register(new SolariumTankBlock());

            // Ores
            blockRegisteryEvent.getRegistry().register(new SaltpeterOre());
            blockRegisteryEvent.getRegistry().register(new BauxiteOre());
            blockRegisteryEvent.getRegistry().register(new CinnabarOre());
            blockRegisteryEvent.getRegistry().register(new RutileOre());
            blockRegisteryEvent.getRegistry().register(new GalenaOre());
            blockRegisteryEvent.getRegistry().register(new EighzoOre());

            // Deepslate ores
            blockRegisteryEvent.getRegistry().register(new DeepslateBauxiteOre());
            blockRegisteryEvent.getRegistry().register(new DeepslateCinnabarOre());
            blockRegisteryEvent.getRegistry().register(new DeepslateRutileOre());
            blockRegisteryEvent.getRegistry().register(new DeepslateGalenaOre());

            // Red Sand ores
            blockRegisteryEvent.getRegistry().register(new RedSaltpeterOre());

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

            blockRegisteryEvent.getRegistry().register(new PressureLadder());

            blockRegisteryEvent.getRegistry().register(new DimensionalLaserBlock());
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

            // Tanks
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_TANK_BLOCK,properties).setRegistryName("aluminum_tank"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_TANK_BLOCK,properties).setRegistryName("titanium_tank"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NETHERITE_TANK_BLOCK,properties).setRegistryName("netherite_tank"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.NIGHALITE_TANK_BLOCK,properties).setRegistryName("nighalite_tank"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.EIGHZO_TANK_BLOCK,properties).setRegistryName("eighzo_tank"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SOLARIUM_TANK_BLOCK,properties).setRegistryName("solarium_tank"));

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

            //Deepslate ores
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_BAUXITE_ORE,properties).setRegistryName("deepslate_bauxite_ore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_CINNABAR_ORE,properties).setRegistryName("deepslate_cinnabar_ore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_RUTILE_ORE,properties).setRegistryName("deepslate_rutile_ore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DEEPSLATE_GALENA_ORE,properties).setRegistryName("deepslate_galena_ore"));

            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RED_SALTPETER_ORE,properties).setRegistryName("red_saltpeter_ore"));

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

            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRESSURE_LADDER, properties).setRegistryName("pressure_ladder"));

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
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_SAWBLADE);
            itemRegisteryEvent.getRegistry().register(VEItems.ROSIN);
            itemRegisteryEvent.getRegistry().register(VEItems.STANDARD_TANK_FRAME);
            itemRegisteryEvent.getRegistry().register(VEItems.ROBUST_TANK_FRAME);
            itemRegisteryEvent.getRegistry().register(VEItems.IMPECCABLE_TANK_FRAME);
            itemRegisteryEvent.getRegistry().register(VEItems.ANIMAL_FAT);
            itemRegisteryEvent.getRegistry().register(VEItems.DOUGH);

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
            itemRegisteryEvent.getRegistry().register(VEItems.SAW_DUST);
            itemRegisteryEvent.getRegistry().register(VEItems.FLOUR_DUST);

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
            itemRegisteryEvent.getRegistry().register(VEItems.MYSTERIOUS_MULTIPLIER);

            //Batteries
            itemRegisteryEvent.getRegistry().register(VEItems.MERCURY_BATTERY);
            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_ACID_BATTERY);

            //Tiny Fuels
            itemRegisteryEvent.getRegistry().register(VEItems.TINY_CHARCOAL);
            itemRegisteryEvent.getRegistry().register(VEItems.TINY_COAL);
            itemRegisteryEvent.getRegistry().register(VEItems.TINY_COAL_COKE);
            itemRegisteryEvent.getRegistry().register(VEItems.TINY_PETCOKE);
            itemRegisteryEvent.getRegistry().register(VEItems.TINY_ROSIN);

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

            // Register Multitools
            multitoolItemRegistry(itemRegisteryEvent);

            // Register custom models //TODO move elsewhere
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.DIMENSIONAL_LASER_BLOCK,properties).setRegistryName("dimensional_laser"));
        }

        public static void multitoolItemRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent){
            /*
             *    MULTITOOLS
             */

            itemRegisteryEvent.getRegistry().register(VEMultitools.EMPTY_MULTITOOL);

            // Iron
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_MULTITOOL);

            // Diamond
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_TRIMMER_MULTITOOL);

            // Titanium
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_TRIMMER_MULTITOOL);

            // Nighalite
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_TRIMMER_MULTITOOL);

            // Eighzo
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_TRIMMER_MULTITOOL);

            // Solarium
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_DRILL_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_CHAIN_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_SCOOPER_MULTITOOL);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_TRIMMER_MULTITOOL);

            /*
             *    BITS
             */

            // Iron
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.IRON_TRIMMER_BIT);

            // Diamond
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.DIAMOND_TRIMMER_BIT);

            // Titanium
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.TITANIUM_TRIMMER_BIT);

            // Nighalite
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.NIGHALITE_TRIMMER_BIT);

            // Eighzo
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.EIGHZO_TRIMMER_BIT);

            // Solarium
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_DRILL_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_CHAIN_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_SCOOPER_BIT);
            itemRegisteryEvent.getRegistry().register(VEMultitools.SOLARIUM_TRIMMER_BIT);

            // Scanner
            itemRegisteryEvent.getRegistry().register(VEItems.FLUID_SCANNER);
            itemRegisteryEvent.getRegistry().register(VEItems.CREATIVE_FLUID_SCANNER);
            itemRegisteryEvent.getRegistry().register(VEItems.RFID_CHIP);
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

            // Tanks
            event.getRegistry().register(BlockEntityType.Builder.of(AluminumTankTile::new, VEBlocks.ALUMINUM_TANK_BLOCK).build(null).setRegistryName("aluminum_tank"));
            event.getRegistry().register(BlockEntityType.Builder.of(TitaniumTankTile::new,VEBlocks.TITANIUM_TANK_BLOCK).build(null).setRegistryName("titanium_tank"));
            event.getRegistry().register(BlockEntityType.Builder.of(NetheriteTankTile::new,VEBlocks.NETHERITE_TANK_BLOCK).build(null).setRegistryName("netherite_tank"));
            event.getRegistry().register(BlockEntityType.Builder.of(NighaliteTankTile::new,VEBlocks.NIGHALITE_TANK_BLOCK).build(null).setRegistryName("nighalite_tank"));
            event.getRegistry().register(BlockEntityType.Builder.of(EighzoTankTile::new,VEBlocks.EIGHZO_TANK_BLOCK).build(null).setRegistryName("eighzo_tank"));
            event.getRegistry().register(BlockEntityType.Builder.of(SolariumTankTile::new,VEBlocks.SOLARIUM_TANK_BLOCK).build(null).setRegistryName("solarium_tank"));

            // custom block stuff
            event.getRegistry().register(BlockEntityType.Builder.of(DimensionalLaserTile::new,VEBlocks.DIMENSIONAL_LASER_BLOCK).build(null).setRegistryName("dimensional_laser"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<MenuType<?>> event){

            event.getRegistry().register(IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveBlastFurnaceContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitiveblastfurnace"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveStirlingGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitivestirlinggenerator"));

            event.getRegistry().register(IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new CrusherContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("crusher"));

            event.getRegistry().register(IForgeMenuType.create((id, inv, data)-> {
                BlockPos pos = data.readBlockPos();
                return new ElectrolyzerContainer(id,VoluminousEnergy.proxy.getClientWorld(),pos,inv,VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("electrolyzer"));

            event.getRegistry().register(IForgeMenuType.create((id, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalAgitatorContainer(id, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("centrifugal_agitator"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("compressor"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new StirlingGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("stirling_generator"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CombustionGeneratorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("combustion_generator"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new AqueoulizerContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("aqueoulizer"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new AirCompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("air_compressor"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new DistillationUnitContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("distillation_unit"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PumpContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("pump"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new GasFiredFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("gas_fired_furnace"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ElectricFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("electric_furnace"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new BatteryBoxContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("battery_box"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new PrimitiveSolarPanelContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("primitive_solar_panel"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SolarPanelContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("solar_panel"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CentrifugalSeparatorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("centrifugal_separator"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ImplosionCompressorContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("implosion_compressor"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new BlastFurnaceContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("blast_furnace"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new ToolingStationContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("tooling_station"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SawmillContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("sawmill"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new AluminumTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("aluminum_tank"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new TitaniumTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("titanium_tank"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new NetheriteTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("netherite_tank"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new NighaliteTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("nighalite_tank"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new EighzoTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("eighzo_tank"));

            event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SolariumTankContainer(windowId, VoluminousEnergy.proxy.getClientWorld(), pos, inv, VoluminousEnergy.proxy.getClientPlayer());
            }).setRegistryName("solarium_tank"));
        }

        @SubscribeEvent
        public static void onRegisterBiome(RegistryEvent.Register<Biome> event) {
            //VEBiomes.prepareRegistration(event, new RedDesert(), BiomeManager.BiomeType.DESERT, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
            //VEBiomes.prepareRegistration(event, new ShallowWarmOcean(), BiomeManager.BiomeType.WARM, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.WATER, BiomeDictionary.Type.WET);
        }

        @SubscribeEvent
        public static void onRegisterSound(RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().register(VESounds.ENERGY_BEAM_ACTIVATE.setRegistryName("energy_beam_activate"));
            event.getRegistry().register(VESounds.ENERGY_BEAM_FIRED.setRegistryName("energy_beam_fired"));
            event.getRegistry().register(VESounds.AIR_COMPRESSOR.setRegistryName("air_compressor_active"));
            event.getRegistry().register(VESounds.AQUEOULIZER.setRegistryName("aqueoulizer_active"));
            event.getRegistry().register(VESounds.COMPRESSOR.setRegistryName("compressor_active"));
            event.getRegistry().register(VESounds.CRUSHER.setRegistryName("crusher_active"));
            event.getRegistry().register(VESounds.FURNACE.setRegistryName("furnace_active"));
            event.getRegistry().register(VESounds.GENERAL_MACHINE_NOISE.setRegistryName("general_machine_noise"));
            event.getRegistry().register(VESounds.IMPLOSION_COMPRESSOR.setRegistryName("implosion_compressor_active"));
        }

        /*@SubscribeEvent
        public static void onRegisterSurfaceBuilder(RegistryEvent.Register<SurfaceBuilder<?>> event){
            VESurfaceBuilders.init();
            VESurfaceBuilders.surfaceBuilders.forEach(surfaceBuilder -> event.getRegistry().register(surfaceBuilder));
        }*/

        @SubscribeEvent
        public static void onRegisterModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event){
            // Animal Fat
            event.getRegistry().register(new AnimalFatLootModifier.Serializer().setRegistryName("animal_fat_from_cow"));
            event.getRegistry().register(new AnimalFatLootModifier.Serializer().setRegistryName("animal_fat_from_pig"));
        }


        @SubscribeEvent
        public static void onRegisterFeature(RegistryEvent.Register<Feature<?>> event) { // REGISTER STRAIGHT UP FEATURES HERE
            // Straight up Features
            event.getRegistry().register(VEFeatures.VE_BSC_LAKE_FEATURE.setRegistryName("ve_bsc_lake_feature"));
            event.getRegistry().register(VEFeatures.VE_BSC_LAKE_SURFACE_FEATURE.setRegistryName("ve_bsc_surface_lake_feature"));
            event.getRegistry().register(VEFeatures.VE_BSC_LAKE_UNDERGROUND_FEATURE.setRegistryName("ve_bsc_underground_lakes_feature"));
            event.getRegistry().register(VEFeatures.VE_GEYSER_FEATURE.setRegistryName("ve_geyser_feature"));
            event.getRegistry().register(VEFeatures.VE_RICE_FEATURE.setRegistryName("ve_rice_feature"));
            event.getRegistry().register(VEFeatures.VE_ORE_DEPOSIT_FEATURE.setRegistryName("ve_ore_deposit_feature"));
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
        builtinConfiguredFeaturesRegistry(VEFeatures.SURFACE_OIL_LAKE_FEATURE, "oil_lake");
        builtinConfiguredFeaturesRegistry(VEFeatures.OIL_GEYSER_FEATURE, "oil_geyser");

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
        builtinFeaturePlacementRegistry(VEFeatures.SURFACE_OIL_LAKE_PLACEMENT, "surface_oil_lake");
        builtinFeaturePlacementRegistry(VEFeatures.UNDERGROUND_OIL_LAKE_PLACEMENT, "underground_oil_lake");
        builtinFeaturePlacementRegistry(VEFeatures.OIL_GEYSER_PLACEMENT, "oil_geyser");

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
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.RICE_CROP, RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.SAWMILL_BLOCK, RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.PRESSURE_LADDER, RenderType.cutout()));
        }

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class OnDatagenEvent {

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event){
            DataGenerator dataGenerator = event.getGenerator();

            if(event.includeServer()) {
                dataGenerator.addProvider(new VETagDataGenerator(dataGenerator, event.getExistingFileHelper()));
                dataGenerator.addProvider(new VELootInjectionData(dataGenerator));
            }
        }
    }
}
