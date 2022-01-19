package com.veteam.voluminousenergy.util;

import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

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

    public static Tag.Named<Block> getStaticBlockTagFromResourceLocation(String blockTagLocation, String stackTraceMessageOnError){ // Doesn't work
        StaticTagHelper<Block> staticTagHelper = StaticTags.create(Registry.BLOCK_REGISTRY, "tags/blocks");
        Tag.Named<Block> namedBlockTag = staticTagHelper.bind(blockTagLocation);
        return namedBlockTag;
    }

    // Use for things like ores
    public static Tags.IOptionalNamedTag<Block> getIOptionalNamedBlockTagFromResourceLocation(ResourceLocation blockTagLocation){
        return ForgeTagHandler.createOptionalTag(ForgeRegistries.BLOCKS, blockTagLocation);
    }
}
