package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.blocks.PrimitiveBlastFurnaceBlock;
import com.veteam.voluminousenergy.blocks.blocks.PrimitiveStirlingGeneratorBlock;
import com.veteam.voluminousenergy.blocks.blocks.SaltpeterOre;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.veteam.voluminousenergy.blocks.*;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupWhenLoadingComplete);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void setup(final FMLCommonSetupEvent event) {
        VEOreGeneration.OreGeneration(); //Setup custom ore generation
        setup.init();
        proxy.init();
    }

    private void setupWhenLoadingComplete(final FMLLoadCompleteEvent event){
        //Dusts
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/niter/saltpeter"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/niter"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/carbon"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/coal"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/coke"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/lapis"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","dusts/sulfur"));

        //Gears
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/iron"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/stone"));
        ItemTags.getCollection().getOrCreate(new ResourceLocation("forge","gears/carbon"));
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

            //Dusts
            itemRegisteryEvent.getRegistry().register(VEItems.COALDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.COKEDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.LAPISDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SULFURDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBONDUST);
            itemRegisteryEvent.getRegistry().register(VEItems.SALTPETERDUST);

            //Ingots and bricks
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_BRICK);

            //Gears
            itemRegisteryEvent.getRegistry().register(VEItems.STONE_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.IRON_GEAR);
            itemRegisteryEvent.getRegistry().register(VEItems.CARBON_GEAR);

        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.info("Hello from tile entity registry!");
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null).setRegistryName("primitiveblastfurnace"));
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("primitivestirlinggenerator"));
        }
    }
}
