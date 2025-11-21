package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;

public class TagUtil {

    public static ArrayList<Fluid> getFluidListFromTagResourceLocation(String fluidTagLocation) {
        TagKey<Fluid> fluidTagKey = TagKey.create(Registries.FLUID, new ResourceLocation(fluidTagLocation));
        ArrayList<Fluid> fluids = new ArrayList<>();

        for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }
        return fluids;
    }

    public static ArrayList<Block> getBlocksFromTagResourceLocation(ResourceLocation blockTagLocation) {
        TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, blockTagLocation);
        ArrayList<Block> blocks = new ArrayList<>();

        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTagKey)) {
            blocks.add(holder.value());
        }
        return blocks;
    }

    public static TagKey<Block> getBlockTagKeyFromLocation(ResourceLocation blockTagLocation) {
        return TagKey.create(Registries.BLOCK, blockTagLocation);
    }

    public static ArrayList<Block> getBlocksFromTagKey(TagKey<Block> blockTagKey) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTagKey)) {
            blocks.add(holder.value());
        }
        return blocks;
    }

    public static ArrayList<Fluid> getFluidListFromTagResourceLocation(ResourceLocation fluidTagLocation) {
        TagKey<Fluid> fluidTagKey = TagKey.create(Registries.FLUID, fluidTagLocation);
        ArrayList<Fluid> fluids = new ArrayList<>();

        for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(fluidTagKey)) {
            fluids.add(holder.value());
        }
        return fluids;
    }

    public static ArrayList<Item> getItemListFromTagResourceLocation(String itemTagLocation) {
        TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, new ResourceLocation(itemTagLocation));
        ArrayList<Item> items = new ArrayList<>();

        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(itemTagKey)) {
            items.add(holder.value());
        }
        return items;
    }

    public static ArrayList<Item> getItemListFromTagResourceLocation(ResourceLocation itemTagLocation) {
        TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, itemTagLocation);
        ArrayList<Item> items = new ArrayList<>();

        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(itemTagKey)) {
            items.add(holder.value());
        }
        return items;
    }

    private static final TagKey<Item> MACHINE_UPGRADE_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(VoluminousEnergy.MODID, "machine_upgrades"));

    public static boolean isTaggedMachineUpgradeItem(ItemStack stack) {
        return stack.is(MACHINE_UPGRADE_TAG);
    }
}
