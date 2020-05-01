package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.blocks.*;
import com.veteam.voluminousenergy.blocks.blocks.ores.BauxiteOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.CinnabarOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.RutileOre;
import com.veteam.voluminousenergy.blocks.blocks.ores.SaltpeterOre;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.world.VEOreGeneration;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupWhenLoadingComplete);
        MinecraftForge.EVENT_BUS.register(this);

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voluminousenergy-common.toml"));
    }
    private void setup(final FMLCommonSetupEvent event) {
        VEOreGeneration.OreGeneration(); //Setup custom ore generation
        setup.init();
        proxy.init();
    }

    private void setupWhenLoadingComplete(final FMLLoadCompleteEvent event){
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

        //Gears
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/iron"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/stone"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/aluminum"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/titanium"));
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Hello from server start!");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegisteryEvent) {
            LOGGER.info("Hello from block registry!");
            //Tile Entities
            blockRegisteryEvent.getRegistry().register(new PrimitiveBlastFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new PrimitiveStirlingGeneratorBlock());

            //Ores
            blockRegisteryEvent.getRegistry().register(new SaltpeterOre());
            blockRegisteryEvent.getRegistry().register(new BauxiteOre());
            blockRegisteryEvent.getRegistry().register(new CinnabarOre());
            blockRegisteryEvent.getRegistry().register(new RutileOre());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent) {
            LOGGER.info("Hello from item registry!");

            //Item Properties
            Item.Properties properties = new Item.Properties();
            Item.Properties shovelProperties = new Item.Properties();
            properties.addToolType(ToolType.PICKAXE,1).group(VESetup.itemGroup);
            shovelProperties.addToolType(ToolType.SHOVEL,1).group(VESetup.itemGroup);

            //Block Items
            //Tile Entities
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK,properties).setRegistryName("primitiveblastfurnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK,properties).setRegistryName("primitivestirlinggenerator"));

            //Ores
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_ORE,shovelProperties).setRegistryName("saltpeterore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.BAUXITE_ORE,properties).setRegistryName("bauxiteore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.CINNABAR_ORE,properties).setRegistryName("cinnabarore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.RUTILE_ORE,properties).setRegistryName("rutileore"));

            //True Items
            itemRegisteryEvent.getRegistry().register(VEItems.PETCOKE);
            itemRegisteryEvent.getRegistry().register(VEItems.COALCOKE);
            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERCHUNK);
            itemRegisteryEvent.getRegistry().register(VEItems.SILICON);

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

            //Ingots and bricks
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_BRICK);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_INGOT);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_INGOT);

            //Gears
            itemRegisteryEvent.getRegistry().register(VEItems.STONE_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.IRON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.ALUMINUM_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.TITANIUM_GEAR);

            //Microchips
            itemRegisteryEvent.getRegistry().register(VEItems.GOLD_MICROCHIP);
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.info("Hello from tile entity registry!");
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null).setRegistryName("primitiveblastfurnace"));
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("primitivestirlinggenerator"));
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
        }
    }
}
