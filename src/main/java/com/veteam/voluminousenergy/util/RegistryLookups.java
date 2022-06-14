package com.veteam.voluminousenergy.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryLookups {

    // Lookup an item's ResourceLocation
    public static ResourceLocation lookupItem(Item item){
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation lookupItem(ItemStack itemStack){
        return lookupItem(itemStack.getItem());
    }

    // Get BlockEntityType key
    /** This uses BlockEntityType.getKey instead of looking up in the Forge registry **/
    public static ResourceLocation getBlockEntityTypeKey(BlockEntityType<?> blockEntityType){
        return BlockEntityType.getKey(blockEntityType);
    }

    /** This uses BlockEntityType.getKey instead of looking up in the Forge registry **/
    public static ResourceLocation getBlockEntityTypeKey(BlockEntity blockEntity){
        return getBlockEntityTypeKey(blockEntity.getType());
    }

    public static ResourceLocation lookupBlockEntityType(BlockEntityType<?> blockEntityType){
        return ForgeRegistries.BLOCK_ENTITIES.getKey(blockEntityType);
    }
}
