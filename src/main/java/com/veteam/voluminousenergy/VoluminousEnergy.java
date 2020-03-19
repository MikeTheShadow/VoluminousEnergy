package com.veteam.voluminousenergy;

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
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void setup(final FMLCommonSetupEvent event) {
        VEOreGeneration.OreGeneration(); //Setup custom ore generation
        setup.init();
        proxy.init();
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
            blockRegisteryEvent.getRegistry().register(new PrimitiveBlastFurnaceBlock());
            blockRegisteryEvent.getRegistry().register(new SaltpeterOre());
            blockRegisteryEvent.getRegistry().register(new PrimitiveStirlingGeneratorBlock());
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
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK,properties).setRegistryName("primitiveblastfurnace"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.SALTPETER_ORE,shovelProperties).setRegistryName("saltpeterore"));
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK,properties).setRegistryName("primitivestirlinggenerator"));

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
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.info("Hello from tile entity registry!");
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveBlastFurnaceTile::new,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK).build(null).setRegistryName("primitiveblastfurnace"));
            event.getRegistry().register(TileEntityType.Builder.create(PrimitiveStirlingGeneratorTile::new,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK).build(null).setRegistryName("primitivestirlinggenerator"));
        }
    }
}
