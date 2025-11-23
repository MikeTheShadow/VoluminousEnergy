package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.*;
import com.veteam.voluminousenergy.blocks.screens.tank.*;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEBlockItems;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

public class VESetup {

    public static void registerCreativeTabs(RegisterEvent event) {
        boolean searchbar = true;

        event.register(Registries.CREATIVE_MODE_TAB, registerer -> registerer.register(
                ResourceKey.create(
                        Registries.CREATIVE_MODE_TAB,
                        new ResourceLocation(VoluminousEnergy.MODID, "voluminous_energy_cumulative_tab")
                ),
                CreativeModeTab.builder()
                        .icon(() -> new ItemStack(VEFluids.RFNA_BUCKET_REG.get()))
                        .title(TextUtil.translateString("tab.voluminousenergy.voluminousenergy"))
                        .displayItems((featureFlags, output) -> {
                            ArrayList<ItemStack> creativeTabStacks = new ArrayList<>();

                            // Fluid items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEFluids.VE_FLUID_ITEMS));

                            // Machine & block items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEBlockItems.VE_BLOCK_ITEM_REGISTRY));

                            // Item items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEItems.VE_ITEM_REGISTRY));

                            // Multitool items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEMultitoolItems.VE_MULTITOOL_ITEM_REGISTRY));

                            // Tool items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VETools.VE_TOOL_REGISTRY));

                            output.acceptAll(creativeTabStacks);
                        })
                        //.hideTitle()
                        //.withSearchBar(12)
                        .build()
        ));

    }
    
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(VEBlocks.PRIMITIVE_BLAST_FURNACE.container().get(), PrimitiveBlastFurnaceScreen::new);
        event.register(VEBlocks.PRIMITIVE_STIRLING_GENERATOR.container().get(), PrimitiveStirlingGeneratorScreen::new);
        event.register(VEBlocks.CRUSHER.container().get(), CrusherScreen::new);
        event.register(VEBlocks.ELECTROLYZER.container().get(), ElectrolyzerScreen::new);
        event.register(VEBlocks.CENTRIFUGAL_AGITATOR.container().get(), CentrifugalAgitatorScreen::new);
        event.register(VEBlocks.COMPRESSOR.container().get(), CompressorScreen::new);
        event.register(VEBlocks.STIRLING_GENERATOR.container().get(), StirlingGeneratorScreen::new);
        event.register(VEBlocks.COMBUSTION_GENERATOR.container().get(), CombustionGeneratorScreen::new);
        event.register(VEBlocks.AQUEOULIZER.container().get(), AqueoulizerScreen::new);
        event.register(VEBlocks.AIR_COMPRESSOR.container().get(), AirCompressorScreen::new);
        event.register(VEBlocks.DISTILLATION_UNIT.container().get(), DistillationUnitScreen::new);
        event.register(VEBlocks.PUMP.container().get(), PumpScreen::new);
        event.register(VEBlocks.GAS_FIRED_FURNACE.container().get(), GasFiredFurnaceScreen::new);
        event.register(VEBlocks.ELECTRIC_FURNACE.container().get(), ElectricFurnaceScreen::new);
        event.register(VEBlocks.BATTERY_BOX.container().get(), BatteryBoxScreen::new);
        event.register(VEBlocks.PRIMITIVE_SOLAR_PANEL.container().get(), PrimitiveSolarPanelScreen::new);
        event.register(VEBlocks.SOLAR_PANEL.container().get(), SolarPanelScreen::new);
        event.register(VEBlocks.CENTRIFUGAL_SEPARATOR.container().get(), CentrifugalSeparatorScreen::new);
        event.register(VEBlocks.IMPLOSION_COMPRESSOR.container().get(), ImplosionCompressorScreen::new);
        event.register(VEBlocks.BLAST_FURNACE.container().get(), BlastFurnaceScreen::new);
        event.register(VEBlocks.TOOLING_STATION.container().get(), ToolingStationScreen::new);
        event.register(VEBlocks.SAWMILL.container().get(), SawmillScreen::new);
        event.register(VEBlocks.ALUMINUM_TANK.container().get(), AluminumTankScreen::new);
        event.register(VEBlocks.TITANIUM_TANK.container().get(), TitaniumTankScreen::new);
        event.register(VEBlocks.NETHERITE_TANK.container().get(), NetheriteTankScreen::new);
        event.register(VEBlocks.NIGHALITE_TANK.container().get(), NighaliteTankScreen::new);
        event.register(VEBlocks.EIGHZO_TANK.container().get(), EighzoTankScreen::new);
        event.register(VEBlocks.SOLARIUM_TANK.container().get(), SolariumTankScreen::new);
        event.register(VEBlocks.DIMENSIONAL_LASER.container().get(), DimensionalLaserScreen::new);
        event.register(VEBlocks.FLUID_ELECTROLYZER.container().get(), FluidElectrolyzerScreen::new);
        event.register(VEBlocks.FLUID_MIXER.container().get(), FluidMixerScreen::new);
        event.register(VEBlocks.HYDROPONIC_INCUBATOR.container().get(), HydroponicIncubatorScreen::new);
    }

    private static List<ItemStack> assembleItemsFromDeferredRegistry(DeferredRegister<Item> deferredRegister) {
        ArrayList<ItemStack> stackStore = new ArrayList<>();

        for (DeferredHolder<Item,? extends Item> itemRegistryObject : deferredRegister.getEntries()) {
            Item item = itemRegistryObject.get();
            stackStore.add(new ItemStack(item));
            //TODO FIX ME
//            if (item instanceof VEEnergyItem) {
//                ItemStack unchargedStack = new ItemStack(item);
//                ItemStack chargedStack = new ItemStack(item);
//
//                IEnergyStorage storage = chargedStack.getCapability(Capabilities.EnergyStorage.ITEM);
//                storage.receiveEnergy(storage.getMaxEnergyStored(),false);
//
//                List<ItemStack> energyItemCollection = List.of(unchargedStack, chargedStack);
//                stackStore.addAll(energyItemCollection);
//            } else {
//                stackStore.add(new ItemStack(item));
//            }
        }

        return stackStore;
    }

    public void init() {
    }

}
