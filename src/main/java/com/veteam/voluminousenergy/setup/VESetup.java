package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEBlockItems;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.items.tools.VETools;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class VESetup {

//    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
//        event.registerCreativeModeTab(new ResourceLocation(VoluminousEnergy.MODID, VoluminousEnergy.MODID), builder -> builder
//                .icon(() -> new ItemStack(VEFluids.RFNA_BUCKET_REG.get()))
//                .displayItems((featureFlags, output) -> {
//                    ArrayList<ItemStack> creativeTabStacks = new ArrayList<>();
//
//                    // Fluid items
//                    creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEFluids.VE_FLUID_ITEMS));
//
//                    // Machine & block items
//                    creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEBlockItems.VE_BLOCK_ITEM_REGISTRY));
//
//                    // Item items
//                    creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEItems.VE_ITEM_REGISTRY));
//
//                    // Multitool items
//                    creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VEMultitools.VE_MULTITOOL_ITEM_REGISTRY));
//
//                    // Tool items
//                    creativeTabStacks.addAll(assembleItemsFromDeferredRegistry(VETools.VE_TOOL_REGISTRY));
//
//                    output.acceptAll(creativeTabStacks);
//                })
//                .title(TextUtil.translateString("tab.voluminousenergy.voluminousenergy"))
////                .hideTitle()
////                .withSearchBar()
//        );
//    }

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
