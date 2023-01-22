package com.veteam.voluminousenergy.setup;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VESetup {

    public static CreativeModeTab itemGroup;

    // I don't know why there's no deferred way to register Creative Mode tabs
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        itemGroup = event.registerCreativeModeTab(new ResourceLocation(VoluminousEnergy.MODID, VoluminousEnergy.MODID), builder -> builder
                .icon(() -> new ItemStack(VEFluids.RFNA_BUCKET_REG.get()))
                .displayItems((featureFlags, output, hasOp) -> {
//                    AtomicReference<net.minecraft.world.item.CreativeModeTab.Output> atomicOutput = new AtomicReference<>(output);
                    AtomicReference<ArrayList<ItemStack>> creativeTabStacks = new AtomicReference<>(new ArrayList<>());

                    ForgeRegistries.ITEMS.getValues().parallelStream()
                            .filter(item -> ForgeRegistries.ITEMS.getKey(item).toString().contains(VoluminousEnergy.MODID + ":"))
                            .forEach(item -> {

                        if (item instanceof VEEnergyItem veEnergyItem) {
                            ItemStack unchargedStack = new ItemStack(item);
                            ItemStack chargedStack = new ItemStack(item);
                            chargedStack.getOrCreateTag().putInt("energy", veEnergyItem.getMaxEnergy());

                            Collection<ItemStack> energyItemCollection = List.of(unchargedStack, chargedStack);
                            creativeTabStacks.get().addAll(energyItemCollection);
                        } else {
                            creativeTabStacks.get().add(new ItemStack(item));
                        }

                    });

                    output.acceptAll(creativeTabStacks.get());
                })
                .title(TextUtil.translateString("tab.voluminousenergy.voluminousenergy"))
                .hideTitle()
                .withSearchBar()
        );
    }

    public void init(){}

}
