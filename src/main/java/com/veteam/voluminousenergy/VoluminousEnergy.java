package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.client.renderers.entity.LaserBlockEntityRenderer;
import com.veteam.voluminousenergy.datagen.VEGlobalLootModifierData;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEBlockItems;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.loot.VELoot;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.world.feature.VEFeatures;
import com.veteam.voluminousenergy.world.modifiers.VEModifiers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod(VoluminousEnergy.MODID)
public class VoluminousEnergy {
    public static final String MODID = "voluminousenergy";

    public static VESetup setup = new VESetup();
    public static boolean JEI_LOADED = false;

    public static final Logger LOGGER = LogManager.getLogger();

    static {
        System.out.println("Hello World!");
        LOGGER.info("Hello World!");
        VoluminousEnergy voluminousEnergy = new VoluminousEnergy();
        voluminousEnergy.init();
    }

    public VoluminousEnergy(IEventBus modEventBus, ModContainer modContainer) {
//        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupWhenLoadingComplete);
        NeoForge.EVENT_BUS.register(this);

//        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        /** Deferred registration **/
        // Recipes
        VERecipes.VE_RECIPE_SERIALIZERS_REGISTRY.register(modEventBus);
        VERecipes.VERecipeTypes.VE_RECIPE_TYPES_REGISTRY.register(modEventBus);

        // Register fluids and respective items/blocks (differed)
        VEFluids.VE_FLUIDS.register(modEventBus);
        VEFluids.VE_FLUID_BLOCKS.register(modEventBus);
        VEFluids.VE_FLUID_ITEMS.register(modEventBus);
        VEFluids.VE_FLUID_TYPES.register(modEventBus);

        // Register Blocks, Tiles, and Containers
        VEBlocks.VE_BLOCKS_REGISTRY.register(modEventBus);
        VEBlocks.VE_TILE_REGISTRY.register(modEventBus);
        VEBlocks.VE_CONTAINER_REGISTRY.register(modEventBus);

        // Deferred Item registration
        VEItems.VE_ITEM_REGISTRY.register(modEventBus);
        VEBlockItems.VE_BLOCK_ITEM_REGISTRY.register(modEventBus);
        VEMultitools.VE_MULTITOOL_ITEM_REGISTRY.register(modEventBus);
        VETools.VE_TOOL_REGISTRY.register(modEventBus);

        // Register features
        VEFeatures.VE_FEATURE_REGISTRY.register(modEventBus);
//        VEFeatures.VE_PLACED_FEATURES.register(modEventBus);

        // Register modifiers
//        VEOres.VE_PLACED_ORE_BLOBS_REGISTRY.register(modEventBus);
        VEModifiers.VE_BIOME_MODIFIER_REGISTRY.register(modEventBus);

        // Register Creative Mode Tabs
        modEventBus.addListener(VESetup::registerCreativeTabs);

        // Register Loot modifiers
        VELoot.VE_LOOT_MODIFIER_REGISTRY.register(modEventBus);

        // TODO: Adding features due to BiomeLoadEvent being replaced
        // TODO: Port registering of Features to use Deferred register
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEOreGeneration::OreGeneration);
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,VEFeatureGeneration::addFeaturesToBiomes);
//        VELoot.registerLoot(modEventBus);

        // Config Files to load
//        String configDir = NeoForgeConfig.defaultConfigPath();

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get(), VoluminousEnergy.MODID);
//        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(VoluminousEnergy.MODID + "-common.toml"));
//        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-client.toml"));
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG, VoluminousEnergy.MODID + "/" + VoluminousEnergy.MODID + "-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_BUILDER.build(), VoluminousEnergy.MODID + "/" + VoluminousEnergy.MODID + "-client.toml");


        modEventBus.addListener(this::registerRenderers); // Register renderer for Dimensional Laser
        modEventBus.addListener(VENetwork::onPayloadRegister); // Register network packets

        JEI_LOADED = ModList.get().isLoaded("jei");
    }

    private static Path getOrCreateDirectory(Path dirPath, String dirLabel) { // Extracted from Forge 45
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of " + dirLabel);
        }
        if (!Files.isDirectory(dirPath)) {
            LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.error("Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.error("Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();

        //Register triggers
        VECriteriaTriggers.init();
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for(VEBlocks.RegistryWithName b : VEBlocks.REGISTERED_BLOCKS) {

            Block block = b.block().get();

            if (b.hasInventory()) {
                event.registerBlock(
                        Capabilities.ItemHandler.BLOCK,
                        (level, pos, state, be, side) -> ((VETileEntity) be).getCapabilityMap().getItemStackHandler(side,be),
                        block);
            }

            if (b.hasEnergy()) {
                event.registerBlock(
                        Capabilities.EnergyStorage.BLOCK,
                        (level, pos, state, be, side) -> ((VETileEntity) be).getCapabilityMap().getEnergyStorage(),
                        block);
            }

            if (b.hasFluids()) {
                event.registerBlock(
                        Capabilities.FluidHandler.BLOCK,
                        (level, pos, state, be, side) -> ((VETileEntity) be).getCapabilityMap().getFluidHandler(side,be),
                        block);
            }
        }
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VEBlocks.DIMENSIONAL_LASER.tile().get(), LaserBlockEntityRenderer::new);
    }

    private void setupWhenLoadingComplete(final FMLLoadCompleteEvent event) {
    }

    public static MinecraftServer server;

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class RegisterEvents {
        @SubscribeEvent
        public static void onRegistry(final RegisterEvent blockRegistryEvent) {
            LOGGER.info("Running: " + blockRegistryEvent.getRegistryKey()); // If you delete this you have to fix it
        }
    }

    @EventBusSubscriber(modid = VoluminousEnergy.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientRegister {

        @SubscribeEvent
        public static void RegisterClientOnSetupEvent(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.RICE_CROP.get(), RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.SAWMILL.block().get(), RenderType.cutout()));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(VEBlocks.PRESSURE_LADDER.get(), RenderType.cutout()));
        }

        @SubscribeEvent
        public static void RegisterMenuScreens(RegisterMenuScreensEvent event) {
            VESetup.registerMenuScreens(event);
        }

    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class OnDatagenEvent {

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event) {
            DataGenerator dataGenerator = event.getGenerator();
            PackOutput packOutput = dataGenerator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            if (event.includeServer()) {
                dataGenerator.addProvider(true, new VETagDataGenerator(dataGenerator.getPackOutput(), lookupProvider, event.getExistingFileHelper()));
                dataGenerator.addProvider(true, new VEGlobalLootModifierData(dataGenerator.getPackOutput(), lookupProvider));
            }
        }
    }
}
