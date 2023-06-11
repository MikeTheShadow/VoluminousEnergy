package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEBlockItems;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

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
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEMultitools.VE_MULTITOOL_ITEM_REGISTRY));

                            // Tool items
                            creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VETools.VE_TOOL_REGISTRY));

                            output.acceptAll(creativeTabStacks);
                        })
                        //.hideTitle()
                        //.withSearchBar(12)
                        .build()
        ));

    }

    private static List<ItemStack> assembleItemsFromDeferredRegistry(DeferredRegister<Item> deferredRegister) {
        ArrayList<ItemStack> stackStore = new ArrayList<>();

        for (RegistryObject<Item> itemRegistryObject : deferredRegister.getEntries()) {
            Item item = itemRegistryObject.get();

            if (item instanceof VEEnergyItem veEnergyItem) {
                ItemStack unchargedStack = new ItemStack(item);
                ItemStack chargedStack = new ItemStack(item);
                chargedStack.getOrCreateTag().putInt("energy", veEnergyItem.getMaxEnergy());

                List<ItemStack> energyItemCollection = List.of(unchargedStack, chargedStack);
                stackStore.addAll(energyItemCollection);
            } else {
                stackStore.add(new ItemStack(item));
            }
        }

        return stackStore;
    }

    public void init(){}

}
