package com.veteam.voluminousenergy.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class TagUtil {

    public static Lazy<ArrayList<Fluid>> getLazyFluids(ResourceLocation fluidTagLocation){
        TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, fluidTagLocation);
        return Lazy.of(() -> {
            HolderSet<Fluid> holderSet = Registry.FLUID.getOrCreateTag(tag);
            AtomicReference<ArrayList<Fluid>> fluidSet = new AtomicReference<>(new ArrayList<>());
            holderSet.stream().forEach(fluidHolder -> {
                fluidSet.get().add(fluidHolder.value());
            });
            return fluidSet.get();
        });
    }

    public static Lazy<ArrayList<FluidStack>> getLazyFluidStacks(ResourceLocation fluidTagLocation, int amount){
        TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, fluidTagLocation);
        return Lazy.of(() -> {
            HolderSet<Fluid> holderSet = Registry.FLUID.getOrCreateTag(tag);
            AtomicReference<ArrayList<FluidStack>> fluidSet = new AtomicReference<>(new ArrayList<>());
            holderSet.stream().forEach(fluidHolder -> {
                fluidSet.get().add(new FluidStack(fluidHolder.value(), amount));
            });
            return fluidSet.get();
        });
    }

    public static Lazy<ArrayList<Item>> getLazyItems(ResourceLocation itemTagLocation){
        TagKey<Item> tag = TagKey.create(Registry.ITEM_REGISTRY, itemTagLocation);
        return Lazy.of(() -> {
           HolderSet<Item> holderSet = Registry.ITEM.getOrCreateTag(tag);
           AtomicReference<ArrayList<Item>> itemSet = new AtomicReference<>(new ArrayList<>());
           holderSet.stream().forEach(itemHolder -> {
               itemSet.get().add(itemHolder.value());
           });
           return itemSet.get();
        });
    }


    public static ArrayList<Fluid> getFluidListFromTagResourceLocationAlternative(String fluidTagLocation){
        TagKey<Fluid> fluidTagKey = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(fluidTagLocation));
        ArrayList<Fluid> fluids = new ArrayList<>();
        AtomicReference<ArrayList<Fluid>> atomicFluids = new AtomicReference<>(fluids);
        ForgeRegistries.FLUIDS.getKeys().forEach(fluidKey -> {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidKey);
            if (fluid.is(fluidTagKey)) atomicFluids.get().add(fluid);
        });

        /*for(Holder<Fluid> holder : Registry.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }*/
        return fluids;
    }

    public static ArrayList<Fluid> getFluidListFromTagResourceLocationAlternative(ResourceLocation fluidTagLocation){
        TagKey<Fluid> fluidTagKey = TagKey.create(Registry.FLUID_REGISTRY, fluidTagLocation);
        ArrayList<Fluid> fluids = new ArrayList<>();
        AtomicReference<ArrayList<Fluid>> atomicFluids = new AtomicReference<>(fluids);
        ForgeRegistries.FLUIDS.getKeys().forEach(fluidKey -> {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidKey);
            if (fluid.is(fluidTagKey)) atomicFluids.get().add(fluid);
        });

        /*for(Holder<Fluid> holder : Registry.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }*/
        return fluids;
    }


    // Original

    public static ArrayList<Fluid> getFluidListFromTagResourceLocation(String fluidTagLocation){
        TagKey<Fluid> fluidTagKey = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(fluidTagLocation));
        ArrayList<Fluid> fluids = new ArrayList<>();

        for(Holder<Fluid> holder : Registry.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }
        return fluids;
    }

    public static ArrayList<Fluid> getFluidListFromTagResourceLocation(ResourceLocation fluidTagLocation){
        TagKey<Fluid> fluidTagKey = TagKey.create(Registry.FLUID_REGISTRY, fluidTagLocation);
        ArrayList<Fluid> fluids = new ArrayList<>();

        for(Holder<Fluid> holder : Registry.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }
        return fluids;
    }

    public static ArrayList<Item> getItemListFromTagResourceLocation(String itemTagLocation){
        TagKey<Item> itemTagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(itemTagLocation));
        ArrayList<Item> items = new ArrayList<>();

        for(Holder<Item> holder : Registry.ITEM.getTagOrEmpty(itemTagKey)) {
            items.add(holder.value());
        }
        return items;
    }

    public static ArrayList<Item> getItemListFromTagResourceLocation(ResourceLocation itemTagLocation){
        TagKey<Item> itemTagKey = TagKey.create(Registry.ITEM_REGISTRY, itemTagLocation);
        ArrayList<Item> items = new ArrayList<>();

        for(Holder<Item> holder : Registry.ITEM.getTagOrEmpty(itemTagKey)) {
            items.add(holder.value());
        }
        return items;
    }
}
