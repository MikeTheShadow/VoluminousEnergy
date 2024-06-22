package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.client.renderers.entity.LaserBlockEntityRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = VoluminousEnergy.MODID)
public class VEGenericListener {

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VEBlocks.DIMENSIONAL_LASER.tile().get(), LaserBlockEntityRenderer::new);
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public static void onRegistry(final RegisterEvent blockRegistryEvent) {
        VoluminousEnergy.LOGGER.info("Running: " + blockRegistryEvent.getRegistryKey()); // If you delete this you have to fix it
    }

    private static void setup(final FMLCommonSetupEvent event) {
        VoluminousEnergy.setup.init();

        //Register triggers
        VECriteriaTriggers.init();
    }

}
