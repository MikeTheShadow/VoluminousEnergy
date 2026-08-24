package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.client.renderers.entity.LaserBlockEntityRenderer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.items.tools.multitool.MuiltiToolFluidHandler;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.tools.energy.VEEnergyItemStorage;
import com.veteam.voluminousenergy.tools.networking.packets.*;
import com.veteam.voluminousenergy.util.CapabilityAdapters;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = VoluminousEnergy.MODID)
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
                        Capabilities.Item.BLOCK,
                        (level, pos, state, be, side) -> {
                            IItemHandler itemHandler = ((VETileEntity) be).getCapabilityMap().getItemStackHandler(side, be);
                            return itemHandler == null ? null : CapabilityAdapters.toResourceHandler(itemHandler);
                        },
                        block);
            }

            if (b.hasEnergy()) {
                event.registerBlock(
                        Capabilities.Energy.BLOCK,
                        (level, pos, state, be, side) -> {
                            IEnergyStorage energyStorage = ((VETileEntity) be).getCapabilityMap().getEnergyStorage();
                            return energyStorage == null ? null : CapabilityAdapters.toEnergyHandler(energyStorage);
                        },
                        block);
            }

            if (b.hasFluids()) {
                event.registerBlock(
                        Capabilities.Fluid.BLOCK,
                        (level, pos, state, be, side) -> {
                            IFluidHandler fluidHandler = ((VETileEntity) be).getCapabilityMap().getFluidHandler(side, be);
                            return fluidHandler == null ? null : CapabilityAdapters.toResourceHandler(fluidHandler);
                        },
                        block);
            }
        }

        VEItems.VE_ITEM_REGISTRY.getEntries().forEach(itemDeferredHolder -> {
            Item item = itemDeferredHolder.get();
            if (item instanceof VEEnergyItem) {
                event.registerItem(Capabilities.Energy.ITEM, (stack, provider) ->
                {
                    VEEnergyItem energyItem = (VEEnergyItem) stack.getItem();
                    VEEnergyItemStorage storage = new VEEnergyItemStorage(stack, energyItem.getMaxEnergy(), energyItem.getMaxTransfer());
                    return CapabilityAdapters.toEnergyHandler(storage);
                }, item);
            }
        });

        event.registerItem(Capabilities.Fluid.ITEM, (stack, provider) ->
                CapabilityAdapters.toResourceHandler(new MuiltiToolFluidHandler(
                        VEDataComponents.SIMPLE_FLUID_DATA_TYPE,
                        stack)),
                VEMultitoolItems.MULTI_TOOL.get());
    }

    @SubscribeEvent
    public static void onRegistry(final RegisterEvent blockRegistryEvent) {
        VoluminousEnergy.LOGGER.info("Running: " + blockRegistryEvent.getRegistryKey()); // If you delete this you have to fix it
    }

    @SubscribeEvent
    private static void setup(final FMLCommonSetupEvent event) {
        VoluminousEnergy.setup.init();
    }

    @SubscribeEvent
    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("ve_1");
        registrar.playToServer(BoolButtonPacket.BoolButtonPayload.TYPE, BoolButtonPacket.BoolButtonPayload.STREAM_CODEC,BoolButtonPacket::handle);
        registrar.playToServer(DirectionButtonPacket.DirectionButtonPayload.TYPE, DirectionButtonPacket.DirectionButtonPayload.STREAM_CODEC,DirectionButtonPacket::handle);
        registrar.playToServer(TankBoolPacket.TankBoolPacketPayload.TYPE, TankBoolPacket.TankBoolPacketPayload.STREAM_CODEC,TankBoolPacket::handle);
        registrar.playToServer(TankDirectionPacket.TankDirectionPayload.TYPE, TankDirectionPacket.TankDirectionPayload.STREAM_CODEC,TankDirectionPacket::handle);
        registrar.playToServer(BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload.TYPE, BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload.STREAM_CODEC,BatteryBoxSendOutPowerPacket::handle);
        registrar.playToClient(ClientBoundFluidDataPacket.ClientBoundFluidDataPayload.TYPE, ClientBoundFluidDataPacket.ClientBoundFluidDataPayload.STREAM_CODEC,ClientBoundFluidDataPacket::handle);
    }
}
