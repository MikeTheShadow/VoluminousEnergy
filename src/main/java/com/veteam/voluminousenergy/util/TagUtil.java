package com.veteam.voluminousenergy.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;

public class TagUtil {

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

    /*

    // USE: TagKey.create(Registry.<TYPE>_REGISTRY, resourceLocation);

    public static Tag.Named<Block> getStaticBlockTagFromResourceLocation(String blockTagLocation){ // Doesn't work
        //StaticTagHelper<Block> staticTagHelper = StaticTags.create(Registry.BLOCK_REGISTRY, blockTagLocation);
        Tag.Named<Block> namedBlockTag = BlockTags.bind(blockTagLocation);
        return namedBlockTag;
    }

    public static <T> Tag.Named<T> tagger(Function<ResourceLocation, Tag.Named<T>> wrapperFactory, String namespace, String path) {
        return wrapperFactory.apply(new ResourceLocation(namespace, path));
    }

    public static <T> Tag.Named<T> taggerNamespacePredefined(Function<ResourceLocation, Tag.Named<T>> wrapperFactory, String completePath) {
        return wrapperFactory.apply(new ResourceLocation(completePath));
    }

    public static Tag.Named<Block> forgeSpaceBlockTag(String target){
        return taggerNamespacePredefined(BlockTags::createOptional, target);
    }

    // Use for things like ores
    public static Tags.IOptionalNamedTag<Block> getIOptionalNamedBlockTagFromResourceLocation(ResourceLocation blockTagLocation){
        return ForgeTagHandler.createOptionalTag(ForgeRegistries.BLOCKS, blockTagLocation);
    }
    */
}
