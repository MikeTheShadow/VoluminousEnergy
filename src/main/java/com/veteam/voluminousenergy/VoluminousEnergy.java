package com.veteam.voluminousenergy;

import com.veteam.voluminousenergy.blocks.PrimitiveBlastFurnaceBlock;
import com.veteam.voluminousenergy.blocks.VEBlocks;
import com.veteam.voluminousenergy.setup.ClientProxy;
import com.veteam.voluminousenergy.setup.IProxy;
import com.veteam.voluminousenergy.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("voluminousenergy")
public class VoluminousEnergy
{
    public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static final Logger LOGGER = LogManager.getLogger();

    public VoluminousEnergy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void setup(final FMLCommonSetupEvent event) {

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
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisteryEvent) {
            LOGGER.info("Hello from item registry!");
            itemRegisteryEvent.getRegistry().register(new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK,new Item.Properties()).setRegistryName("primitiveblastfurnace"));
        }
    }
}
