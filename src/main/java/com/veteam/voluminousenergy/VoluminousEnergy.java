package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.blocks.*;
import com.veteam.voluminousenergy.blocks.blocks.ores.*;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.VEFeatureGeneration;
import com.veteam.voluminousenergy.world.biomes.RedDesert;
import com.veteam.voluminousenergy.world.biomes.VEBiomes;
import com.veteam.voluminousenergy.world.ores.VEOreGeneration;
import com.veteam.voluminousenergy.world.surfaceBulider.VESurfaceBuilders;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VoluminousEnergy.MODID)
public class VoluminousEnergy
{
    public static final String MODID = "voluminousenergy";

    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static VESetup setup = new VESetup();

    public static final Logger LOGGER = LogManager.getLogger();

    public VoluminousEnergy() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupWhenLoadingComplete);
        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register recipes (differed)
        VERecipes.RECIPE_SERIALIZERS.register(modEventBus);
        // Register fluids and respective items/blocks (differed)
        VEFluids.VE_FLUIDS.register(modEventBus);
        VEFluids.VE_FLUID_BLOCKS.register(modEventBus);
        VEFluids.VE_FLUID_ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEOreGeneration::OreGeneration);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEFeatureGeneration::addFeaturesToBiomes);

        // Config Files to load
        //Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-common.toml"));
    }

    private void setup(final FMLCommonSetupEvent event) {
        //VEOreGeneration.OreGeneration(); // Setup custom ore generation
        //VEFeatureGeneration.VEFeatureGenerationSetup(); // Setup feature generation
        setup.init();
        proxy.init();
    }

    private void setupWhenLoadingComplete(final FMLLoadCompleteEvent event){/* //TODO: Test if needed, if so find a FIX!
        //True Items
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","silicon"));

        //Dusts
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/niter/saltpeter"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/niter"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/coal"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/coke"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/lapis"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/sulfur"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/aluminum"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/bauxite"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/cinnabar"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/iron"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/quartz"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/sand"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/soulsand"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/titanium"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/rutile"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/galena"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/lead"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/silver"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/gold"));

        //Gears
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/iron"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/stone"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/aluminum"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/titanium"));

        //Ingots
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","ingots/aluminum"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","ingots/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","ingots/titanium"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","ingots/lead"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","ingots/silver"));

        //Plates
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "plates/aluminum"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "plates/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","plates/titanium"));

        //Ores
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ores/saltpeter"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ores/bauxite"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ores/cinnabar"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ores/rutile"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "ores/galena"));
        */

    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Hello from Voluminous Energy on server start!");
    }

    @SubscribeEvent
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        VoluminousEnergy.LOGGER.info("Hello from Voluminous Energy about to server start!");
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

            //Ores
            blockRegisteryEvent.getRegistry().register(new SaltpeterOre());
            blockRegisteryEvent.getRegistry().register(new BauxiteOre());
            blockRegisteryEvent.getRegistry().register(new CinnabarOre());
            blockRegisteryEvent.getRegistry().register(new RutileOre());
            blockRegisteryEvent.getRegistry().register(new GalenaOre());

            //Shells and Machine Frames
            blockRegisteryEvent.getRegistry().register(new AluminumShellBlock());
            blockRegisteryEvent.getRegistry().register(new CarbonShieldedAluminumMachineFrame());
            blockRegisteryEvent.getRegistry().register(new AluminumMachineCasingBlock());
            blockRegisteryEvent.getRegistry().register(new TitaniumMachineCasingBlock());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent) {
            LOGGER.info("Hello from Voluminous Energy's item registry!");

            //Item Properties
            Item.Properties properties = new Item.Properties();
            Item.Properties shovelProperties = new Item.Properties();
            properties.addToolType(ToolType.PICKAXE,1).group(VESetup.itemGroup);
            shovelProperties.addToolType(ToolType.SHOVEL,1).group(VESetup.itemGroup);

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

            //True Blocks
            //Ores
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_ORE,shovelProperties).setRegistryName("saltpeterore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BAUXITE_ORE,properties).setRegistryName("bauxiteore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CINNABAR_ORE,properties).setRegistryName("cinnabarore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RUTILE_ORE,properties).setRegistryName("rutileore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.GALENA_ORE,properties).setRegistryName("galena_ore"));

            //Shells
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_SHELL, properties).setRegistryName("aluminum_shell"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME, properties).setRegistryName("carbon_shielded_aluminum_machine_frame"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK, properties).setRegistryName("aluminum_machine_casing"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK, properties).setRegistryName("titanium_machine_casing"));

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

            //Ingots and bricks
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_BRICK);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.LEAD_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_INGOT);

            //Gears
            itemRegisteryEvent.getRegistry().register(VEItems.STONE_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.IRON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_GEAR);

            //Plates
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_PLATE);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_PLATE);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_PLATE);

            //Microchips
            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_MICROCHIP);
            itemRegisteryEvent.getRegistry().register(VEItems.SILVER_MICROCHIP);
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.info("Hello from Voluminous Energy's tile entity registry!");
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null).setRegistryName("primitiveblastfurnace"));
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("primitivestirlinggenerator"));
            event.getRegistry().register(TileEntityType.Builder.create(CrusherTile::new,VEBlocks.CRUSHER_BLOCK).build(null).setRegistryName("crusher"));
            event.getRegistry().register(TileEntityType.Builder.create(ElectrolyzerTile::new,VEBlocks.ELECTROLYZER_BLOCK).build(null).setRegistryName("electrolyzer"));
            event.getRegistry().register(TileEntityType.Builder.create(CentrifugalAgitatorTile::new,VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK).build(null).setRegistryName("centrifugal_agitator"));
            event.getRegistry().register(TileEntityType.Builder.create(CompressorTile::new,VEBlocks.COMPRESSOR_BLOCK).build(null).setRegistryName("compressor"));
            event.getRegistry().register(TileEntityType.Builder.create(StirlingGeneratorTile::new,VEBlocks.STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("stirling_generator"));
            event.getRegistry().register(TileEntityType.Builder.create(CombustionGeneratorTile::new,VEBlocks.COMBUSTION_GENERATOR_BLOCK).build(null).setRegistryName("combustion_generator"));
            event.getRegistry().register(TileEntityType.Builder.create(AqueoulizerTile::new,VEBlocks.AQUEOULIZER_BLOCK).build(null).setRegistryName("aqueoulizer"));
            event.getRegistry().register(TileEntityType.Builder.create(AirCompressorTile::new,VEBlocks.AIR_COMPRESSOR_BLOCK).build(null).setRegistryName("air_compressor"));
            event.getRegistry().register(TileEntityType.Builder.create(DistillationUnitTile::new,VEBlocks.DISTILLATION_UNIT_BLOCK).build(null).setRegistryName("distillation_unit"));
            event.getRegistry().register(TileEntityType.Builder.create(PumpTile::new,VEBlocks.PUMP_BLOCK).build(null).setRegistryName("pump"));
            event.getRegistry().register(TileEntityType.Builder.create(GasFiredFurnaceTile::new,VEBlocks.GAS_FIRED_FURNACE_BLOCK).build(null).setRegistryName("gas_fired_furnace"));
            event.getRegistry().register(TileEntityType.Builder.create(ElectricFurnaceTile::new,VEBlocks.ELECTRIC_FURNACE_BLOCK).build(null).setRegistryName("electric_furnace"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event){

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
        }

        @SubscribeEvent
        public static void onRegisterBiome(RegistryEvent.Register<Biome> event) {
            VEBiomes.prepareRegistration(event, new RedDesert(), BiomeManager.BiomeType.DESERT, 5, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        }

        @SubscribeEvent
        public static void onRegisterSurfaceBuilder(RegistryEvent.Register<SurfaceBuilder<?>> event){
            VESurfaceBuilders.init();
            VESurfaceBuilders.surfaceBuilders.forEach(surfaceBuilder -> event.getRegistry().register(surfaceBuilder));
        }
    }
}
