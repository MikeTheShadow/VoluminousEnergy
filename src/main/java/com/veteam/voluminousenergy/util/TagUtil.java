package com.veteam.voluminousenergy.util;

import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

public class TagUtil {

    public static Tag<Fluid> getFluidTagFromResourceLocation(ResourceLocation fluidTagLocation, String stackTraceMessageOnError){
        return SerializationTags.getInstance().getTagOrThrow(Registry.FLUID_REGISTRY, fluidTagLocation, (tempTag) -> {
            return new JsonSyntaxException("Invalid ResourceLocation for getFluidTagFromResourceLocation passed by: " + stackTraceMessageOnError + "; The offending tag is: '" + tempTag + "'");
        });
    }

    public static Tag<Item> getItemTagFromResourceLocation(ResourceLocation itemTagLocation, String stackTraceMessageOnError){
        return SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, itemTagLocation, (tempTag) -> {
            return new JsonSyntaxException("Invalid ResourceLocation for getItemTagFromResourceLocation passed by: " + stackTraceMessageOnError + "; The offending tag is: '" + tempTag + "'");
        });
    }

    public static Tag<Block> getBlockTagFromResourceLocation(ResourceLocation blockTagLocation, String stackTraceMessageOnError){
        return SerializationTags.getInstance().getTagOrThrow(Registry.BLOCK_REGISTRY, blockTagLocation, (tempTag) -> {
            return new JsonSyntaxException("Invalid ResourceLocation for getBlockTagFromResourceLocation passed by: " + stackTraceMessageOnError + "; The offending tag is: '" + tempTag + "'");
        });
    }

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
}
