package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.client.renderers.entity.LaserBlockEntityRenderer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.items.tools.multitool.CombustionMultitool;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = VoluminousEnergy.MODID)
public class VEGenericListener {

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VEBlocks.DIMENSIONAL_LASER.tile().get(), LaserBlockEntityRenderer::new);
    }

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (VEBlocks.RegistryWithName b : VEBlocks.REGISTERED_BLOCKS) {

            Block block = b.block().get();

            if (b.hasInventory()) {
                event.registerBlock(
                        Capabilities.ItemHandler.BLOCK,
                        (level, pos, state, be, side) -> ((VETileEntity) be).getCapabilityMap().getItemStackHandler(side, be),
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
                        (level, pos, state, be, side) -> ((VETileEntity) be).getCapabilityMap().getFluidHandler(side, be),
                        block);
            }
        }

        VEItems.VE_ITEM_REGISTRY.getEntries().forEach(itemDeferredHolder -> {
            Item item = itemDeferredHolder.get();
            if (item instanceof VEEnergyItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, provider) ->
                {
                    VEEnergyItem energyItem = (VEEnergyItem) stack.getItem();
                    return new VEEnergyStorage(energyItem.getMaxEnergy(), energyItem.getMaxTransfer());
                }, item);
            }
        });

        VEMultitools.VE_MULTITOOL_ITEM_REGISTRY.getEntries().forEach(itemDeferredHolder -> {
            Item item = itemDeferredHolder.get();
            if (item instanceof CombustionMultitool multitool) {
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, provider) ->
                        new FluidHandlerItemStack(VEDataComponents.SIMPLE_FLUID_DATA_TYPE,
                                stack, multitool.TANK_CAPACITY), item);
            }
        });
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
